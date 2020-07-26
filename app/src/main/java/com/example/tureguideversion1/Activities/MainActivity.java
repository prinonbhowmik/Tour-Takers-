package com.example.tureguideversion1.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.tureguideversion1.Fragments.EventFragment;
import com.example.tureguideversion1.Fragments.LoaderFragment;
import com.example.tureguideversion1.Fragments.OnGoingTour;
import com.example.tureguideversion1.Fragments.TourFragment;
import com.example.tureguideversion1.Fragments.WeatherFragment;
import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.LocationSelection_bottomSheet;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.Notifications.Token;
import com.example.tureguideversion1.R;
import com.example.tureguideversion1.Services.EventCommentsNotify;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        LocationSelection_bottomSheet.BottomSheetListener,
        TourFragment.navDrawerCheck {
    private static final String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;
    private ImageView nav_icon;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toast toast = null;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    private DatabaseReference reference, prevRef;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String userId, name, email, image, currentFragment;
    private Uri imageUri;
    private ImageView circularImageView;
    private TextView UserName, userEmail;
    private LinearLayout ratingLaout;
    private Boolean tokenUpdated = false;
    private ValueEventListener prevlistener;
    private boolean hasTour;
    private boolean hasEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        registerReceiver(connectivityReceiver, intentFilter);
        new InternetCheck(internet -> {
            if (internet) {
                if (getIntent().getAction() != null) {
                    if (getIntent().getAction().matches("event")) {
                        currentFragment = "event";
                        Bundle bundle = new Bundle();
                        bundle.putString("shortcut", "event");
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, LoaderFragment.class, bundle);
                        transaction.commit();
                        navigationView.getMenu().getItem(1).setChecked(true);
                    } else if (getIntent().getAction().matches("weather")) {
                        currentFragment = "weather";
                        Bundle bundle = new Bundle();
                        bundle.putString("shortcut", "weather");
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, LoaderFragment.class, bundle);
                        transaction.commit();
                        navigationView.getMenu().getItem(2).setChecked(true);
                    }
                } else {
                    if (savedInstanceState == null) {
                        currentFragment = "tour";
                        FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
                        tour.replace(R.id.fragment_container, new LoaderFragment());
                        tour.commit();
                        navigationView.getMenu().getItem(0).setChecked(true);
                    }
                }
                userId = auth.getUid();

                garbageCollect();

                getUserActivity(new userActivityCallback() {
                    @Override
                    public void onTourCallback(boolean tour) {
                        hasTour = tour;
                    }

                    @Override
                    public void onEventCallback(boolean event) {
                        hasEvent = event;
                    }
                });

                if (userId != null) {
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
                                if (!image.isEmpty() || !image.matches("")) {
                                    try {
                                        Glide.with(MainActivity.this)
                                                .load(image)
                                                .fitCenter()
                                                .into(circularImageView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //Toast.makeText(getApplicationContext(), "Can't load profile image!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    String sex = profile.getSex();
                                    if (sex.matches("male")) {
                                        try {
                                            Glide.with(MainActivity.this)
                                                    .load(getImageFromDrawable("man"))
                                                    .centerInside()
                                                    .into(circularImageView);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (sex.matches("female")) {
                                        try {
                                            Glide.with(MainActivity.this)
                                                    .load(getImageFromDrawable("woman"))
                                                    .centerInside()
                                                    .into(circularImageView);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Toasty.error(getApplicationContext(), "Data has changed!", Toasty.LENGTH_LONG).show();
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
                }

                circularImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new InternetCheck(internet1 -> {
                            if(internet1){
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
                            }else {
                                startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
                            }
                        });
                    }
                });
                //eventCommentsNotification();
                setNewTokens();
                //preventMultiDeviceLogin();
            } else {
                startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
                finish();
            }
        });
//        new CountDownTimer(8000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            public void onFinish() {
//                bettaryOptimization();
//            }
//        }.start();
    }

    private void garbageCollect() {
        ArrayList<String> eventList = new ArrayList<>();
        ArrayList<String> tourList = new ArrayList<>();
        DatabaseReference userEventActivityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
        DatabaseReference userTourtActivityRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        userEventActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnap: snapshot.getChildren()){
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        eventList.add((String) map.get("eventID"));
                    }
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("event");
                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot child: snapshot.getChildren()){
                                    HashMap<String, Object> map2 = (HashMap<String, Object>) child.getValue();
                                    if(map2.get("eventPublisherId").toString().matches(auth.getUid())){
                                        if(!eventList.contains(child.getKey())){
                                            DatabaseReference removeEventRef = FirebaseDatabase.getInstance().getReference()
                                                    .child("event").child(child.getKey());
                                            removeEventRef.removeValue();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userTourtActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnap: snapshot.getChildren()){
                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap.getValue();
                        tourList.add((String) map.get("tourID"));
                    }
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("tour");
                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot child: snapshot.getChildren()){
                                    HashMap<String, Object> map2 = (HashMap<String, Object>) child.getValue();
                                    if(map2.get("eventPublisherId").toString().matches(auth.getUid())){
                                        if(!tourList.contains(child.getKey())){
                                            DatabaseReference removeTourtRef = FirebaseDatabase.getInstance().getReference()
                                                    .child("tour").child(child.getKey());
                                            removeTourtRef.removeValue();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void eventCommentsNotification() {
        ComponentName componentName = new ComponentName(this, EventCommentsNotify.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(1);
        Log.d(TAG, "Job cancelled");
    }

    private void init() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
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
                hideKeyboardFrom(getApplicationContext());
            }
        });
        circularImageView = navigationView.getHeaderView(0).findViewById(R.id.navImageView);
        UserName = navigationView.getHeaderView(0).findViewById(R.id.namefromNavigation);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.email_fromNavigation);
        ratingLaout = navigationView.getHeaderView(0).findViewById(R.id.ratingLayout);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentManager fm = getSupportFragmentManager();
        switch (menuItem.getItemId()) {

            case R.id.tour:
                new InternetCheck(internet -> {
                    if (internet) {
                        if(hasTour || hasEvent){
                            currentFragment = "tour";
                            FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
                            tour.replace(R.id.fragment_container, new OnGoingTour());
                            tour.commit();
                            drawerLayout.closeDrawers();
                            navigationView.getMenu().getItem(0).setChecked(true);
                            navigationView.getMenu().getItem(1).setChecked(false);
                            navigationView.getMenu().getItem(2).setChecked(false);
                        }else {
                            currentFragment = "tour";
                            FragmentTransaction tour = getSupportFragmentManager().beginTransaction();
                            tour.replace(R.id.fragment_container, new TourFragment());
                            tour.commit();
                            drawerLayout.closeDrawers();
                            navigationView.getMenu().getItem(0).setChecked(true);
                            navigationView.getMenu().getItem(1).setChecked(false);
                            navigationView.getMenu().getItem(2).setChecked(false);
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                    }
                });
                break;

            case R.id.event:
                new InternetCheck(internet -> {
                    if (internet) {
                        currentFragment = "event";
                        FragmentTransaction event = getSupportFragmentManager().beginTransaction();
                        event.replace(R.id.fragment_container, new EventFragment());
                        event.commit();
                        drawerLayout.closeDrawers();
                        navigationView.getMenu().getItem(1).setChecked(true);
                        navigationView.getMenu().getItem(0).setChecked(false);
                        navigationView.getMenu().getItem(2).setChecked(false);

                    } else {
                        startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                    }
                });
                break;

            case R.id.weather:
                new InternetCheck(internet -> {
                    if (internet) {
                        currentFragment = "weather";
                        FragmentTransaction weather = getSupportFragmentManager().beginTransaction();
                        weather.replace(R.id.fragment_container, new WeatherFragment());
                        weather.commit();
                        drawerLayout.closeDrawers();
                        navigationView.getMenu().getItem(2).setChecked(true);
                        navigationView.getMenu().getItem(1).setChecked(false);
                        navigationView.getMenu().getItem(0).setChecked(false);
                    } else {
                        startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                    }
                });
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.terms:
                startActivity(new Intent(MainActivity.this, TermAndCondition.class));
                //  overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
        }

        return false;
    }

//    public boolean checkConnection() {
//        boolean isConnected = ConnectivityReceiver.isConnected();
//        return isConnected;
//    }

    private void bettaryOptimization() {
        if (checkBatteryOptimized()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setMessage("Please ignore Battery Optimization for working this app properly.")
                    .setCancelable(true)
                    .setTitle("Warning...")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startBatteryOptimizeDialog();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * return false if in settings "Not optimized" and true if "Optimizing battery use"
     */
    private boolean checkBatteryOptimized() {
        final PowerManager pwrm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return !pwrm.isIgnoringBatteryOptimizations(getBaseContext().getPackageName());
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @SuppressLint("BatteryLife")
    private void startBatteryOptimizeDialog() {
        try {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivity(intent);
            }


        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setUserStatus(Boolean status) {
        if (status) {
            reference.child(auth.getUid()).child("status").setValue("active");
            //reference.child(auth.getUid()).child("prevention").setValue("true");
        } else {
            reference.child(auth.getUid()).child("status").setValue("inactive");
        }
    }

    private void preventMultiDeviceLogin() {
        prevRef = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getUid()).child("token");
        prevlistener = prevRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = (String) dataSnapshot.getValue();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            if (!token.matches(instanceIdResult.getToken())) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivity.this, SignIn.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent.putExtra("prevention", "true"));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                finish();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNewTokens() {
        ArrayList<String> eventIDs = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String token = preferences.getString("newToken", "none");
        //Log.d(TAG, "newToken: "+token);
        if (!token.equals("none")) {
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens");
            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            eventIDs.add(snapshot.getKey());
                            //Log.d(TAG, "events: "+snapshot.getKey());
                        }
                        for (int i = 0; i < eventIDs.size(); i++) {
                            DatabaseReference IDRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventIDs.get(i));
                            Query query = IDRef.orderByKey().equalTo(auth.getUid());
                            int finalI = i;
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        updateToken(token, eventIDs.get(finalI));
                                        //Log.d(TAG, "onDataChange: " + eventIDs.get(finalI));
                                        query.removeEventListener(this);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        preferences.edit().clear().apply();
                        eventRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens");
            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            eventIDs.add(snapshot.getKey());
                            //Log.d(TAG, "events: "+snapshot.getKey());
                        }
                        for (int i = 0; i < eventIDs.size(); i++) {
                            if (auth.getUid() != null) {
                                DatabaseReference IDRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventIDs.get(i));
                                Query query = IDRef.orderByKey().equalTo(auth.getUid());
                                int finalI = i;
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                @Override
                                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                                    updateToken(instanceIdResult.getToken(), eventIDs.get(finalI));
                                                }
                                            });
                                            query.removeEventListener(this);
                                            //Log.d(TAG, "onDataChange: " + eventIDs.get(finalI));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        preferences.edit().clear().apply();
                        eventRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateToken(String token, String eventID) {
        if (auth.getUid() != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens");
            Token token1 = new Token(token);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userID", auth.getUid());
            hashMap.put("token", token1.getToken());
            ref.child(eventID).child(auth.getUid()).setValue(hashMap);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getUid());
            userRef.child("token").setValue(token1.getToken()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //preventMultiDeviceLogin();
                }
            });
        }
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            //message = "Connected to Internet";
            if (snackbar != null) {
                snackbar.dismiss();
                if (currentFragment.matches("tour") || currentFragment == null) {

                } else {
                    ActivityManager mgr = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                    if (mgr != null) {
                        List<ActivityManager.AppTask> tasks = mgr.getAppTasks();
                        String className = "";
                        if (tasks != null && !tasks.isEmpty()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                className = tasks.get(0).getTaskInfo().topActivity.getClassName().substring(41);
                                if (className.matches("MainActivity")) {
                                    Fragment frag = this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.detach(frag);
                                    fragmentTransaction.attach(frag);
                                    fragmentTransaction.commit();
                                }
                            }
                        }
                    }


                }
            }
        } else {
            message = "No internet! Please connect to network.";
            snackbar(message);
            //unregisterReceiver(connectivityReceiver);
            //startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
        }


    }

    public interface userActivityCallback {
        void onTourCallback(boolean tour);
        void onEventCallback(boolean event);
    }

    private void getUserActivity(userActivityCallback activityCallback){
        DatabaseReference activityEventRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
        activityEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    activityCallback.onEventCallback(true);
                }else {
                    activityCallback.onEventCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference activityTourRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("tours");
        activityTourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    activityCallback.onTourCallback(true);
                }else {
                    activityCallback.onTourCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getExtras().getString("EventDetails").matches("event")) {
                FragmentTransaction event = getSupportFragmentManager().beginTransaction();
                event.replace(R.id.fragment_container, new EventFragment());
                event.commit();
                navigationView.getMenu().getItem(2).setChecked(true);
            }
        }
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
                            R.drawable.shutdown, null),
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
        try {
            if (connectivityReceiver != null)
                unregisterReceiver(connectivityReceiver);

        } catch (Exception e) {
        }
        toast.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            if (connectivityReceiver != null)
                unregisterReceiver(connectivityReceiver);

        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public void selectedLocation(String location) {
        TourFragment f = (TourFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        f.receivedLocationData(location);
    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void checked(int value) {
        if (value == 1) {
            navigationView.getMenu().getItem(value).setChecked(true);
        }
    }

    public int getImageFromDrawable(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());

        return drawableResourceId;
    }

    static class InternetCheck extends AsyncTask<Void, Void, Boolean> {

        private Consumer mConsumer;

        public interface Consumer {
            void accept(Boolean internet);
        }

        public InternetCheck(Consumer consumer) {
            mConsumer = consumer;
            execute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean internet) {
            mConsumer.accept(internet);
        }
    }
}
