package com.example.coolweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.coolweather.gson.AirNow;
import com.example.coolweather.gson.DayForecast;
import com.example.coolweather.gson.HourForecast;
import com.example.coolweather.gson.Suggestion;
import com.example.coolweather.gson.WeatherForecast;
import com.example.coolweather.gson.WeatherHour;
import com.example.coolweather.gson.WeatherNow;
import com.example.coolweather.recycleview.HourAdapter;
import com.example.coolweather.recycleview.HourData;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipeRefresh;//下拉更新

    private String mWeatherId;

    private ImageView bingPicImg;//每日一图

    private ScrollView weatherLayout;//天气主体界面大框架

    public DrawerLayout drawerLayout;//更新城市

    private Button navButton;

    private Button newsButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;//预测界面

    private LinearLayout hourLayout;//小时天气数据页面

    private List<HourData> hourDataList = new ArrayList<>();//小时天气list

    private RecyclerView HourRecyclerView;

    //其他空气数据
    private TextView aqiText;

    private TextView pm25Text;

    private TextView airQualityText;

    private TextView feelLikeText;

    private TextView visibilityText;

    private TextView humidityText;

    //建议数据
    private TextView datingText;

    private TextView sunscreenText;

    private TextView umbrellaText;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //背景图与状态栏融合
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        //初始化控件
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        //titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        feelLikeText = (TextView)findViewById(R.id.feelsLike_text);
        visibilityText = (TextView)findViewById(R.id.visibility_text);
        degreeText = (TextView)findViewById(R.id.degree_text);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //hourLayout = (LinearLayout)findViewById(R.id.hour_layout);
        HourRecyclerView = (RecyclerView)findViewById(R.id.hour_Recycler_view);

        navButton = (Button)findViewById(R.id.nav_button);
        newsButton = (Button)findViewById(R.id.news_button);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        airQualityText = (TextView)findViewById(R.id.quality_text);
        humidityText = (TextView)findViewById(R.id.humidity_text);
        datingText = (TextView)findViewById(R.id.dating_text);
        sunscreenText = (TextView)findViewById(R.id.sunscreen_text);
        umbrellaText = (TextView)findViewById(R.id.umbrella_text);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs = getSharedPreferences("WeatherData",MODE_PRIVATE);
        String weatherNowString = prefs.getString("weatherNow",null);
        String weatherForecastString = prefs.getString("weatherForecast",null);
        String weatherHourString = prefs.getString("weatherHour",null);
        String airNowString = prefs.getString("airNow",null);
        String suggestionString = prefs.getString("suggestion",null);

        //处理图片
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        /*
        判断是否有没有缓存 自动更新数据
         */
        if(false&&weatherNowString != null && weatherForecastString!=null &&
        weatherHourString != null && airNowString!=null){
            //有缓存则直接解析天气数据
            WeatherForecast weatherForecast = Utility.handleWeatherForecastResponse(weatherForecastString);
            WeatherNow weatherNow = Utility.handleWeatherNowResponse(weatherNowString);
            WeatherHour weatherHour = Utility.handleWeatherHourResponse(weatherHourString);
            AirNow airNow = Utility.handleAirNowResponse(airNowString);
            Suggestion suggestion = Utility.handleSuggestionResponse(suggestionString);
            mWeatherId = weatherForecast.location.cityId;

            showWeatherForecastInfo(weatherForecast);
            showWeatherNowInfo(weatherNow);
            showAirInfo(airNow);
            showWeatherHourInfo(weatherHour);
            showSuggestion(suggestion);
        }else{
            //无缓存则去服务器查询天气
            String weatherId = getIntent().getStringExtra("cityId");
            mWeatherId = weatherId;
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this,"更新城市",Toast.LENGTH_LONG).show();
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                Toast.makeText(WeatherActivity.this,"更新数据",
                        Toast.LENGTH_SHORT).show();
            }
        });
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this,"打开新闻",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WeatherActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });
    }

    //根据城市ID请求城市信息
    public void requestWeather(final String weatherId) {
        String key = "SkoWrm11o2enBSrSJ";
        String forecastUrl = "https://api.seniverse.com/v3/weather/daily.json?key="+key +
                "&start=0&days=7&location=" + weatherId;
        String nowUrl = "https://api.seniverse.com/v3/weather/now.json?key="+key +
                "&location=" + weatherId;
        String hourUrl = "https://api.seniverse.com/v3/weather/hourly.json?key="+key +
                "&start=0&hours=12&location=" + weatherId;
        String airUrl = "https://api.seniverse.com/v3/air/now.json?key="+key +
                "&location=" + weatherId;
        String suggestionUrl = "https://api.seniverse.com/v3/life/suggestion.json?key="+key +
                "&location=" + weatherId;

        //加载图片
        loadBingPic();
        //获取now的数据
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"访问天气URL失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherNow weatherNow = Utility.handleWeatherNowResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weatherNow!=null && weatherNow.now.clouds!=null){
                            SharedPreferences.Editor editor = getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                            editor.putString("weatherNow",responseText);
                            editor.apply();
                            showWeatherNowInfo(weatherNow);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取当天天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //获取Air的数据
        HttpUtil.sendOkHttpRequest(airUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"访问天气URL失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final AirNow airNow = Utility.handleAirNowResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(airNow!=null){
                            SharedPreferences.Editor editor = getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                            editor.putString("airNow",responseText);
                            editor.apply();
                            showAirInfo(airNow);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取当天天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //获取forecast的数据
        HttpUtil.sendOkHttpRequest(forecastUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取未来天气信息失败",Toast.LENGTH_LONG).show();
                    }
                });
                swipeRefresh.setRefreshing(false);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherForecast weatherForecast = Utility.handleWeatherForecastResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weatherForecast!=null && weatherForecast.location!=null){
                            SharedPreferences.Editor editor = getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                            editor.putString("weatherForecast",responseText);
                            editor.apply();
                            mWeatherId = weatherForecast.location.cityId;
                            showWeatherForecastInfo(weatherForecast);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取未来天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //获取hour的数据
        HttpUtil.sendOkHttpRequest(hourUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取未来小时的天气信息失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherHour weatherHour = Utility.handleWeatherHourResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weatherHour!=null){
                            SharedPreferences.Editor editor = getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                            editor.putString("weatherHour",responseText);
                            editor.apply();
                            showWeatherHourInfo(weatherHour);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取未来小时天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //获取suggestion的数据
        HttpUtil.sendOkHttpRequest(suggestionUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取建议信息失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Suggestion suggestion = Utility.handleSuggestionResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(suggestion!=null){
                            SharedPreferences.Editor editor = getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                            editor.putString("suggestion",responseText);
                            editor.apply();
                            showSuggestion(suggestion);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取建议信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //展示当前时刻天气数据
    private void showWeatherNowInfo(WeatherNow weatherNow){
        if(weatherNow==null){
            Toast.makeText(WeatherActivity.this,"展示的数据为空",Toast.LENGTH_SHORT).show();
            return;
        }

        String updateTime = weatherNow.updateTime.split("T")[1];
        updateTime =updateTime.split(":")[0]+":"+updateTime.split(":")[1];


        String degree = weatherNow.now.temperature+"℃";
        String phenomena = weatherNow.now.weatherPhenomena;
        String feelLike = weatherNow.now.feelsLike+"℃";
        String humidity = weatherNow.now.humidity+"%";
        String visibility = weatherNow.now.visibility+"公里";

        humidityText.setText(humidity);
        feelLikeText.setText(feelLike);
        degreeText.setText(degree);
        visibilityText.setText(visibility);
        weatherInfoText.setText(phenomena);
        weatherLayout.setVisibility(View.VISIBLE);
        //titleUpdateTime.setText(updateTime);
    }

    //展示当前时刻空气数据
    private void showAirInfo(AirNow airNow){
        if(airNow==null){
            Toast.makeText(WeatherActivity.this,"展示的空气数据为空",Toast.LENGTH_SHORT).show();
            return;
        }

        String aqi= airNow.city.aqi;
        String pm25 = airNow.city.pm25;
        String quality = airNow.city.airQuality;

        aqiText.setText(aqi);
        pm25Text.setText(pm25);
        airQualityText.setText(quality);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //展示未来每天天气数据
    private void showWeatherForecastInfo(WeatherForecast weatherForecast){
        if(weatherForecast==null){
            Toast.makeText(WeatherActivity.this,"展示的数据为空",Toast.LENGTH_SHORT).show();
            return;
        }
        String cityName = weatherForecast.location.cityName;
        titleCity.setText(cityName);

        forecastLayout.removeAllViews();
        for(DayForecast forecast:weatherForecast.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.weatherPhenomena);
            maxText.setText(forecast.highTemperature);
            minText.setText(forecast.lowTemperature);

            forecastLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //展示未来每小时天气数据
    private void showWeatherHourInfo(WeatherHour weatherHour) {
        if(weatherHour==null){
            Toast.makeText(WeatherActivity.this,"展示每小时的数据为空",Toast.LENGTH_SHORT).show();
            return;
        }
        HourRecyclerView.removeAllViews();
        for(HourForecast hourforecast:weatherHour.hourlyList){
            //构建时间
            String Time = hourforecast.time.split("T")[1];
            Time =Time.split(":")[0]+":"+Time.split(":")[1];
            HourData hourData = new HourData(Time,hourforecast.weatherPhenomena,
                    hourforecast.temperature);
            hourDataList.add(hourData);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        HourRecyclerView.setLayoutManager(linearLayoutManager);
        HourAdapter adapter = new HourAdapter(hourDataList);
        HourRecyclerView.setAdapter(adapter);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //展示当前时刻建议数据
    private void showSuggestion(Suggestion suggestion){
        if(suggestion==null){
            Toast.makeText(WeatherActivity.this,"展示的建议数据为空",Toast.LENGTH_SHORT).show();
            return;
        }

        String dating= "约会建议: "+suggestion.dating.brief+"\n"+suggestion.dating.details;
        String sunscreen = "防晒建议: "+suggestion.sunscreen.brief+"\n"+suggestion.sunscreen.details;
        String umbrella = "雨伞建议: "+suggestion.umbrella.brief+"\n"+suggestion.umbrella.details;

        datingText.setText(dating);
        sunscreenText.setText(sunscreen);
        umbrellaText.setText(umbrella);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }


}
