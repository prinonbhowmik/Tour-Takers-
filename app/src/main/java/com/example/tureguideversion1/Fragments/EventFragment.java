package com.example.tureguideversion1.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tureguideversion1.Activities.CreateEvent;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.Activities.UserProfile;
import com.example.tureguideversion1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    FloatingActionButton createEvent;
    private EditText eventSearch;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_event, container, false);
        init(view);

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateEvent.class));

            }
        });

        eventSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    eventSearch.setCursorVisible(true);
            }
        });

        return view;
    }

    private void init(View view) {

        createEvent=view.findViewById(R.id.create_event);
        eventSearch=view.findViewById(R.id.event_searchET);
    }
}