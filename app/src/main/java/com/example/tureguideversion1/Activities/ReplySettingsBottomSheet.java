package com.example.tureguideversion1.Activities;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tureguideversion1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReplySettingsBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "ReplySettingsBottomSheet";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ImageView editIV, deleteIV, copyIv;
    private TextView editTV, deleteTV, copyTV;
    private LinearLayout copyLayout, editLayout, deleteLayout;
    private String r_id, e_id, c_id, message;
    View view;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_comment_settings_bottom_sheet, container, false);
        init(view);

        Bundle mArgs = getArguments();
        String check = mArgs.getString("check");
        r_id = mArgs.getString("r_id");
        e_id = mArgs.getString("e_id");
        c_id = mArgs.getString("c_id");
        message = mArgs.getString("message");
        if (check.equals("true")) {
            copyLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.VISIBLE);
            deleteLayout.setVisibility(View.VISIBLE);
        } else if (check.equals("false")) {
            copyLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
            deleteLayout.setVisibility(View.GONE);
        }

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlertMessage();
            }
        });

        copyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(message);
                ReplySettingsBottomSheet.this.dismiss();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditReplyActivity.class).putExtra("eventId", e_id).putExtra("commentId", c_id)
                        .putExtra("replyId", r_id));
                ReplySettingsBottomSheet.this.dismiss();
            }
        });

        return view;
    }

    private void init(View view) {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTV = view.findViewById(R.id.editTV);
        copyTV = view.findViewById(R.id.copyTV);
        deleteTV = view.findViewById(R.id.deleteTV);
        copyLayout = view.findViewById(R.id.copyLayout);
        deleteLayout = view.findViewById(R.id.deleteLayout);
        editLayout = view.findViewById(R.id.editLayout);
    }

    private void deleteAlertMessage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Delete..!!");
        dialog.setIcon(R.drawable.ic_delete_white);
        dialog.setMessage("Do you want to delete this Comment?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*DatabaseReference repRef = databaseReference.child("eventCommentsReply").child(e_id).child(c_id).child(r_id);
                repRef.setValue(null);
                Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                ReplySettingsBottomSheet.this.dismiss();*/
                DatabaseReference ref = databaseReference.child("eventCommentsReply").child(e_id).child(c_id);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        //Log.d(TAG, "onDataChange: "+count);
                        if (count == 1) {
                            ref.child(r_id).removeValue();
                            DatabaseReference cRef = databaseReference.child("eventComments").child(e_id).child(c_id);
                            cRef.child("hasReply").setValue("no");
                            Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            ReplySettingsBottomSheet.this.dismiss();
                        }else if (count > 1) {
                            ref.child(r_id).removeValue();
                            Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            ReplySettingsBottomSheet.this.dismiss();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ReplySettingsBottomSheet.this.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();


    }


}
