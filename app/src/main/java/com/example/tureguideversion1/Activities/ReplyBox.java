package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.ReplyAdapter;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Reply;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ReplyBox extends AppCompatActivity {
    public static final String TAG = "ReplyBox";
    private ImageView backToComment;
    private String commentId, eventId, eventId2, senderID, senderName, senderImage, senderSex;
    private TextView senderName1, showMessage1, commentTimeTV1;
    private CircleImageView comment_profileImage1;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageButton sendReply;
    private EditText replyET;
    private MediaPlayer sendSound, receiveSound;
    private RecyclerView replyRecyclerView;
    private List<Reply> mReply;
    private ReplyAdapter replyAdapter;
    private List<String> notificationMemberList;
    private APIService apiService;
    private boolean notify = false;
    private int e = 1, p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_box);
        init();
        Intent intent2 = getIntent();
        eventId = intent2.getStringExtra("eventId");
        commentId = intent2.getStringExtra("commentId");
        readCommentData();
        readReplyMessage();
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
        memberListForNotification(new ReplyBox.memberListForNotificationCallback() {
            @Override
            public void onCallback(List<String> memberList) {
                notificationMemberList = new ArrayList<>();
                notificationMemberList = memberList;
                //Log.d(TAG, "onCallback: "+notificationMemberList);
            }
        });
        backToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        DatabaseReference sender = databaseReference.child("profile").child(senderID);
