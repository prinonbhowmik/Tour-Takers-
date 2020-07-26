package com.example.tureguideversion1.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tureguideversion1.Activities.EventDetails;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.opencensus.stats.Aggregation;

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
        loadinAmin.playAnimation();
        loadinAmin.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    if (getArguments() != null) {
                        if (bundle.getString("shortcut").matches("event")) {
                            FragmentTransaction event = getParentFragmentManager().beginTransaction();
                            event.replace(R.id.fragment_container, new EventFragment());
                            event.commit();
                        } else if (bundle.getString("shortcut").matches("weather")) {
                            FragmentTransaction event = getParentFragmentManager().beginTransaction();
                            event.replace(R.id.fragment_container, new WeatherFragment());
                            event.commit();
                        }
                    } else {
                        getUserActivity();
                    }
                }catch (IllegalStateException e){
                    Log.d(TAG, "onFinish: "+e.getMessage());
                }
            }
        });

        return view;
    }

    private void getUserActivity(){
        loadinAmin.setRepeatCount(Animation.INFINITE);
        DatabaseReference activityEventRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        activityEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loadinAmin.clearAnimation();
                    FragmentTransaction event = getParentFragmentManager().beginTransaction();
                    event.replace(R.id.fragment_container, new OnGoingTour());
                    event.commit();
                }else {
                    DatabaseReference activityTourRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
                    activityTourRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                loadinAmin.clearAnimation();
                                FragmentTransaction event = getParentFragmentManager().beginTransaction();
                                event.replace(R.id.fragment_container, new OnGoingTour());
                                event.commit();
                            }else {
                                loadinAmin.clearAnimation();
                                FragmentTransaction event = getParentFragmentManager().beginTransaction();
                                event.replace(R.id.fragment_container, new TourFragment());
                                event.commit();
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
