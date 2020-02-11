package com.example.tureguideversion1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

        Animation topAnim,bottomAnim,leftAnim,rightAnim,ball1Anim,ball2Anim,ball3Anim;
        private TextView tourtv,ball1,ball2;
        private ImageView logo;

        private  static int splash_time_out=3500;
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

            tourtv.setAnimation(bottomAnim);
            ball1.setAnimation(ball1Anim);
            ball2.setAnimation(ball2Anim);
            logo.setAnimation(topAnim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent myintent=new Intent(Splash.this,AccessPhoneNumber.class);
                    startActivity(myintent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
            },splash_time_out);
        }

    private void init() {

        tourtv=findViewById(R.id.tourtv);
        ball1=findViewById(R.id.ball1);
        ball2=findViewById(R.id.ball2);
        logo=findViewById(R.id.logoIV);
    }

}
