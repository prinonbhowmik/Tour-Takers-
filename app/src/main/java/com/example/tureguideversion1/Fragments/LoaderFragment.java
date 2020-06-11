package com.example.tureguideversion1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tureguideversion1.Activities.EventDetails;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.R;

public class LoaderFragment extends Fragment {
    private LottieAnimationView loadinAmin;

    public LoaderFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loader, container, false);
        loadinAmin = view.findViewById(R.id.loadinAmin);
        Bundle bundle = getArguments();
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                //startActivity(new Intent(getContext(), MainActivity.class).putExtra("FromLoader","tour"));
                if(getArguments() != null) {
                    if (bundle.getString("shortcut").matches("event")) {
                        FragmentTransaction event = getParentFragmentManager().beginTransaction();
                        event.replace(R.id.fragment_container, new EventFragment());
                        event.commit();
                    }else if (bundle.getString("shortcut").matches("weather")) {
                        FragmentTransaction event = getParentFragmentManager().beginTransaction();
                        event.replace(R.id.fragment_container, new WeatherFragment());
                        event.commit();
                    }
                }else {
                    FragmentTransaction event = getParentFragmentManager().beginTransaction();
                    event.replace(R.id.fragment_container, new TourFragment());
                    event.commit();
                }
            }
        }.start();

        return view;
    }
}
