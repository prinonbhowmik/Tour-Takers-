package com.example.tureguideversion1.Model;

public class DailyForcastList {
    private String daliyImage;
    private Float minTemp;
    private Float maxTemp;
    private String daliyDescription;
    private Long dailytime;
    private Long sunrise;
    private Long sunset;
    private float humidity;
    private float wind_speed;
    private int clouds;
    private float dDewPoint;

    public DailyForcastList(String daliyImage, Float minTemp, Float maxTemp, String daliyDescription, Long dailytime, Long sunrise, Long sunset, float humidity, float wind_speed, int clouds, float dDewPoint) {
        this.daliyImage = daliyImage;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.daliyDescription = daliyDescription;
        this.dailytime = dailytime;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.humidity = humidity;
        this.wind_speed = wind_speed;
        this.clouds = clouds;
        this.dDewPoint = dDewPoint;
    }

    public String getDaliyImage() {
        return daliyImage;
    }

    public Float getMinTemp() {
        return minTemp;
    }

    public Float getMaxTemp() {
        return maxTemp;
    }

    public String getDaliyDescription() {
        return daliyDescription;
    }

    public Long getDailytime() {
        return dailytime;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public int getClouds() {
        return clouds;
    }

    public float getdDewPoint() {
        return dDewPoint;
    }
}
