package com.example.tureguideversion1.Activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.tureguideversion1.Adapters.Adapter;
import com.example.tureguideversion1.Model.CardView;
import com.example.tureguideversion1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LocationImage extends AppCompatActivity {

    private View rootLayout;
    ArrayList<String> location;
    private ViewPager viewPager;
    private Adapter adapter;
    List<CardView> models;
    private Integer[] colors = null;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    String slide, locationForViewPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_image);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        init();
        Intent intent = getIntent();
        slide = Objects.requireNonNull(intent.getExtras()).getString("slide");
        locationForViewPage = intent.getExtras().getString("location");
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(locationForViewPage);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectImageNInfo((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void init() {

        rootLayout = findViewById(R.id.locationImageRootLayout);

    }

    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() / 2;
        // int cy = rootLayout.getHeight() - getDips(650);
        int cy = rootLayout.getTop() + getDips(170);

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(700);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    private void collectImageNInfo(Map<String, Object> locatios) {

        location = new ArrayList<>();

        //iterate through each user, ignoring their UID
        models = new ArrayList<>();
        for (Map.Entry<String, Object> entry : locatios.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            location.add((String) singleUser.get("locationName"));

            models.add(new CardView((String) singleUser.get("image"),(String) singleUser.get("locationName"),(String) singleUser.get("description")));
        }

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);
        //Toast.makeText(getApplicationContext(),slide,Toast.LENGTH_LONG).show();
        viewPager.setCurrentItem(location.indexOf(slide),true);

        colors = new Integer[]{
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
        };

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() - 1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        int cx = rootLayout.getWidth() - getDips(205);
        //int cy = rootLayout.getBottom() - getDips(650);
        int cy = rootLayout.getTop() + getDips(170);
        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                int v = viewPager.getCurrentItem();
                Intent intent = new Intent();
                intent.putExtra("slide", v);
                setResult(RESULT_OK, intent);
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
