package com.wh.qr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import cn.bingoogolapple.qrcode.core.QRCodeView;

public class QRCodeScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    QRCodeView qrCode;
    String TAG = "WH_" + QRCodeScanActivity.class.getSimpleName();
    boolean forResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);

        Intent intent = getIntent();
        String forResult = intent.getStringExtra("forResult") + "";
        this.forResult = "1".equals(forResult);
        qrCode = findViewById(R.id.qrcode);
        qrCode.setDelegate(this);
    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        qrCode.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        qrCode.stopCamera();
        super.onStop();
    }

    @Override
    public void onScanQRCodeSuccess(final String result) {
        Log.d(TAG, "onScanQRCodeSuccess");
        qrCode.stopCamera();
        if (this.forResult) {
            Intent intent = new Intent();
            intent.putExtra("result", result);
            this.setResult(0, intent);
            this.finish();
        } else {
            AlertDialog resultAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("扫描结果")
                    .setItems(new String[]{result}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("scanResult", result);
                            clipboardManager.setPrimaryClip(mClipData);
                            Toast.makeText(QRCodeScanActivity.this, "text copied", Toast.LENGTH_LONG).show();
                            QRCodeScanActivity.this.finish();
                        }
                    })
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            QRCodeScanActivity.this.finish();
                        }
                    })
                    .setNegativeButton("back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            QRCodeScanActivity.this.finish();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            QRCodeScanActivity.this.finish();
                        }
                    })
                    .create();
            resultAlertDialog.show();
        }
    }


    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "onScanQRCodeOpenCameraError");
        qrCode.startSpotAndShowRect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        qrCode.onDestroy();
        super.onDestroy();
    }
}
