package com.wh.qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QrGenTransparentActivity extends AppCompatActivity {
    private String TAG = "WH_" + getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_buttom_sheet_gen);

        Intent intent = getIntent();
        if (intent == null) {
            this.finish();
        }
        String action = intent.getAction();
        Log.d(TAG, "onCreate: " + action);
        if (action == null) {
            this.finish();
        }

        String s = null;
        if (Intent.ACTION_SEND.equals(action)) {
            s = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (Intent.ACTION_PROCESS_TEXT.equals(action)) {
            s = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
        }
        if (s != null) {
            BottomSheet bottomSheet = BottomSheet.getInstance(s);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            return;
        }

        final int[] press_which = {-1000};

        EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Input Text For Generation")
                .setView(editText)
                .setPositiveButton("Gen", (dialog, which) -> {
                    press_which[0] = which;
                    BottomSheet bottomSheet = BottomSheet.getInstance(editText.getText().toString());
                    bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                })
                .setNegativeButton("Close", (dialog, which) -> {
                    press_which[0] = which;
                    finish();
                })
                .setOnDismissListener(dialog -> {
                    if (press_which[0] == -1000) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    public static class BottomSheet extends BottomSheetDialogFragment {
        private String s;
        private ImageView iv_qr_code;
        private String TAG = "WH_" + getClass().getSimpleName();

        public BottomSheet(String s) {
            this.s = s;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_buttom_sheet_gen, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d(TAG, "onViewCreated: ");
            iv_qr_code = view.findViewById(R.id.iv_qr_code);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.d(TAG, "onActivityCreated: ");
            if (s == null) {
                requireActivity().finish();
            }
            iv_qr_code.setImageBitmap(new QRCodeGenerator(s, 500, 500).getQRCode());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            requireActivity().finish();
        }

        public static BottomSheet getInstance(String s) {
            return new BottomSheet(s);
        }
    }
}