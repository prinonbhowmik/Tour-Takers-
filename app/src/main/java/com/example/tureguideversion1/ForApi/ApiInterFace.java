package com.example.tureguideversion1.ForApi;

import com.example.tureguideversion1.Weather.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterFace {
    @GET("weather?q=dhaka,bd&units=metric&appid=618e3a096dcd96b86ffa64b35ef140e1")
    Call<WeatherResponse> getWeather(@Query("city") String city,
                                     @Query("appid")String api);
}
