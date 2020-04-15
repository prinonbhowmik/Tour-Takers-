package com.example.tureguideversion1.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.androdocs.httprequest.HttpRequest;
import com.example.tureguideversion1.ForApi.ApiInterFace;
import com.example.tureguideversion1.ForApi.ApiUtils;
import com.example.tureguideversion1.R;
import com.example.tureguideversion1.Weather.WeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class WeatherFragment extends Fragment {

    String API = "618e3a096dcd96b86ffa64b35ef140e1";

    String CITY = "";

    FusedLocationProviderClient providerClient;

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;


    private String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

    double lat, lon;

    int MY_PERMISSION ;
    ApiInterFace api;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_weather, container, false);
        addressTxt = view.findViewById(R.id.address);
        updated_atTxt = view.findViewById(R.id.updated_at);
        statusTxt = view.findViewById(R.id.status);
        tempTxt = view.findViewById(R.id.temp);
        temp_minTxt = view.findViewById(R.id.temp_min);
        temp_maxTxt = view.findViewById(R.id.temp_max);
        sunriseTxt = view.findViewById(R.id.sunrise);
        sunsetTxt = view.findViewById(R.id.sunset);
        windTxt = view.findViewById(R.id.wind);
        pressureTxt = view.findViewById(R.id.pressure);
        humidityTxt = view.findViewById(R.id.humidity);
        api = ApiUtils.getUserService();
        providerClient = new FusedLocationProviderClient(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(permission,0);
            }
        }

        Task location = providerClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Location currentLocation = (Location) task.getResult();
                lat = currentLocation.getLatitude();
                lon = currentLocation.getLongitude();
                findweather(lat,lon);
            }
        });
        return view;
    }

    private void findweather(final double lat, final double lon) {
        Call<WeatherResponse> call = api.getWeather(lat,lon,API);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()){
                    if (response.body()==null){
                        return;
                    }else{
                        WeatherResponse weatherResponse = response.body();
                        String address = "";

                        Geocoder geocoder = new Geocoder(getContext(),Locale.getDefault());

                        List<Address>  addresses;

                        try {
                            addresses = geocoder.getFromLocation(lat,lon,1);
                            if(addresses.size()>0){
                                address = addresses.get(0).getLocality();
                                addressTxt.setText(address);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                      //  addressTxt.setText(weatherResponse.name+","+weatherResponse.sys.country);

                        Float updatedAt = weatherResponse.dt;
                        // Date time  = new Date(updatedAt.longValue());
                        String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt.longValue() *1000));
                        updated_atTxt.setText(updatedAtText);
                        statusTxt.setText(weatherResponse.weather.get(0).description);
                        int temp = (int) weatherResponse.main.temp;
                        tempTxt.setText(String.valueOf(temp)+"°C");
                        temp_minTxt.setText("Min Temp: "+String.valueOf(weatherResponse.main.temp_min)+"°C");
                        temp_maxTxt.setText("Max Temp: "+String.valueOf(weatherResponse.main.temp_max)+"°C");

                        long sunrise = weatherResponse.sys.sunrise;
                        String sunrisetime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise*1000));
                        sunriseTxt.setText(sunrisetime);
                        long sunset = weatherResponse.sys.sunset;
                        String sunsettime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset*1000));
                        sunsetTxt.setText(sunsettime);
                        windTxt.setText(String.valueOf(weatherResponse.wind.speed));
                        pressureTxt.setText(String.valueOf(weatherResponse.main.pressure));
                        humidityTxt.setText(String.valueOf(weatherResponse.main.humidity));
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });
    }


}
