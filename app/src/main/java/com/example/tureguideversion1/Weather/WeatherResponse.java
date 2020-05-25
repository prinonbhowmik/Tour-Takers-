package com.example.tureguideversion1.Weather;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherResponse {
    @SerializedName("message")
    public String message;

    @SerializedName("current")
    public CurrentWeather currentWeather;

    @SerializedName("hourly")
    public ArrayList<HourlyForcast> hourlyWeather = new ArrayList<HourlyForcast>();

    @SerializedName("daily")
    public ArrayList<DailyForcast> dailyForcasts = new ArrayList<DailyForcast>();
//    @SerializedName("coord")
//    public Coord coord;
//    @SerializedName("sys")
//    public Sys sys;
//    @SerializedName("weather")
//    public ArrayList<Weather> weather = new ArrayList<Weather>();
//    @SerializedName("main")
//    public Main main;
//    @SerializedName("wind")
//    public Wind wind;
//    @SerializedName("rain")
//    public Rain rain;
//    @SerializedName("clouds")
//    public Clouds clouds;
//    @SerializedName("dt")
//    public float dt;
//    @SerializedName("id")
//    public int id;
//    @SerializedName("name")
//    public String name;
//    @SerializedName("cod")
//    public float cod;
}

