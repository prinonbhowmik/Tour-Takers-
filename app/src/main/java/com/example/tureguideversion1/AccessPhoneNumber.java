package com.example.tureguideversion1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class AccessPhoneNumber extends AppCompatActivity {

    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_phone_number);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        next=findViewById(R.id.nextbtn1);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent=new Intent(AccessPhoneNumber.this,CodeVerification.class);
                startActivity(myintent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


            }
        });


    }
}
