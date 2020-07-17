package com.example.tureguideversion1.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.tureguideversion1.Adapters.OnGoingTourAdapter;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnGoingTour extends Fragment {
    public static final String TAG = "OnGoingTour";
    OnGoingTourAdapter adapter;
    ViewPager viewPager;
    View view;
    List<Event> eventList;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ongoing_tour,container,false);
        auth = FirebaseAuth.getInstance();
        eventList = new ArrayList<>();
        adapter = new OnGoingTourAdapter(eventList,getContext());
        viewPager = (ViewPager)view.findViewById(R.id.onGoingViewPager);
        viewPager.setAdapter(adapter);
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event");
        Query query = eventRef.orderByChild("eventPublisherId").equalTo(auth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapShot:snapshot.getChildren()) {
                        Event event = childSnapShot.getValue(Event.class);
                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}
