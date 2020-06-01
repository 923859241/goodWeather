package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class HourForecast {
    public String time;//时间
    @SerializedName("text")
    public String weatherPhenomena;//天气现象文字
    public String temperature;//该小时内的温度
}
