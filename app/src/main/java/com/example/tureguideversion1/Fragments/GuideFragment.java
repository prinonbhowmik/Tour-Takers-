package com.example.tureguideversion1.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tureguideversion1.Activities.CreateEvent;
import com.example.tureguideversion1.Activities.NoInternetConnection;
import com.example.tureguideversion1.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment extends Fragment {

    private Button btn;

    public GuideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_guide, container, false);
        init(view);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NoInternetConnection.class));

            }
        });


        return view;
    }

    private void init(View view) {

        btn=view.findViewById(R.id.internet);
    }
}
