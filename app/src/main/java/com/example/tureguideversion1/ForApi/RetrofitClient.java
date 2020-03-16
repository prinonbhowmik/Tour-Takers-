package com.example.tureguideversion1.ForApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private  static final String BASE_URL="http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit;


    public static Retrofit getRetrofitClient(){
        if(retrofit!=null){
            return retrofit;
        }

        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
