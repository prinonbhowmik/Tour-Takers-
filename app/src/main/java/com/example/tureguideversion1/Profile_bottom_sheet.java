package com.example.tureguideversion1;

import android.content.Context;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Profile_bottom_sheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_bottom_sheet, container, false);
        Button saveBTN = v.findViewById(R.id.saveBTN);
        TextView text = v.findViewById(R.id.text);
        TextInputLayout inputLayout = v.findViewById(R.id.bottomSheetInputLayout);
        final EditText input = v.findViewById(R.id.bottomSheetInput);
        Bundle mArgs = getArguments();
        String updateKeyPhone = mArgs.getString("phone");
        String updateKeyName = mArgs.getString("name");
        String userID = mArgs.getString("id");
        final String nameForHint = mArgs.getString("nameForHint");
        final String phoneForHint = mArgs.getString("phoneForHint");

        if(updateKeyPhone != null) {
            input.setKeyListener(new DigitsKeyListener());
            String cap1 = updateKeyPhone.substring(0, 1).toUpperCase() + updateKeyPhone.substring(1);
            text.setText("Enter your " + updateKeyPhone + " to update");
            inputLayout.setHint(cap1);
            input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        input.setHint(phoneForHint);
                        //Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                    } else {
                        input.setHint("");
                        //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else if(updateKeyName != null){
            String cap2 = updateKeyName.substring(0, 1).toUpperCase() + updateKeyName.substring(1);
            text.setText("Enter your " + updateKeyName + " to update");
            inputLayout.setHint("Full "+cap2);
            input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        input.setHint(nameForHint);
                        //Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                    } else {
                        input.setHint("");
                        //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        if(updateKeyPhone != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profile").child(userID).child(updateKeyPhone);
            saveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.setValue(input.getText().toString());
                    dismiss();
                }
            });
        }else if(updateKeyName != null){
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profile").child(userID).child(updateKeyName);
            saveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.setValue(input.getText().toString());
                    dismiss();
                }
            });
        }
        return v;
    }
}
