package com.example.tureguideversion1.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tureguideversion1.Adapters.HourlyForcastAdapter;
import com.example.tureguideversion1.AppConstants;
import com.example.tureguideversion1.ForApi.ApiInterFace;
import com.example.tureguideversion1.ForApi.ApiUtils;
import com.example.tureguideversion1.GpsUtils;
import com.example.tureguideversion1.Model.HourlyForcastList;
import com.example.tureguideversion1.R;
import com.example.tureguideversion1.Weather.WeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    String API = "618e3a096dcd96b86ffa64b35ef140e1";

    String CITY = "";

    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LottieAnimationView weatherAnim;
    private TextView addressTxt, updated_atTxt, statusTxt, tempTxt, feels_like, windTxt, pressureTxt, humidityTxt, sunRise, sunSet, dewPoint, cloudness, visibility, uvi;
    private RecyclerView hourlyRecycleView;
    private HourlyForcastAdapter hourlyForcastAdapter;
    private List<HourlyForcastList> hourlyForcastLists;
    private DrawerLayout wDrawerLayout;
    private ImageView weather_nav_icon;
    private RelativeLayout mainContainer;
    private SwipeRefreshLayout refreshLayout;
    private LottieAnimationView loadinWeather;

    private ApiInterFace api;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;
    private View view;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_weather, container, false);
        mainContainer = view.findViewById(R.id.mainContainer);
        loadinWeather = view.findViewById(R.id.loadinWeather);
        wDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
        weather_nav_icon = view.findViewById(R.id.weather_nav_icon);
        addressTxt = view.findViewById(R.id.address);
        updated_atTxt = view.findViewById(R.id.updated_at);
        statusTxt = view.findViewById(R.id.status);
        tempTxt = view.findViewById(R.id.temp);
        feels_like = view.findViewById(R.id.feels_like);
        windTxt = view.findViewById(R.id.wind);
        pressureTxt = view.findViewById(R.id.pressure);
        humidityTxt = view.findViewById(R.id.humidity);
        weatherAnim = view.findViewById(R.id.weatherAnim);
        sunRise = view.findViewById(R.id.sunrise);
        sunSet = view.findViewById(R.id.sunset);
        dewPoint = view.findViewById(R.id.dewPoint);
        cloudness = view.findViewById(R.id.cloudiness);
        visibility = view.findViewById(R.id.visibility);
        uvi = view.findViewById(R.id.uv);
        api = ApiUtils.getUserService();

        refreshLayout = view.findViewById(R.id.refreshLayout);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(view.getContext()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            findweather(wayLatitude,wayLongitude);
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            Toast.makeText(getContext(),stringBuilder.toString(),Toast.LENGTH_SHORT).show();
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        hourlyForcastLists = new ArrayList<>();
        hourlyRecycleView = view.findViewById(R.id.hourlyForcastRecycleView);
        hourlyForcastAdapter = new HourlyForcastAdapter(hourlyForcastLists, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        hourlyRecycleView.setLayoutManager(layoutManager);
        hourlyRecycleView.setAdapter(hourlyForcastAdapter);

        if (!isGPS) {
            Toasty.info(view.getContext(),"Please turn on GPS",Toasty.LENGTH_SHORT).show();
        }
        isContinue = false;
        getLocation();

        refresh();

        weather_nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        return view;
    }

    private void refresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocation();
            }
        });
    }

    private void findweather(final double lat, final double lon) {
        Call<WeatherResponse> call = api.getWeather(lat, lon, "metric", API);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Toasty.error(view.getContext(),"Can't get weather data!",Toasty.LENGTH_SHORT).show();
                    } else {
                        WeatherResponse weatherResponse = response.body();
                        String address = "";
                        Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());

                        List<Address> addresses;

                        try {
                            addresses = geocoder.getFromLocation(lat, lon, 1);
                            if (addresses.size() > 0) {
                                address = addresses.get(0).getLocality();
                                addressTxt.setText(address);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //  addressTxt.setText(weatherResponse.name+","+weatherResponse.sys.country);
                        float updatedAt = weatherResponse.currentWeather.dt;
                        // Date time  = new Date(updatedAt.longValue());
                        if (weatherResponse.currentWeather.weather.get(0).icon.contains("d")) {
                            if (weatherResponse.currentWeather.weather.get(0).description.matches("light thunderstorm") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("heavy thunderstorm") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("ragged thunderstorm")) {
                                weatherAnim.setAnimation("thunder.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with light rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with heavy rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with light drizzle") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with drizzle") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with heavy drizzle")) {
                                weatherAnim.setAnimation("stormshowersday.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Drizzle") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("light intensity shower rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("shower rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("heavy intensity shower rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("ragged shower rain")) {
                                weatherAnim.setAnimation("dizzle.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Snow") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("freezing rain")) {
                                weatherAnim.setAnimation("snow.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Clear")) {
                                weatherAnim.setAnimation("sunny.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("few clouds")) {
                                weatherAnim.setAnimation("partly_cloudy.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("scattered clouds") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("broken clouds") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("overcast clouds")) {
                                weatherAnim.setAnimation("windy.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("light rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("moderate rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("heavy intensity rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("very heavy rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("extreme rain")) {
                                weatherAnim.setAnimation("partly_shower.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Fog")) {
                                weatherAnim.setAnimation("foggyDay.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Mist") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Smoke") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Haze") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Dust") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Sand") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Ash") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Squall")) {
                                weatherAnim.setAnimation("mist.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Tornado")) {
                                weatherAnim.setAnimation("tornado.json");
                                weatherAnim.playAnimation();
                            }
                        } else if (weatherResponse.currentWeather.weather.get(0).icon.contains("n")) {
                            if (weatherResponse.currentWeather.weather.get(0).description.matches("light thunderstorm") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("heavy thunderstorm") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("ragged thunderstorm")) {
                                weatherAnim.setAnimation("thunder.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with light rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with heavy rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with light drizzle") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with drizzle") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("thunderstorm with heavy drizzle")) {
                                weatherAnim.setAnimation("storm.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Drizzle") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("light intensity shower rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("shower rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("heavy intensity shower rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("ragged shower rain")) {
                                weatherAnim.setAnimation("dizzle.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Snow") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("freezing rain")) {
                                weatherAnim.setAnimation("snow.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Clear")) {
                                weatherAnim.setAnimation("night.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("few clouds")) {
                                weatherAnim.setAnimation("cloudynight.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("scattered clouds") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("broken clouds") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("overcast clouds")) {
                                weatherAnim.setAnimation("windy.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).description.matches("light rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("moderate rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("heavy intensity rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("very heavy rain") ||
                                    weatherResponse.currentWeather.weather.get(0).description.matches("extreme rain")) {
                                weatherAnim.setAnimation("rainynight.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Fog")) {
                                weatherAnim.setAnimation("mist.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Mist") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Smoke") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Haze") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Dust") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Sand") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Ash") ||
                                    weatherResponse.currentWeather.weather.get(0).main.matches("Squall")) {
                                weatherAnim.setAnimation("mist.json");
                                weatherAnim.playAnimation();
                            } else if (weatherResponse.currentWeather.weather.get(0).main.matches("Tornado")) {
                                weatherAnim.setAnimation("tornado.json");
                                weatherAnim.playAnimation();
                            }
                        }
                        String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date((long) updatedAt * 1000));
                        updated_atTxt.setText(updatedAtText);
                        statusTxt.setText(weatherResponse.currentWeather.weather.get(0).description);
                        tempTxt.setText(Math.round(weatherResponse.currentWeather.temp) + "°C");
                        feels_like.setText("Feels Like: " + Math.round(weatherResponse.currentWeather.feels_like) + "°C");
                        windTxt.setText(Math.round(weatherResponse.currentWeather.wind_speed) + " Meter/Sec");
                        pressureTxt.setText(String.valueOf(weatherResponse.currentWeather.pressure));
                        humidityTxt.setText(Math.round(weatherResponse.currentWeather.humidity) + "%");
                        String riseTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(weatherResponse.currentWeather.sunrise*1000));
                        sunRise.setText(riseTime);
                        String setTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(weatherResponse.currentWeather.sunset*1000));
                        sunSet.setText(setTime);
                        dewPoint.setText(Math.round(weatherResponse.currentWeather.dew_point) + "°C");
                        cloudness.setText(weatherResponse.currentWeather.clouds+"%");
                        float v = (float) weatherResponse.currentWeather.visibility / 1000;
                        visibility.setText(v+" km");
                        if((weatherResponse.currentWeather.uvi >= 0) && (weatherResponse.currentWeather.uvi <= 2)){
                            uvi.setText("Low");
                        }else if((weatherResponse.currentWeather.uvi >= 3) && (weatherResponse.currentWeather.uvi <= 5)){
                            uvi.setText("Moderate");
                        }else if((weatherResponse.currentWeather.uvi >= 6) && (weatherResponse.currentWeather.uvi <= 7)){
                            uvi.setText("High");
                        }else if((weatherResponse.currentWeather.uvi >= 8) && (weatherResponse.currentWeather.uvi <= 10)){
                            uvi.setText("Very high");
                        }else if(weatherResponse.currentWeather.uvi >= 11){
                            uvi.setText("Extreme");
                        }
                        hourlyForcastLists.clear();
                        for (int i = 1; i <= 24; i++) {
                            HourlyForcastList forcastList = new HourlyForcastList(
                                    weatherResponse.hourlyWeather.get(i).hourlyWeatherForcasts.get(0).icon,
                                    weatherResponse.hourlyWeather.get(i).temp,
                                    weatherResponse.hourlyWeather.get(i).hourlyWeatherForcasts.get(0).description,
                                    weatherResponse.hourlyWeather.get(i).dt);
                            hourlyForcastLists.add(forcastList);
                            hourlyForcastAdapter.notifyDataSetChanged();
                        }
                        mainContainer.setVisibility(View.VISIBLE);
                        loadinWeather.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });
        refreshLayout.setRefreshing(false);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            WeatherFragment.this.findweather(wayLatitude, wayLongitude);
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isContinue) {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                } else {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                WeatherFragment.this.findweather(wayLatitude, wayLongitude);
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    });
                }
            } else {
                loadinWeather.setAnimation("confused.json");
                loadinWeather.playAnimation();
                Toasty.error(view.getContext(), "Location permission denied!", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
}
