package com.example.tureguideversion1.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.EventAdapter;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private EditText eventSearch;
    private DatabaseReference databaseReference;
    private RecyclerView eventRecyclerview;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private String pid, userName, userPhone, userImage;
    private FirebaseAuth auth;
    private DrawerLayout mdrawrelayout;
    private ImageView event_nav_icon;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_event, container, false);
        mdrawrelayout = getActivity().findViewById(R.id.drawer_layout);
        init(view);


        event_nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdrawrelayout.openDrawer(GravityCompat.START);
            }
        });

        eventSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    eventSearch.setCursorVisible(true);
            }
        });

        getData(view);

        //eventAdapter.notifyDataSetChanged();
        return view;
    }

    private void getData(View view) {
        DatabaseReference eventInfoRef = databaseReference.child("event");
        eventInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Event event = data.getValue(Event.class);
                        eventList.add(event);

                        //eventAdapter.notifyDataSetChanged();
                    }
                    Collections.reverse(eventList);
                    eventAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }


    private void init(View view) {
        eventSearch=view.findViewById(R.id.event_searchET);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventList = new ArrayList<>();
        eventRecyclerview = view.findViewById(R.id.event_recyclerview);
        eventRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        eventAdapter = new EventAdapter(eventList, getContext());
        eventRecyclerview.setAdapter(eventAdapter);
        auth = FirebaseAuth.getInstance();
        event_nav_icon = view.findViewById(R.id.event_nav_icon);
        //mdrawrelayout=view.findViewById(R.id.nav_view);

    }
}
