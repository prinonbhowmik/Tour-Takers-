package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class EditCommentActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private EditText messageET;
    private CircleImageView imageIV;
    private TextView nameTV;
    private String eventId, commentId, name, message, image;
    private Button cancelBtn, updateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comment);
        init();
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        commentId = intent.getStringExtra("commentId");
        getData();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = messageET.getText().toString().trim();
                updateComment(m);
            }
        });

        messageET.addTextChangedListener(textWatcher);


    }

    private void updateComment(final String m) {
        DatabaseReference getRef = databaseReference.child("eventComments").child(eventId).child(commentId);
        getRef.child("message").setValue(m).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.success(EditCommentActivity.this, "" + e.getMessage(), Toasty.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.success(EditCommentActivity.this, "Update Comment", Toasty.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String updateMassage = messageET.getText().toString().trim();
            if (!updateMassage.equals(message)) {
                updateBtn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private void getData() {
        DatabaseReference getRef = databaseReference.child("eventComments").child(eventId).child(commentId);
        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    name = chat.getSenderName();
                    message = chat.getMessage();
                    image = chat.getSenderImage();
                    messageET.setText(message);
                    nameTV.setText(name);
                    if (!image.isEmpty() || !image.matches("")) {
                        try {
                            GlideApp.with(EditCommentActivity.this)
                                    .load(image)
                                    .fitCenter()
                                    .into(imageIV);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(getApplicationContext(), "Can't load profile image!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String sex = chat.getSenderSex();
                        if (sex.matches("male")) {
                            try {
                                GlideApp.with(EditCommentActivity.this)
                                        .load(R.drawable.man)
                                        .centerInside()
                                        .into(imageIV);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        } else if (sex.matches("female")) {
                            try {
                                GlideApp.with(EditCommentActivity.this)
                                        .load(R.drawable.woman)
                                        .centerInside()
                                        .into(imageIV);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        messageET = findViewById(R.id.messageET);
        nameTV = findViewById(R.id.senderName);
        imageIV = findViewById(R.id.commentProfileImage);
        cancelBtn = findViewById(R.id.cancel);
        updateBtn = findViewById(R.id.update);
    }
}