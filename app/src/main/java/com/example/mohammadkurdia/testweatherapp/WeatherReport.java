package com.example.mohammadkurdia.testweatherapp;

/**
 * Created by Mohammad Kurdia on 12/22/2017.
 */

//To easily Store data and Retrieve data

public class WeatherReport {

    private String cityName;
    private String maxTemp;
    private String minTemp;
    private String weatherType;
    private String pressure;
    private String seaLevel;


    public WeatherReport(String cityName, String maxTemp, String minTemp,String weatherType,String pressure,String seaLevel) {
        this.cityName = cityName;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weatherType = weatherType;
        this.pressure = pressure;
        this.seaLevel = seaLevel;
    }

    public String getCityName() {
        return cityName;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getWeatherType() {
        return weatherType;
    }
    public String getPressure() {
        return pressure;
    }
    public String getSeaLevel() {
        return seaLevel;
    }
}
