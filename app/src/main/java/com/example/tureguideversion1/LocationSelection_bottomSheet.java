package com.example.tureguideversion1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        Bundle mArgs = getArguments();
        String location = mArgs.getString("location").toLowerCase();
        selectedLocation = mArgs.getStringArrayList("selectedLocation");
        if (selectedLocation != null) {
            String count = Integer.toString(selectedLocation.size());
            if (selectedLocation.size() == 1) {
                locationStatus.setText(count + " tourism place is selected");
            } else if (selectedLocation.size() > 1) {

                locationStatus.setText(count + " tourism places is selected");
            } else {
                locationStatus.setText("Select tourism places that you want to visit");
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

                if (selectedLocation.contains(singleUser.get("locationName").toString())) {
                    LocationSelectionItem location = new LocationSelectionItem("s" + (String) singleUser.get("locationName"));
                    locationList.add(location);
                    adapter.notifyDataSetChanged();
                } else {
                    LocationSelectionItem location = new LocationSelectionItem((String) singleUser.get("locationName"));
                    locationList.add(location);
                    adapter.notifyDataSetChanged();
                }


            }


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

        String count = Integer.toString(selectedLocation.size());
        if (selectedLocation.size() == 1) {
            locationStatus.setText(count + " tourism place is selected");
        } else if (selectedLocation.size() > 1) {

            locationStatus.setText(count + " tourism places is selected");
        } else {
            locationStatus.setText("Select tourism places that you want to visit");
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
