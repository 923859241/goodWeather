package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class DayForecast {
    public String date;//日期
    @SerializedName("text_day")
    public String weatherPhenomena;//天气现象文字
    @SerializedName("high")
    public String highTemperature;//最高温
    @SerializedName("low")
    public String lowTemperature;//最低温
}
