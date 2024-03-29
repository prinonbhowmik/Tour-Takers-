package com.example.tureguideversion1.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.ekalips.fancybuttonproj.FancyButton;
import com.example.tureguideversion1.AppConstants;
import com.example.tureguideversion1.ClusterManagerRenderer;
import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.Fragments.WeatherFragment;
import com.example.tureguideversion1.GpsUtils;
import com.example.tureguideversion1.Model.CardView;
import com.example.tureguideversion1.Model.ClusterMarker;
import com.example.tureguideversion1.PolylineData;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import es.dmoral.toasty.Toasty;

public class Map extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "Map";
    private double latForMeetingPlace, lonForMeetingPlace;
    private GoogleMap map;
    private FloatingActionButton currentLocationBTN;
    private ExtendedFloatingActionButton locationPickBTN;
    private Geocoder geocoder;
    private String apiKey, selectedPlace, guidePlace;
    private StringBuilder stringBuilder;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean isContinue = false;
    private boolean isGPS = false;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GeoApiContext geoApiContext;
    private List<PolylineData> polylineData;
    private Intent intent;
    double duration = 999999999;
    private AutocompleteSupportFragment autocompleteFragment;
    private View event_searchCV;
    private ClusterManager<ClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();

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
            Toasty.info(getApplicationContext(), "Please turn on your GPS", Toasty.LENGTH_SHORT).show();
        }
        isContinue = false;

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
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
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

                    if (!address.isEmpty()) {
                        if((intent.getStringExtra("from").matches("tour")) && (intent.getStringExtra("for").matches("guidePlace"))){
                            Intent intent2 = new Intent();
                            intent2.putExtra("location", address.get(0).getAddressLine(0))
                                    .putExtra("latForMeetingPlace", latForMeetingPlace)
                                    .putExtra("lonForMeetingPlace", lonForMeetingPlace)
                                    .putExtra("for", "guidePlace");
                            setResult(2, intent2);
                            finish();
                        }else if((intent.getStringExtra("from").matches("tour")) && (intent.getStringExtra("for").matches("meetingPlace"))){
                            Intent intent2 = new Intent();
                            intent2.putExtra("location", address.get(0).getAddressLine(0))
                                    .putExtra("latForMeetingPlace", latForMeetingPlace)
                                    .putExtra("lonForMeetingPlace", lonForMeetingPlace)
                                    .putExtra("for", "meetingPlace")
                                    .putExtra("subLocality", address.get(0).getSubLocality());
                            setResult(2, intent2);
                            finish();
                        }
                    }else {
                        Toasty.info(getApplicationContext(),"Pick a location from map.",Toasty.LENGTH_SHORT).show();
                    }
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
        apiKey = getString(R.string.place_api);
        geocoder = new Geocoder(getApplicationContext());
        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();
        }
        event_searchCV = findViewById(R.id.event_searchCV);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragment = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setCountries("BD");
        currentLocationBTN = findViewById(R.id.currentLocationBTN);
        locationPickBTN = findViewById(R.id.locationPickBTN);
        locationPickBTN.shrink();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
        polylineData = new ArrayList<>();
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnPolylineClickListener(this);
        map.setOnInfoWindowClickListener(this);
        intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getStringExtra("from").matches("tour")) {
                if (intent.getStringExtra("for").matches("meetingPlace")) {
                    List<Address> meetingPlace = null;
                    try {
                        latForMeetingPlace = intent.getDoubleExtra("latForMeetingPlace", 0);
                        lonForMeetingPlace = intent.getDoubleExtra("lonForMeetingPlace", 0);
                        if (latForMeetingPlace != 0 && lonForMeetingPlace != 0) {
                            meetingPlace = geocoder.getFromLocation(intent.getDoubleExtra("latForMeetingPlace", 0),
                                    intent.getDoubleExtra("lonForMeetingPlace", 0), 5);
                            map.addMarker(new MarkerOptions().position(new LatLng(meetingPlace.get(0).getLatitude(), meetingPlace.get(0).getLongitude())));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(meetingPlace.get(0).getLatitude(), meetingPlace.get(0).getLongitude()), 18));
                            locationPickBTN.setText("Picked Location");
                            locationPickBTN.extend();
                        } else {
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
//                                                latForMeetingPlace = location.getLatitude();
//                                                lonForMeetingPlace = location.getLongitude();
                                                List<Address> address = null;
                                                try {
                                                    address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                map.clear();
                                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(wayLatitude, wayLongitude), 18), 600, null);
                                            } else {
                                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (intent.getStringExtra("for").matches("guidePlace")) {
                    currentLocationBTN.setVisibility(View.GONE);
                    event_searchCV.setVisibility(View.GONE);
                    List<Address> meetingPlace = null;
                    try {
                        latForMeetingPlace = intent.getDoubleExtra("latForMeetingPlace", 0);
                        lonForMeetingPlace = intent.getDoubleExtra("lonForMeetingPlace", 0);
                        guidePlace = intent.getStringExtra("guideLocation");
                        if (guidePlace.trim().length() != 0) {
                            if (latForMeetingPlace != 0 && lonForMeetingPlace != 0) {
                                meetingPlace = geocoder.getFromLocation(intent.getDoubleExtra("latForMeetingPlace", 0),
                                        intent.getDoubleExtra("lonForMeetingPlace", 0), 5);
                                map.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(meetingPlace.get(0).getLatitude(),
                                                        meetingPlace.get(0).getLongitude()),
                                                Float.parseFloat("13.5")));
                                addMapMarkers(guidePlace);
                                LatLng latLng = new LatLng(meetingPlace.get(0).getLatitude(),meetingPlace.get(0).getLongitude());
                                Marker marker = map.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(meetingPlace.get(0).getAddressLine(0))
                                );
                                marker.showInfoWindow();
                                locationPickBTN.setText("Picked Location");
                                locationPickBTN.extend();
                                markers.add(marker);
                            }else {
                                meetingPlace = geocoder.getFromLocationName(guidePlace, 5);
                                map.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(meetingPlace.get(0).getLatitude(),
                                                        meetingPlace.get(0).getLongitude()),
                                                Float.parseFloat("13.5")));
                                addMapMarkers(guidePlace);
                            }
                        } else {
                            getLocation();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (intent.getStringExtra("from").matches("eventDetails")) {
                latForMeetingPlace = intent.getDoubleExtra("latForMeetingPlace", 0);
                lonForMeetingPlace = intent.getDoubleExtra("lonForMeetingPlace", 0);
                getRoute();
                locationPickBTN.setVisibility(View.GONE);
                currentLocationBTN.setVisibility(View.GONE);
                event_searchCV.setVisibility(View.GONE);
            }
        } else {
            getLocation();
        }

        if (intent.getExtras() != null) {
            if (intent.getStringExtra("from").matches("tour")) {
                if (intent.getStringExtra("for").matches("guidePlace")) {
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
                            for(Marker marker: markers){
                                marker.remove();
                            }
                            // Animating to the touched position
                            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                            // Placing a marker on the touched position
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(address.get(0).getAddressLine(0))
                            );
                            marker.showInfoWindow();
                            locationPickBTN.shrink();
                            locationPickBTN.setText("Pick location");
                            locationPickBTN.extend();
                            //map.addMarker(markerOptions);
                            markers.add(marker);
                        }
                    });
                }else if (intent.getStringExtra("for").matches("meetingPlace")) {
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
                            //map.addMarker(markerOptions);
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(address.get(0).getAddressLine(0))
                            );
                            marker.showInfoWindow();
                            locationPickBTN.shrink();
                            locationPickBTN.setText("Pick location");
                            locationPickBTN.extend();
                        }
                    });
                }
            }
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (intent.getExtras() != null) {
                    if (intent.getStringExtra("from").matches("eventDetails")) {
                        if (marker.getTitle().matches("You")) {
                            return false;
                        }
                        LatLng latLng = marker.getPosition();
                        String title = marker.getTitle();
                        Marker mkr = map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(title));
                        mkr.showInfoWindow();
                        return true;
                    }
                }
                return false;
            }
        });
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
                            LatLng latLng = new LatLng(wayLatitude,wayLongitude);
                            List<Address> address = null;
                            try {
                                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            map.clear();
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(address.get(0).getAddressLine(0))
                            );
                            marker.showInfoWindow();
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(wayLatitude, wayLongitude), 18), 600, null);
                            locationPickBTN.shrink();
                            locationPickBTN.setText("Pick location");
                            locationPickBTN.extend();
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getRoute() {
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
                            List<Address> address = null;
                            try {
                                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            map.addMarker(new MarkerOptions().position(new LatLng(wayLatitude, wayLongitude)).title("You"));
                            //map.addMarker(new MarkerOptions().position(new LatLng(latForMeetingPlace, lonForMeetingPlace)).title("Meeting Place"));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(wayLatitude, wayLongitude), 13));
                            calculateDirections(wayLatitude, wayLongitude);
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
                if (intent.getStringExtra("from").matches("eventDetails")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getRoute();
                    }
                } else {
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
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(wayLatitude, wayLongitude), 18));
                                } else {
                                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                }
                            }
                        });
                    }
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

    private void calculateDirections(double lat, double lon) {
        Log.d(null, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                latForMeetingPlace,
                lonForMeetingPlace
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        lat,
                        lon
                )
        );
        Log.d(null, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(null, "onResult: routes: " + result.routes[0].toString());
                Log.d(null, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(null, "onFailure: " + e.getMessage());

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(null, "run: result routes: " + result.routes.length);

                if (polylineData.size() > 0) {
                    for (PolylineData polylineData : polylineData) {
                        polylineData.getPolyline().remove();
                    }
                    polylineData.clear();
                    polylineData = new ArrayList<>();
                }

                for (DirectionsRoute route : result.routes) {
                    Log.d(null, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.GRAY);
                    polyline.setClickable(true);
                    polylineData.add(new PolylineData(polyline, route.legs[0]));
                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }
                }
            }
        });
    }

    private void addMapMarkers(String place) {

        if (map != null) {

            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), map);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new ClusterManagerRenderer(
                        getApplicationContext(),
                        map,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            int avatar = R.drawable.guide_icon; // set the default avatar
            DatabaseReference onlineGuideRef = FirebaseDatabase.getInstance().getReference().child("guidesAreOnline").child(place);
            onlineGuideRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mClusterManager.clearItems();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> onlineMap = (HashMap<String, Object>) childSnapshot.getValue();
                            //Log.d(TAG, "onDataChange: " + onlineMap.get("ID"));
                            DatabaseReference guideRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child((String) onlineMap.get("ID"));
                            guideRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        HashMap<String, Object> userMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                        ClusterMarker newClusterMarker = new ClusterMarker(
                                                new LatLng((double) onlineMap.get("currentLocationLatitude"),
                                                        (double) onlineMap.get("currentLocationLongitude")),
                                                (String) userMap.get("name"),
                                                "",
                                                avatar
                                        );
                                        mClusterManager.addItem(newClusterMarker);
                                        mClusterMarkers.add(newClusterMarker);
                                        mClusterManager.cluster();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (map == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 180;
        LatLngBounds latLngBounds = boundsBuilder.build();

        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for (PolylineData polylineData : polylineData) {
            //Log.d(null, "onPolylineClick: toString: " + polylineData.toString());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);
                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Meeting Place")
                        .snippet("Duration: " + polylineData.getLeg().duration));

                marker.showInfoWindow();
                zoomRoute(polyline.getPoints());
            } else {
                polylineData.getPolyline().setColor(Color.GRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        if (marker.getTitle().contains("Meeting Place")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Want to go now?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try {
                                if (mapIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            } catch (NullPointerException e) {
                                Log.e(null, "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (marker.getTitle().equals("You")) {
                marker.hideInfoWindow();
            }
//            else{
//
//                final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setMessage(marker.getSnippet())
//                        .setCancelable(true)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                                resetSelectedMarker();
//                                mSelectedMarker = marker;
//                                calculateDirections(marker);
//                                dialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                                dialog.cancel();
//                            }
//                        });
//                final AlertDialog alert = builder.create();
//                alert.show();
//            }
        }

    }
}
