package com.example.tureguideversion1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class AccessPhoneNumber extends AppCompatActivity {

    private Button next;
    private EditText phone_Et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_phone_number);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        next=findViewById(R.id.nextbtn1);
        phone_Et = findViewById(R.id.phone_ET);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = phone_Et.getText().toString().trim();

                if (phone_Et.length()==11){
                    Intent myintent=new Intent(AccessPhoneNumber.this,CodeVerification.class);
                    myintent.putExtra("phone", phone);
                    startActivity(myintent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
                else{
                    phone_Et.setError("Enter Valid Phone No");
                }


            }
        });


    }
}
