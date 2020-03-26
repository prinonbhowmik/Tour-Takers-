package com.example.tureguideversion1.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class SignUp extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{


    private EditText emailEt, nameEt, phoneNoEt, passwordEt;
    private Button signupBtn;
    private TextView txt1;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String email, name, phone, password, image, rating;
    Animation topAnim, bottomAnim, leftAnim, rightAnim, ball1Anim, ball2Anim, ball3Anim, edittext_anim;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    private Uri userImage;
    private String userId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        txt1 = findViewById(R.id.txt1);
        emailEt = findViewById(R.id.email_ET);
        signupBtn = findViewById(R.id.signup_BTN);
        animation();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        init();
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        emailEt.setText(email);
        progressDialog = new ProgressDialog(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEt.getText().toString();
                name = nameEt.getText().toString();
                phone = phoneNoEt.getText().toString();
                password = passwordEt.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailEt.setError("Enter email");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Enter valid email");
                    emailEt.requestFocus();
                } else if (TextUtils.isEmpty(name)) {
                    nameEt.setError("Enter User name");
                    nameEt.requestFocus();
                } else if (TextUtils.isEmpty(phone)) {
                    phoneNoEt.setError("Enter phone No.");
                    phoneNoEt.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    passwordEt.setError("Enter Password!");
                    passwordEt.requestFocus();
                } else if (passwordEt.length() < 6) {
                    passwordEt.setError("At least 6 characters!", null);
                    passwordEt.requestFocus();
                }else {
                    signupBtn.setEnabled(false);
                    checkmail();
                }

            }
        });

    }

    private void checkmail() {
        email = emailEt.getText().toString();
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        if (!check) {
                            //Toast.makeText(getApplicationContext(),"Email not found",Toast.LENGTH_SHORT).show();
                            signup(email, name, phone, password);
                        } else {
                            signupBtn.setEnabled(true);
                            emailEt.setError("This email address is already in use by another account!");
                            emailEt.requestFocus();
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


        txt1.setAnimation(topAnim);
        signupBtn.setAnimation(bottomAnim);
        emailEt.setAnimation(edittext_anim);

    }

    private void signup(final String email, final String name, final String phone, final String password) {
        progressDialog.setTitle("Signing up...");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signupBtn.setEnabled(true);
                if (task.isSuccessful()) {
                    userId = auth.getCurrentUser().getUid();
                    DatabaseReference dataref = reference.child("profile").child(userId);

                    HashMap<String, Object> userInfo = new HashMap<>();

                    userInfo.put("name", name);
                    userInfo.put("email", email);
                    userInfo.put("phone", phone);
                    userInfo.put("password", password);
                    userInfo.put("image", "");
                    userInfo.put("rating", "");
                    userInfo.put("Id", userId);


                    dataref.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toasty.success(getApplicationContext(), "Successfully Signed Up.", Toast.LENGTH_SHORT).show();
                                            Toasty.Config.getInstance().setTextSize(14).allowQueue(true).apply();
                                            Toasty.info(getApplicationContext(), "Please check your mailbox for verification!", Toast.LENGTH_LONG).show();
                                            Toasty.Config.reset();
                                            startActivity(new Intent(SignUp.this, SignIn.class).putExtra("email", email));
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
                signupBtn.setEnabled(true);
            }
        } else {
            message = "No internet! Please connect to network.";
            snackbar(message);
            signupBtn.setEnabled(false);
        }


    }

    private void snackbar(String text) {
        snackbar = Snackbar
                .make(findViewById(R.id.signup_BTN), text, Snackbar.LENGTH_INDEFINITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(CENTER_HORIZONTAL);
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

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if(connectivityReceiver!=null)
                unregisterReceiver(connectivityReceiver);

        }catch(Exception e){}

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
        super.onBackPressed();
        unregisterReceiver(connectivityReceiver);
    }

    private void init() {

        emailEt = findViewById(R.id.email_ET);
        nameEt = findViewById(R.id.name_ET);
        phoneNoEt = findViewById(R.id.phoneEt);
        passwordEt = findViewById(R.id.password_ET);
        signupBtn = findViewById(R.id.signup_BTN);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

}
