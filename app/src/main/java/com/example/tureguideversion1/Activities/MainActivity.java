package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tureguideversion1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ImageView nav_icon;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    boolean doubleBackToExitPressedOnce = false;
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
       // toolbar = findViewById(R.id.toolbar);
        nav_icon = findViewById(R.id.nav_icon);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

/*
    @Override
    public void finish() {
        super.finish();
       // overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
    */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, UserProfile.class));
                drawerLayout.closeDrawers();
                break;

            case R.id.event:
                Toast.makeText(this, "Event", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;

            case R.id.map:
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;
            case R.id.weather:
                Toast.makeText(this, "Weather", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                break;

            case R.id.guide:
                Toast.makeText(this, "Guide", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                break;

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