//        sender.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                java.util.Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
//                senderName = values.get("name").toString();
//                senderImage = values.get("image").toString();
//                senderSex = values.get("sex").toString();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String reply = replyET.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                String commentTime = simpleDateFormat.format(calendar.getTime());
                if (reply.trim().length() != 0) {
                    SendReply(reply, senderID, senderName, senderImage, senderSex, commentTime);
                }
                replyET.setText(null);
            }
        });
    }

    private void SendReply(String reply, String senderID, String senderName, String senderImage, String senderSex, String commentTime) {

        DatabaseReference ref = databaseReference.child("eventCommentsReply").child(eventId).child(commentId);
        String id = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", reply);
        hashMap.put("senderID", senderID);
        hashMap.put("senderName", senderName);
        hashMap.put("senderImage", senderImage);
        hashMap.put("senderSex", senderSex);
        hashMap.put("ID", id);
        hashMap.put("replyTime", commentTime);
        hashMap.put("eventID", eventId);
        hashMap.put("commentID", commentId);
        ref.child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendSound.start();
            }
        });
        DatabaseReference cRef = databaseReference.child("eventComments").child(eventId).child(commentId);
        cRef.child("hasReply").setValue("yes");
        DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventId);
        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        list.add(childSnapshot.getKey());
                        //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                    }
                    if (!list.contains(auth.getUid())) {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                updateToken(instanceIdResult.getToken());
                                commentNotification();
                            }
                        });
                    } else {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                updateToken(instanceIdResult.getToken());
                            }
                        });
                    }
                } else {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            updateToken(instanceIdResult.getToken());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (notify) {
            sendNotifiaction(eventId, senderName, "also replied");
        }
        notify = false;
        e = 1;
    }

    private void commentNotification() {

        DatabaseReference ref = databaseReference.child("notificationStatus").child("eventCommentNotifiaction").child(eventId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ID", auth.getUid());
        hashMap.put("status", "enabled");
        ref.child(auth.getUid()).setValue(hashMap);

    }

    private void updateToken(String token) {
        DatabaseReference ref = databaseReference.child("eventCommentsTokens");
        Token token1 = new Token(token);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", auth.getUid());
        hashMap.put("token", token1.getToken());
        ref.child(eventId).child(auth.getUid()).setValue(hashMap);
    }

    private void memberListForNotification(ReplyBox.memberListForNotificationCallback memberListForNotificationCallback) {
        ArrayList<String> membersIDEnableList = new ArrayList<>();
        DatabaseReference notificationEnabledMemberID = FirebaseDatabase.getInstance().getReference()
                .child("notificationStatus")
                .child("eventCommentNotifiaction")
                .child(eventId);
        notificationEnabledMemberID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map map = (Map) snapshot.getValue();
                        if (!auth.getUid().matches((String) map.get("ID"))) {
                            if (map.get("status").equals("enabled")) {
                                if (!membersIDEnableList.contains(snapshot.getKey())) {
                                    membersIDEnableList.add(snapshot.getKey());
                                }
                            } else if (map.get("status").equals("disabled")) {
                                membersIDEnableList.remove(snapshot.getKey());
                            }
                        }
                    }
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child("eventComments")
                            .child(eventId)
                            .child(commentId);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ArrayList<String> membersIDForNotification = new ArrayList<>();
                                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                if (!auth.getUid().matches((String) map.get("senderID"))) {
                                    if (membersIDEnableList.contains(map.get("senderID"))) {
                                        if (!membersIDForNotification.contains(map.get("senderID"))) {
                                            membersIDForNotification.add((String) map.get("senderID"));
                                        }
                                    }
                                }
                                DatabaseReference membersToken = FirebaseDatabase.getInstance().getReference()
                                        .child("eventCommentsReply")
                                        .child(eventId)
                                        .child(commentId);
                                membersToken.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Map map = (Map) snapshot.getValue();
                                                if (!auth.getUid().matches((String) map.get("senderID"))) {
                                                    if (membersIDEnableList.contains(map.get("senderID"))) {
                                                        if (!membersIDForNotification.contains(map.get("senderID"))) {
                                                            membersIDForNotification.add((String) map.get("senderID"));
                                                        }
                                                    }
                                                }
                                                //Log.d(TAG, "onDataChange: "+map.get("userID"));
                                            }
                                            memberListForNotificationCallback.onCallback(membersIDForNotification);
                                        }else {
                                            memberListForNotificationCallback.onCallback(membersIDForNotification);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface memberListForNotificationCallback {
        void onCallback(List<String> memberList);
    }

    public interface userInfoCallback {
        void onImageCallback(String url);
        void onNameCallback(String name);
        void onSexCallback(String sex);
    }

    private void getUserInfo(ReplyBox.userInfoCallback infoCallback){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
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

    private void sendNotifiaction(String eventID, final String username, final String message) {
        Log.d(TAG, "sendNotifiaction: "+notificationMemberList);
        if (notificationMemberList != null) {
            for (int i = 0; i < notificationMemberList.size(); i++) {
                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventID);
                Query query = tokens.orderByKey().equalTo(notificationMemberList.get(i));
                int finalI = i;
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getValue();
                                Token token = snapshot.getValue(Token.class);
                                Data data = new Data(eventID, R.drawable.ic_stat_ic_notification, username + " " + message, "Event", notificationMemberList.get(finalI), auth.getUid(), "ReplyBox", commentId, senderImage, senderSex);

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
                        } else {
                            Log.d(TAG, "onDataChange: not exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

    }


    private void init() {
        backToComment = findViewById(R.id.backToComment);
        senderName1 = findViewById(R.id.senderName1);
        showMessage1 = findViewById(R.id.showMessage1);
        commentTimeTV1 = findViewById(R.id.commentTimeTV1);
        comment_profileImage1 = findViewById(R.id.comment_profileImage1);
        sendReply = findViewById(R.id.sendReply);
        replyET = findViewById(R.id.replyET);
        auth = FirebaseAuth.getInstance();
        senderID = auth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sendSound = MediaPlayer.create(this, R.raw.comment_send);
        receiveSound = MediaPlayer.create(this, R.raw.appointed);
        replyRecyclerView = findViewById(R.id.replyRecycler);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    private void readCommentData() {
        DatabaseReference sender = databaseReference.child("eventComments").child(eventId).child(commentId);
        sender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("senderName").getValue().toString();
                    String message = dataSnapshot.child("message").getValue().toString();
                    String time = dataSnapshot.child("commentTime").getValue().toString();
                    String imageUrl = dataSnapshot.child("senderImage").getValue().toString();
                    String sex = dataSnapshot.child("senderSex").getValue().toString();

                    senderName1.setText(name);
                    showMessage1.setText(message);
                    if (imageUrl != null) {
                        if (!imageUrl.isEmpty()) {
                            GlideApp.with(ReplyBox.this)
                                    .load(imageUrl)
                                    .fitCenter()
                                    .into(comment_profileImage1);
                        } else {
                            if (sex.matches("male")) {
                                GlideApp.with(ReplyBox.this)
                                        .load(R.drawable.man)
                                        .centerInside()
                                        .into(comment_profileImage1);
                            } else if (sex.matches("female")) {
                                GlideApp.with(ReplyBox.this)
                                        .load(R.drawable.woman)
                                        .centerInside()
                                        .into(comment_profileImage1);
                            }
                        }
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                    Date past = null;
                    try {
                        past = format.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date now = new Date();
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
                    if (seconds < 60) {
                        commentTimeTV1.setText("now");
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            commentTimeTV1.setText("1 minute ago");
                        } else
                            commentTimeTV1.setText(minutes + " minutes ago");
                    } else if (hours < 24) {
                        if (hours == 1) {
                            commentTimeTV1.setText("1 hour ago");
                        } else
                            commentTimeTV1.setText(hours + " hours ago");
                    } else {
                        if (days == 1) {
                            commentTimeTV1.setText("1 day ago");
                        } else
                            commentTimeTV1.setText(days + " days ago");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readReplyMessage() {
        mReply = new ArrayList<>();
        DatabaseReference ref = databaseReference.child("eventCommentsReply").child(eventId).child(commentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mReply.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Reply reply = childSnapshot.getValue(Reply.class);
                        mReply.add(reply);

                        replyAdapter = new ReplyAdapter(ReplyBox.this, mReply);
                        replyRecyclerView.setAdapter(replyAdapter);
                        replyRecyclerView.setLayoutManager(new LinearLayoutManager(ReplyBox.this));
                        replyAdapter.notifyDataSetChanged();
                    }
                    //for sound effect
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