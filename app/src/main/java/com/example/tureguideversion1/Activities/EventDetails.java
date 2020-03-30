package com.example.tureguideversion1.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetails extends AppCompatActivity {

    private TextView event_place, event_date, event_time, meeting_place, event_description, publish_date,
            publisher_name, publisher_phone, event_attending_member;
    private ImageView event_image, event_publisher_image;
    private Button joinBtn;
    private String place, date, time, m_place, description, p_date, p_name, p_phone, p_image, attend_member_count, publisher_id;
    private Integer member_count;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId, event_Id;
    private String member_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        init();
        userId = auth.getCurrentUser().getUid();

        getData();

        setData();

    }

    private void getData() {
        place = getIntent().getStringExtra("event_place");
        date = getIntent().getStringExtra("event_date");
        time = getIntent().getStringExtra("event_time");
        m_place = getIntent().getStringExtra("event_meeting_place");
        description = getIntent().getStringExtra("event_description");
        p_date = getIntent().getStringExtra("event_publish_date");
        publisher_id = getIntent().getStringExtra("publisher_id");
        p_name = getIntent().getStringExtra("publisher_name");
        p_phone = getIntent().getStringExtra("publisher_phone");
        p_image = getIntent().getStringExtra("publisher_image");
        attend_member_count = getIntent().getStringExtra("event_join_member_count");
        event_Id = getIntent().getStringExtra("event_id");
    }

    private void setData() {
        event_place.setText(place);
        event_date.setText(date);
        event_time.setText(time);
        meeting_place.setText(m_place);
        event_description.setText(description);
        publish_date.setText(p_date);
        publisher_name.setText(p_name);
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


    }
}
