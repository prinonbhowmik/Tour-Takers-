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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tureguideversion1.Adapters.InboxAdapter;
import com.example.tureguideversion1.Adapters.OnGoingTourAdapter;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.Model.Inbox;
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

public class OnGoingTour extends Fragment implements OnGoingTourAdapter.OnPosition, View.OnClickListener, OnGoingTourAdapter.OnEvent {
    public static final String TAG = "OnGoingTour";
    private OnGoingTourAdapter adapter;
    private InboxAdapter inboxAdapter;
    private ViewPager viewPager;
    private View view;
    private List<Event> eventList;
    private List<Inbox> inboxes;
    private RecyclerView inboxRecycler;
    private FirebaseAuth auth;
    private DrawerLayout tDrawerLayout;
    private ImageView nav_icon;
    private FloatingActionButton createTourBTN, fab1, fab2, inboxBTN;
    private IconSwitch iconSwitch;
    private Animation anim;
    private TextView tourTitle, emptyInboxTV;
    private LottieAnimationView emptyAmin, loadinAmin;
    private Bundle bundle;
    private Animation fab_open, inboxOpen, fab_close, inboxClose, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false, isInboxOpen = false;
    private ArrayList<String> eventIDs = new ArrayList<>();
    private ArrayList<Integer> eventIDsPosition = new ArrayList<>();
    private ArrayList<String> admins = new ArrayList<>();
    private int indexForEventID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ongoing_tour, container, false);
        auth = FirebaseAuth.getInstance();
        nav_icon = view.findViewById(R.id.nav_icon);
        tDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
        createTourBTN = view.findViewById(R.id.createTourBTN);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        inboxOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        inboxClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        inboxBTN = view.findViewById(R.id.inboxBTN);
        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);
        inboxBTN.setOnClickListener(this);
        createTourBTN.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        iconSwitch = view.findViewById(R.id.tourSwitch);
        tourTitle = view.findViewById(R.id.tourTitle);
        emptyInboxTV = view.findViewById(R.id.emptyInboxTV);
        emptyAmin = view.findViewById(R.id.emptyAmin);
        loadinAmin = view.findViewById(R.id.loadinAmin);
        loadinAmin.setAnimation("material-wave-loading.json");
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        eventList = new ArrayList<>();
        adapter = new OnGoingTourAdapter(eventList, getContext(), null, OnGoingTour.this, OnGoingTour.this);
        viewPager = view.findViewById(R.id.onGoingViewPager);
        viewPager.setAdapter(adapter);
        inboxes = new ArrayList<>();
        inboxAdapter = new InboxAdapter(inboxes, getContext());
        inboxRecycler = view.findViewById(R.id.inboxRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        inboxRecycler.setLayoutManager(layoutManager);
        inboxRecycler.setAdapter(inboxAdapter);
        bundle = getArguments();

        firstLoads();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isInboxOpen) {
                    loadinAmin.cancelAnimation();
                    loadinAmin.setVisibility(View.GONE);
                    emptyInboxTV.setVisibility(View.GONE);
                    inboxBTN.setImageResource(R.drawable.ic_mail);
                    inboxRecycler.startAnimation(inboxClose);
                    inboxClose.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            inboxes.clear();
                            inboxAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    isInboxOpen = false;
                } else {
                    inboxes.clear();
                    inboxAdapter.notifyDataSetChanged();
                }
                indexForEventID = eventIDsPosition.indexOf(position);

                if(admins.get(indexForEventID).matches(auth.getUid())){
                    inboxBTN.setVisibility(View.VISIBLE);
                    inboxRecycler.setVisibility(View.VISIBLE);
                }else {
                    inboxBTN.setVisibility(View.GONE);
                    inboxRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tDrawerLayout.openDrawer(GravityCompat.START);
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

    private void loadInbox() {
        loadinAmin.setVisibility(View.VISIBLE);
        loadinAmin.playAnimation();
        int p = indexForEventID;
        if (eventIDs.size() != 0) {
            inboxes.clear();
            ArrayList<String> memberList = new ArrayList<>();
            DatabaseReference adminChatRef = FirebaseDatabase.getInstance().getReference().child("chatWithAdmin").child(eventIDs.get(indexForEventID));
            adminChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        loadinAmin.cancelAnimation();
                        loadinAmin.setVisibility(View.GONE);
                        emptyInboxTV.setVisibility(View.GONE);
                        for (DataSnapshot chilSnap : snapshot.getChildren()) {
                            memberList.add(chilSnap.getKey());
                        }
                        for (int i = 0; i < memberList.size(); i++) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile").child(memberList.get(i));
                            int finalI = i;
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                                        String image = map.get("image").toString();
                                        String sex = map.get("sex").toString();
                                        if(p == indexForEventID) {
                                            Inbox inbox = new Inbox(eventIDs.get(indexForEventID), memberList.get(finalI), image, sex);
                                            inboxes.add(inbox);
                                            inboxAdapter.notifyDataSetChanged();
                                            inboxRecycler.startAnimation(inboxOpen);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }else {
                        loadinAmin.cancelAnimation();
                        loadinAmin.setVisibility(View.GONE);
                        emptyInboxTV.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void firstLoads() {
        if (bundle != null) {
            if (bundle.getString("forView").matches("tour")) {
                onGoingTourLoad(0);
            } else if (bundle.getString("forView").matches("event")) {
                onGoingEventLoad(0);
            }
        } else {
            emptyAmin.setAnimation("continuous-wave-loader.json");
            emptyAmin.playAnimation();
            DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
            activityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventList = new ArrayList<>();
                    adapter = new OnGoingTourAdapter(eventList, getContext(), "tour", OnGoingTour.this, OnGoingTour.this);
                    viewPager = (ViewPager) view.findViewById(R.id.onGoingViewPager);
                    viewPager.setAdapter(adapter);
                    if (snapshot.exists()) {
                        emptyAmin.cancelAnimation();
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
                                        viewPager.setOffscreenPageLimit(list.size());
                                        inboxBTN.setVisibility(View.GONE);
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
    }

    private void onGoingTourLoad(int position) {
        emptyAmin.setAnimation("continuous-wave-loader.json");
        emptyAmin.playAnimation();
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        activityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList = new ArrayList<>();
                adapter = new OnGoingTourAdapter(eventList, getContext(), "tour", OnGoingTour.this, OnGoingTour.this);
                viewPager = (ViewPager) view.findViewById(R.id.onGoingViewPager);
                viewPager.setAdapter(adapter);
                if (snapshot.exists()) {
                    emptyAmin.cancelAnimation();
                    emptyAmin.setVisibility(View.GONE);
                    ArrayList<String> list = new ArrayList<>();
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        list.add((String) map.get("tourID"));
                    }
                    for (int i = 0; i < list.size(); i++) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("tour").child(list.get(i));
                        int finalI = i;
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Event event = snapshot.getValue(Event.class);
                                    eventList.add(event);
                                    adapter.notifyDataSetChanged();
                                    inboxBTN.setVisibility(View.GONE);
                                    if (bundle != null) {
                                        if ((event.getStartDate().matches(bundle.getString("startDate"))) && (event.getReturnDate().matches(bundle.getString("returnDate")))) {
                                            viewPager.setOffscreenPageLimit(list.size());
                                            viewPager.setCurrentItem(finalI, true);
                                            bundle = null;
                                        }
                                    } else {
                                        viewPager.setOffscreenPageLimit(list.size());
                                        viewPager.setCurrentItem(position, true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    emptyAmin.setVisibility(View.VISIBLE);
                    emptyAmin.setAnimation("sleepy_cat.json");
                    emptyAmin.playAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGoingEventLoad(int position) {
        emptyAmin.setAnimation("continuous-wave-loader.json");
        emptyAmin.playAnimation();
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
        activityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList = new ArrayList<>();
                adapter = new OnGoingTourAdapter(eventList, getContext(), "event", OnGoingTour.this, OnGoingTour.this);
                viewPager = view.findViewById(R.id.onGoingViewPager);
                viewPager.setAdapter(adapter);
                if (snapshot.exists()) {
                    emptyAmin.cancelAnimation();
                    emptyAmin.setVisibility(View.GONE);
                    ArrayList<String> list = new ArrayList<>();
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        list.add((String) map.get("eventID"));
                    }
                    for (int i = 0; i < list.size(); i++) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(list.get(i));
                        int finalI = i;
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Event event = snapshot.getValue(Event.class);
                                    eventList.add(event);
                                    inboxBTN.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                    if (bundle != null) {
                                        if ((event.getStartDate().matches(bundle.getString("startDate"))) && (event.getReturnDate().matches(bundle.getString("returnDate")))) {
                                            viewPager.setOffscreenPageLimit(list.size());
                                            viewPager.setCurrentItem(finalI, true);
                                            bundle = null;
                                        }
                                    } else {
                                        viewPager.setOffscreenPageLimit(list.size());
                                        viewPager.setCurrentItem(position, true);
                                    }
                                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event").child(list.get(0));
                                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                HashMap<String,Object> map = (HashMap<String, Object>) snapshot.getValue();
                                                if(map.get("eventPublisherId").toString().matches(auth.getUid())){
                                                    inboxBTN.setVisibility(View.VISIBLE);
                                                    inboxRecycler.setVisibility(View.VISIBLE);
                                                }else {
                                                    inboxBTN.setVisibility(View.GONE);
                                                    inboxRecycler.setVisibility(View.GONE);
                                                }
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
                } else {
                    emptyAmin.setVisibility(View.VISIBLE);
                    emptyAmin.setAnimation("sleepy_cat.json");
                    emptyAmin.playAnimation();
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

    @Override
    public void onClick(View view) {
        TourFragment tourFragment = new TourFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction event = getParentFragmentManager().beginTransaction();
        int id = view.getId();
        switch (id) {
            case R.id.inboxBTN:
                animateInbox();
                break;
            case R.id.createTourBTN:
                animateFAB();
                break;
            case R.id.fab1:
                bundle.putString("for", "event");
                tourFragment.setArguments(bundle);
                event.replace(R.id.fragment_container, tourFragment);
                event.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                event.commit();
                break;
            case R.id.fab2:
                bundle.putString("for", "tour");
                tourFragment.setArguments(bundle);
                event.replace(R.id.fragment_container, tourFragment);
                event.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                event.commit();
                break;
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            createTourBTN.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            createTourBTN.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            if (isInboxOpen) {
                inboxBTN.setImageResource(R.drawable.ic_mail);
                inboxRecycler.startAnimation(inboxClose);
                inboxClose.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        inboxes.clear();
                        inboxAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                isInboxOpen = false;
            }
        }
    }

    public void animateInbox() {
        if (isInboxOpen) {
            loadinAmin.setVisibility(View.GONE);
            emptyInboxTV.setVisibility(View.GONE);
            inboxBTN.setImageResource(R.drawable.ic_mail);
            inboxRecycler.startAnimation(inboxClose);
            inboxClose.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    inboxes.clear();
                    inboxAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            isInboxOpen = false;
        } else {
            if(isFabOpen){
                createTourBTN.startAnimation(rotate_backward);
                fab1.startAnimation(fab_close);
                fab2.startAnimation(fab_close);
                fab1.setClickable(false);
                fab2.setClickable(false);
                isFabOpen = false;
            }
            inboxBTN.setImageResource(R.drawable.ic_drafts);
            loadInbox();
            isInboxOpen = true;
        }
    }


    @Override
    public void getEventID(int position, String ID, String admin) {
        eventIDsPosition.add(position);
        eventIDs.add(ID);
        admins.add(admin);
    }
}
