package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private EditText emailEt,nameEt,phoneNoEt,passwordEt,addressEt;
    private Button signupBtn;
    private ImageButton backBtn;
    private TextView txt1;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String email,name,phone,password,address;
    Animation topAnim,bottomAnim,leftAnim,rightAnim,ball1Anim,ball2Anim,ball3Anim,edittext_anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txt1 = findViewById(R.id.txt1);
        signupBtn = findViewById(R.id.signup_BTN);
        animation();
        init();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this,SignIn.class));
            }
        });


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEt.getText().toString();
                name = nameEt.getText().toString();
                phone = phoneNoEt.getText().toString();
                password = passwordEt.getText().toString();
                address = addressEt.getText().toString();


                if (TextUtils.isEmpty(email)){
                    emailEt.setError("Enter valid email");
                }else if (TextUtils.isEmpty(name)){
                    nameEt.setError("Enter User name");
                }else if (TextUtils.isEmpty(phone)){
                    phoneNoEt.setError("Enter phone No.");
                }
                else if (TextUtils.isEmpty(password)){
                    passwordEt.setError("Enter Password!");
                }
                else if (TextUtils.isEmpty(address)){
                    addressEt.setError("Address is required!");
                }
                else {
                    signup(email,name,phone,password,address);
                }
            }
        });

    }

    private void animation() {

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        leftAnim= AnimationUtils.loadAnimation(this,R.anim.left_animation);
        rightAnim= AnimationUtils.loadAnimation(this,R.anim.right_animation);
        ball1Anim=AnimationUtils.loadAnimation(this,R.anim.ball1_animation);
        ball2Anim=AnimationUtils.loadAnimation(this,R.anim.ball2_animation);
        ball3Anim=AnimationUtils.loadAnimation(this,R.anim.ball3_animation);
        edittext_anim=AnimationUtils.loadAnimation(this,R.anim.edittext_anim);

        txt1.setAnimation(topAnim);
        signupBtn.setAnimation(bottomAnim);

    }

    private void signup(final String email, final String name, final String phone, final String password, final String address) {

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    String userId = auth.getCurrentUser().getUid();
                    DatabaseReference dataref = reference.child("profile").child(userId);

                    HashMap<String,Object> userInfo = new HashMap<>();

                    userInfo.put("name",name);
                    userInfo.put("email",email);
                    userInfo.put("phone",phone);
                    userInfo.put("password",password);
                    userInfo.put("Id",userId);
                    userInfo.put("address",address);

                    dataref.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUp.this, "Successfully Sign Up", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this,SignIn.class));
                                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void init() {

        emailEt = findViewById(R.id.email_ET);
        nameEt = findViewById(R.id.name_ET);
        phoneNoEt = findViewById(R.id.phoneEt);
        passwordEt = findViewById(R.id.password_ET);
        addressEt = findViewById(R.id.address_ET);
        signupBtn = findViewById(R.id.signup_BTN);
        backBtn = findViewById(R.id.backBtn);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }

}
