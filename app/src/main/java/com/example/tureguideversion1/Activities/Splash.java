package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.R;

public class Splash extends AppCompatActivity {

        Animation topAnim,bottomAnim,leftAnim,rightAnim,ball1Anim,ball2Anim,ball3Anim;
        private TextView tourtv,ball1,ball2,ball3;
        private ImageView logo;

        private  static int splash_time_out=2000;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            init();

            topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
            bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
            leftAnim= AnimationUtils.loadAnimation(this,R.anim.left_animation);
            rightAnim= AnimationUtils.loadAnimation(this,R.anim.right_animation);
            ball1Anim=AnimationUtils.loadAnimation(this,R.anim.ball1_animation);
            ball2Anim=AnimationUtils.loadAnimation(this,R.anim.ball2_animation);
            ball3Anim=AnimationUtils.loadAnimation(this,R.anim.ball3_animation);

            tourtv.setAnimation(bottomAnim);
            ball1.setAnimation(ball1Anim);
            ball2.setAnimation(ball2Anim);
            ball3.setAnimation(ball3Anim);
            logo.setAnimation(topAnim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getIntent().getAction() != null) {
                        if (getIntent().getAction().matches("event")) {
                            Intent myintent = new Intent(Splash.this, SignIn.class).setAction("event");
                            startActivity(myintent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }else if (getIntent().getAction().matches("weather")) {
                            Intent myintent = new Intent(Splash.this, SignIn.class);
                            myintent.setAction("weather");
                            startActivity(myintent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();
                        }else {
                            Intent myintent = new Intent(Splash.this, WelcomeScreenActivity.class);
                            startActivity(myintent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    }
//                    Intent myintent = new Intent(Splash.this, SignIn.class);
//                    myintent.putExtra("name", "Toast Test");
//                    startActivity(myintent);
//                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//                    finish();
                }
            },splash_time_out);
        }

    private void init() {

        tourtv=findViewById(R.id.tourtv);
        ball1=findViewById(R.id.ball1);
        ball2=findViewById(R.id.ball2);
        logo=findViewById(R.id.logoIV);
        ball3=findViewById(R.id.ball3);
    }

}
