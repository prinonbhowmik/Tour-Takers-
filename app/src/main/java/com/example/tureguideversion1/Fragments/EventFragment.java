package com.example.tureguideversion1.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private EditText eventSearch;
    private TextView moreTv;
    private DatabaseReference databaseReference;
    private RecyclerView eventRecyclerview;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private String userId;
    private FirebaseAuth auth;
    private DrawerLayout mdrawrelayout;
    private ImageView event_nav_icon, searchAction;
    View view;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_event, container, false);
        mdrawrelayout = getActivity().findViewById(R.id.drawer_layout);
        //registerForContextMenu(moreTv);
        init(view);
        userId = auth.getCurrentUser().getUid();
        moreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

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

        searchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(eventSearch.getText().toString());
            }
        });
        //eventAdapter.notifyDataSetChanged();
        return view;
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), moreTv);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.event_menu);
        popup.show();

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

    private void search(final String value){
        if(!value.isEmpty()) {
            DatabaseReference eventInfoRef = databaseReference.child("event");
            eventInfoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        eventList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Map values = (Map) data.getValue();
                            String s = value.substring(0, 1).toUpperCase() + value.substring(1);
                            if (values.get("place").toString().matches(s) || values.get("groupName").toString().matches(value)) {
                                Event event = data.getValue(Event.class);
                                eventList.add(event);
                                //eventAdapter.notifyDataSetChanged();
                            }
                        }
                        Collections.reverse(eventList);
                        eventAdapter.notifyDataSetChanged();
                        if(eventList.size() == 0){
                            Toasty.info(view.getContext(),"No event found. Please check your input!",Toasty.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        }
    }

    private void init(View view) {
        eventSearch=view.findViewById(R.id.event_searchET);
        moreTv = view.findViewById(R.id.moreTV);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventList = new ArrayList<>();
        eventRecyclerview = view.findViewById(R.id.event_recyclerview);
        eventRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        eventAdapter = new EventAdapter(eventList, getContext());
        eventRecyclerview.setAdapter(eventAdapter);
        auth = FirebaseAuth.getInstance();
        event_nav_icon = view.findViewById(R.id.event_nav_icon);
        //mdrawrelayout=view.findViewById(R.id.nav_view);
        searchAction = view.findViewById(R.id.searchIV);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_event:
                DatabaseReference eventInfoRef = databaseReference.child("event");
                eventInfoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            eventList.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Map values = (Map) data.getValue();
                                if(values.get("eventPublisherId").toString().matches(userId)) {
                                    Event event = data.getValue(Event.class);
                                    eventList.add(event);
                                    //eventAdapter.notifyDataSetChanged();
                                }
                            }
                            Collections.reverse(eventList);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Toasty.info(view.getContext(), "My Event", Toasty.LENGTH_SHORT).show();
                return false;
            case R.id.all_event:
                getData(view);
                Toasty.info(view.getContext(), "All Event", Toasty.LENGTH_SHORT).show();

            default:
                return false;
        }
    }
}
