package com.example.tureguideversion1.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.EventJoinMemberAdapter;
import com.example.tureguideversion1.Model.EventJoinMemberList;
import com.example.tureguideversion1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class JoinMemberDetails extends AppCompatActivity {

    private RecyclerView member_list_recyclerView;
    private EventJoinMemberAdapter eventJoinMemberAdapter;
    private List<EventJoinMemberList> eventJoinMemberLists;
    private String event_id;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member_details);
        init();
        // getData();
        eventJoinMemberAdapter.notifyDataSetChanged();
    }



/*    private void getData() {
        event_id=getIntent().getStringExtra("event_id");
        DatabaseReference memRef=databaseReference.child("eventJoinMember").child(event_id);
        memRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
*/

    private void init() {
        member_list_recyclerView = findViewById(R.id.member_details_recycler_view);
        eventJoinMemberAdapter = new EventJoinMemberAdapter(eventJoinMemberLists);
        member_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        member_list_recyclerView.setAdapter(eventJoinMemberAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
