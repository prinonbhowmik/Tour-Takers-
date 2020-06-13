package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.ChatAdapter;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsBox extends AppCompatActivity {
    private String senderID, currentEventId, senderName, senderImage, senderSex;
    private EditText commentET;
    private ImageButton sendMessage;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView chatRecyclerView;
    private List<Chat> mChat;
    private ChatAdapter chatAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_box);
        init();
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("eventId");
        DatabaseReference sendr = databaseReference.child("profile").child(senderID);
        sendr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
                senderName = values.get("name").toString();
                senderImage = values.get("image").toString();
                senderSex = values.get("sex").toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = commentET.getText().toString();
                if (mess.trim().length() != 0) {
                    setSendMessage(mess, senderID, senderName, senderImage, senderSex);
                } else {
                    Toast.makeText(CommentsBox.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                commentET.setText(null);
            }
        });

        Profile profile = new Profile();
        Chat chat = new Chat();
        readMessage();
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
        chatAdapter = new ChatAdapter(getApplicationContext(), mChat);
        chatRecyclerView.setAdapter(chatAdapter);
        //chatRecyclerView.setHasFixedSize(true);
    }

    void setSendMessage(String message, String senderID, String senderName, String senderImage, String senderSex) {

        DatabaseReference ref = databaseReference.child("eventComments").child(currentEventId);
        String id = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("senderID", senderID);
        hashMap.put("senderName", senderName);
        hashMap.put("senderImage", senderImage);
        hashMap.put("senderSex", senderSex);
        hashMap.put("ID", id);
        ref.child(id).setValue(hashMap);
    }

    private void readMessage() {
        mChat = new ArrayList<>();
        DatabaseReference ref = databaseReference.child("eventComments").child(currentEventId);
        Query locations = ref.orderByKey();
        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mChat.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                        Chat chat = childSnapshot.getValue(Chat.class);
                        mChat.add(chat);
                        chatAdapter = new ChatAdapter(CommentsBox.this, mChat);
                        chatRecyclerView.setAdapter(chatAdapter);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}