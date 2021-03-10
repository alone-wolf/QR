package com.wh.qr;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import androidx.core.content.ContextCompat;

public class WifiUtils {
    public WifiManager prepare(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        return wifiManager;
    }

    public static void connect(WifiManager wifiManager,String n, String p,int t){
        new Thread() {
            @Override
            public void run() {
                super.run();
                wifiManager.addNetwork(createConfiguration(n,p,t));
            }
        }.start();
    }

    private static WifiConfiguration createConfiguration(String ssid,String password,int type){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

//        config.SSID =
        return config;
    }
}
