package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEt;
    private ActionProcessButton enterBtn;
    private String email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEt.getText().toString();
                if (email.isEmpty()) {
                    emailEt.setError("Enter email address!");
                    emailEt.requestFocus();
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEt.setText("");
                    emailEt.setError("Enter valid email address!");
                    emailEt.requestFocus();
                }
                else{
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toasty.info(ForgotPassword.this,"Password sent to your mail",Toasty.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPassword.this,SignIn.class));
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void init() {
        emailEt = findViewById(R.id.email_ET);
        enterBtn = findViewById(R.id.enter_BTN);
        auth = FirebaseAuth.getInstance();
    }
}