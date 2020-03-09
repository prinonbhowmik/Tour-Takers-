package com.example.tureguideversion1.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.tureguideversion1.R;

public class EditUserProfile extends AppCompatActivity {

    private EditText nameEdit,emailEdit,phoneEdit,passEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);

        Intent intent = getIntent();

        String name  = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        nameEdit.setText(name);
        emailEdit.setText(email);
        phoneEdit.setText(phone);

    }
}
