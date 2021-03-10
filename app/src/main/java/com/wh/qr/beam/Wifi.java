package com.wh.qr.beam;

import android.net.wifi.WifiManager;

public class Wifi {
    String ssid,password;
    int type;

    public Wifi(String ssid, String password, int type) {
        this.ssid = ssid;
        this.password = password;
        this.type = type;

    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
