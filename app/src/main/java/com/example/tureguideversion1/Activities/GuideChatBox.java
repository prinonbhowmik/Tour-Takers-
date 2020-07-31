package com.example.tureguideversion1.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.ChatAdapter;
import com.example.tureguideversion1.Adapters.GuideChatAdapter;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.GuidChat;
import com.example.tureguideversion1.MyEditText;
import com.example.tureguideversion1.Notifications.APIService;
import com.example.tureguideversion1.Notifications.Client;
import com.example.tureguideversion1.Notifications.Data;
import com.example.tureguideversion1.Notifications.Response;
import com.example.tureguideversion1.Notifications.Sender;
import com.example.tureguideversion1.Notifications.Token;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import retrofit2.Call;
import retrofit2.Callback;

public class GuideChatBox extends AppCompatActivity {
    public static final String TAG = "GuideChatBox";
    private String senderID, currentEventId, senderName, senderImage, senderSex, chatPartnerID;
    private MyEditText commentET;
    private ImageButton sendMessage;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView chatRecyclerView;
    private List<GuidChat> mChat;
    private GuideChatAdapter chatAdapter;
    private APIService apiService;
    private boolean notify = false;
    private RadioRealButtonGroup radioGroup;
    private RadioRealButton radioBTN1, radioBTN2;
    private LinearLayout radioLayout;
    private ImageView notificationIcon, closeIcon;
    private int e = 1, p = 0;
    private MediaPlayer sendSound, receiveSound;
    private TextView chatPartnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_chat_box);
        init();
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("eventId");
        chatPartnerID = intent.getStringExtra("chatPartnerID");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child(chatPartnerID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<String,Object> map = (HashMap<String, Object>) snapshot.getValue();
                    chatPartnerName.setText(map.get("name").toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readNotificationStatus();
        getUserInfo(new userInfoCallback() {
            @Override
            public void onImageCallback(String url) {
                senderImage = url;
            }

            @Override
            public void onNameCallback(String name) {
                senderName = name;
            }

            @Override
            public void onSexCallback(String sex) {
                senderSex = sex;
            }
        });

        commentET.setKeyBoardInputCallbackListener(new MyEditText.KeyBoardInputCallbackListener() {
            @Override
            public void onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
                String image = inputContentInfo.getLinkUri().toString();
                notify = true;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                String chatTime = simpleDateFormat.format(calendar.getTime());
                if (image.trim().length() != 0) {
                    setSendMessage("", image, senderID, senderName, senderImage, senderSex, chatTime);
                }
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String mess = commentET.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                String chatTime = simpleDateFormat.format(calendar.getTime());
                if (mess.trim().length() != 0) {
                    setSendMessage(mess, "",senderID, senderName, senderImage, senderSex, chatTime);
                }
                commentET.setText(null);
            }
        });

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioLayout.getVisibility() == View.GONE) {
                    radioLayout.setVisibility(View.VISIBLE);
                    radioLayout.setAlpha(0.0f);

                    // Start the animation
                    radioLayout.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(200)
                            .setListener(null);
                } else if (radioLayout.getVisibility() == View.VISIBLE) {
                    radioLayout.animate()
                            .translationY(-150)
                            .alpha(0.0f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    radioLayout.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });

        radioGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0) {
                    radioBTN1.setText("Enabled");
                    radioBTN2.setText("Disable");
                    chatNotification(true);
                    new CountDownTimer(500, 500) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            if (radioLayout.getVisibility() == View.VISIBLE) {
                                radioLayout.animate()
                                        .translationY(-150)
                                        .alpha(0.0f)
                                        .setDuration(200)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                radioLayout.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    }.start();

                } else if (position == 1) {
                    radioBTN1.setText("Enable");
                    radioBTN2.setText("Disabled");
                    chatNotification(false);
                    new CountDownTimer(500, 500) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            if (radioLayout.getVisibility() == View.VISIBLE) {
                                radioLayout.animate()
                                        .translationY(-150)
                                        .alpha(0.0f)
                                        .setDuration(200)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                radioLayout.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    }.start();
                }
            }
        });

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        readMessage();
    }

    private void readMessage() {
        DatabaseReference ref = databaseReference.child("chatWithGuide").child(currentEventId);
        Query locations = ref.orderByKey();
        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat = new ArrayList<>();
                chatAdapter = new GuideChatAdapter(GuideChatBox.this, mChat);
                chatRecyclerView.setAdapter(chatAdapter);
                chatRecyclerView.setHasFixedSize(true);
                chatRecyclerView.setItemViewCacheSize(50);
                chatRecyclerView.setDrawingCacheEnabled(true);
                chatAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    mChat.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        GuidChat chat = childSnapshot.getValue(GuidChat.class);
                        mChat.add(chat);
                        chatAdapter.notifyDataSetChanged();
                    }
                    if (p != 1) {
                        if (e != 1) {
                            receiveSound.start();
                            e = 0;
                        } else if (e == 1) {
                            e = 0;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chatNotification(Boolean status) {
        if (status) {
            DatabaseReference ref = databaseReference.child("notificationStatus").child("chatNotification").child(currentEventId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ID", auth.getUid());
            hashMap.put("status", "enabled");
            ref.child(auth.getUid()).setValue(hashMap);
        } else {
            DatabaseReference ref = databaseReference.child("notificationStatus").child("chatNotification").child(currentEventId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ID", auth.getUid());
            hashMap.put("status", "disabled");
            ref.child(auth.getUid()).setValue(hashMap);
        }
    }

    void setSendMessage(String message, String image, String senderID, String senderName, String senderImage, String senderSex, String commentTime) {

        DatabaseReference ref = databaseReference.child("chatWithGuide").child(currentEventId);
        String id = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("imageMessage", image);
        hashMap.put("senderID", senderID);
        hashMap.put("senderName", senderName);
        hashMap.put("senderImage", senderImage);
        hashMap.put("senderSex", senderSex);
        hashMap.put("ID", id);
        hashMap.put("commentTime", commentTime);
        hashMap.put("eventID", currentEventId);
        ref.child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendSound.start();
            }
        });

        if (notify) {
            if(image.trim().length() != 0){
                sendNotifiaction(currentEventId, senderName, "send a sticker", chatPartnerID);
            }else {
                sendNotifiaction(currentEventId, senderName, message, chatPartnerID);
            }
        }
        notify = false;
        e = 1;
    }

    private void sendNotifiaction(String eventID, final String username, final String message, String receiverID) {
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child(receiverID);
            tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(eventID, R.drawable.ic_stat_ic_notification, username + ": " + message, "Event", receiverID, auth.getUid(), "guideChat", senderImage, senderSex);
                        Sender sender = new Sender(data, token.getToken());
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public interface userInfoCallback {
        void onImageCallback(String url);

        void onNameCallback(String name);

        void onSexCallback(String sex);
    }

    private void getUserInfo(userInfoCallback infoCallback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    infoCallback.onImageCallback((String) map.get("image"));
                    infoCallback.onNameCallback((String) map.get("name"));
                    infoCallback.onSexCallback((String) map.get("sex"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readNotificationStatus() {
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                .child("notificationStatus")
                .child("chatNotification")
                .child(currentEventId)
                .child(auth.getUid());
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    java.util.Map map = (Map) dataSnapshot.getValue();
                    if (map.get("status").equals("enabled")) {
                        notificationIcon.setImageResource(R.drawable.ic_notification_enable);
                        radioGroup.setPosition(0);
                        radioBTN1.setText("Enabled");
                    } else if (map.get("status").equals("disabled")) {
                        notificationIcon.setImageResource(R.drawable.ic_notification_disable);
                        radioGroup.setPosition(1);
                        radioBTN2.setText("Disabled");
                    }
                } else {
                    notificationIcon.setImageResource(R.drawable.ic_notifications_paused);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        commentET = findViewById(R.id.commentET);
        sendMessage = findViewById(R.id.sendMessage);
        auth = FirebaseAuth.getInstance();
        senderID = auth.getUid();
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatRecyclerView = findViewById(R.id.chatRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new GuideChatAdapter(getApplicationContext(), mChat);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setHasFixedSize(true);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        radioGroup = findViewById(R.id.radioGroup);
        notificationIcon = findViewById(R.id.notiBTMS);
        closeIcon = findViewById(R.id.closeBTMS);
        radioLayout = findViewById(R.id.radioLayout);
        radioLayout.setTranslationY(-150);
        radioBTN1 = (RadioRealButton) findViewById(R.id.radioBTN1);
        radioBTN2 = (RadioRealButton) findViewById(R.id.radioBTN2);
        sendSound = MediaPlayer.create(this, R.raw.comment_send);
        receiveSound = MediaPlayer.create(this, R.raw.appointed);
        chatPartnerName = findViewById(R.id.chatPartnerName);
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser(auth.getUid());
        p = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentUser("none");
        p = 1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiveSound.stop();
    }

    @Override
    public void finish() {
        super.finish();
        receiveSound.stop();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}