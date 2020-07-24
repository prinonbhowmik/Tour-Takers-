package com.example.tureguideversion1.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tureguideversion1.Adapters.OnGoingTourAdapter;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnGoingTour extends Fragment implements OnGoingTourAdapter.OnPosition {
    public static final String TAG = "OnGoingTour";
    private OnGoingTourAdapter adapter;
    private ViewPager viewPager;
    private View view;
    private List<Event> eventList;
    private FirebaseAuth auth;
    private DrawerLayout tDrawerLayout;
    private ImageView nav_icon;
    private FloatingActionButton createTourBTN;
    private IconSwitch iconSwitch;
    private Animation anim;
    private TextView tourTitle;
    private LottieAnimationView emptyAmin;
    private List<String> tourListfromActivity;
    private List<String> eventListfromActivity;
    private int pagePosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ongoing_tour, container, false);
        auth = FirebaseAuth.getInstance();
        nav_icon = view.findViewById(R.id.nav_icon);
        tDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
        createTourBTN = view.findViewById(R.id.createTourBTN);
        iconSwitch = view.findViewById(R.id.tourSwitch);
        tourTitle = view.findViewById(R.id.tourTitle);
        emptyAmin = view.findViewById(R.id.emptyAmin);
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        eventList = new ArrayList<>();
        adapter = new OnGoingTourAdapter(eventList, getContext(), null, OnGoingTour.this);
        viewPager = (ViewPager) view.findViewById(R.id.onGoingViewPager);
        viewPager.setAdapter(adapter);

        firstLoads();

        nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        createTourBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction event = getParentFragmentManager().beginTransaction();
                event.replace(R.id.fragment_container, new TourFragment());
                event.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                event.commit();
            }
        });

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (iconSwitch.getChecked()) {
                    case LEFT:
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                tourTitle.setText("Tour");
                            }
                        });
                        tourTitle.startAnimation(anim);
                        onGoingTourLoad(0);
                        break;
                    case RIGHT:
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                tourTitle.setText("Event");
                            }
                        });
                        tourTitle.startAnimation(anim);
                        onGoingEventLoad(0);
                        break;
                }
            }
        });

        return view;
    }

    private void firstLoads() {
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList = new ArrayList<>();
                adapter = new OnGoingTourAdapter(eventList, getContext(), "tour", OnGoingTour.this);
                viewPager = (ViewPager) view.findViewById(R.id.onGoingViewPager);
                viewPager.setAdapter(adapter);
                if (snapshot.exists()) {
                    emptyAmin.setVisibility(View.GONE);
                    ArrayList<String> list = new ArrayList<>();
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        if (!list.contains(map.get("tourID"))) {
                            list.add((String) map.get("tourID"));
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("tour").child(list.get(i));
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Event event = snapshot.getValue(Event.class);
                                    eventList.add(event);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGoingTourLoad(int position) {
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        activityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList = new ArrayList<>();
                adapter = new OnGoingTourAdapter(eventList, getContext(), "tour", OnGoingTour.this);
                viewPager = (ViewPager) view.findViewById(R.id.onGoingViewPager);
                viewPager.setAdapter(adapter);
                if (snapshot.exists()) {
                    emptyAmin.setVisibility(View.GONE);
                    ArrayList<String> list = new ArrayList<>();
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        list.add((String) map.get("tourID"));
                    }
                    for (int i = 0; i < list.size(); i++) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("tour").child(list.get(i));
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Event event = snapshot.getValue(Event.class);
                                    eventList.add(event);
                                    adapter.notifyDataSetChanged();
                                    viewPager.setCurrentItem(position);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    emptyAmin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGoingEventLoad(int position) {
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList = new ArrayList<>();
                adapter = new OnGoingTourAdapter(eventList, getContext(), "event", OnGoingTour.this);
                viewPager = view.findViewById(R.id.onGoingViewPager);
                viewPager.setAdapter(adapter);
                if (snapshot.exists()) {
                    emptyAmin.setVisibility(View.GONE);
                    ArrayList<String> list = new ArrayList<>();
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        list.add((String) map.get("eventID"));
                    }
                    for (int i = 0; i < list.size(); i++) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(list.get(i));
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Event event = snapshot.getValue(Event.class);
                                    eventList.add(event);
                                    adapter.notifyDataSetChanged();
                                    viewPager.setCurrentItem(position);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    emptyAmin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void position(int position, String forView) {
        if (forView.matches("tour")) {
            onGoingTourLoad(position);
        } else if (forView.matches("event")) {
            onGoingEventLoad(position);
        }
    }
}
