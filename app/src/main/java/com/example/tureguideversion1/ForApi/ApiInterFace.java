package com.example.tureguideversion1.ForApi;

import com.example.tureguideversion1.Weather.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterFace {
    @GET("onecall")
    Call<WeatherResponse> getWeather(@Query("lat") Double lat,
                                     @Query("lon") Double lon,
                                     @Query("units") String units,
                                     @Query("appid")String api);
}
