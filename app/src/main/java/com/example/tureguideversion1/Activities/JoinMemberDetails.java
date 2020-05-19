package com.example.tureguideversion1.Activities;

import android.os.Bundle;

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
import java.util.Map;

public class JoinMemberDetails extends AppCompatActivity {

    private RecyclerView member_list_recyclerView;
    private EventJoinMemberAdapter eventJoinMemberAdapter;
    private List<EventJoinMemberList> eventJoinMemberListList;
    private String event_id;
    private ArrayList<String> uid;

    public JoinMemberDetails() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_member_details);
        init();
        getData();
    }


    private void getData() {
        event_id = getIntent().getStringExtra("event_id");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventJoinMember").child(event_id);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        joinMemberList((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void joinMemberList(Map<String, Object> users) {

        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get district field and append to list
            uid.add((String) singleUser.get("id"));
        }

        for (int i = 0; i < uid.size(); i++) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("profile").child(uid.get(i));
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            //joinMemberInfoList((Map<String, Object>) dataSnapshot.getValue());
                            Map<String,Object> values = (Map<String, Object>) dataSnapshot.getValue();
                            EventJoinMemberList members = new EventJoinMemberList((String) values.get("name"),(String) values.get("email"),(String) values.get("image"),(String) values.get("sex"));
                            eventJoinMemberListList.add(members);
                            eventJoinMemberAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }
    }

    private void init() {
        eventJoinMemberListList = new ArrayList<>();
        member_list_recyclerView = findViewById(R.id.member_details_recycler_view);
        eventJoinMemberAdapter = new EventJoinMemberAdapter(eventJoinMemberListList, JoinMemberDetails.this);
        member_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        member_list_recyclerView.setAdapter(eventJoinMemberAdapter);
        uid = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
//        Intent intent=new Intent(JoinMemberDetails.this,EventDetails.class);
//        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }
}
