package com.wh.qr;

import java.util.regex.Pattern;

public class Singleton {
    private static Singleton instance;

    private static final String regex_string_url = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_+.~#?&//=]*)";
    public Pattern regex_pattern_is_url;

    private static final String regex_string_qr_ = "";
    private static final String regex_string_qr_wifi = "";
    private static final String regex_string_qr_vcard = "";
    private static final String regex_string_qr_send_sms = "";
    private static final String regex_string_qr_geo = "";

    public Singleton() {
        regex_pattern_is_url = Pattern.compile(regex_string_url);
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
