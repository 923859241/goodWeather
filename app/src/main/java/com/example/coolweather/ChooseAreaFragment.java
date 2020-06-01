package com.example.coolweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolweather.db.City;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    //查询栏
    private  Button searchCity;
    private EditText inputText;
    //ListView
    private ListView listView;
    private ArrayAdapter adapter;
    private List<String> dataList = new ArrayList<>();
    //城市列表
    private List<City> cityList = new ArrayList<>();
    private City selectedCity;

    //查询的url
    private String searchCityURL = "https://api.seniverse.com/v3/location/search.json?key=S76dHQUmvm2BGki0k&q=";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        searchCity = (Button) view.findViewById(R.id.search_city);
        inputText = (EditText)view.findViewById(R.id.input_city);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),R.layout.simple_list_item,dataList);
        listView.setAdapter(adapter);
        searchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.search_city:
                        String context = inputText.getText().toString();
                        if(!"".equals(context)){
                            inputText.setText("");
                            showCity(searchCityURL+context);
                        }else{
                            Toast.makeText(getContext(),"输入为空，请重新输入！",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = cityList.get(position);
                String cityId = selectedCity.getCityID();
                Intent intent = new Intent(getContext(),WeatherActivity.class);
                intent.putExtra("cityId",cityId);
                SharedPreferences.Editor editor = getContext().getSharedPreferences("WeatherData", Context.MODE_PRIVATE).edit();
                editor.putString("cityId",cityId);
                editor.apply();
                startActivity(intent);
                Toast.makeText(getContext(),
                        "你当前选择的城市路径为："+selectedCity.getCityPath(),Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }


    //在listView上显示查询结果
    public void showCity(String cityURL){
        HttpUtil.sendOkHttpRequest(cityURL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                adapter.setNotifyOnChange(true);
                boolean result = false;
                String responseText = response.body().string();
                result = Utility.handleCityResponse(responseText,cityList);
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dataList.clear();
                            if(cityList.size()<1){
                                Toast.makeText(getContext(),"返回值为空，请重新输入！",Toast.LENGTH_SHORT).show();
                            }else{
                                for (City city : cityList) {
                                    dataList.add(city.getCityPath());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"返回值为空",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

}
