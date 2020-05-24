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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
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
    private LottieAnimationView weatherAnim;
    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, feels_like, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;


    private String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

    double lat, lon;

    int MY_PERMISSION ;
    ApiInterFace api;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_weather, container, false);
        addressTxt = view.findViewById(R.id.address);
        updated_atTxt = view.findViewById(R.id.updated_at);
        statusTxt = view.findViewById(R.id.status);
        tempTxt = view.findViewById(R.id.temp);
        feels_like = view.findViewById(R.id.feels_like);
        sunriseTxt = view.findViewById(R.id.sunrise);
        sunsetTxt = view.findViewById(R.id.sunset);
        windTxt = view.findViewById(R.id.wind);
        pressureTxt = view.findViewById(R.id.pressure);
        humidityTxt = view.findViewById(R.id.humidity);
        weatherAnim = view.findViewById(R.id.weatherAnim);
        api = ApiUtils.getUserService();
        providerClient = new FusedLocationProviderClient(getContext());

        if (checkLocationPermission()){
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
        }
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
                        if(weatherResponse.weather.get(0).icon.contains("d")) {
                            if (weatherResponse.weather.get(0).description.matches("light thunderstorm") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm") ||
                                    weatherResponse.weather.get(0).description.matches("heavy thunderstorm") ||
                                    weatherResponse.weather.get(0).description.matches("ragged thunderstorm")) {
                                weatherAnim.setAnimation("thunder.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("thunderstorm with light rain") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with rain") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with heavy rain") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with light drizzle") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with drizzle") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with heavy drizzle")) {
                                weatherAnim.setAnimation("stormshowersday.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Drizzle") ||
                                    weatherResponse.weather.get(0).description.matches("light intensity shower rain") ||
                                    weatherResponse.weather.get(0).description.matches("shower rain") ||
                                    weatherResponse.weather.get(0).description.matches("heavy intensity shower rain") ||
                                    weatherResponse.weather.get(0).description.matches("ragged shower rain")) {
                                weatherAnim.setAnimation("dizzle.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Snow") ||
                                    weatherResponse.weather.get(0).description.matches("freezing rain")) {
                                weatherAnim.setAnimation("snow.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Clear")) {
                                weatherAnim.setAnimation("sunny.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("few clouds")) {
                                weatherAnim.setAnimation("partly_cloudy.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("scattered clouds") ||
                                    weatherResponse.weather.get(0).description.matches("broken clouds") ||
                                    weatherResponse.weather.get(0).description.matches("overcast clouds")) {
                                weatherAnim.setAnimation("windy.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("light rain") ||
                                    weatherResponse.weather.get(0).description.matches("moderate rain") ||
                                    weatherResponse.weather.get(0).description.matches("heavy intensity rain") ||
                                    weatherResponse.weather.get(0).description.matches("very heavy rain") ||
                                    weatherResponse.weather.get(0).description.matches("extreme rain")) {
                                weatherAnim.setAnimation("partly_shower.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Fog")) {
                                weatherAnim.setAnimation("foggyDay.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Mist") ||
                                    weatherResponse.weather.get(0).main.matches("Smoke") ||
                                    weatherResponse.weather.get(0).main.matches("Haze") ||
                                    weatherResponse.weather.get(0).main.matches("Dust") ||
                                    weatherResponse.weather.get(0).main.matches("Sand") ||
                                    weatherResponse.weather.get(0).main.matches("Ash") ||
                                    weatherResponse.weather.get(0).main.matches("Squall")) {
                                weatherAnim.setAnimation("mist.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Tornado")) {
                                weatherAnim.setAnimation("tornado.json");
                                weatherAnim.playAnimation();
                            }
                        }else if(weatherResponse.weather.get(0).icon.contains("n")) {
                            if (weatherResponse.weather.get(0).description.matches("light thunderstorm") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm") ||
                                    weatherResponse.weather.get(0).description.matches("heavy thunderstorm") ||
                                    weatherResponse.weather.get(0).description.matches("ragged thunderstorm")) {
                                weatherAnim.setAnimation("thunder.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("thunderstorm with light rain") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with rain") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with heavy rain") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with light drizzle") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with drizzle") ||
                                    weatherResponse.weather.get(0).description.matches("thunderstorm with heavy drizzle")) {
                                weatherAnim.setAnimation("storm.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Drizzle") ||
                                    weatherResponse.weather.get(0).description.matches("light intensity shower rain") ||
                                    weatherResponse.weather.get(0).description.matches("shower rain") ||
                                    weatherResponse.weather.get(0).description.matches("heavy intensity shower rain") ||
                                    weatherResponse.weather.get(0).description.matches("ragged shower rain")) {
                                weatherAnim.setAnimation("dizzle.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Snow") ||
                                    weatherResponse.weather.get(0).description.matches("freezing rain")) {
                                weatherAnim.setAnimation("snow.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Clear")) {
                                weatherAnim.setAnimation("night.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("few clouds")) {
                                weatherAnim.setAnimation("cloudynight.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("scattered clouds") ||
                                    weatherResponse.weather.get(0).description.matches("broken clouds") ||
                                    weatherResponse.weather.get(0).description.matches("overcast clouds")) {
                                weatherAnim.setAnimation("windy.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).description.matches("light rain") ||
                                    weatherResponse.weather.get(0).description.matches("moderate rain") ||
                                    weatherResponse.weather.get(0).description.matches("heavy intensity rain") ||
                                    weatherResponse.weather.get(0).description.matches("very heavy rain") ||
                                    weatherResponse.weather.get(0).description.matches("extreme rain")) {
                                weatherAnim.setAnimation("rainynight.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Fog")) {
                                weatherAnim.setAnimation("mist.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Mist") ||
                                    weatherResponse.weather.get(0).main.matches("Smoke") ||
                                    weatherResponse.weather.get(0).main.matches("Haze") ||
                                    weatherResponse.weather.get(0).main.matches("Dust") ||
                                    weatherResponse.weather.get(0).main.matches("Sand") ||
                                    weatherResponse.weather.get(0).main.matches("Ash") ||
                                    weatherResponse.weather.get(0).main.matches("Squall")) {
                                weatherAnim.setAnimation("mist.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.weather.get(0).main.matches("Tornado")) {
                                weatherAnim.setAnimation("tornado.json");
                                weatherAnim.playAnimation();
                            }
                        }
                        String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt.longValue() *1000));
                        updated_atTxt.setText(updatedAtText);
                        statusTxt.setText(weatherResponse.weather.get(0).description);
                        tempTxt.setText(Math.round(weatherResponse.main.temp)+"°C");
                        feels_like.setText("Feels Like: "+Math.round(weatherResponse.main.feels_like)+"°C");
                        long sunrise = weatherResponse.sys.sunrise;
                        String sunrisetime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise*1000));
                        sunriseTxt.setText(sunrisetime);
                        long sunset = weatherResponse.sys.sunset;
                        String sunsettime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset*1000));
                        sunsetTxt.setText(sunsettime);
                        windTxt.setText(Math.round(weatherResponse.wind.speed)+" Meter/Sec");
                        pressureTxt.setText(String.valueOf(weatherResponse.main.pressure));
                        humidityTxt.setText(Math.round(weatherResponse.main.humidity)+"%");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkLocationPermission(){
        if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            return false;
        }

        return true;
    }

}
