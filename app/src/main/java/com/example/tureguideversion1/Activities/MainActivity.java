package com.example.tureguideversion1.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.tureguideversion1.Fragments.EventFragment;
import com.example.tureguideversion1.Fragments.GuideFragment;
import com.example.tureguideversion1.Fragments.MapFragment;
import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.Fragments.WeatherFragment;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.LocationSelection_bottomSheet;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.R;
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

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener, LocationSelection_bottomSheet.BottomSheetListener {

    boolean doubleBackToExitPressedOnce = false;
    private ImageView nav_icon;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toast toast = null;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String userId, name, email, image, phone;
    private Uri imageUri;
    private ImageView circularImageView;
    private TextView UserName, userEmail;
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
        if (checkConnection()) {
            storageReference = FirebaseStorage.getInstance().getReference();
            if (savedInstanceState == null) {
                FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
                tour.replace(R.id.fragment_container, new TourFragment());
                tour.commit();
                navigationView.getMenu().getItem(0).setChecked(true);
            }

            userId = auth.getUid();

            DatabaseReference showref = reference.child(userId);

            showref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Profile profile = dataSnapshot.getValue(Profile.class);

                    try {
                        name = profile.getName();
                        email = profile.getEmail();
                        image = profile.getImage();

                        UserName.setText(name);
                        userEmail.setText(email);
                        if (!image.isEmpty()) {
                            try {
                                GlideApp.with(MainActivity.this)
                                        .load(image)
                                        .fitCenter()
                                        .into(circularImageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(), "Can't load profile image!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(),"Data has changed!",Toasty.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, SignIn.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    //Toast.makeText(getApplicationContext(),image,Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            circularImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkConnection()) {
                        // Check if we're running on Android 5.0 or higher
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(circularImageView, "imageTransition");
                            pairs[1] = new Pair<View, String>(ratingLaout, "ratingTransition");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                            startActivity(new Intent(MainActivity.this, UserProfile.class), options.toBundle());
                        } else {
                            // Swap without transition
                            startActivity(new Intent(MainActivity.this, UserProfile.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
                    }

                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
            finish();
        }
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
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.email_fromNavigation);
        ratingLaout = navigationView.getHeaderView(0).findViewById(R.id.ratingLayout);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.tour:
                if (checkConnection()) {
                    FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
                    tour.replace(R.id.fragment_container, new TourFragment());
                    tour.commit();
                    drawerLayout.closeDrawers();
                    navigationView.getMenu().getItem(0).setChecked(true);

                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
                break;

            case R.id.map:
                if (checkConnection()) {
                    FragmentTransaction map = getSupportFragmentManager().beginTransaction();
                    map.replace(R.id.fragment_container, new MapFragment());
                    map.commit();
                    drawerLayout.closeDrawers();
                    navigationView.getMenu().getItem(3).setChecked(true);

                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
                break;

            case R.id.event:
                if (checkConnection()) {
                    FragmentTransaction event = getSupportFragmentManager().beginTransaction();
                    event.replace(R.id.fragment_container, new EventFragment());
                    event.commit();
                    drawerLayout.closeDrawers();
                    navigationView.getMenu().getItem(2).setChecked(true);

                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
                break;

            case R.id.weather:
                if (checkConnection()) {
                    FragmentTransaction weather = getSupportFragmentManager().beginTransaction();
                    weather.replace(R.id.fragment_container, new WeatherFragment());
                    weather.commit();
                    drawerLayout.closeDrawers();
                    navigationView.getMenu().getItem(4).setChecked(true);
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
                break;

            case R.id.guide:
                if (checkConnection()) {
                    FragmentTransaction guide = getSupportFragmentManager().beginTransaction();
                    guide.replace(R.id.fragment_container, new GuideFragment());
                    guide.commit();
                    drawerLayout.closeDrawers();
                    navigationView.getMenu().getItem(1).setChecked(true);
                } else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.terms:
                startActivity(new Intent(MainActivity.this, Term_And_Condition.class));
                //  overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
        }

        return false;
    }

    public boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
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
//            message = "No internet! Please connect to network.";
//            snackbar(message);
            //unregisterReceiver(connectivityReceiver);
            startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
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
        //showSnack(isConnected);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (connectivityReceiver != null)
                unregisterReceiver(connectivityReceiver);

        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            toast = Toasty.custom(getApplicationContext(),
                    R.string.exit_toast,
                    ResourcesCompat.getDrawable(getResources(),
                    R.drawable.shutdown,null),
                    R.color.colorPrimary,
                    R.color.colorYellow,
                    Toasty.LENGTH_SHORT, true,
                    false);
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

    @Override
    public void selectedLocation(String location) {
        TourFragment f = (TourFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        f.receivedLocationData(location);
    }
}
