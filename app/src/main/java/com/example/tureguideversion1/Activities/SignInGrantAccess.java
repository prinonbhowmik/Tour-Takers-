package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SignInGrantAccess extends AppCompatActivity {

    private EditText passEt;
    private Button singin;
    private String email,password;
    private TextView txt1;
    private ImageView logo;
    private FirebaseAuth auth;
    Animation topAnim,bottomAnim,leftAnim,rightAnim,ball1Anim,ball2Anim,ball3Anim,edittext_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_grant_access);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        singin=findViewById(R.id.signin_BTN);
        passEt = findViewById(R.id.password_ET);
        auth = FirebaseAuth.getInstance();
        txt1 = findViewById(R.id.txt1);
        logo = findViewById(R.id.logoG);

        animation();

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                email = intent.getExtras().getString("email");
                password = passEt.getText().toString();

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(SignInGrantAccess.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                }
                else if (passEt.length()<6){
                    Toast.makeText(SignInGrantAccess.this, "Please enter password!", Toast.LENGTH_SHORT).show();
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

        logo.setAnimation(leftAnim);
        txt1.setAnimation(topAnim);
        passEt.setAnimation(edittext_anim);
        singin.setAnimation(bottomAnim);

    }

    private void signinuser(final String email, final String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignInGrantAccess.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInGrantAccess.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    startActivity(intent);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInGrantAccess.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        super.finish();
    }


}
