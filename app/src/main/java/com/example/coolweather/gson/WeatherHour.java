package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherHour {
    @SerializedName("hourly")
    public List<HourForecast> hourlyList;
}
