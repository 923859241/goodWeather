package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecast {
    @SerializedName("last_update")
    public String lastUpdateTime;

    @SerializedName("daily")
    public List<DayForecast> forecastList;

    public Location location;
    public class Location{
        @SerializedName("name")
        public String cityName;
        @SerializedName("id")
        public String cityId;

    }

}
