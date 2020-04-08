package com.example.tureguideversion1.Activities;

import android.os.Bundle;
import android.widget.Toast;

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

public class JoinMemberDetails extends AppCompatActivity {

    private RecyclerView member_list_recyclerView;
    private EventJoinMemberAdapter eventJoinMemberAdapter;
    private List<EventJoinMemberList> eventJoinMemberListList;
    private String event_id;
    private String id;
    private DatabaseReference databaseReference;
    private ArrayList<String> uid;

    public JoinMemberDetails() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member_details);
        init();
        getData();
        eventJoinMemberAdapter.notifyDataSetChanged();


    }


    private void getData() {
        event_id = getIntent().getStringExtra("event_id");
        DatabaseReference memRef = databaseReference.child("eventJoinMember").child(event_id);
        memRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> uid = new ArrayList<>();
                for (final DataSnapshot data : dataSnapshot.getChildren()) {

                    id = (String) data.child("id").getValue();
                    final int count = (int) data.getChildrenCount();
                    uid.add(id);
                    for (int a = 1; a < count; a++) {
                        String url = uid.get(a);
                        Toast.makeText(JoinMemberDetails.this, "" + url, Toast.LENGTH_SHORT).show();
                        DatabaseReference uRef = databaseReference.child("profile").child(url);
                        uRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    eventJoinMemberListList.clear();
                                    for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                                        String pid = (String) data1.child("Id").getValue();
                                        //if (id.equals(pid)) {
                                        EventJoinMemberList eventJoinMember = data1.getValue(EventJoinMemberList.class);
                                        eventJoinMemberListList.add(eventJoinMember);
                                        //   break;
                                        // }
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


    private void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventJoinMemberListList = new ArrayList<>();
        member_list_recyclerView = findViewById(R.id.member_details_recycler_view);
        eventJoinMemberAdapter = new EventJoinMemberAdapter(eventJoinMemberListList, JoinMemberDetails.this);
        member_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        member_list_recyclerView.setAdapter(eventJoinMemberAdapter);

    }
}
