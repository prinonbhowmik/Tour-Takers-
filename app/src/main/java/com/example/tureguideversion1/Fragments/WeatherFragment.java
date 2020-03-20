package com.example.tureguideversion1.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.androdocs.httprequest.HttpRequest;
import com.example.tureguideversion1.R;
import com.example.tureguideversion1.Weather.Common;
import com.example.tureguideversion1.Weather.Helper;
import com.example.tureguideversion1.Weather.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    String CITY = "dhaka,bd";
    String API = "618e3a096dcd96b86ffa64b35ef140e1";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;


    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION ;

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


        class weatherTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                /* Showing the ProgressBar, Making the main design GONE */
                view.findViewById(R.id.loader).setVisibility(View.VISIBLE);
                view.findViewById(R.id.mainContainer).setVisibility(View.GONE);
                view.findViewById(R.id.errorText).setVisibility(View.GONE);
            }

            protected String doInBackground(String... args) {
                String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
                return response;
            }

            @Override
            protected void onPostExecute(String result) {


                try {
                    JSONObject jsonObj = new JSONObject(result);
                    JSONObject main = jsonObj.getJSONObject("main");
                    JSONObject sys = jsonObj.getJSONObject("sys");
                    JSONObject wind = jsonObj.getJSONObject("wind");
                    JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                    Long updatedAt = jsonObj.getLong("dt");
                    String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                    String temp = main.getString("temp") + "°C";
                    String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                    String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                    String pressure = main.getString("pressure");
                    String humidity = main.getString("humidity");

                    Long sunrise = sys.getLong("sunrise");
                    Long sunset = sys.getLong("sunset");
                    String windSpeed = wind.getString("speed");
                    String weatherDescription = weather.getString("description");

                    String address = jsonObj.getString("name") + ", " + sys.getString("country");


                    /* Populating extracted data into our views */
                    addressTxt.setText(address);
                    updated_atTxt.setText(updatedAtText);
                    statusTxt.setText(weatherDescription.toUpperCase());
                    tempTxt.setText(temp);
                    temp_minTxt.setText(tempMin);
                    temp_maxTxt.setText(tempMax);
                    sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                    sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                    windTxt.setText(windSpeed);
                    pressureTxt.setText(pressure);
                    humidityTxt.setText(humidity);

                    /* Views populated, Hiding the loader, Showing the main design */
                    view.findViewById(R.id.loader).setVisibility(View.GONE);
                    view.findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    view.findViewById(R.id.loader).setVisibility(View.GONE);
                    view.findViewById(R.id.errorText).setVisibility(View.VISIBLE);
                }

            }
        }
        new weatherTask().execute();

        return view;
    }


}
