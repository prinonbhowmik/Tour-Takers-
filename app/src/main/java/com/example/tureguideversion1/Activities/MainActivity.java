package com.example.tureguideversion1.Activities;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tureguideversion1.Fragments.GuideFragment;
import com.example.tureguideversion1.Fragments.MapFragment;
import com.example.tureguideversion1.Fragments.WeatherFragment;
import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skyfishjy.library.RippleBackground;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private ImageView nav_icon;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    boolean doubleBackToExitPressedOnce = false;
    private Toast toast = null;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String userId, name, email;
    private Uri image;
    private ImageView circularImageView;
    private TextView UserName, userEmail;
    private RippleBackground rippleBackground;
    private int left, right, top, bottom;
    private LinearLayout ratingLaout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        storageReference = FirebaseStorage.getInstance().getReference();
        FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
        tour.replace(R.id.fragment_container,new TourFragment());
        tour.commit();

        left = circularImageView.getPaddingLeft();
        top = circularImageView.getPaddingTop();
        right = circularImageView.getPaddingRight();
        bottom = circularImageView.getPaddingBottom();
        userId = auth.getUid();
        storageReference.child("userProfileImage/"+userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'userProfileImage/"+userId'
                image = uri;
                Glide.with(MainActivity.this)
                        .load(image)
                        .into(circularImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        DatabaseReference showref = reference.child(userId);

        showref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Profile profile = dataSnapshot.getValue(Profile.class);



                name = profile.getName();
                email = profile.getEmail();
                UserName.setText(name);
                userEmail.setText(email);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularImageView.setEnabled(false);
                circularImageView.setPadding(left+10,top+10,right+10,bottom+10);
                rippleBackground.startRippleAnimation();
                new CountDownTimer(722, 1) {

                    public void onTick(long millisUntilFinished) {
                        //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        rippleBackground.stopRippleAnimation();
                        circularImageView.setPadding(left,top,right,bottom);
                        circularImageView.setEnabled(true);
                        //drawerLayout.closeDrawers();
                        new CountDownTimer(300, 1) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                // Check if we're running on Android 5.0 or higher
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    // Apply activity transition
                                    Pair[] pairs = new Pair[4];
                                    pairs[0] = new Pair<View,String>(circularImageView,"imageTransition");
                                    pairs[1] = new Pair<View,String>(userEmail,"emailTransition");
                                    pairs[2] = new Pair<View,String>(UserName,"nameTransition");
                                    pairs[3] = new Pair<View,String>(ratingLaout,"ratingTransition");
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                                    startActivity(new Intent(MainActivity.this, UserProfile.class),options.toBundle());
                                } else {
                                    // Swap without transition
                                    startActivity(new Intent(MainActivity.this, UserProfile.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            }
                        }.start();


                    }
                }.start();


            }
        });
    }

    private void init() {
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        nav_icon = findViewById(R.id.nav_icon);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        reference = FirebaseDatabase.getInstance().getReference("profile");
        navigationView.setNavigationItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();
        nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        circularImageView = navigationView.getHeaderView(0).findViewById(R.id.navImageView);
        UserName = navigationView.getHeaderView(0).findViewById(R.id.namefromNavigation);
        rippleBackground=navigationView.getHeaderView(0).findViewById(R.id.content);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.email_fromNavigation);
        ratingLaout = navigationView.getHeaderView(0).findViewById(R.id.ratingLayout);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, UserProfile.class));
                drawerLayout.closeDrawers();
                break;

            case R.id.tour:
                FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
                tour.replace(R.id.fragment_container,new TourFragment());
                tour.commit();
                drawerLayout.closeDrawers();
                return true;

            case R.id.map:
                FragmentTransaction map = getSupportFragmentManager().beginTransaction();
                map.replace(R.id.fragment_container,new MapFragment());
                map.commit();
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;

            case R.id.weather:

                FragmentTransaction weather = getSupportFragmentManager().beginTransaction();
                weather.replace(R.id.fragment_container,new WeatherFragment());
                weather.commit();
                Toast.makeText(this, "Weather", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;

            case R.id.guide:

                FragmentTransaction guide = getSupportFragmentManager().beginTransaction();
                guide.replace(R.id.fragment_container,new GuideFragment());
                guide.commit();
                Toast.makeText(this, "Guide", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

        }

        return false;
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            //message = "Connected to Internet";
            if (snackbar != null) {
                snackbar.dismiss();

            }
        } else {
            message = "No internet! Please connect to network.";
            snackbar(message);

        }


    }

    private void snackbar(String text) {
        snackbar = Snackbar
                .make(findViewById(R.id.nav_view), text, Snackbar.LENGTH_INDEFINITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(connectivityReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*register connection status listener*/
        Connection.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            toast.setText("Press again to exit");
            toast.show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onStop() {
        toast.cancel();
        super.onStop();
    }
}
