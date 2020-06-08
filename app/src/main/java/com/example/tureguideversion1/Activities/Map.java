package com.example.tureguideversion1.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.tureguideversion1.AppConstants;
import com.example.tureguideversion1.Fragments.WeatherFragment;
import com.example.tureguideversion1.GpsUtils;
import com.example.tureguideversion1.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import es.dmoral.toasty.Toasty;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private double latForMeetingPlace, lonForMeetingPlace;
    private GoogleMap map;
    private FloatingActionButton currentLocationBTN, locationPickBTN;
    private Geocoder geocoder;
    private String apiKey, selectedPlace;
    private StringBuilder stringBuilder;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean isContinue = false;
    private boolean isGPS = false;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    public Map() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        init();
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
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

                            //findweather(wayLatitude,wayLongitude);
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            Toast.makeText(getApplicationContext(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        if (!isGPS) {
            Toasty.info(getApplicationContext(), "Please turn on GPS", Toasty.LENGTH_SHORT).show();
        }
        isContinue = false;

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setCountries("BD");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //location_searchET.setText(place.getName());
                autocompleteFragment.setText(place.getName());
                selectedPlace = place.getName();
                List<Address> latlonaddress = null;
                try {
                    latlonaddress = geocoder.getFromLocationName(place.getName(), 5);
                    Address location = latlonaddress.get(0);
                    //map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), String.valueOf(status), Toast.LENGTH_SHORT).show();
            }
        });

        locationPickBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(latForMeetingPlace, lonForMeetingPlace, 5);
                    //Toast.makeText(getApplicationContext(),address.get(0).getSubLocality(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    if (!address.isEmpty()) {
                        intent.putExtra("location", address.get(0).getAddressLine(0))
                                .putExtra("latForMeetingPlace", latForMeetingPlace)
                                .putExtra("lonForMeetingPlace", lonForMeetingPlace)
                                .putExtra("subLocality", address.get(0).getSubLocality());
                        setResult(2, intent);
                    }
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        currentLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    private void init() {
        this.getSupportFragmentManager();
        geocoder = new Geocoder(getApplicationContext());
        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        apiKey = getString(R.string.place_api);
        currentLocationBTN = findViewById(R.id.currentLocationBTN);
        locationPickBTN = findViewById(R.id.locationPickBTN);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            List<Address> meetingPlace = null;
            try {
                //meetingPlace = geocoder.getFromLocationName(intent.getExtras().getString("meetingPlace"), 5);
                latForMeetingPlace = intent.getDoubleExtra("latForMeetingPlace", 0);
                lonForMeetingPlace = intent.getDoubleExtra("lonForMeetingPlace", 0);
                if (latForMeetingPlace != 0 && lonForMeetingPlace != 0) {
                    meetingPlace = geocoder.getFromLocation(intent.getDoubleExtra("latForMeetingPlace", 0),
                            intent.getDoubleExtra("lonForMeetingPlace", 0), 5);
                    map.addMarker(new MarkerOptions().position(new LatLng(meetingPlace.get(0).getLatitude(), meetingPlace.get(0).getLongitude())));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(meetingPlace.get(0).getLatitude(), meetingPlace.get(0).getLongitude()), 17));
                } else {
                    getLocation();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getLocation();
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(latLng);
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
                    markerOptions.title(address.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latForMeetingPlace = latLng.latitude;
                lonForMeetingPlace = latLng.longitude;
                // Clears the previously touched position
                map.clear();
                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                map.addMarker(markerOptions);
            }
        });

//        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
//            @Override
//            public void onPoiClick(PointOfInterest pointOfInterest) {
//                Toast.makeText(getApplicationContext(),pointOfInterest.placeId,Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            latForMeetingPlace = location.getLatitude();
                            lonForMeetingPlace = location.getLongitude();
                            List<Address> address = null;
                            try {
                                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            map.clear();
                            map.addMarker(new MarkerOptions().position(new LatLng(wayLatitude, wayLongitude)).title(address.get(0).getAddressLine(0)));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(wayLatitude, wayLongitude), 17));
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
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                map.addMarker(new MarkerOptions().position(new LatLng(wayLatitude, wayLongitude)));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(wayLatitude, wayLongitude), 17));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    });
                }
            } else {
                Toasty.error(getApplicationContext(), "Location permission denied!", Toasty.LENGTH_SHORT).show();
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

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
}
