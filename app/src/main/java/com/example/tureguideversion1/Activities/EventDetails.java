package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.tureguideversion1.Adapters.EventLocationListAdapter;
import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.EventLocationList;
import com.example.tureguideversion1.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class EventDetails extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private TextView event_place, event_date, return_date, event_time, meeting_place, event_description, publish_date,
            publisher_name, publisher_phone, event_attending_member, event_cost, group_name, view, deleteTV, txt7;
    private ImageView descriptionIV, meetingIV, groupIV, costIV, event_image, event_publisher_image;
    private Button joinBtn, cancel_joinBtn;
    private String place, s_date, r_date, time, m_place, description, p_date, attend_member_count, g_name, e_cost, publisher_id;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId, event_Id, event_user_id;
    private String member_name, member_image, member_phone;
    private String update_des;
    private long count;
    private List<EventLocationList> locationLists;
    private List<String> locationWillBeVisit;
    private EventLocationListAdapter locationAdapter;
    private RecyclerView locationRecycleView;
    private SliderLayout imageSlider;
    private int slide;
    private TourFragment.navDrawerCheck check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        init();
        getData();
        userId = auth.getCurrentUser().getUid();
        moreImageShow();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(place.toLowerCase());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectImageNInfo((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference eRef = databaseReference.child("event").child(event_Id);
                DatabaseReference mRef = databaseReference.child("eventJoinMember").child(event_Id);
                DatabaseReference lRef = databaseReference.child("eventLocationList").child(event_Id);
                eRef.setValue(null);
                mRef.setValue(null);
                lRef.setValue(null);
                Toasty.success(getApplicationContext(), "Delete Success", Toasty.LENGTH_SHORT).show();
                startActivity(new Intent(EventDetails.this, MainActivity.class));
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference memberRef = databaseReference.child("eventJoinMember").child(event_Id).child(userId);
                memberRef.child("id").setValue(userId);
                DatabaseReference memberRef2 = databaseReference.child("eventJoinMember").child(event_Id);
                memberRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        count = dataSnapshot.getChildrenCount();
                        DatabaseReference e_Ref = databaseReference.child("event").child(event_Id);
                        e_Ref.child("joinMemberCount").setValue(count);
                        String c = String.valueOf(count);
                        event_attending_member.setText(c);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                joinBtn.setVisibility(View.GONE);

            }
        });
        cancel_joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRef = databaseReference.child("eventJoinMember").child(event_Id).child(userId);
                mRef.removeValue();
                DatabaseReference memberRef2 = databaseReference.child("eventJoinMember").child(event_Id);
                memberRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        count = dataSnapshot.getChildrenCount();
                        DatabaseReference e_Ref = databaseReference.child("event").child(event_Id);
                        e_Ref.child("joinMemberCount").setValue(count);
                        String c = String.valueOf(count);
                        event_attending_member.setText(c);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                cancel_joinBtn.setVisibility(View.GONE);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                slide = data.getIntExtra("slide", 0);

                if (imageSlider.getCurrentPosition() < slide) {
                    if (slide - imageSlider.getCurrentPosition() > 1) {

                    } else {
                        imageSlider.setCurrentPosition(slide, false);
                    }
                } else if (imageSlider.getCurrentPosition() > slide) {
                    if (imageSlider.getCurrentPosition() - slide > 1) {

                    } else {
                        imageSlider.setCurrentPosition(slide, false);
                    }
                }
            }
        }
    }

    private void collectImageNInfo(Map<String, Object> locations) {

        ArrayList<String> location = new ArrayList<>();
        ArrayList<String> image = new ArrayList<>();
        //ArrayList<String> description = new ArrayList<>();

        //iterate through each user, ignoring their UID
        if (locations != null) {
            for (Map.Entry<String, Object> entry : locations.entrySet()) {

                //Get user map
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list
                if(locationWillBeVisit.contains(singleUser.get("locationName").toString())) {
                    location.add((String) singleUser.get("locationName"));
                    image.add((String) singleUser.get("image"));
                }
            }

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.placeholder(R.drawable.placeholder)
            //.error(R.drawable.placeholder);
            //imageSlider.removeAllSliders();

            for (int i = 0; i < image.size(); i++) {
                TextSliderView sliderView = new TextSliderView(this);
                // if you want show image only / without description text use DefaultSliderView instead

                // initialize SliderLayout
                sliderView
                        .image(image.get(i))
                        .description(location.get(i))
                        .setRequestOption(requestOptions)
                        .setProgressBarVisible(true)
                        .setOnSliderClickListener(this);

                //add your extra information
                sliderView.bundle(new Bundle());
                sliderView.getBundle().putString("extra", location.get(i));
                imageSlider.addSlider(sliderView);
                //loading.setVisibility(View.INVISIBLE);
            }
            // set Slider Transition Animation
            if (imageSlider.getSliderImageCount() < 2) {
                imageSlider.stopAutoCycle();
                imageSlider.setPagerTransformer(false, new BaseTransformer() {
                    @Override
                    protected void onTransform(View view, float v) {
                    }
                });
                imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
            } else {

                imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                imageSlider.startAutoCycle();
                imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                imageSlider.setCustomAnimation(new DescriptionAnimation());
                imageSlider.setDuration(4000);
                imageSlider.addOnPageChangeListener(this);
                imageSlider.stopCyclingWhenTouch(true);

            }
        } else {
            Toasty.warning(getApplicationContext(), "Image not found!", Toasty.LENGTH_SHORT).show();
        }

    }

    private void member_counter() {
        DatabaseReference memberCountRef = databaseReference.child("eventJoinMember").child(event_Id);
        memberCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //count = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void moreImageShow() {
        if (publisher_id != null) {
            if (publisher_id.equals(userId)) {
                deleteTV.setVisibility(View.VISIBLE);
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


    private void joinButtonShow() {
        DatabaseReference memberRef = databaseReference.child("eventJoinMember").child(event_Id);
        memberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String id = (String) data.child("id").getValue();
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


    }


    private void getData() {
        place = getIntent().getStringExtra("event_place");
        s_date = getIntent().getStringExtra("event_start_date");
        r_date = getIntent().getStringExtra("event_return_date");
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

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("eventLocationList").child(event_Id);
        ref1.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        locationList((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void locationList(Map<String, Object> value) {
        if(value != null) {
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                //Get user map
                Map singleUser = (Map) entry.getValue();
                //Get location field and append to list
                locationWillBeVisit.add((String) singleUser.get("locationName"));
                EventLocationList location = new EventLocationList((String) singleUser.get("locationName"));
                locationLists.add(location);
                locationAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setData() {
        event_place.setText(place);
        event_date.setText(s_date);
        return_date.setText(r_date);
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
        return_date = findViewById(R.id.return_dateTV);
        event_time = findViewById(R.id.event_timeTV);
        meeting_place = findViewById(R.id.meeting_placeTV);
        event_description = findViewById(R.id.event_descriptionTV);
        publish_date = findViewById(R.id.event_publish_dateTV);
        publisher_name = findViewById(R.id.event_publish_nameTV);
        event_attending_member = findViewById(R.id.attending_memberTV);
        view = findViewById(R.id.viewTV);
        deleteTV = findViewById(R.id.deleteTV);
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
        locationLists = new ArrayList<>();
        locationWillBeVisit = new ArrayList<>();
        locationAdapter = new EventLocationListAdapter(locationLists,getApplicationContext());
        locationRecycleView = findViewById(R.id.eventLocationList_recyclerview);
        locationRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        locationRecycleView.setHasFixedSize(true);
        locationRecycleView.setAdapter(locationAdapter);
        imageSlider = findViewById(R.id.sliderFromEventDetails);
    }




    @Override
    public void onResume() {
        imageSlider.startAutoCycle();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent i = new Intent(view.getContext(), LocationImage.class)
                .putExtra("slide", slider.getBundle().getString("extra"))
                .putExtra("location", place.toLowerCase())
                .putStringArrayListExtra("willVisit", (ArrayList<String>) locationWillBeVisit);
        startActivityForResult(i, 1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
