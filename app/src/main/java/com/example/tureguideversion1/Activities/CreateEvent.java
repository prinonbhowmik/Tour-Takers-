package com.example.tureguideversion1.Activities;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class CreateEvent extends AppCompatActivity {
    private View rootLayout;
    private TextInputEditText eventDate, eventTime, eventPlace, meetingPlace, groupName, eventCost;
    private EditText eventDescription;
    private Button eventBtn;
    private String id, date, time, publishDate, place, meetPlace, description, eventPublisherId,
            group_name, cost, join_member_info;
    private int joinMemberCount;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public String format;
    private FirebaseAuth auth;
    private String userId, member_name, member_phone, member_image, member_pro_id;
    private static String eventId;

    private String memberName, memberPhone, memberImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        init();
        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);
            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }


        getUserDataFromProfile();


        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickers();
            }
        });
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickers();
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh:mm a");
                publishDate = simpleDateFormat.format(calendar.getTime());

                DatabaseReference eventRef = databaseReference.child("event");
                eventId = eventRef.push().getKey();


                eventPublisherId = member_pro_id;
                memberName = member_name;
                memberPhone = member_phone;
                memberImage = member_image;
                date = eventDate.getText().toString();
                time = eventTime.getText().toString();
                place = eventPlace.getText().toString();
                description = eventDescription.getText().toString();
                meetPlace = meetingPlace.getText().toString();
                group_name = groupName.getText().toString();
                cost = eventCost.getText().toString();
                id = eventId;
                joinMemberCount = 1;


                if (TextUtils.isEmpty(place)) {
                    Toasty.info(getApplicationContext(), "Please fill Tour Place", Toasty.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(date)) {
                    Toasty.info(getApplicationContext(), "Please fill Event Date", Toasty.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(time)) {
                    Toasty.info(getApplicationContext(), "Please fill Event Time", Toasty.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(meetPlace)) {
                    Toasty.info(getApplicationContext(), "Please fill Meeting Place", Toasty.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toasty.info(getApplicationContext(), "Please fill Description Box", Toasty.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(group_name)) {
                    Toasty.info(getApplicationContext(), "Please fill Group Name", Toasty.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(cost)) {
                    Toasty.info(getApplicationContext(), "Please fill Event Cost", Toasty.LENGTH_SHORT).show();
                } else {
                    addEventInDB(id, date, time, place, meetPlace, description, publishDate, joinMemberCount,
                            eventPublisherId, group_name, cost);
                    eventDescription.setText(null);
                    eventDate.setText(null);
                    eventTime.setText(null);
                    meetingPlace.setText(null);
                    eventPlace.setText(null);
                    groupName.setText(null);
                    eventCost.setText(null);

                }


            }
        });
    }

    private void memberCounter() {
        DatabaseReference memberCountRef = databaseReference.child("eventJoinMember").child(eventId);
        memberCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                DatabaseReference e_Ref = databaseReference.child("event").child(eventId);
                e_Ref.child("joinMemberCount").setValue(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addJoinMember() {
        DatabaseReference memberRef = databaseReference.child("eventJoinMember").child(eventId).child(userId);
        memberRef.child("id").setValue(userId);
    }


    private void addEventInDB(String id, String date, String time, String place, String meetPlace, String description,
                              String publishDate, int joinMemberCount, String eventPublisherId, String group_name, String cost) {
        final DatabaseReference eventRef = databaseReference.child("event");

        final Event event = new Event(id, date, time, place, meetPlace, description, publishDate, joinMemberCount,
                eventPublisherId, group_name, cost);

        eventRef.child(eventId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toasty.success(getApplicationContext(), "Your Event Successfully Added", Toasty.LENGTH_SHORT).show();
                    addJoinMember();

                } else {
                    Toasty.error(getApplicationContext(), "Unsuccessful", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getUserDataFromProfile() {
        userId = auth.getCurrentUser().getUid();

        DatabaseReference profileRef = databaseReference.child("profile").child(userId);
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                member_name = (String) dataSnapshot.child("name").getValue();
                member_image = (String) dataSnapshot.child("image").getValue();
                member_phone = (String) dataSnapshot.child("phone").getValue();
                member_pro_id = (String) dataSnapshot.child("Id").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void init() {
        rootLayout=findViewById(R.id.rootLayout);
        eventDate = findViewById(R.id.event_date);
        eventTime = findViewById(R.id.event_time);
        eventPlace = findViewById(R.id.event_place);
        eventDescription = findViewById(R.id.event_description);
        groupName = findViewById(R.id.group_name);
        eventCost = findViewById(R.id.cost);
        eventBtn = findViewById(R.id.create_eventBtn);
        meetingPlace = findViewById(R.id.meeting_place);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    private void DatePickers() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                /*      Your code   to get date and time    */
                selectedmonth = selectedmonth + 1;
                eventDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    private void TimePickers() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour == 0) {
                    selectedHour += 12;
                    format = "AM";
                } else if (selectedHour == 12) {
                    format = "PM";
                } else if (selectedHour > 12) {
                    selectedHour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                eventTime.setText(selectedHour + ":" + selectedMinute + " " + format);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.show();

    }


    // slide animation
    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() -getDips(48);
        int cy = rootLayout.getHeight() -getDips(48);

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    //slide animation
    private int getDips(int dps) {
        Resources resources=getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    //slide animation
    @Override
    public void onBackPressed() {
        int cx = rootLayout.getWidth()-getDips(48);
        int cy = rootLayout.getBottom()-getDips(48);
        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        circularReveal.setDuration(600);
        circularReveal.start();
    }
}
