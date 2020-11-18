package com.wh.qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_buttom_sheet);

        Intent intent = getIntent();
        if(intent==null){
            this.finish();
        }
        String action = intent.getAction();
        if(action==null){
            this.finish();
        }
        String type = intent.getType();
        if(type==null){
            this.finish();
        }

        String s = null;

        switch (action){
            case Intent.ACTION_SEND:{
                s = intent.getStringExtra(Intent.EXTRA_TEXT);
                break;
            }
            case Intent.ACTION_PROCESS_TEXT:{
                s = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
                break;
            }
        }
        if(s==null){
            this.finish();
        }

        BottomSheet bottomSheet = BottomSheet.getInstance(s);
        bottomSheet.show(getSupportFragmentManager(),bottomSheet.getTag());

    }

    public static class BottomSheet extends BottomSheetDialogFragment {
        private String s;
        private ImageView iv_qr_code;
        private Context mParent;
        private String TAG = "WH_"+getClass().getSimpleName();

        public BottomSheet() {}
        public BottomSheet(String s){
            this.s = s;
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            mParent = context;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_buttom_sheet,container,false);
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
            if(s==null){
                requireActivity().finish();
            }
            iv_qr_code.setImageBitmap(new QRCodeGenerator(s,500,500).getQRCode());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            requireActivity().finish();
        }

        public static BottomSheet getInstance(String s){
            return new BottomSheet(s);
        }
    }
}