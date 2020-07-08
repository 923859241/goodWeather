package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class WeatherNow {
    public Now now;

    @SerializedName("last_update")
    public String updateTime;

    public class Now{
        @SerializedName("text")
        public String weatherPhenomena;//天气现象文字

        public String code;//天气现象标识

        public String temperature;//温度，单位为c摄氏度或f华氏度

        @SerializedName("feels_like")
        public String feelsLike;//体感温度，单位为c摄氏度或f华氏度

        public String pressure;//气压，单位为mb百帕或in英寸

        public String humidity;//相对湿度，0~100，单位为百分比

        public String visibility;//能见度，单位为km公里或mi英里

        @SerializedName("wind_direction")
        public String windDirection;//风向文字

        @SerializedName("wind_speed")
        public String windSpeed;//风速

        @SerializedName("wind_scale")
        public String windScale;//风力等级

        public String clouds;//云量，单位%，范围0~100，天空被云覆盖的百分比

        //最后更新时间
        @SerializedName("last_update")
        public String lastUpdateTime;
    }

}
