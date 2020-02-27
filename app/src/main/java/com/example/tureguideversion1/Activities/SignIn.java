package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

public class SignIn extends AppCompatActivity {

    private EditText nameET,passEt;
    private Button singin;
    private TextView signupTv,txt1;
    private String email,password;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    Animation topAnim,bottomAnim,leftAnim,rightAnim,ball1Anim,ball2Anim,ball3Anim,edittext_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        singin=findViewById(R.id.signin_BTN);
        nameET = findViewById(R.id.name_ET);
        passEt = findViewById(R.id.password_ET);
        signupTv = findViewById(R.id.signupTv);
        auth = FirebaseAuth.getInstance();
        txt1 = findViewById(R.id.txt1);

        animation();


        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,SignUp.class));
            }
        });


        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = nameET.getText().toString();
                password = passEt.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignIn.this, "Please enter email Id!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(SignIn.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                }
                else if (passEt.length()<6){
                    Toast.makeText(SignIn.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                }
                else{
                    signinuser(email,password);
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
        nameET.setAnimation(edittext_anim);
        passEt.setAnimation(edittext_anim);
        singin.setAnimation(bottomAnim);

    }

    private void signinuser(final String email, final String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignIn.this, "Sign in successfull!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignIn.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
