package com.example.tureguideversion1.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tureguideversion1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class EventUpdateBottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText updateText;
    private TextInputLayout inputLayout;
    private TextView title;
    private Button updateBtn;
    private String u_description_title, u_description_input, u_description_text, u_group_title, u_group_input,
            u_meeting_title, u_meeting_input, event_id, u_cost_title, u_cost_input;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_update_bottom_sheet, container, false);

        init(view);

        Bundle bundle = getArguments();
        event_id = bundle.getString("event_id");
        u_description_title = bundle.getString("description");
        u_description_input = bundle.getString("input_d");
        u_group_title = bundle.getString("group_name");
        u_group_input = bundle.getString("input_g");
        u_meeting_title = bundle.getString("meeting_place");
        u_meeting_input = bundle.getString("input_m");
        u_cost_title = bundle.getString("cost");
        u_cost_input = bundle.getString("input_c");


        if (u_description_title != null) {
            title.setText("Update " + u_description_title);
            inputLayout.setHint(u_description_title);
            updateText.setText(u_description_input);
            updateText.setMaxLines(12);
        } else if (u_meeting_title != null) {
            title.setText("Update " + u_meeting_title);
            inputLayout.setHint(u_meeting_title);
            updateText.setText(u_meeting_input);
        } else if (u_group_title != null) {
            title.setText("Update " + u_group_title);
            inputLayout.setHint(u_group_title);
            updateText.setText(u_group_input);
        } else if (u_cost_title != null) {
            title.setText("Update " + u_cost_title);
            inputLayout.setHint(u_cost_title);
            updateText.setText(u_cost_input);
        }


        if (u_description_title != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event").child(event_id).child("description");
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.setValue(updateText.getText().toString());
                    Toasty.success(getContext(), "Description Updated", Toasty.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        } else if (u_group_title != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event").child(event_id).child("groupName");
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.setValue(updateText.getText().toString());

                    Toasty.success(getContext(), "Group Name Updated", Toasty.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        } else if (u_meeting_title != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event").child(event_id).child("meetPlace");
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.setValue(updateText.getText().toString());
                    Toasty.success(getContext(), "Meeting Place Updated", Toasty.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        } else if (u_cost_title != null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event").child(event_id).child("cost");
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.setValue(updateText.getText().toString());
                    Toasty.success(getContext(), "Total Cost Updated", Toasty.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        }


        return view;
    }

    private void init(View view) {
        updateText = view.findViewById(R.id.updateTIET);
        inputLayout = view.findViewById(R.id.updateTIL);
        title = view.findViewById(R.id.titleTV);
        updateBtn = view.findViewById(R.id.updateBtn);

    }
}
