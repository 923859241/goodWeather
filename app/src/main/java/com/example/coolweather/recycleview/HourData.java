package com.example.coolweather.recycleview;

public class HourData {
    private String time;//时间
    private String weatherPhenomena;//天气现象文字
    private String temperature;//该小时内的温度

    public HourData(String time,String weatherPhenomena,String temperature){
        this.time = time;
        this.weatherPhenomena = weatherPhenomena;
        this.temperature = temperature;
    }

    public String getTime(){
        return this.time;
    }
    public String getTemperature() {
        return temperature;
    }
    public String getWeatherPhenomena() {
        return weatherPhenomena;
    }
}
