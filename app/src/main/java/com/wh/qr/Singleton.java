package com.wh.qr;

import java.util.regex.Pattern;

public class Singleton {
    private String a = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_+.~#?&//=]*)";
    public Pattern regex_pattern_is_url;

    private static Singleton instance;

    public Singleton(){
        regex_pattern_is_url = Pattern.compile(a);
    }

    public static Singleton getInstance(){
        if(instance==null){
            instance = new Singleton();
        }
        return instance;
    }
}
