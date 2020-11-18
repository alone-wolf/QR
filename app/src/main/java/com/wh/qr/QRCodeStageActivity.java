package com.wh.qr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class QRCodeStageActivity extends AppCompatActivity {
    TextView textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrcode_stage);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        String qrstring = intent.getStringExtra("qrcodestring");

        if(qrstring==null){
            String action = intent.getAction()+"";
            String type = intent.getType()+"";
            if("text/plain".equals(type)){
                switch (action){
                    case Intent.ACTION_SEND:{
                        qrstring = intent.getStringExtra(Intent.EXTRA_TEXT) + "";
                        break;
                    }
                    case Intent.ACTION_PROCESS_TEXT:{
                        qrstring= intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) + "";
                        break;
                    }
                }
            }
        }

        if (qrstring != null) {
            textView.setText(qrstring);
//            imageView.setImageResource(R.drawable.ic_launcher_background);
            imageView.setImageBitmap(new QRCodeGenerator(qrstring, 300, 300).getQRCode());
        }

        String finalQrstring = qrstring;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("qrcodestring", finalQrstring);
                clipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(QRCodeStageActivity.this, "copied", Toast.LENGTH_LONG).show();
            }
        });
    }
}
