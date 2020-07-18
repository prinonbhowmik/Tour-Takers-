package com.example.tureguideversion1.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.tureguideversion1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ReplySettingsBottomSheet extends BottomSheetDialogFragment {


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_comment_settings_bottom_sheet, container, false);
        init(v);

        return v;
    }

    private void init(View v) {
    }
}
