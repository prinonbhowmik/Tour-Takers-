package com.example.tureguideversion1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Password extends AppCompatActivity {

    private EditText nameET,passEt;
    private Button singin;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        singin=findViewById(R.id.signin_BTN);
        nameET = findViewById(R.id.name_ET);
        passEt = findViewById(R.id.password_ET);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();


        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameET.getText().toString();
                String password = passEt.getText().toString();

                String userId = auth.getCurrentUser().getUid();
                DatabaseReference dataref = reference.child("profile").child(userId);

                HashMap<String,Object> userInfo = new HashMap<>();

                userInfo.put("name",name);
                userInfo.put("email",password);

                dataref.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Password.this, "User Created!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Password.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent myintent=new Intent(Password.this,Term_And_Condition.class);
                startActivity(myintent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


            }
        });
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
