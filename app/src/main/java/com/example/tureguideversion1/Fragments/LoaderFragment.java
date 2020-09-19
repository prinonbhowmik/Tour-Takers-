package com.example.tureguideversion1.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class LoaderFragment extends Fragment {
    public static final String TAG = "LoaderFragment";
    private LottieAnimationView loadinAmin;
    private FirebaseAuth auth;

    public LoaderFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loader, container, false);
        loadinAmin = view.findViewById(R.id.loadinAmin);
        auth = FirebaseAuth.getInstance();

        Bundle bundle = getArguments();
        loadinAmin.setAnimation("dot_circle_loading.json");
        loadinAmin.setRepeatCount(Animation.INFINITE);
        loadinAmin.playAnimation();
        if (getArguments() != null) {
            if (bundle.getString("shortcut").matches("event")) {
                loadinAmin.setRepeatCount(1);
                loadinAmin.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            FragmentTransaction event = getParentFragmentManager().beginTransaction();
                            event.replace(R.id.fragment_container, new EventFragment());
                            event.commit();
                        }catch (IllegalStateException e){
                            Log.d(TAG, "onFinish: "+e.getMessage());
                        }
                    }
                });

            } else if (bundle.getString("shortcut").matches("weather")) {
                loadinAmin.setRepeatCount(1);
                loadinAmin.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            FragmentTransaction event = getParentFragmentManager().beginTransaction();
                            event.replace(R.id.fragment_container, new WeatherFragment());
                            event.commit();
                        }catch (IllegalStateException e){
                            Log.d(TAG, "onFinish: "+e.getMessage());
                        }
                    }
                });
            }
        } else {
            getUserActivity();
        }


        return view;
    }

    private void getUserActivity(){
        DatabaseReference activityEventRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        activityEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loadinAmin.setRepeatCount(0);
                    loadinAmin.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            try {
                                FragmentTransaction event = getParentFragmentManager().beginTransaction();
                                event.replace(R.id.fragment_container, new OnGoingTour());
                                event.commit();
                            }catch (IllegalStateException e){
                                Log.d(TAG, "onFinish: "+e.getMessage());
                            }
                        }
                    });
                }else {
                    DatabaseReference activityTourRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
                    activityTourRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                loadinAmin.setRepeatCount(0);
                                loadinAmin.addAnimatorListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        try {
                                            FragmentTransaction event = getParentFragmentManager().beginTransaction();
                                            event.replace(R.id.fragment_container, new OnGoingTour());
                                            event.commit();
                                        }catch (IllegalStateException e){
                                            Log.d(TAG, "onFinish: "+e.getMessage());
                                        }
                                    }
                                });
                            }else {
                                loadinAmin.setRepeatCount(0);
                                loadinAmin.addAnimatorListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        try {
                                            FragmentTransaction event = getParentFragmentManager().beginTransaction();
                                            event.replace(R.id.fragment_container, new TourFragment());
                                            event.commit();
                                        }catch (IllegalStateException e){
                                            Log.d(TAG, "onFinish: "+e.getMessage());
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
