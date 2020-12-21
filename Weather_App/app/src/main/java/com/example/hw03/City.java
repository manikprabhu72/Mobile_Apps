package com.example.hw03;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class City implements Serializable {
    private String cityKey, cityName, countryName,adminArea, weatherText, headline;
    private int weatherIcon;
    private Double metricTemp;
    private Date localObservationDateTime;
    private ArrayList<Forecast> forecastArrayList;
    private boolean saved = false;


    public ArrayList<Forecast> getForecastArrayList() {
        return forecastArrayList;
    }

    public void setForecastArrayList(ArrayList<Forecast> forecastArrayList) {
        this.forecastArrayList = forecastArrayList;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getCityKey() {
        return cityKey;
    }

    public void setCityKey(String cityKey) {
        this.cityKey = cityKey;
    }



//    public City(String cityName, String countryName, String weatherText, int weatherIcon, Double metricTemp, Date localObservationDateTime) {
//        this.cityName = cityName;
//        this.countryName = countryName;
//        WeatherText = weatherText;
//        this.weatherIcon = weatherIcon;
//        this.metricTemp = metricTemp;
//        this.localObservationDateTime = localObservationDateTime;
//    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", WeatherText='" + weatherText + '\'' +
                ", weatherIcon=" + weatherIcon +
                ", metricTemp=" + metricTemp +
                ", localObservationDateTime=" + localObservationDateTime +
                '}';
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherText() {
        return this.weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public Double getMetricTemp() {
        return metricTemp;
    }

    public void setMetricTemp(Double metricTemp) {
        this.metricTemp = metricTemp;
    }

    public Date getLocalObservationDateTime() {
        return localObservationDateTime;
    }

    public void setLocalObservationDateTime(Date localObservationDateTime) {
        this.localObservationDateTime = localObservationDateTime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        City city = (City) obj;
        if(this.getCityKey().equals(city.getCityKey())){
            return true;
        }
        return false;
    }
}
