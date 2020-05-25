package com.example.tureguideversion1.Weather;

import com.google.gson.annotations.SerializedName;

public class DailyWeatherForcast {
    @SerializedName("id")
    public int id;
    @SerializedName("main")
    public String main;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;
}
