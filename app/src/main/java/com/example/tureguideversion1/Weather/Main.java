package com.example.tureguideversion1.Weather;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    public float temp;
    @SerializedName("feels_like")
    public float feels_like;
    @SerializedName("humidity")
    public float humidity;
    @SerializedName("pressure")
    public float pressure;
    @SerializedName("temp_min")
    public float temp_min;
    @SerializedName("temp_max")
    public float temp_max;
}
