package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.ReplyAdapter;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Reply;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class ReplyBox extends AppCompatActivity {

    private ImageView backToComment;
    private String commentId, eventId, commentId2, eventId2, senderID, senderName, senderImage, senderSex;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_box);
        init();

        Intent intent2 = getIntent();
        //eventId=intent2.getStringExtra("eventID");
        //commentId=intent2.getStringExtra("commentID");

        commentId = "-MAcyZz5xl5T4PfLNNWs";
        eventId = "-MATDe7YnxugIMJCuSHk";
        readCommentData();
        readReplyMessage();
        backToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DatabaseReference sender = databaseReference.child("profile").child(senderID);
        sender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                java.util.Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
                senderName = values.get("name").toString();
                senderImage = values.get("image").toString();
                senderSex = values.get("sex").toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        ref.child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendSound.start();
            }
        });
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}