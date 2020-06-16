package com.example.tureguideversion1.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tureguideversion1.Activities.CommentsBox;
import com.example.tureguideversion1.Adapters.ChatAdapter;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EventCommentsNotify extends JobService {

    private static final String TAG = "EventCommentsNotify";
    private boolean jobCancelled = false;
    private List<Chat> mChat;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        commentsNotify(jobParameters);
        return true;
    }

    private void commentsNotify(final JobParameters params) {
        ArrayList<String> eventIDs = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            DatabaseReference userActivity = databaseReference.child("userActivities").child(user.getUid()).child("events");
            userActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            HashMap<String,Object> data = (HashMap<String, Object>) childSnapshot.getValue();
                            eventIDs.add((String) data.get("eventID"));

                        }
                        //Log.d(TAG, "onDataChange: " + eventIDs);
                        for (int i = 0; i < eventIDs.size(); i++) {
                            DatabaseReference ref = databaseReference.child("eventComments").child(eventIDs.get(i));
                            Query locations = ref.orderByKey().limitToLast(1);
                            locations.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            //Log.d(TAG, "onDataChange: " + childSnapshot.getValue());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }
        jobFinished(params, false);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
