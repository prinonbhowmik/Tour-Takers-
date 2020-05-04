package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SignInGrantAccess extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private EditText passEt;
    private Button singin;
    private String email, password;
    private TextView txt1;
    private ImageView logo;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    Animation topAnim, bottomAnim, leftAnim, rightAnim, ball1Anim, ball2Anim, ball3Anim, edittext_anim, blink;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_grant_access);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        singin = findViewById(R.id.signin_BTN);
        passEt = findViewById(R.id.password_ET);
        passEt.setSelected(false);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("profile");
        txt1 = findViewById(R.id.txt1);
        logo = findViewById(R.id.logoG);
        animation();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                email = intent.getExtras().getString("email");
                password = passEt.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    passEt.setError("Please enter password!", null);
                } else if (passEt.length() < 6) {
                    passEt.setError("At least 6 characters!", null);
                } else {
                    singin.setText("Connecting");
                    logo.startAnimation(blink);
                    singin.setEnabled(false);
                    signinuser(email, password);
                }

            }
        });
    }

    private void animation() {

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        leftAnim = AnimationUtils.loadAnimation(this, R.anim.left_animation);
        rightAnim = AnimationUtils.loadAnimation(this, R.anim.right_animation);
        ball1Anim = AnimationUtils.loadAnimation(this, R.anim.ball1_animation);
        ball2Anim = AnimationUtils.loadAnimation(this, R.anim.ball2_animation);
        ball3Anim = AnimationUtils.loadAnimation(this, R.anim.ball3_animation);
        edittext_anim = AnimationUtils.loadAnimation(this, R.anim.edittext_anim);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink_anim);

        logo.setAnimation(leftAnim);
        txt1.setAnimation(topAnim);
        passEt.setAnimation(edittext_anim);
        singin.setAnimation(bottomAnim);

    }

    private void signinuser(final String email, final String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                singin.setEnabled(true);
                if (task.isSuccessful()) {
                    logo.clearAnimation();
                    if (auth.getCurrentUser().isEmailVerified()) {
                        singin.setText("Login");
                        final String ID = auth.getCurrentUser().getUid();
                        DatabaseReference showref = reference.child(ID);
                        showref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    unregisterReceiver(connectivityReceiver);
                                    Intent intent = new Intent(SignInGrantAccess.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    startActivity(intent);

                                } else {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GuideProfile").child(ID);
                                    ref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                Toasty.error(getApplicationContext(), "You can't use guide account to this app!", Toasty.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(SignInGrantAccess.this, SignIn.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toasty.error(getApplicationContext(),"Sign In failed. Please contact to the support!",Toasty.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(SignInGrantAccess.this, SignIn.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    } else {
                        singin.setText("Login");
                        Toasty.info(getApplicationContext(), "Please verify your email address!", Toasty.LENGTH_LONG).show();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                singin.setText("Login");
                logo.clearAnimation();
                Toast.makeText(SignInGrantAccess.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            //message = "Connected to Internet";
            if (snackbar != null) {
                snackbar.dismiss();
                singin.setEnabled(true);
            }
        } else {
            message = "No internet! Please connect to network.";
            snackbar(message);
            singin.setEnabled(false);
        }


    }

    private void snackbar(String text) {
        snackbar = Snackbar
                .make(findViewById(R.id.logoG), text, Snackbar.LENGTH_INDEFINITE);

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
    protected void onResume() {
        super.onResume();
        /*register connection status listener*/
        Connection.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
