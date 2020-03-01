package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;

public class SignIn extends AppCompatActivity {

    private EditText nameET;
    private Button singin;
    private TextView txt1;
    private String email;
    private ImageView logo;
    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth auth;
    Animation topAnim,bottomAnim,leftAnim,rightAnim,ball1Anim,ball2Anim,ball3Anim,edittext_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        singin=findViewById(R.id.continue_BTN);
        nameET = findViewById(R.id.name_ET);
        auth = FirebaseAuth.getInstance();
        txt1 = findViewById(R.id.txt1);
        logo = findViewById(R.id.logoS);
        final long[] mLastClickTime = {0};
        animation();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            nameET.setText(intent.getExtras().getString("email"));
        }
        if (user != null && user.isEmailVerified()) {
            // User is signed in
            intent = new Intent(SignIn.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            startActivity(intent);
        }

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = nameET.getText().toString();



                if (SystemClock.elapsedRealtime() - mLastClickTime[0] < 2000) {
                    return;
                }
                mLastClickTime[0] = SystemClock.elapsedRealtime();
                if (view == singin) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        checkmail();
                    }else{
                        Toast.makeText(getApplicationContext(),"Invalid email address!",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

    private void checkmail(){
        email = nameET.getText().toString();
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        if(!check){
                            //Toast.makeText(getApplicationContext(),"Email not found",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this,SignUp.class).putExtra("email",email));
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }else {
                            //Toast.makeText(getApplicationContext(),"Email found",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this,SignInGrantAccess.class).putExtra("email",email));
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
        nameET.setAnimation(edittext_anim);
        singin.setAnimation(bottomAnim);

    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
