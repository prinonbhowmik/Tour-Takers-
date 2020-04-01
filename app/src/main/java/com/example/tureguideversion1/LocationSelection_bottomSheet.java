package com.example.tureguideversion1;

import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.LocationSelection_bottomSheet_adapter;
import com.example.tureguideversion1.Model.LocationItem;
import com.example.tureguideversion1.Model.LocationSelectionItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationSelection_bottomSheet extends BottomSheetDialogFragment implements LocationSelection_bottomSheet_adapter.InfoAdapterInterface {

    private List<LocationSelectionItem> locationList;
    private List<String> selectedLocation;
    private LocationSelection_bottomSheet_adapter adapter;
    private RecyclerView locationRecyleView;
    private TextView locationStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_selection_bottom_sheet, container, false);
        View designLayout = inflater.inflate(R.layout.location_selection_layout, container, false);
        locationRecyleView = v.findViewById(R.id.locationSelection_recyclerview);
        locationRecyleView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        locationStatus = v.findViewById(R.id.selectionStatus);
        selectedLocation = new ArrayList<>();

        Bundle mArgs = getArguments();
        String location = mArgs.getString("location").toLowerCase();
        //Toast.makeText(getContext(),location,Toast.LENGTH_SHORT).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(location);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        //loading.setVisibility(View.VISIBLE);
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

        locationList = new ArrayList<>();
        if (locations != null) {
            for (Map.Entry<String, Object> entry : locations.entrySet()) {

                //Get user map
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list
                LocationSelectionItem location = new LocationSelectionItem((String) singleUser.get("locationName"));
                locationList.add(location);
                //adapter.notifyDataSetChanged();
            }
            adapter = new LocationSelection_bottomSheet_adapter(locationList, getContext(), this);
            locationRecyleView.setAdapter(adapter);

        }
    }


    @Override
    public void OnItemClicked(String location) {
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
            locationStatus.setText(count + " place is selected");
        } else if (selectedLocation.size() > 1) {

            locationStatus.setText(count + " places is selected");
        }else {
            locationStatus.setText("Select places that you want to visit");
        }
    }
}
