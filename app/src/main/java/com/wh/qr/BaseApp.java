package com.wh.qr;

import android.app.Application;
import android.util.Log;

import androidx.annotation.LongDef;

import java.util.Arrays;

public class BaseApp extends Application {
    private String TAG = "WH_" + getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG, "onCreate: " + RegexUtils.matchUrl("http://39.103.143.157:5000"));
//        Log.d(TAG, "onCreate: " + Arrays.toString(RegexUtils.matchWifi("WIFI:T:WPA;S:TP-LINK_1BE0;P:1;;")));
//        Log.d(TAG, "onCreate: "+ Arrays.toString(RegexUtils.matchSendSms("smsto:123456:aasdasd")));
//        Log.d(TAG, "onCreate: "+ Arrays.toString(RegexUtils.matchGeo("GEO:123,123")));
//        Log.d(TAG, "onCreate: "+ Arrays.toString(RegexUtils.matchVCard("VERSION:3.0\n" +
//                "N;CHARSET=UTF-8:姥;姥;;;\n" +
//                "FN;CHARSET=UTF-8:姥姥\n" +
//                "TEL;TYPE=VOICE:1234567891\n" +
//                "TEL;TYPE=CELL:12345678911\n" +
//                "TEL;TYPE=CELL:12345678911\n" +
//                "TEL;TYPE=VOICE:12345678911\n" +
//                "TEL;TYPE=手机:12345678911\n" +
//                "TEL;TYPE=其他:12345678911\n" +
//                "END:VCARD")));
    }
}
