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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatBox extends AppCompatActivity {
    private String userId, currentEventId;
    private EditText commentET;
    private ImageButton sendMessage;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView chatRecyclerView;
    private List<Chat> mChat;
    private List<Profile> mProfile;
    private ChatAdapter chatAdapter;
    ArrayList<String> messageID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        init();
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("eventId");
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = commentET.getText().toString();
                if (!mess.equals("")) {
                    setSendMessage(mess, userId);
                } else {
                    Toast.makeText(ChatBox.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                commentET.setText(null);
            }
        });

        Profile profile = new Profile();
        chatRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        readMessage();
    }

    private void init() {
        commentET = findViewById(R.id.commentET);
        sendMessage = findViewById(R.id.sendMessage);
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatRecyclerView = findViewById(R.id.chatRecycler);

    }

    void setSendMessage(String message, String sender) {

        DatabaseReference ref = databaseReference.child("chat").child(currentEventId);
        String id = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("sender", sender);
        hashMap.put("id", id);
        ref.child(id).setValue(hashMap);
    }

    private void readMessage() {
        mChat = new ArrayList<>();
        mProfile = new ArrayList<>();
        DatabaseReference ref = databaseReference.child("chat").child(currentEventId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                mProfile.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    String senderid = chat.getSender();
                    DatabaseReference sendr = databaseReference.child("profile").child(senderid);
                    sendr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            java.util.Map<String, Object> values = (java.util.Map<String, Object>) dataSnapshot.getValue();
                            Profile profile = new Profile((String) values.get("name"), (String) values.get("image"), (String) values.get("sex"));
                            //Profile profile=snapshot1.getValue(Profile.class);

                            mProfile.add(profile);
                            mChat.add(chat);
                            chatAdapter = new ChatAdapter(ChatBox.this, mChat, mProfile);
                            chatRecyclerView.setAdapter(chatAdapter);
                            chatAdapter.notifyDataSetChanged();
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