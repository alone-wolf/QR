package com.wh.qr;

import android.app.Application;

public class BaseApp extends Application {
    private String TAG = "WH_" + getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
