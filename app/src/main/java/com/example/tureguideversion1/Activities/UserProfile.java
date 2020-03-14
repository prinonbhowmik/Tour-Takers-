package com.example.tureguideversion1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.Profile_bottom_sheet;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class UserProfile extends AppCompatActivity{

    private TextView profilename, profileemail, profilephoneno;
    private CardView phoneUpdate,nameUpdate;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String userId, name, email, phone, image, rating;
    private StorageReference storageReference;
    private Uri imageUri;
    private ImageView profileImage;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        storageReference = FirebaseStorage.getInstance().getReference();
        init();
        userId = auth.getUid();

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
                if (!image.isEmpty()) {
                    try {
                        Glide.with(UserProfile.this)
                                .load(image)
                                .into(profileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Can't load profile image!", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nameUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("name", "name");
                args.putString("id", userId);
                args.putString("nameForHint", name);
                Profile_bottom_sheet bottom_sheet = new Profile_bottom_sheet();
                bottom_sheet.setArguments(args);
                bottom_sheet.show(getSupportFragmentManager(),"bottomSheet");
            }
        });

        phoneUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("phone", "phone");
                args.putString("id", userId);
                args.putString("phoneForHint", phone);
                Profile_bottom_sheet bottom_sheet = new Profile_bottom_sheet();
                bottom_sheet.setArguments(args);
                bottom_sheet.show(getSupportFragmentManager(),"bottomSheet");
            }
        });



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(UserProfile.this);
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
                if(!image.isEmpty()) {
                    deleteImage();
                }else{
                    uploadImage();
                }
                //imageIV.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage() {

        if(imageUri != null)
        {
            StorageReference ref = storageReference.child("userProfileImage/"+userId );
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child("userProfileImage/"+userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                            Toast.makeText(UserProfile.this, "Image upload failed!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            Toast.makeText(UserProfile.this, "Uploaded "+(int)progress+"%", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteImage(){
        storageReference.child("userProfileImage/"+userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        Toast.makeText(getApplicationContext(),"Faild",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void init() {
        nameUpdate = findViewById(R.id.nameUpdate);
        phoneUpdate = findViewById(R.id.phoneUpdate);
        profilename = findViewById(R.id.profileusername);
        profileemail = findViewById(R.id.profileemail);
        profilephoneno = findViewById(R.id.profilephoneNo);
        progressBar = findViewById(R.id.progressBar);
        profileImage = findViewById(R.id.profileIV);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("profile");


    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }


}
