package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static final String TAG = "SignIn";
    private EditText nameET;
    private ActionProcessButton singin;
    private TextView txt1;
    private String email;
    private ImageView logo;
    boolean doubleBackToExitPressedOnce = false;
    private Toast toast = null;
    FirebaseAuth auth;
    Animation topAnim, bottomAnim, leftAnim, rightAnim, ball1Anim, ball2Anim, ball3Anim, edittext_anim, blink;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        singin = findViewById(R.id.continue_BTN);
        singin.setMode(ActionProcessButton.Mode.ENDLESS);
        nameET = findViewById(R.id.name_ET);
        auth = FirebaseAuth.getInstance();
        txt1 = findViewById(R.id.txt1);
        logo = findViewById(R.id.logoS);
        animation();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            nameET.setText(intent.getExtras().getString("email"));
            if(intent.getStringExtra("prevention").matches("true")){
                preventionAlertDialog();
            }
        }
        if (user != null && user.isEmailVerified()) {
            // User is signed in
            if (intent.getAction() != null) {
                if (getIntent().getAction().matches("event")) {
                    unregisterReceiver(connectivityReceiver);
                    intent = new Intent(SignIn.this, MainActivity.class).setAction("event");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent);
                }else if (getIntent().getAction().matches("weather")) {
                    unregisterReceiver(connectivityReceiver);
                    intent = new Intent(SignIn.this, MainActivity.class).setAction("weather");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent);
                }
            }else {
                unregisterReceiver(connectivityReceiver);
                intent = new Intent(SignIn.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);
            }
        }

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboardFrom(getApplicationContext());
                email = nameET.getText().toString();
                if (email.isEmpty()) {
                    nameET.setError("Enter email address!");
                    nameET.requestFocus();
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    singin.setProgress(1);
                    singin.setEnabled(false);
                    logo.startAnimation(blink);
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
                    checkmail();
                } else {
                    nameET.setError("Invalid email address!");
                    nameET.requestFocus();
                }

            }
        });

        Intent myIntent = getIntent();
        String text = myIntent.getStringExtra("name");
        //Toast.makeText(this, "" + text, Toast.LENGTH_SHORT).show();
    }

    private void checkmail() {
        email = nameET.getText().toString();
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        singin.setEnabled(true);
                        logo.clearAnimation();
                        singin.setProgress(100);
                        try {
                            boolean check = !task.getResult().getSignInMethods().isEmpty();
                            if (!check) {
                                //Toast.makeText(getApplicationContext(),"Email not found",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignIn.this, SignUp.class).putExtra("email", email));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else {
                                //Toast.makeText(getApplicationContext(),"Email found",Toast.LENGTH_SHORT).show();
                                unregisterReceiver(connectivityReceiver);
                                startActivity(new Intent(SignIn.this, SignInGrantAccess.class).putExtra("email", email));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        } catch (Exception e) {
                            singin.setText("Continue");
                            e.printStackTrace();
                            String message = "Couldn't reach to the server! Try again leter.";
                            snackbar(message);
                        }
                    }
                });
    }

    private void preventionAlertDialog() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Log.d(TAG, "preventionAlertDialog: "+auth.getUid());
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        dialog.setTitle("Multiple login detected!");
        dialog.setIcon(R.drawable.batman);
        dialog.setMessage("You can't use you account with multiple devices at the same time. If you are not doing this then you should change your password to secure this account.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = dialog.create();
        alertDialog.show();
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
                .make(findViewById(R.id.logoS), text, Snackbar.LENGTH_INDEFINITE);

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
        nameET.setAnimation(edittext_anim);
        singin.setAnimation(bottomAnim);

    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
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

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if(connectivityReceiver!=null)
                unregisterReceiver(connectivityReceiver);

        }catch(Exception e){}
        if(alertDialog != null) {
            alertDialog.dismiss();
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
    public void onBackPressed() {
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

    @Override
    protected void onStop() {
        try{
            if(connectivityReceiver!=null)
                unregisterReceiver(connectivityReceiver);

        }catch(Exception e){}
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
        toast.cancel();
        super.onStop();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.finish();
    }
}
