package com.example.tureguideversion1;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.LocationSelection_bottomSheet_adapter;
import com.example.tureguideversion1.Model.LocationSelectionItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationSelection_bottomSheet extends BottomSheetDialogFragment implements LocationSelection_bottomSheet_adapter.InfoAdapterInterface {

    private List<LocationSelectionItem> locationList;
    private List<String> selectedLocation;
    private LocationSelection_bottomSheet_adapter adapter;
    private RecyclerView locationRecyleView;
    private TextView locationStatus;
    private BottomSheetListener mListener;
    Animation anim;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_selection_bottom_sheet, container, false);
        locationRecyleView = v.findViewById(R.id.locationSelection_recyclerview);
        locationRecyleView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        locationRecyleView.setHasFixedSize(true);
        locationStatus = v.findViewById(R.id.selectionStatus);
        locationList = new ArrayList<>();
        selectedLocation = new ArrayList<>();
        adapter = new LocationSelection_bottomSheet_adapter(locationList, getContext(), this);
        locationRecyleView.setAdapter(adapter);
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        Bundle mArgs = getArguments();
        String location = mArgs.getString("location").toLowerCase();
        selectedLocation = mArgs.getStringArrayList("selectedLocation");
        if (selectedLocation != null) {
            final String count = Integer.toString(selectedLocation.size());
            if (selectedLocation.size() == 1) {
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) { }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        locationStatus.setText(count + " tourism place is selected");
                    }
                });
                locationStatus.startAnimation(anim);
            } else if (selectedLocation.size() > 1) {
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) { }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        locationStatus.setText(count + " tourism places is selected");
                    }
                });
                locationStatus.startAnimation(anim);
            } else {
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) { }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        locationStatus.setText("Select tourism places that you want to visit");
                    }
                });
                locationStatus.startAnimation(anim);
            }
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(location);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectLocationNInfo((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { }
                            @Override
                            public void onAnimationEnd(Animation animation) { }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                locationStatus.setText("Please enter district name correctly");
                            }
                        });
                        locationStatus.setAnimation(anim);
                    }


                });


        return v;
    }

    private void collectLocationNInfo(Map<String, Object> locations) {

        if (locations != null) {
            locationList.clear();
            for (Map.Entry<String, Object> entry : locations.entrySet()) {

                //Get locations map
                Map singleUser = (Map) entry.getValue();
                //Get location field and append to list
                if(singleUser.get("locationName") != null) {
                    if (selectedLocation.contains(singleUser.get("locationName").toString())) {
                        LocationSelectionItem location = new LocationSelectionItem("s" + singleUser.get("locationName"));
                        locationList.add(location);
                        adapter.notifyDataSetChanged();
                    } else {
                        LocationSelectionItem location = new LocationSelectionItem((String) singleUser.get("locationName"));
                        locationList.add(location);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) { }
                        @Override
                        public void onAnimationEnd(Animation animation) { }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            locationStatus.setText("Please enter district name!");
                            locationStatus.setTextColor(Color.RED);
                        }
                    });
                    locationStatus.setAnimation(anim);
                }
            }

        }else {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) { }
                @Override
                public void onAnimationRepeat(Animation animation) {
                    locationStatus.setText("Please enter district name correctly!");
                    locationStatus.setTextColor(Color.RED);
                }
            });
            locationStatus.setAnimation(anim);
        }
    }

    public interface BottomSheetListener {
        void selectedLocation(String location);
    }

    @Override
    public void OnItemClicked(String location) {
        mListener.selectedLocation(location);
        if (location.substring(0, 2).matches("un")) {
            String s = location.substring(2);
            selectedLocation.remove(s);

        } else {
            if (!selectedLocation.contains(location)) {
                selectedLocation.add(location);
            }
        }

        final String count = Integer.toString(selectedLocation.size());
        if (selectedLocation.size() == 1) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) { }
                @Override
                public void onAnimationRepeat(Animation animation) {
                    locationStatus.setText(count + " tourism place is selected");
                }
            });
            locationStatus.startAnimation(anim);
        } else if (selectedLocation.size() > 1) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) { }
                @Override
                public void onAnimationRepeat(Animation animation) {
                    locationStatus.setText(count + " tourism places is selected");
                }
            });
            locationStatus.startAnimation(anim);
        } else {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) { }
                @Override
                public void onAnimationRepeat(Animation animation) {
                    locationStatus.setText("Select tourism places that you want to visit");
                }
            });
            locationStatus.startAnimation(anim);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
