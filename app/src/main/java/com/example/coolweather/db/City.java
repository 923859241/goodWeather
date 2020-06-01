package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private String cityID;
    private String cityName;
    private String cityPath;

    public String getCityID(){
        return this.cityID;
    }
    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityPath() {
        return cityPath;
    }
    public void setCityPath(String cityPath) {
        this.cityPath = cityPath;
    }
}
