package com.example.tureguideversion1.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.ChatAdapter;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Reply;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;

public class CommentsBox extends AppCompatActivity {
    public static final String TAG = "CommentsBox";
    private String senderID, currentEventId, senderName, senderImage, senderSex;
    private MyEditText commentET;
    private ImageButton sendMessage;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView chatRecyclerView;
    private List<Chat> mChat;
    private List<Reply> replyList;
    private ChatAdapter chatAdapter;
    private APIService apiService;
    private boolean notify = false;
    private RadioRealButtonGroup radioGroup;
    private RadioRealButton radioBTN1, radioBTN2;
    private LinearLayout radioLayout;
    private ImageView notificationIcon, closeIcon;
    private int e = 1, p = 0;
    private List<String> notificationMemberList;
    private MediaPlayer sendSound, receiveSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_box);
        init();
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("eventId");
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
        memberListForNotification(new memberListForNotificationCallback() {
            @Override
            public void onCallback(List<String> memberList) {
                notificationMemberList = new ArrayList<>();
                notificationMemberList = memberList;
                //Log.d(TAG, "onCallback: "+notificationMemberList);
            }
        });
//        DatabaseReference sendr = databaseReference.child("profile").child(senderID);
//        sendr.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
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
        commentET.setKeyBoardInputCallbackListener(new MyEditText.KeyBoardInputCallbackListener() {
            @Override
            public void onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
                String image = inputContentInfo.getLinkUri().toString();
                notify = true;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                String commentTime = simpleDateFormat.format(calendar.getTime());
                if (image.trim().length() != 0) {
                    setSendMessage("", image, senderID, senderName, senderImage, senderSex, commentTime);
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
                String commentTime = simpleDateFormat.format(calendar.getTime());
                if (mess.trim().length() != 0) {
                    setSendMessage(mess, "", senderID, senderName, senderImage, senderSex, commentTime);
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
                    commentNotification(true);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            updateToken(instanceIdResult.getToken());
                        }
                    });
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
                    commentNotification(false);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            updateToken(instanceIdResult.getToken());
                        }
                    });
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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(chatRecyclerView);

        readMessage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            currentEventId = data.getStringExtra("eventId");
        }
        if (requestCode == 2) {
            recreate();
        }

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    String CID = chatAdapter.getSwipePosition(viewHolder.getAdapterPosition());
                    Intent intent2 = new Intent(CommentsBox.this, ReplyBox.class);
                    intent2.putExtra("commentId", CID);
                    intent2.putExtra("eventId", currentEventId);
                    startActivityForResult(intent2, 2);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case ItemTouchHelper.LEFT:
                    String cID = chatAdapter.getSwipePosition(viewHolder.getAdapterPosition());
                    DatabaseReference deleteRef = databaseReference.child("eventComments").child(currentEventId).child(cID);
                    deleteRef.setValue(null);
                    DatabaseReference repRef = databaseReference.child("eventCommentsReply").child(currentEventId).child(cID);
                    repRef.setValue(null);
                    Toast.makeText(CommentsBox.this, "Comment Delete", Toast.LENGTH_SHORT).show();

                    break;
            }


        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightActionIcon(R.drawable.ic_reply)
                    .addSwipeRightLabel("Reply")
                    .setSwipeRightLabelTypeface(Typeface.DEFAULT_BOLD)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(CommentsBox.this, R.color.colorYellow))

                    .addSwipeLeftActionIcon(R.drawable.swipe_delete)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTypeface(Typeface.DEFAULT_BOLD)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(CommentsBox.this, R.color.red))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.9f;
        }
    };


    private void init() {
        commentET = (MyEditText) findViewById(R.id.commentET);
        sendMessage = findViewById(R.id.sendMessage);
        auth = FirebaseAuth.getInstance();
        senderID = auth.getUid();
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatRecyclerView = findViewById(R.id.chatRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(getApplicationContext(), mChat, replyList);
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
    }

    private void readNotificationStatus() {
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                .child("notificationStatus")
                .child("eventCommentNotifiaction")
                .child(currentEventId)
                .child(auth.getUid());
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map map = (Map) dataSnapshot.getValue();
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

    private void commentNotification(Boolean status) {
        if (status) {
            DatabaseReference ref = databaseReference.child("notificationStatus").child("eventCommentNotifiaction").child(currentEventId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ID", auth.getUid());
            hashMap.put("status", "enabled");
            ref.child(auth.getUid()).setValue(hashMap);
        } else {
            DatabaseReference ref = databaseReference.child("notificationStatus").child("eventCommentNotifiaction").child(currentEventId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ID", auth.getUid());
            hashMap.put("status", "disabled");
            ref.child(auth.getUid()).setValue(hashMap);
        }
    }

    void setSendMessage(String message, String image, String senderID, String senderName, String senderImage, String senderSex, String commentTime) {

        DatabaseReference ref = databaseReference.child("eventComments").child(currentEventId);
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
        hashMap.put("hasReply", "no");
        hashMap.put("replyType", "none");
        ref.child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendSound.start();
            }
        });
        DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(currentEventId);
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
                                commentNotification(true);
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
            sendNotifiaction(currentEventId, senderName, "also commented");
        }
        notify = false;
        e = 1;
    }

    private void readMessage() {
        DatabaseReference ref = databaseReference.child("eventComments").child(currentEventId);
        Query locations = ref.orderByKey();
        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat = new ArrayList<>();
                replyList = new ArrayList<>();
                chatAdapter = new ChatAdapter(CommentsBox.this, mChat, replyList);
                chatRecyclerView.setAdapter(chatAdapter);
                chatRecyclerView.setHasFixedSize(true);
                chatRecyclerView.setItemViewCacheSize(50);
                chatRecyclerView.setDrawingCacheEnabled(true);
                chatAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    mChat.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Chat chat = childSnapshot.getValue(Chat.class);
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

    private void updateToken(String token) {
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        DatabaseReference ref = databaseReference.child("eventCommentsTokens");
        Token token1 = new Token(token);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", auth.getUid());
        hashMap.put("token", token1.getToken());
        ref.child(currentEventId).child(auth.getUid()).setValue(hashMap);
    }

    private void memberListForNotification(memberListForNotificationCallback memberListForNotificationCallback) {
        ArrayList<String> membersIDEnableList = new ArrayList<>();
        DatabaseReference notificationEnabledMemberID = FirebaseDatabase.getInstance().getReference()
                .child("notificationStatus")
                .child("eventCommentNotifiaction")
                .child(currentEventId);
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
                    DatabaseReference membersToken = FirebaseDatabase.getInstance().getReference()
                            .child("eventCommentsTokens")
                            .child(currentEventId);
                    membersToken.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ArrayList<String> membersIDForNotification = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Map map = (Map) snapshot.getValue();
                                    if (!auth.getUid().matches((String) map.get("userID"))) {
                                        if (membersIDEnableList.contains(snapshot.getKey())) {
                                            if (!membersIDForNotification.contains(snapshot.getKey())) {
                                                membersIDForNotification.add((String) map.get("userID"));
                                            }
                                        }
                                    }
                                    //Log.d(TAG, "onDataChange: "+map.get("userID"));
                                }
                                //Log.d(TAG, "onDataChange: "+membersIDForNotification);
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

    public interface memberListForNotificationCallback {
        void onCallback(List<String> memberList);
    }

    public interface userInfoCallback {
        void onImageCallback(String url);
        void onNameCallback(String name);
        void onSexCallback(String sex);
    }

    private void getUserInfo(userInfoCallback infoCallback){
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
                                Data data = new Data(eventID, R.drawable.ic_stat_ic_notification, username + " " + message, "Event", notificationMemberList.get(finalI), auth.getUid(), "CommentBox",senderImage, senderSex);

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