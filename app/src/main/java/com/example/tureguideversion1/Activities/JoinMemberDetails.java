package com.example.tureguideversion1.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.EventJoinMemberAdapter;
import com.example.tureguideversion1.Model.EventJoinMemberList;
import com.example.tureguideversion1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class JoinMemberDetails extends AppCompatActivity {

    private RecyclerView member_list_recyclerView;
    private EventJoinMemberAdapter eventJoinMemberAdapter;
    private List<EventJoinMemberList> eventJoinMemberList;
    private String event_id;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member_details);
        init();
        getData();

    }


    private void getData() {
        event_id=getIntent().getStringExtra("event_id");
        DatabaseReference memRef=databaseReference.child("eventJoinMember").child(event_id);
        memRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String id = (String) data.child("id").getValue();
                    DatabaseReference uRef = databaseReference.child("profile").child(id);
                    uRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                eventJoinMemberList.clear();
                                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                                    EventJoinMemberList eventJoinMember = data1.getValue(EventJoinMemberList.class);
                                    eventJoinMemberList.add(eventJoinMember);
                                    eventJoinMemberAdapter.notifyDataSetChanged();
                                    Toasty.normal(JoinMemberDetails.this, "success", Toasty.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toasty.normal(getApplicationContext(), "" + databaseError, Toasty.LENGTH_SHORT).show();


                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventJoinMemberList = new ArrayList<>();
        member_list_recyclerView = findViewById(R.id.member_details_recycler_view);
        eventJoinMemberAdapter = new EventJoinMemberAdapter(eventJoinMemberList);
        member_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        member_list_recyclerView.setAdapter(eventJoinMemberAdapter);

    }
}
