package com.wh.qr;


import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private static String TAG = "WH___";

    public static String matchUrl(String text){
        Pattern pattern = Pattern.compile("^(http://|https://)[0-9a-zA-Z-_.~!*'();:@&=+$,/?#\\[\\]]+");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

//    public static String[] matchWifi(String text){
//        Pattern pattern = Pattern.compile("WIFI:(([STP]|[ST]):(.+);)");
//        Matcher matcher = pattern.matcher(text);
//        Log.d(TAG, "matchWifi: found "+matcher.find());
//        if(matcher.find()){
//            String[] strings = new String[matcher.groupCount()];
////            String a = matcher.group(1);
////            Log.d(TAG, "matchWifi: "+a);
//            Log.d(TAG, "matchWifi: "+matcher.find(0));
//            for (int i = 0; i < matcher.groupCount()+1; i++) {
//                strings[i]=matcher.group(i);
//            }
//            return strings;
//        }
//        return null;
//    }

//    // buggy
//    public static String[] matchVCard(String text){
//        Pattern pattern = Pattern.compile("BEGIN:VCARD\n" +
//                "N;CHARSET=UTF-8:(.+?);(.+?);;;\n" +
//                "FN;CHARSET=UTF-8:(.+?)\n" +
////                "(TEL;TYPE=(.+?):([1-9\\-]+?)\n)+" +
//                "END:VCARD");
//        Matcher matcher = pattern.matcher(text);
//        if(matcher.find()){
//            String[] strings = new String[matcher.groupCount()];
//            for (int i = 0; i < matcher.groupCount(); i++) {
//                strings[i]=matcher.group(i+1);
//            }
//            return strings;
//        }
//        return null;
//    }
    public static String[] matchTel(String text){
        Pattern pattern = Pattern.compile("^((i?)tel):(.+)");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            String[] strings = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                strings[i]=matcher.group(i+1);
            }
            return strings;
        }
        return null;
    }
    public static String matchTel1(String text){
        Pattern pattern = Pattern.compile("^((i?)tel):(.+)");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

    public static String[] matchSendSms(String text){
        Pattern pattern = Pattern.compile("^((i?)smsto):(.+?):(.+)");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            String[] strings = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                strings[i]=matcher.group(i+1);
            }
            return strings;
        }
        return null;
    }

    public static String[] matchGeo(String text){
        Pattern pattern = Pattern.compile("^((i?)geo):(.+?),(.+)");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            String[] strings = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                strings[i]=matcher.group(i+1);
            }
            return strings;
        }
        return null;
    }
    public static String matchGeo1(String text){
        Pattern pattern = Pattern.compile("^((i?)geo):(.+?),(.+)");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }
}
