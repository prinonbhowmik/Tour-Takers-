package com.example.tureguideversion1.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class EventDetails extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private TextView event_place, event_date, event_time, meeting_place, event_description, publish_date,
            publisher_name, publisher_phone, event_attending_member, event_cost, group_name, view, moreTV, txt7;
    private ImageView descriptionIV, meetingIV, groupIV, costIV, event_image, event_publisher_image;
    private Button joinBtn, cancel_joinBtn;
    private String place, date, time, m_place, description, p_date, attend_member_count, g_name, e_cost, publisher_id;
    private Integer member_count;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId, event_Id, event_user_id;
    private String member_name, member_image, member_phone, a;
    private String update_des;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        init();
        registerForContextMenu(moreTV);
        getData();
        userId = auth.getCurrentUser().getUid();
        moreImageShow();


        //joinButtonShow();
        moreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference memberRef = databaseReference.child("eventJoinMember").child(event_Id).child(userId);
                memberRef.child("id").setValue(userId);
                a = event_attending_member.getText().toString();
                int b = Integer.parseInt(a);
                b = b + 1;
                String c = String.valueOf(b);
                event_attending_member.setText(c);
                joinBtn.setVisibility(View.GONE);
                //joinButtonShow();
                member_counter();
            }
        });
        cancel_joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRef = databaseReference.child("eventJoinMember").child(event_Id).child(userId);
                mRef.removeValue();
                a = event_attending_member.getText().toString();
                int b = Integer.parseInt(a);
                b = b - 1;
                String c = String.valueOf(b);
                event_attending_member.setText(c);
                cancel_joinBtn.setVisibility(View.GONE);
                //joinButtonShow();
                member_counter();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                Intent intent = new Intent(EventDetails.this, JoinMemberDetails.class);

                intent.putExtra("event_id", event_Id);
                startActivity(intent);
            }
        });
        setData();

        descriptionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("description", "Description");
                args.putString("event_id", event_Id);
                args.putString("input_d", description);
                EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                bottomSheet.setArguments(args);
                bottomSheet.show(getSupportFragmentManager(), "test");
            }
        });

        meetingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("meeting_place", "Meeting Place");
                args.putString("event_id", event_Id);
                args.putString("input_m", m_place);
                EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                bottomSheet.setArguments(args);
                bottomSheet.show(getSupportFragmentManager(), "test");
            }
        });

        groupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("group_name", "Group Name");
                args.putString("event_id", event_Id);
                args.putString("input_g", g_name);
                EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                bottomSheet.setArguments(args);
                bottomSheet.show(getSupportFragmentManager(), "test");
            }
        });

        costIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("cost", "Total Cost");
                args.putString("event_id", event_Id);
                args.putString("input_c", e_cost);
                EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                bottomSheet.setArguments(args);
                bottomSheet.show(getSupportFragmentManager(), "test");
            }
        });


    }

    private void member_counter() {
        DatabaseReference memberCountRef = databaseReference.child("eventJoinMember").child(event_Id);
        memberCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                DatabaseReference e_Ref = databaseReference.child("event").child(event_Id);
                e_Ref.child("joinMemberCount").setValue(count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(this, moreTV);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.event_update_menu);
        popup.show();
    }


    private void moreImageShow() {
        final DatabaseReference eRef = databaseReference.child("event").child(event_Id);
        eRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event_user_id = (String) dataSnapshot.child("eventPublisherId").getValue();
                if (event_user_id != null) {
                    if (event_user_id.equals(userId)) {
                        moreTV.setVisibility(View.VISIBLE);
                        txt7.setVisibility(View.VISIBLE);
                        meetingIV.setVisibility(View.VISIBLE);
                        descriptionIV.setVisibility(View.VISIBLE);
                        groupIV.setVisibility(View.VISIBLE);
                        costIV.setVisibility(View.VISIBLE);
                    } else {
                        joinButtonShow();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void joinButtonShow() {
        DatabaseReference memberRef=databaseReference.child("eventJoinMember").child(event_Id);
        memberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String id= (String) data.child("id").getValue();
                    if (userId.equals(id)) {
                        cancel_joinBtn.setVisibility(View.VISIBLE);
                    } else {
                        joinBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        getUpdateData();

    }

    private void getUpdateData() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

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
        g_name = getIntent().getStringExtra("group_name");
        e_cost = getIntent().getStringExtra("cost");

        //for user name
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("profile").child(publisher_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    member_name = (String) dataSnapshot.child("name").getValue();
                    member_image = (String) dataSnapshot.child("image").getValue();
                    publisher_name.setText(member_name);
                    if (!member_image.isEmpty()) {
                        try {

                            GlideApp.with(EventDetails.this)
                                    .load(member_image)
                                    .fitCenter()
                                    .into(event_publisher_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setData() {
        event_place.setText(place);
        event_date.setText(date);
        event_time.setText(time);
        meeting_place.setText(m_place);
        event_description.setText(description);
        publish_date.setText(p_date);
        event_attending_member.setText(attend_member_count);
        group_name.setText(g_name);
        event_cost.setText(e_cost);
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
        moreTV = findViewById(R.id.moreTV);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        joinBtn = findViewById(R.id.joinBtn);
        cancel_joinBtn = findViewById(R.id.cancel_joinBtn);
        txt7 = findViewById(R.id.txt7);
        event_cost = findViewById(R.id.event_costTV);
        group_name = findViewById(R.id.group_nameTV);
        event_publisher_image = findViewById(R.id.event_publish_imageIV);
        descriptionIV = findViewById(R.id.edit_description);
        groupIV = findViewById(R.id.edit_groupName);
        meetingIV = findViewById(R.id.edit_meetingPlace);
        costIV = findViewById(R.id.edit_costIV);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update:
                Intent intent = new Intent(EventDetails.this, UpdateEvent.class);
                startActivity(intent);
                return false;
            case R.id.delete:
                DatabaseReference dRef = databaseReference.child("event").child(event_Id).child("eventPublisherId");
                dRef.setValue(null);
                DatabaseReference eRef = databaseReference.child("event").child(event_Id);
                DatabaseReference mRef = databaseReference.child("eventJoinMember").child(event_Id);
                eRef.removeValue();
                mRef.removeValue();
                onBackPressed();
                Toasty.success(getApplicationContext(), "Delete Success", Toasty.LENGTH_SHORT).show();

                return false;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
