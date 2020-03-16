package com.example.tureguideversion1.ForApi;

import com.example.tureguideversion1.Model.CurrentWeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterFace {

    @GET("weather?q=dhaka&units=metric&appid=618e3a096dcd96b86ffa64b35ef140e1")
    Call<CurrentWeatherModel> getCurrentWeather();
}
