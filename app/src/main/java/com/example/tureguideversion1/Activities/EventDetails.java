package com.example.tureguideversion1.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.tureguideversion1.Adapters.EventLocationListAdapter;
import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.Model.EventLocationList;
import com.example.tureguideversion1.Notifications.APIService;
import com.example.tureguideversion1.Notifications.Client;
import com.example.tureguideversion1.Notifications.Data;
import com.example.tureguideversion1.Notifications.Response;
import com.example.tureguideversion1.Notifications.Sender;
import com.example.tureguideversion1.Notifications.Token;
import com.example.tureguideversion1.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

public class EventDetails extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView event_place, event_date, return_date, event_time, meeting_place, event_description, publish_date,
            publisher_name, publisher_phone, event_attending_member, event_cost, group_name, viewMember, deleteTV, txt7, commentCountTV, eventRunning, lateForJoin;
    private ImageView descriptionIV, meetingIV, groupIV, costIV, event_image;
    private CircleImageView event_publisher_image;
    private Button joinBtn, cancel_joinBtn;
    private String place, s_date, r_date, time, m_place, description, p_date, g_name, e_cost, publisher_id, counter,
            place1, username;
    private int attend_member_count;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId, event_Id, guideID;
    private String member_name, member_image, member_phone, member_email, member_gender;
    private String update_des;
    private long count, commentCounter;
    private List<EventLocationList> locationLists;
    private List<String> locationWillBeVisit;
    private EventLocationListAdapter locationAdapter;
    private RecyclerView locationRecycleView;
    private SliderLayout imageSlider;
    private int slide;
    private TourFragment.navDrawerCheck check;
    private Dialog profileDialog;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    private double latForMeetingPlace, lonForMeetingPlace;
    private RelativeLayout relative16, relative15;
    private TextView viewComment;
    public static final String TAG = "EventDetails";
    private APIService apiService;
    private MediaPlayer sendSound, receiveSound;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        init();
        registerReceiver(connectivityReceiver, intentFilter);
        getData();
        commentCounter();
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
                if (checkConnection()) {
                    deletealertmessage();
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });

        meeting_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventDetails.this, com.example.tureguideversion1.Activities.Map.class)
                        .putExtra("from", "eventDetails")
                        .putExtra("latForMeetingPlace", latForMeetingPlace)
                        .putExtra("lonForMeetingPlace", lonForMeetingPlace));
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    joinAlertDialog();
                    notify = true;
                    DatabaseReference memberRef = databaseReference.child("eventJoinMember").child(event_Id).child(userId);
                    memberRef.child("id").setValue(userId).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setUserActivity();
                        }
                    });
                    DatabaseReference memberRef2 = databaseReference.child("eventJoinMember").child(event_Id);
                    memberRef2.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    sendNotification();
                    joinBtn.setVisibility(View.GONE);
                    relative16.setVisibility(View.VISIBLE);
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });
        cancel_joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    cancelAlertDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });

        viewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this, JoinMemberDetails.class);
                intent.putExtra("event_id", event_Id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        descriptionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    Bundle args = new Bundle();
                    args.putString("description", "Description");
                    args.putString("event_id", event_Id);
                    args.putString("input_d", description);
                    EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                    bottomSheet.setArguments(args);
                    bottomSheet.show(getSupportFragmentManager(), "test");
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });

        meetingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    Bundle args = new Bundle();
                    args.putString("meeting_place", "Meeting Place");
                    args.putString("event_id", event_Id);
                    args.putString("input_m", m_place);
                    EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                    bottomSheet.setArguments(args);
                    bottomSheet.show(getSupportFragmentManager(), "test");
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });

        groupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    Bundle args = new Bundle();
                    args.putString("group_name", "Group Name");
                    args.putString("event_id", event_Id);
                    args.putString("input_g", g_name);
                    EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                    bottomSheet.setArguments(args);
                    bottomSheet.show(getSupportFragmentManager(), "test");
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });

        costIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    Bundle args = new Bundle();
                    args.putString("cost", "Total Cost");
                    args.putString("event_id", event_Id);
                    args.putString("input_c", e_cost);
                    EventUpdateBottomSheet bottomSheet = new EventUpdateBottomSheet();
                    bottomSheet.setArguments(args);
                    bottomSheet.show(getSupportFragmentManager(), "test");
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });
        viewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this, CommentsBox.class);
                intent.putExtra("eventId", event_Id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private void updateToken(String token) {
        DatabaseReference ref = databaseReference.child("eventJoinTokens");
        Token token1 = new Token(token);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", publisher_id);
        hashMap.put("token", token1.getToken());
        ref.child(event_Id).child(publisher_id).setValue(hashMap);

    }

    private void sendNotification() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> userMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    DatabaseReference publisherRef = FirebaseDatabase.getInstance().getReference().child("profile").child(publisher_id);
                    publisherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                HashMap<String, Object> publishermMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                Data data = new Data(event_Id,
                                        R.drawable.ic_stat_ic_notification,
                                        userMap.get("name") + " has joined your event!",
                                        "Your Event: " + place,
                                        publisher_id,
                                        auth.getUid(),
                                        "EventDetails",
                                        (String) userMap.get("image"),
                                        (String) userMap.get("sex"));
                                Sender sender = new Sender(data, (String) publishermMap.get("token"));
                                apiService.sendNotification(sender)
                                        .enqueue(new Callback<Response>() {
                                            @Override
                                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                                if (response.code() == 200) {
                                                    if (response.body().success != 1) {
                                                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Response> call, Throwable t) {

                                            }
                                        });
                                publisherRef.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    userRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void commentCounter() {
        DatabaseReference comment = databaseReference.child("eventComments").child(event_Id);
        comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentCounter = dataSnapshot.getChildrenCount();
                String cCounter = String.valueOf(commentCounter);
                commentCountTV.setText(cCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void joinAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Congratulation");
        dialog.setIcon(R.drawable.ic_congratulation);
        dialog.setMessage("Now you are a member of this event");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void cancelAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Alert..!!");
        dialog.setIcon(R.drawable.ic_leave_white);
        dialog.setMessage("Do you want to leave this event?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> eventKeys = new ArrayList<>();
                DatabaseReference mRef = databaseReference.child("eventJoinMember").child(event_Id).child(userId);
                mRef.removeValue();
                DatabaseReference removeToken = databaseReference.child("eventCommentsTokens").child(event_Id).child(userId);
                removeToken.removeValue();
                DatabaseReference removeActivity = databaseReference.child("userActivities").child(userId).child("events");
                removeActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                //eventKeys.add(childSnapshot.getKey());
                                HashMap<String, Object> hashMap = (HashMap<String, Object>) childSnapshot.getValue();
                                if (hashMap.get("eventID").toString().equals(event_Id)) {
                                    eventKeys.add(childSnapshot.getKey());
                                    //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                }
                            }
                            for (int i = 0; i < eventKeys.size(); i++) {
                                DatabaseReference remove = databaseReference.child("userActivities").child(userId).child("events").child(eventKeys.get(i));
                                remove.removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
                relative16.setVisibility(View.GONE);

            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();


    }

    private void deletealertmessage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete..!!");
        dialog.setIcon(R.drawable.ic_delete_white);
        dialog.setMessage("Do you want to delete this event?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference eRef = databaseReference.child("event").child(event_Id);
                DatabaseReference mRef = databaseReference.child("eventJoinMember").child(event_Id);
                DatabaseReference lRef = databaseReference.child("eventLocationList").child(event_Id);
                DatabaseReference cRef = databaseReference.child("eventComments").child(event_Id);
                DatabaseReference commentsTokenRef = databaseReference.child("eventCommentsTokens").child(event_Id);
                DatabaseReference replyRef = databaseReference.child("eventCommentsReply").child(event_Id);
                DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("notificationStatus").child("eventCommentNotifiaction").child(event_Id);
                DatabaseReference guideChat = FirebaseDatabase.getInstance().getReference().child("chatWithGuide").child(event_Id);
                DatabaseReference adminChat = FirebaseDatabase.getInstance().getReference().child("chatWithAdmin").child(event_Id);
                adminChat.removeValue();
                guideChat.removeValue();
                notiRef.removeValue();
                eRef.removeValue();
                mRef.removeValue();
                lRef.removeValue();
                cRef.removeValue();
                commentsTokenRef.removeValue();
                replyRef.removeValue();
                ArrayList<String> userIDs = new ArrayList<>();
                DatabaseReference removeActivity = FirebaseDatabase.getInstance().getReference().child("userActivities");
                removeActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                userIDs.add(childSnapshot.getKey());
                                //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                            }
                            for (int i = 0; i < userIDs.size(); i++) {
                                DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(userIDs.get(i)).child("events");
                                int finalI = i;
                                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                HashMap<String, Object> hashMap = (HashMap<String, Object>) childSnapshot.getValue();
                                                if (hashMap.get("eventID").toString().equals(event_Id)) {
                                                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("userActivities")
                                                            .child(userIDs.get(finalI))
                                                            .child("events")
                                                            .child(childSnapshot.getKey());
                                                    eventRef.removeValue();
                                                    //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                                }
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
                Toasty.success(getApplicationContext(), "Delete Success", Toasty.LENGTH_SHORT).show();
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();


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
                if (locationWillBeVisit.contains(singleUser.get("locationName").toString())) {
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
                        .setProgressBarVisible(false)
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
        /*SimpleDateFormat dteFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date lDate = null;
        try {
            lDate = dteFormat.parse(s_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        if (publisher_id != null) {

            if (publisher_id.equals(userId)) {
                DatabaseReference guideRef = databaseReference.child("event").child(event_Id);
                guideRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild("guideID")) {
                            deleteTV.setVisibility(View.VISIBLE);
                            txt7.setVisibility(View.VISIBLE);
                            meetingIV.setVisibility(View.VISIBLE);
                            descriptionIV.setVisibility(View.VISIBLE);
                            groupIV.setVisibility(View.VISIBLE);
                            costIV.setVisibility(View.VISIBLE);
                            relative16.setVisibility(View.VISIBLE);
                            lateForJoin.setVisibility(View.GONE);
                            eventRunning.setVisibility(View.GONE);
                        } else {
                            deleteTV.setVisibility(View.VISIBLE);
                            txt7.setVisibility(View.VISIBLE);
                            relative16.setVisibility(View.VISIBLE);
                            lateForJoin.setVisibility(View.GONE);
                            eventRunning.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }/*else if (publisher_id.equals(userId) && System.currentTimeMillis() > lDate.getTime()) {
                    txt7.setVisibility(View.VISIBLE);
                    eventRunning.setVisibility(View.VISIBLE);

            }*/ else {
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date sDate = null;
                    try {
                        sDate = dateFormat.parse(s_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (id != null) {
                        if (userId.equals(id)) {
                            //dateFormat.parse(holder.rDate.getText().toString())<System.currentTimeMillis()
                            if (System.currentTimeMillis() < sDate.getTime()) {
                                joinBtn.setVisibility(View.GONE);
                                cancel_joinBtn.setVisibility(View.VISIBLE);
                                relative16.setVisibility(View.VISIBLE);

                            } else if (System.currentTimeMillis() == sDate.getTime()) {
                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
                                Date sTime = null;
                                try {
                                    sTime = dateFormat.parse(time);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (System.currentTimeMillis() <= sTime.getTime()) {
                                    joinBtn.setVisibility(View.GONE);
                                    cancel_joinBtn.setVisibility(View.VISIBLE);


                                } else {
                                    eventRunning.setVisibility(View.VISIBLE);
                                    cancel_joinBtn.setVisibility(View.GONE);
                                    joinBtn.setVisibility(View.GONE);

                                }
                                relative16.setVisibility(View.VISIBLE);

                            } else if (System.currentTimeMillis() > sDate.getTime()) {
                                eventRunning.setVisibility(View.VISIBLE);
                                cancel_joinBtn.setVisibility(View.GONE);
                                joinBtn.setVisibility(View.GONE);
                                relative16.setVisibility(View.VISIBLE);
                                lateForJoin.setVisibility(View.GONE);
                                relative15.setVisibility(View.GONE);

                            }
                            break;
                        } else {
                            if (System.currentTimeMillis() < sDate.getTime()) {
                                joinBtn.setVisibility(View.VISIBLE);


                            } else if (System.currentTimeMillis() >= sDate.getTime()) {
                                lateForJoin.setVisibility(View.VISIBLE);
                                eventRunning.setVisibility(View.VISIBLE);
                                relative15.setVisibility(View.GONE);


                            }

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void setUserActivity() {
        DatabaseReference userActivityRef = databaseReference
                .child("userActivities")
                .child(userId)
                .child("events");
        String id = userActivityRef.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("eventID", event_Id);
        userActivityRef.child(id).setValue(hashMap);
    }


    private void getData() {
        event_Id = getIntent().getStringExtra("event_id");
        publisher_id = getIntent().getStringExtra("member_id");
        place = getIntent().getStringExtra("event_place");

        //for update details
        DatabaseReference updateref = databaseReference.child("event").child(event_Id);
        updateref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    place1 = event.getPlace();
                    p_date = event.getPublishDate();
                    s_date = event.getStartDate();
                    r_date = event.getReturnDate();
                    time = event.getTime();
                    m_place = event.getMeetPlace();
                    g_name = event.getGroupName();
                    description = event.getDescription();
                    e_cost = event.getCost();
                    attend_member_count = event.getJoinMemberCount();
                    latForMeetingPlace = event.getLatForMeetingPlace();
                    lonForMeetingPlace = event.getLonForMeetingPlace();
                    guideID = event.getGuideID();
                    event_place.setText(place);
                    publish_date.setText(p_date);
                    event_date.setText(s_date);
                    return_date.setText(r_date);
                    event_time.setText(time);
                    meeting_place.setText(m_place);
                    group_name.setText(g_name);
                    event_description.setText(description);
                    event_cost.setText(e_cost);
                    counter = String.valueOf(attend_member_count);
                    event_attending_member.setText(counter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //for user info
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("profile").child(publisher_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    member_name = (String) dataSnapshot.child("name").getValue();
                    member_image = (String) dataSnapshot.child("image").getValue();
                    member_phone = (String) dataSnapshot.child("phone").getValue();
                    member_email = (String) dataSnapshot.child("email").getValue();
                    member_gender = (String) dataSnapshot.child("sex").getValue();
                    publisher_name.setText(member_name);
                    if (!member_image.isEmpty() || !member_image.matches("")) {
                        try {

                            GlideApp.with(EventDetails.this)
                                    .load(member_image)
                                    .fitCenter()
                                    .into(event_publisher_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (member_gender.matches("male")) {
                            GlideApp.with(EventDetails.this)
                                    .load(getImageFromDrawable("man"))
                                    .centerInside()
                                    .into(event_publisher_image);
                        } else if (member_gender.matches("female")) {
                            GlideApp.with(EventDetails.this)
                                    .load(getImageFromDrawable("woman"))
                                    .centerInside()
                                    .into(event_publisher_image);
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
        if (value != null) {
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
        viewMember = findViewById(R.id.viewTV);
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
        locationAdapter = new EventLocationListAdapter(locationLists, getApplicationContext());
        locationRecycleView = findViewById(R.id.eventLocationList_recyclerview);
        locationRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        locationRecycleView.setHasFixedSize(true);
        locationRecycleView.setAdapter(locationAdapter);
        imageSlider = findViewById(R.id.sliderFromEventDetails);
        profileDialog = new Dialog(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        relative16 = findViewById(R.id.relative16);
        relative15 = findViewById(R.id.relative15);
        viewComment = findViewById(R.id.viewComment);
        commentCountTV = findViewById(R.id.commentCount);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        sendSound = MediaPlayer.create(this, R.raw.comment_send);
        receiveSound = MediaPlayer.create(this, R.raw.appointed);
        eventRunning = findViewById(R.id.eventRunning);
        lateForJoin = findViewById(R.id.lateForJoin);
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
        if (checkConnection()) {
            Intent i = new Intent(viewMember.getContext(), LocationImage.class)
                    .putExtra("slide", slider.getBundle().getString("extra"))
                    .putExtra("location", place.toLowerCase())
                    .putStringArrayListExtra("willVisit", (ArrayList<String>) locationWillBeVisit);
            startActivityForResult(i, 1);
        } else {
            startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
        }
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

    public void ShowProfilePopup(View v) {
        TextView namePopUp, phonePopUp, emailPopUp;
        ImageView pic;
        LinearLayout buttonLayout;
        CircleImageView close;
        profileDialog.setContentView(R.layout.profile_popup);
        close = profileDialog.findViewById(R.id.close);
        namePopUp = profileDialog.findViewById(R.id.namePopUp);
        phonePopUp = profileDialog.findViewById(R.id.phonePopUp);
        emailPopUp = profileDialog.findViewById(R.id.emailPopUp);
        pic = profileDialog.findViewById(R.id.pic);
        buttonLayout = profileDialog.findViewById(R.id.buttonLayout);
        buttonLayout.setVisibility(View.GONE);

        GlideApp.with(profileDialog.getContext())
                .load(getImageFromDrawable("ic_close"))
                .centerCrop()
                .into(close);

        namePopUp.setText(member_name);
        phonePopUp.setText(member_phone);
        emailPopUp.setText(member_email);
        if (!member_image.matches("")) {
            GlideApp.with(profileDialog.getContext())
                    .load(member_image)
                    .fitCenter()
                    .into(pic);
        } else {
            if (member_gender.matches("male")) {
                GlideApp.with(profileDialog.getContext())
                        .load(getImageFromDrawable("man"))
                        .fitCenter()
                        .into(pic);
            } else if (member_gender.matches("female")) {
                GlideApp.with(profileDialog.getContext())
                        .load(getImageFromDrawable("woman"))
                        .fitCenter()
                        .into(pic);
            }
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialog.dismiss();
            }
        });
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    public boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            if (snackbar != null) {
                snackbar.dismiss();
//                Intent i = new Intent(EventDetails.this, EventDetails.class);
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(i);
//                overridePendingTransition(0, 0);
            }
        } else {
            message = "No internet! Please connect to network.";
            snackbar(message);
            //unregisterReceiver(connectivityReceiver);
            //startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
        }


    }

    private void snackbar(String text) {
        snackbar = Snackbar
                .make(findViewById(R.id.relative1), text, Snackbar.LENGTH_INDEFINITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(connectivityReceiver, intentFilter);
        Connection.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (connectivityReceiver != null)
                unregisterReceiver(connectivityReceiver);

        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (connectivityReceiver != null)
                unregisterReceiver(connectivityReceiver);

        } catch (Exception e) {
        }
    }

    public int getImageFromDrawable(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());

        return drawableResourceId;
    }
}
