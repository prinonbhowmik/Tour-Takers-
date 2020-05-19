package com.example.tureguideversion1.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Internet.Connection;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.Profile_bottom_sheet;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import es.dmoral.toasty.Toasty;

public class UserProfile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private LinearLayout ratingLayout, totalTourLayout, totalEventLayout;
    private TextView profilename, profileemail, profilephoneno, n2, p2;
    private CardView phoneUpdate, nameUpdate;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String userId, name, email, phone, image, rating;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Uri imageUri;
    private ImageView profileImage, n1, e2, e1, p1;
    private Snackbar snackbar;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    ProgressBar progressBar;
    Animation topAnim, bottomAnim, leftAnim, rightAnim, fadeIn, scaleAnim, ball3Anim, edittext_anim, blink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        storageReference = FirebaseStorage.getInstance().getReference();
        init();
        registerReceiver(connectivityReceiver, intentFilter);
        userId = auth.getUid();
        animation();
        DatabaseReference showref = reference.child(userId);

        showref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Profile profile = dataSnapshot.getValue(Profile.class);

                progressBar.setVisibility(View.GONE);

                name = profile.getName();
                email = profile.getEmail();
                phone = profile.getPhone();
                image = profile.getImage();

                profilename.setText(name);
                profileemail.setText(email);
                profilephoneno.setText(phone);
                if (!image.isEmpty() || !image.matches("")) {
                    try {
                        GlideApp.with(UserProfile.this)
                                .load(image)
                                .fitCenter()
                                .into(profileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Can't load profile image!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    String sex = profile.getSex();
                    if(sex.matches("male")){
                        GlideApp.with(UserProfile.this)
                                .load(getImageFromDrawable("man"))
                                .centerInside()
                                .into(profileImage);
                    }else if(sex.matches("female")){
                        GlideApp.with(UserProfile.this)
                                .load(getImageFromDrawable("woman"))
                                .centerInside()
                                .into(profileImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new CountDownTimer(300, 1) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                YoYo.with(Techniques.StandUp)
                        .duration(1700)
                        .repeat(0)
                        .playOn(ratingLayout);
            }
        }.start();

        YoYo.with(Techniques.FadeIn)
                .duration(1700)
                .repeat(0)
                .playOn(profilename);

        YoYo.with(Techniques.FadeIn)
                .duration(1700)
                .repeat(0)
                .playOn(profileemail);

        YoYo.with(Techniques.FadeIn)
                .duration(1700)
                .repeat(0)
                .playOn(profilephoneno);

        nameUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection()) {
                    Bundle args = new Bundle();
                    args.putString("name", "name");
                    args.putString("id", userId);
                    args.putString("nameForHint", name);
                    Profile_bottom_sheet bottom_sheet = new Profile_bottom_sheet();
                    bottom_sheet.setArguments(args);
                    bottom_sheet.show(getSupportFragmentManager(), "bottomSheet");
                }else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });

        phoneUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection()) {
                    Bundle args = new Bundle();
                    args.putString("phone", "phone");
                    args.putString("id", userId);
                    args.putString("phoneForHint", phone);
                    Profile_bottom_sheet bottom_sheet = new Profile_bottom_sheet();
                    bottom_sheet.setArguments(args);
                    bottom_sheet.show(getSupportFragmentManager(), "bottomSheet");
                }else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection()) {
                    CropImage.activity()
                            .setFixAspectRatio(true)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(UserProfile.this);
                }else {
                    startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri = resultUri;
                progressBar.setVisibility(View.VISIBLE);
                if (!image.isEmpty()) {
                    if(checkConnection()) {
                        deleteImage();
                    }else {
                        startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                    }
                } else {
                    if(checkConnection()) {
                        uploadImage();
                    }else {
                        startActivity(new Intent(getApplicationContext(), NoInternetConnection.class));
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage() {

        if (imageUri != null) {
            StorageReference ref = storageReference.child("userProfileImage/" + userId);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child("userProfileImage/" + userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUri = uri;
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profile").child(userId).child("image");
                                    databaseReference.setValue(imageUri.toString());
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toasty.error(UserProfile.this, "Image upload failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot
                                    .getTotalByteCount();
                            if(progress<100){
                                Toasty.info(getApplicationContext(),"Uploading...",Toasty.LENGTH_SHORT).show();
                                //Toasty.info(getApplicationContext(),"Uploaded " + (int) progress + "% done",Toasty.LENGTH_SHORT).show();
                            }else {
                                Toasty.success(getApplicationContext(),"Successfully uploaded",Toasty.LENGTH_SHORT).show();
                            }

                            //Toast.makeText(UserProfile.this, "Uploaded " + (int) progress + "%", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteImage() {
        storageReference.child("userProfileImage/" + userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        //Log.d(TAG, "onSuccess: deleted file");
                        uploadImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        //Log.d(TAG, "onFailure: did not delete file");
                        progressBar.setVisibility(View.GONE);
                        Toasty.error(getApplicationContext(), "Faild", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void animation() {

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        leftAnim = AnimationUtils.loadAnimation(this, R.anim.left_animation);
        rightAnim = AnimationUtils.loadAnimation(this, R.anim.right_animation);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        ball3Anim = AnimationUtils.loadAnimation(this, R.anim.ball3_animation);
        edittext_anim = AnimationUtils.loadAnimation(this, R.anim.edittext_anim);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink_anim);

//        profilename.setAnimation(scaleAnim);
//        profileemail.setAnimation(scaleAnim);
//        profilephoneno.setAnimation(scaleAnim);
        e1.setAnimation(leftAnim);
        e2.setAnimation(rightAnim);
        n1.setAnimation(leftAnim);
        n2.setAnimation(rightAnim);
        p1.setAnimation(leftAnim);
        p2.setAnimation(rightAnim);
        totalEventLayout.setAnimation(bottomAnim);
        totalTourLayout.setAnimation(bottomAnim);
    }

    private void init() {
        ratingLayout = findViewById(R.id.ratingLayout);
        totalTourLayout = findViewById(R.id.totalTourLayout);
        totalEventLayout = findViewById(R.id.totalEventLayout);
        nameUpdate = findViewById(R.id.nameUpdate);
        phoneUpdate = findViewById(R.id.phoneUpdate);
        profilename = findViewById(R.id.profileusername);
        profileemail = findViewById(R.id.profileemail);
        profilephoneno = findViewById(R.id.profilephoneNo);
        progressBar = findViewById(R.id.progressBar);
        profileImage = findViewById(R.id.profileIV);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("profile");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        n1 = findViewById(R.id.n1);
        n2 = findViewById(R.id.n2);
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(connectivityReceiver, intentFilter);
        Connection.getInstance().setConnectivityListener(this);
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
    protected void onStop() {
        super.onStop();
        try {
            if (connectivityReceiver != null)
                unregisterReceiver(connectivityReceiver);

        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(UserProfile.this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    public boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            if (snackbar != null) {
                snackbar.dismiss();
                Intent i = new Intent(UserProfile.this, UserProfile.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        } else {
            message = "No internet! Please connect to network.";
            snackbar(message);
            //unregisterReceiver(connectivityReceiver);
            //startActivity(new Intent(MainActivity.this, NoInternetConnection.class));
        }


    }

    private void snackbar(String text) {
        snackbar = Snackbar
                .make(findViewById(R.id.profileIV), text, Snackbar.LENGTH_INDEFINITE);

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

    public int getImageFromDrawable(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());

        return drawableResourceId;
    }
}
