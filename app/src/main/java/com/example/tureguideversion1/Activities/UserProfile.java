package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    private TextView profilename, profileemail, profilephoneno, profileaddress;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String userId, name, email, phone, address, image;
    private FloatingActionButton editBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        init();


        userId = auth.getUid();

        DatabaseReference showref = reference.child(userId);

        showref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Profile profile = dataSnapshot.getValue(Profile.class);

                progressBar.setVisibility(View.GONE);

                name = profile.getName();
                email = profile.getEmail();
                phone = profile.getPhone();
                address = profile.getAddress();
                image = profile.getImage();

                profilename.setText(name);
                profileemail.setText(email);
                profilephoneno.setText(phone);
                profileaddress.setText(address);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void init() {

        profilename = findViewById(R.id.profileusername);
        profileemail = findViewById(R.id.profileemail);
        profilephoneno = findViewById(R.id.profilephoneNo);
        profileaddress = findViewById(R.id.profileaddress);
        progressBar = findViewById(R.id.progressBar);
        editBtn = findViewById(R.id.editBtn);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("profile");


    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}
