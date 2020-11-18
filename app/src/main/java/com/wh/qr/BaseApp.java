package com.wh.qr;

import android.app.Application;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Singleton.getInstance();
    }
}
