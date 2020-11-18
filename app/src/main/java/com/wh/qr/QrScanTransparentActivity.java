package com.wh.qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cn.bingoogolapple.qrcode.core.QRCodeView;

public class QrScanTransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomSheet.getInstance().show(getSupportFragmentManager(),null);
    }

    public static class BottomSheet extends BottomSheetDialogFragment implements QRCodeView.Delegate{
        private String TAG = "WH_"+getClass().getSimpleName();
        private QRCodeView qrCode;
        public static BottomSheet getInstance(){
            return new BottomSheet();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_buttom_sheet_scan,container,false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            qrCode = view.findViewById(R.id.qrcode1);
            qrCode.setDelegate(this);
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d(TAG, "onStart");
            qrCode.startSpotAndShowRect();
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop");
            qrCode.stopCamera();
            super.onStop();
        }

        @Override
        public void onDestroy() {
            qrCode.onDestroy();
            super.onDestroy();
            requireActivity().finish();
        }

        @Override
        public void onScanQRCodeSuccess(String result) {
            qrCode.stopCamera();
            Toast.makeText(requireActivity(),result,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScanQRCodeOpenCameraError() {
            Log.e(TAG, "onScanQRCodeOpenCameraError");
            qrCode.startSpotAndShowRect();
        }
    }
}