package com.example.tureguideversion1.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private double latitude, longtitude;
    private GoogleMap map;
    private DrawerLayout mdrawrelayout;
    private FloatingActionButton floatingActionButton;
    private Geocoder geocoder;
    private String apiKey;



    public Map() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        init();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //location_searchET.setText(place.getName());
                autocompleteFragment.setText(place.getName());
                Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(),String.valueOf(status),Toast.LENGTH_SHORT).show();
            }
        });
//        searchIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                geocoder = new Geocoder(getApplicationContext());
//                List<Address> latlonaddress = null;
//                try {
//                    latlonaddress = geocoder.getFromLocationName(location_searchET.getText().toString(),5);
//                    Address location = latlonaddress.get(0);
//                    map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("location", "Dhaka");
                setResult(2,intent);
                finish();
            }
        });
    }

    private void init() {
        this.getSupportFragmentManager();

        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        apiKey = getString(R.string.place_api);
        mdrawrelayout = findViewById(R.id.drawer_layout);
        floatingActionButton = findViewById(R.id.floatingActionButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;


        currentLocation();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void currentLocation() {
        if (checkLocationPermission()) {
            FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Location curentlocation = (Location) task.getResult();
                    latitude = curentlocation.getLatitude();
                    longtitude = curentlocation.getLongitude();
                    map.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curentlocation.getLatitude(), curentlocation.getLongitude()), 15));
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkLocationPermission() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            return false;
        }

        return true;
    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
}
