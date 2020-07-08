package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class AirNow {
    public City city;

    public class City{
        public String aqi;
        public String pm25;
        @SerializedName("quality")
        public String airQuality;
    }
}
