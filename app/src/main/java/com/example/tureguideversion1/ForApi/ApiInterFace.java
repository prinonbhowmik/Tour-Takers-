package com.example.tureguideversion1.ForApi;

import com.example.tureguideversion1.Weather.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterFace {
    @GET("weather?lat=23.7508961&lon=90.3842619&units=metric&appid=618e3a096dcd96b86ffa64b35ef140e1")
    Call<WeatherResponse> getWeather(@Query("lat") double lat,
                                     @Query("lon")double lon,
                                     @Query("appid")String api);
}
