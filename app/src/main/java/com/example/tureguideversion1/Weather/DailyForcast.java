package com.example.tureguideversion1.Weather;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DailyForcast {
    @SerializedName("dt")
    public long dt;
    @SerializedName("temp")
    public DailyTemp dailyTemps;
    @SerializedName("weather")
    public ArrayList<DailyWeatherForcast> dailyWeatherForcasts = new ArrayList<DailyWeatherForcast>();
}
