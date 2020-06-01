package com.example.coolweather.util;

import android.text.TextUtils;
import com.example.coolweather.db.City;
import com.example.coolweather.gson.AirNow;
import com.example.coolweather.gson.Suggestion;
import com.example.coolweather.gson.WeatherForecast;
import com.example.coolweather.gson.WeatherHour;
import com.example.coolweather.gson.WeatherNow;
import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Utility {
    public static boolean handleCityResponse(String response,List<City> responseCityList){
        if(!TextUtils.isEmpty(response)){
            try {
                if(!responseCityList.isEmpty()) responseCityList.clear();
                JSONObject JSONresponse = new JSONObject(response);
                JSONArray allCity = JSONresponse.getJSONArray("results");
                for(int i = 0;i<allCity.length();i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityID(cityObject.getString("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setCityPath(cityObject.getString("path"));
                    responseCityList.add(city);
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    //把Now的数据转换为实体
    public static WeatherNow handleWeatherNowResponse(String response){
        try{
            JSONObject JSONresponse = new JSONObject(response);
            JSONArray resultsData = JSONresponse.getJSONArray("results");
            String weatherContent = resultsData.getJSONObject(0).toString();
            //String weatherContent = cityData.getJSONObject("now").toString();
            return new Gson().fromJson(weatherContent,WeatherNow.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //把Forecast数据转换为实体
    public static WeatherForecast handleWeatherForecastResponse(String response){
        try{
            JSONObject JSONresponse = new JSONObject(response);
            JSONArray cityData = JSONresponse.getJSONArray("results");
            String weatherContent = cityData.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,WeatherForecast.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //把hour数据转换为实体
    public static WeatherHour handleWeatherHourResponse(String response){
        try{
            JSONObject JSONresponse = new JSONObject(response);
            JSONArray cityData = JSONresponse.getJSONArray("results");
            String weatherContent = cityData.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, WeatherHour.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //把Air数据转换为实体
    public static AirNow handleAirNowResponse(String response){
        try{
            JSONObject JSONresponse = new JSONObject(response);
            JSONArray resultsData = JSONresponse.getJSONArray("results");
            JSONObject cityData = resultsData.getJSONObject(0);
            String weatherContent = cityData.getJSONObject("air").toString();
            return new Gson().fromJson(weatherContent,AirNow.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //把suggestion数据转换为实体
    public static Suggestion handleSuggestionResponse(String response){
        try{
            JSONObject JSONresponse = new JSONObject(response);
            JSONArray resultsData = JSONresponse.getJSONArray("results");
            JSONObject cityData = resultsData.getJSONObject(0);
            String weatherContent = cityData.getJSONObject("suggestion").toString();
            return new Gson().fromJson(weatherContent, Suggestion.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



}
