package com.example.tureguideversion1.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.example.tureguideversion1.R;

public class CreateEvent extends AppCompatActivity {

    private View rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        init();

        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }



    }

    private void init() {

        rootLayout=findViewById(R.id.rootLayout);
    }


    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() -getDips(48);
        int cy = rootLayout.getHeight() -getDips(48);

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private int getDips(int dps) {
        Resources resources=getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        int cx = rootLayout.getWidth()-getDips(48);
        int cy = rootLayout.getBottom()-getDips(48);
        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        circularReveal.setDuration(600);
        circularReveal.start();
    }
}
