package com.example.tureguideversion1.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.tureguideversion1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private double latitude, longtitude;
    private GoogleMap map;
    private ImageView map_nav;
    private DrawerLayout mdrawrelayout;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        getActivity().getSupportFragmentManager();

        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        map_nav = view.findViewById(R.id.map_nav);
        mdrawrelayout = getActivity().findViewById(R.id.drawer_layout);

        map_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdrawrelayout.openDrawer(GravityCompat.START);
                hideKeyboardFrom(view.getContext());

            }
        });

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
            FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
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
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            return false;
        }

        return true;
    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
}
