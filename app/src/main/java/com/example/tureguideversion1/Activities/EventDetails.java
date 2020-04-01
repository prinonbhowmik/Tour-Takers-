package com.example.tureguideversion1.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class EventDetails extends AppCompatActivity {

    private TextView event_place, event_date, event_time, meeting_place, event_description, publish_date,
            publisher_name, publisher_phone, event_attending_member, view;
    private ImageView event_image, event_publisher_image, moreIV;
    private Button joinBtn;
    private String place, date, time, m_place, description, p_date, p_name, p_phone, p_image, attend_member_count, publisher_id;
    private Integer member_count;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId, event_Id, event_user_id;
    String member_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        init();
        getData();
        userId = auth.getCurrentUser().getUid();
        moreImageShow();
        //joinButtonShow();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("profile").child(publisher_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    member_name = (String) dataSnapshot.child("name").getValue();
                    publisher_name.setText(member_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        setData();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(EventDetails.this,JoinMemberDetails.class);
//                intent.putExtra("event_id",event_Id);
//                startActivity(intent);
                Toasty.normal(getApplicationContext(), "Update Comes Soon", Toasty.LENGTH_SHORT).show();
            }
        });


    }

  /*  private void joinButtonShow() {
        DatabaseReference memberRef=databaseReference.child("eventJoinMember").child(event_Id);
        memberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String id= (String) data.child("id").getValue();
                    if (id.equals(userId)) {
                        joinBtn.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }*/

    private void moreImageShow() {
        final DatabaseReference eRef = databaseReference.child("event").child(event_Id);
        eRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event_user_id = (String) dataSnapshot.child("eventPublisherId").getValue();
                if (event_user_id.equals(userId)) {
                    moreIV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getData() {
        place = getIntent().getStringExtra("event_place");
        date = getIntent().getStringExtra("event_date");
        time = getIntent().getStringExtra("event_time");
        m_place = getIntent().getStringExtra("event_meeting_place");
        description = getIntent().getStringExtra("event_description");
        p_date = getIntent().getStringExtra("event_publish_date");
        attend_member_count = getIntent().getStringExtra("event_join_member_count");
        event_Id = getIntent().getStringExtra("event_id");
        publisher_id = getIntent().getStringExtra("member_id");
    }

    private void setData() {
        event_place.setText(place);
        event_date.setText(date);
        event_time.setText(time);
        meeting_place.setText(m_place);
        event_description.setText(description);
        publish_date.setText(p_date);
        event_attending_member.setText(attend_member_count);

    }

    private void init() {
        event_place = findViewById(R.id.event_placeTV);
        event_date = findViewById(R.id.event_dateTV);
        event_time = findViewById(R.id.event_timeTV);
        meeting_place = findViewById(R.id.meeting_placeTV);
        event_description = findViewById(R.id.event_descriptionTV);
        publish_date = findViewById(R.id.event_publish_dateTV);
        publisher_name = findViewById(R.id.event_publish_nameTV);
        event_attending_member = findViewById(R.id.attending_memberTV);
        view = findViewById(R.id.viewTV);
        moreIV = findViewById(R.id.moreIV);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


    }
}
