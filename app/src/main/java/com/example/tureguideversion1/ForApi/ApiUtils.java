package com.example.tureguideversion1.ForApi;

public class ApiUtils {

    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    public static ApiInterFace getUserService(){

        return RetrofitClient.getClient(BASE_URL).create(ApiInterFace.class);
    }
}
