package com.example.coolweather.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class AllWeatherData extends LitePalSupport {
    @Column(unique = true)
    private String cityID;
    private String airNow;
    private String suggestion;
    private String weatherForecast;
    private String weatherHour;
    private String weatherNow;

    public String getSuggestion() {
        return suggestion;
    }

    public String getWeatherForecast() {
        return weatherForecast;
    }

    public String getWeatherHour() {
        return weatherHour;
    }

    public String getWeatherNow() {
        return weatherNow;
    }

    public String getCityID() {
        return cityID;
    }

    public String getAirNow() {
        return airNow;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public void setAirNow(String airNow) {
        this.airNow = airNow;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public void setWeatherForecast(String weatherForecast) {
        this.weatherForecast = weatherForecast;
    }

    public void setWeatherHour(String weatherHour) {
        this.weatherHour = weatherHour;
    }

    public void setWeatherNow(String weatherNow) {
        this.weatherNow = weatherNow;
    }
}
