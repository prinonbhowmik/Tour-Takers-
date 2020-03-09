package com.example.tureguideversion1;

import android.content.Context;
import android.os.Bundle;
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
        String updateKey = mArgs.getString("phone");
        String userID = mArgs.getString("id");
        String cap = updateKey.substring(0, 1).toUpperCase() + updateKey.substring(1);
        text.setText("Enter your "+updateKey+" to update");
        inputLayout.setHint(cap);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("profile").child(userID).child(updateKey);

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.setValue(input.getText().toString());
                dismiss();
            }
        });

        return v;
    }
}
