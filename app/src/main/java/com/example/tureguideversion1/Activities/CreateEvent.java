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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {
    private View rootLayout;
    private TextInputEditText eventDate, eventTime, eventPlace, meetingPlace;
    private EditText eventDescription;
    private Button eventBtn;
    private String date, time, publishDate, place, meetPlace, description, eventImage, publisherName, publisherImage, join_member_info;
    private int joinMemberCount = 1;
    private DatabaseReference databaseReference;
    public String format;
    public int hour, minute;



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
                date = eventDate.getText().toString();
                time = eventTime.getText().toString();
                place = eventPlace.getText().toString();
                description = eventDescription.getText().toString();
                meetPlace = meetingPlace.getText().toString();
                publishDate = simpleDateFormat.format(calendar.getTime());
                if (TextUtils.isEmpty(place)) {
                    Toast.makeText(CreateEvent.this, "Please fill Tour Place", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(date)) {
                    Toast.makeText(CreateEvent.this, "Please fill Event Date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(time)) {
                    Toast.makeText(CreateEvent.this, "Please fill Event Time", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(meetPlace)) {
                    Toast.makeText(CreateEvent.this, "Please fill Meeting Place", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(CreateEvent.this, "Please fill Event Description", Toast.LENGTH_SHORT).show();
                } else {
                    addInDB(date, time, place, meetPlace, description, publishDate, joinMemberCount);
                }
            }
        });
    }

    private void addInDB(String date, String time, String place, String meetPlace, String description, String publishDate, int joinMemberCount) {
        DatabaseReference eventRef = databaseReference.child("event");

        Event event = new Event(date, time, place, meetPlace, description, publishDate, joinMemberCount);
        eventRef.push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateEvent.this, "Event Successfully Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateEvent.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        rootLayout=findViewById(R.id.rootLayout);
        eventDate = findViewById(R.id.event_date);
        eventTime = findViewById(R.id.event_time);
        eventPlace = findViewById(R.id.event_place);
        eventDescription = findViewById(R.id.event_description);
        eventBtn = findViewById(R.id.create_eventBtn);
        meetingPlace = findViewById(R.id.meeting_place);
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
                //selectedTimeFormat(selectedHour);
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
        // mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    public void selectedTimeFormat(int hour) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "Am";
        } else {
            format = "PM";
        }
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
