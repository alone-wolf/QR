package com.wh.qr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /** tag */
    private static final String TAG = "Utils";

    /**
     * 安装某个应用
     *
     * @param context
     * @param apkFile
     * @return
     */
    public static boolean installApp(Context context, File apkFile) {
        try {
            context.startActivity(getInstallAppIntent(apkFile));
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return false;
    }

    /**
     * 获取安装应用的Intent
     *
     * @param apkFile
     * @return
     */
    public static Intent getInstallAppIntent(File apkFile) {
        if (apkFile == null || !apkFile.exists()) {
            return null;
        }

        Utils.chmod("777", apkFile.getAbsolutePath());
        Uri uri = Uri.fromFile(apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * �?查某个包名的App是否已经安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean hasAppInstalled(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据包名启动第三方App
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean launchAppByPackageName(Context context,
                                                 String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        try {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            if (intent != null) {
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return false;
    }

    public static String getAssetsFie(Context context, String name)
            throws IOException {

        InputStream is = context.getAssets().open(name);
        int size = is.available();

        // Read the entire asset into a local byte buffer.
        byte[] buffer = new byte[size];
        String tmp=null;
        if(is.read(buffer)>0){
            tmp = new String(buffer,StandardCharsets.UTF_8);
        }
        is.close();

        return tmp;

    }

    /**
     * 是否为wifi连接状�??
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        if (networkinfo != null) {
            if ("wifi".equals(networkinfo.getTypeName().toLowerCase(Locale.US))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有网络连�?
     *
     * @param context
     * @return
     */
    public static boolean isNetConnect(Context context) {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        return networkinfo != null;
    }

    /**
     * 获取权限
     *
     * @param permission
     *            权限
     * @param path
     *            文件路径
     */
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            Log.e(TAG, "chmod", e);
        }
    }

    /**
     * 是否安装了sdcard�?
     *
     * @return true表示有，false表示没有
     */
    public static boolean haveSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取系统内部可用空间大小
     *
     * @return available size
     */
    public static long getSystemAvailableSize() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        return availCount * blockSize;
    }

    /**
     * 获取sd卡可用空间大�?
     *
     * @return available size
     */
    public static long getSDCardAvailableSize() {
        long available = 0;
        if (haveSDCard()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            long blocSize = statfs.getBlockSize();
            long availaBlock = statfs.getAvailableBlocks();

            available = availaBlock * blocSize;
        } else {
            available = -1;
        }
        return available;
    }

    /**
     * 获取application层级的metadata
     *
     * @param context
     * @param key
     * @return
     */
    public static String getApplicationMetaData(Context context, String key) {
        try {
            Object metaObj = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData
                    .get(key);
            if (metaObj instanceof String) {
                return metaObj.toString();
            } else if (metaObj instanceof Integer) {
                return (Integer) metaObj + "";
            } else if (metaObj instanceof Boolean) {
                return (Boolean) metaObj + "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }
        return "";
    }

    /**
     * 获取版本�?
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }
        return null;
    }

    /**
     * 获取版本�?
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }
        return 0;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param pxValue
     *            （DisplayMetrics类中属�?�density�?
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param dipValue
     *            （DisplayMetrics类中属�?�density�?
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context
     *            （DisplayMetrics类中属�?�scaledDensity�?
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param context
     *            （DisplayMetrics类中属�?�scaledDensity�?
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 隐藏键盘
     *
     * @param activity
     *            activity
     */
    public static void hideInputMethod(Activity activity) {
        hideInputMethod(activity, activity.getCurrentFocus());
    }

    /**
     * 隐藏键盘
     *
     * @param context
     *            context
     * @param view
     *            The currently focused view
     */
    public static void hideInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示输入键盘
     *
     * @param context
     *            context
     * @param view
     *            The currently focused view, which would like to receive soft
     *            keyboard input
     */
    public static void showInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * Bitmap缩放，注意源Bitmap在缩放后将会被回收�??
     *
     * @param origin
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getScaleBitmap(Bitmap origin, int width, int height) {
        float originWidth = origin.getWidth();
        float originHeight = origin.getHeight();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width) / originWidth;
        float scaleHeight = ((float) height) / originHeight;

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap scale = Bitmap.createBitmap(origin, 0, 0, (int) originWidth,
                (int) originHeight, matrix, true);
        origin.recycle();
        return scale;
    }

    /**
     * 计算某一时间与现在时间间隔的文字提示
     */
    public static String countTimeIntervalText(long time) {
        long dTime = System.currentTimeMillis() - time;
        // 15分钟
        if (dTime < 15 * 60 * 1000) {
            return "刚刚";
        } else if (dTime < 60 * 60 * 1000) {
            // �?小时
            return "�?小时�?";
        } else if (dTime < 24 * 60 * 60 * 1000) {
            return (int) (dTime / (60 * 60 * 1000)) + "小时�?";
        } else {
            return DateFormat.format("MM-dd kk:mm", System.currentTimeMillis())
                    .toString();
        }
    }

    /**
     * 获取通知栏高�?
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int x = 0, statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取标题栏高�?
     *
     * @param context
     * @return
     */
    public static int getTitleBarHeight(Activity context) {
        int contentTop = context.getWindow()
                .findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentTop - getStatusBarHeight(context);
    }

    /**
     * 获取屏幕宽度，px
     *
     * @param context
     * @return
     */
    public static float getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度，px
     *
     * @param context
     * @return
     */
    public static float getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕像素密度
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取scaledDensity
     *
     * @param context
     * @return
     */
    public static float getScaledDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.scaledDensity;
    }

    /**
     * 获取当前小时分钟�?24小时�?
     *
     * @return
     */
    public static String getTime24Hours() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 获取电池电量,0~1
     *
     * @param context
     * @return
     */
    @SuppressWarnings("unused")
    public static float getBattery(Context context) {
        Intent batteryInfoIntent = context.getApplicationContext()
                .registerReceiver(null,
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryInfoIntent.getIntExtra("status", 0);
        int health = batteryInfoIntent.getIntExtra("health", 1);
        boolean present = batteryInfoIntent.getBooleanExtra("present", false);
        int level = batteryInfoIntent.getIntExtra("level", 0);
        int scale = batteryInfoIntent.getIntExtra("scale", 0);
        int plugged = batteryInfoIntent.getIntExtra("plugged", 0);
        int voltage = batteryInfoIntent.getIntExtra("voltage", 0);
        int temperature = batteryInfoIntent.getIntExtra("temperature", 0); // 温度的单位是10�?
        String technology = batteryInfoIntent.getStringExtra("technology");
        return ((float) level) / scale;
    }

    /**
     * 获取手机名称
     *
     * @return
     */
    public static String getMobileName() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    /**
     * 是否安装了sdcard�?
     *
     * @return true表示有，false表示没有
     */
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取sd卡可用空�?
     *
     * @return available size
     */
    public static long getAvailableExternalSize() {
        long available = 0;
        if (hasSDCard()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            long blocSize = statfs.getBlockSize();
            long availaBlock = statfs.getAvailableBlocks();

            available = availaBlock * blocSize;
        } else {
            available = -1;
        }
        return available;
    }

    /**
     * 获取内存可用空间
     *
     * @return available size
     */
    public static long getAvailableInternalSize() {
        long available = 0;
        if (hasSDCard()) {
            File path = Environment.getRootDirectory();
            StatFs statfs = new StatFs(path.getPath());
            long blocSize = statfs.getBlockSize();
            long availaBlock = statfs.getAvailableBlocks();

            available = availaBlock * blocSize;
        } else {
            available = -1;
        }
        return available;
    }

    /*
     * 版本控制部分
     */

    /**
     * 是否�?2.2版本及以�?
     *
     * @return
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 是否�?2.3版本及以�?
     *
     * @return
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * 是否�?3.0版本及以�?
     *
     * @return
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 是否�?3.1版本及以�?
     *
     * @return
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * 是否�?4.1版本及以�?
     *
     * @return
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static String getPhoneType() {

        String phoneType = android.os.Build.MODEL;

        Log.d(TAG, "phoneType is : " + phoneType);

        return phoneType;
    }

    /**
     * 获取系统版本�?
     *
     * @return
     */
    public static String getOsVersion() {
        String osversion;
        int osversion_int = getOsVersionInt();
        osversion = osversion_int + "";
        return osversion;

    }

    /**
     * 获取系统版本�?
     *
     * @return
     */
    public static int getOsVersionInt() {
        return Build.VERSION.SDK_INT;

    }

//    /**
//     * 获取ip地址
//     *
//     * @return
//     */
//    @SuppressLint("LongLogTag")
//    public static String getHostIp() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface
//                    .getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf
//                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()
//                            && InetAddressUtils.isIPv4Address(inetAddress
//                            .getHostAddress())) {
//                        if (!inetAddress.getHostAddress().toString()
//                                .equals("null")
//                                && inetAddress.getHostAddress() != null) {
//                            return inetAddress.getHostAddress().toString()
//                                    .trim();
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Log.e("WifiPreference IpAddress", ex.toString());
//        }
//        return "";
//    }

    /**
     * 获取手机号，几乎获取不到
     *
     * @param context
     * @return
     */
    public static String getPhoneNum(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
        @SuppressLint({"MissingPermission", "HardwareIds"})
        String phoneNum = mTelephonyMgr.getLine1Number();
        return TextUtils.isEmpty(phoneNum) ? "" : phoneNum;
    }

    /**
     * 获取imei�?
     *
     * @param context
     * @return
     */
    public static String getPhoneImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
        @SuppressLint({"MissingPermission", "HardwareIds"})
        String phoneImei = mTelephonyMgr.getDeviceId();
        Log.d(TAG, "IMEI is : " + phoneImei);
        return TextUtils.isEmpty(phoneImei) ? "" : phoneImei;
    }

    /**
     * 获取imsi�?
     *
     * @param context
     * @return
     */
    public static String getPhoneImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
        @SuppressLint({"MissingPermission", "HardwareIds"})
        String phoneImsi = mTelephonyMgr.getSubscriberId();
        Log.d(TAG, "IMSI is : " + phoneImsi);

        return TextUtils.isEmpty(phoneImsi) ? "" : phoneImsi;
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String Mac = null;
        try {
            String path = "sys/class/net/wlan0/address";
            if ((new File(path)).exists()) {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    Mac = new String(buffer, 0, byteCount, "utf-8");
                }
                fis.close();
            }

            if (Mac == null || Mac.length() == 0) {
                path = "sys/class/net/eth0/address";
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis.read(buffer_name);
                if (byteCount_name > 0) {
                    Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
                fis.close();
            }

            if (Mac == null || Mac.length() == 0) {
                return "";
            } else if (Mac.endsWith("\n")) {
                Mac = Mac.substring(0, Mac.length() - 1);
            }
        } catch (Exception io) {
            Log.w(TAG, "Exception", io);
        }

        return TextUtils.isEmpty(Mac) ? "" : Mac;
    }

    /**
     * 获取重复字段�?多的个数
     *
     * @param s
     * @return
     */
    public static int getRepeatTimes(String s) {
        if (TextUtils.isEmpty(s)) {
            return 0;
        }

        int mCount = 0;
        char[] mChars = s.toCharArray();
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        for (int i = 0; i < mChars.length; i++) {
            char key = mChars[i];
            Integer value = map.get(key);
            int count = value == null ? 0 : value.intValue();
            map.put(key, ++count);
            if (mCount < count) {
                mCount = count;
            }
        }

        return mCount;
    }

    /**
     * �?单判断是否为手机号码
     *
     * @param num
     * @return
     */
    public static boolean isPhoneNum(String num) {
        // 确保每一位都是数�?
        return !TextUtils.isEmpty(num) && num.matches("1[0-9]{10}")
                && !isRepeatedStr(num) && !isContinuousNum(num);
    }

    /**
     * 判断是否400服务代码
     *
     * @param num
     * @return
     */
    public static boolean is400or800(String num) {
        return !TextUtils.isEmpty(num)
                && (num.startsWith("400") || num.startsWith("800"))
                && num.length() == 10;
    }

    /**
     * 判断是否区域号码
     *
     * @param num
     * @return
     */
    public static boolean isAdCode(String num) {
        return !TextUtils.isEmpty(num) && num.matches("[0][0-9]{2,3}")
                && !isRepeatedStr(num);
    }

    /**
     * 判断是否座机号码
     *
     * @param num
     * @return
     */
    public static boolean isPhoneHome(String num) {
        return !TextUtils.isEmpty(num) && num.matches("[0-9]{7,8}")
                && !isRepeatedStr(num);
    }

    /**
     * 判断是否为重复字符串
     *
     * @param str
     *            ，需要检查的字符�?
     */
    public static boolean isRepeatedStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int len = str.length();
        if (len <= 1) {
            return false;
        } else {
            char firstChar = str.charAt(0);// 第一个字�?
            for (int i = 1; i < len; i++) {
                char nextChar = str.charAt(i);// 第i个字�?
                if (firstChar != nextChar) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 判断字符串是否为连续数字 45678901�?
     */
    public static boolean isContinuousNum(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        if (!isNumbericString(str))
            return true;
        int len = str.length();
        for (int i = 0; i < len - 1; i++) {
            char curChar = str.charAt(i);
            char verifyChar = (char) (curChar + 1);
            if (curChar == '9')
                verifyChar = '0';
            char nextChar = str.charAt(i + 1);
            if (nextChar != verifyChar) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为连续字母 xyZaBcd�?
     */
    public static boolean isContinuousWord(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        if (!isAlphaBetaString(str))
            return true;
        int len = str.length();
        String local = str.toLowerCase();
        for (int i = 0; i < len - 1; i++) {
            char curChar = local.charAt(i);
            char verifyChar = (char) (curChar + 1);
            if (curChar == 'z')
                verifyChar = 'a';
            char nextChar = local.charAt(i + 1);
            if (nextChar != verifyChar) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为纯数字
     *
     * @param str
     *            ，需要检查的字符�?
     */
    public static boolean isNumbericString(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern p = Pattern.compile("^[0-9]+$");// 从开头到结尾必须全部为数�?
        Matcher m = p.matcher(str);

        return m.find();
    }

    /**
     * 判断是否为纯字母
     *
     * @param str
     * @return
     */
    public static boolean isAlphaBetaString(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern p = Pattern.compile("^[a-zA-Z]+$");// 从开头到结尾必须全部为字母或者数�?
        Matcher m = p.matcher(str);

        return m.find();
    }

    /**
     * 判断是否为纯字母或数�?
     *
     * @param str
     * @return
     */
    public static boolean isAlphaBetaNumbericString(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");// 从开头到结尾必须全部为字母或者数�?
        Matcher m = p.matcher(str);

        return m.find();
    }

    private static String regEx = "[\u4e00-\u9fa5]";
    private static Pattern pat = Pattern.compile(regEx);

    /**
     * 判断是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainsChinese(String str) {
        return pat.matcher(str).find();
    }

    public static boolean patternMatcher(String pattern, String input) {
        if (TextUtils.isEmpty(pattern) || TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(input);

        return matcher.find();
    }

    /****************************************************************************/
    // import PPutils
    private static int id = 1;

    public static int getNextId() {
        return id++;
    }

    /**
     * 将输入流转为字节数组
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] read2Byte(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();

        return outSteam.toByteArray();
    }

    /**
     * 判断是否符合月和年的过期时间规则
     *
     * @param date
     * @return
     */
    public static boolean isMMYY(String date) {
        try {
            if (!TextUtils.isEmpty(date) && date.length() == 4) {
                int mm = Integer.parseInt(date.substring(0, 2));
                return mm > 0 && mm < 13;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return false;
    }

    /**
     * 20120506 共八位，前四�?-年，中间两位-月，�?后两�?-�?
     *
     * @param date
     * @return
     */
    public static boolean isRealDate(String date, int yearlen) {
        // if(yearlen != 2 && yearlen != 4)
        // return false;
        int len = 4 + yearlen;
        if (date == null || date.length() != len)
            return false;

        if (!date.matches("[0-9]+"))
            return false;

        int year = Integer.parseInt(date.substring(0, yearlen));
        int month = Integer.parseInt(date.substring(yearlen, yearlen + 2));
        int day = Integer.parseInt(date.substring(yearlen + 2, yearlen + 4));

        if (year <= 0)
            return false;
        if (month <= 0 || month > 12)
            return false;
        if (day <= 0 || day > 31)
            return false;

        switch (month) {
            case 4:
            case 6:
            case 9:
            case 11:
                return day > 30 ? false : true;
            case 2:
                if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                    return day > 29 ? false : true;
                return day > 28 ? false : true;
            default:
                return true;
        }
    }

    /**
     * 判断字符串是否为连续字符 abcdef 45678�?
     */
    public static boolean isContinuousStr(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char curChar = str.charAt(i);
            char nextChar = (char) (curChar + 1);
            if (i + 1 < len) {
                nextChar = str.charAt(i + 1);
            }
            if (nextChar != (curChar + 1)) {
                return false;
            }
        }
        return true;
    }

    public static final String REGULAR_NUMBER = "(-?[0-9]+)(,[0-9]+)*(\\.[0-9]+)?";

    /**
     * 为字符串中的数字染颜�?
     *
     * @param str
     *            ，待处理的字符串
     * @param color
     *            ，需要染的颜�?
     * @return
     */
    public static SpannableString setDigitalColor(String str, int color) {
        if (str == null)
            return null;
        SpannableString span = new SpannableString(str);

        Pattern p = Pattern.compile(REGULAR_NUMBER);
        Matcher m = p.matcher(str);
        while (m.find()) {
            int start = m.start();
            int end = start + m.group().length();
            span.setSpan(new ForegroundColorSpan(color), start, end,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return span;
    }

    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }

    public static String getFixedNumber(String str, int length) {
        if (str == null || length <= 0 || str.length() < length) {
            return null;
        }
        Log.d(TAG, "getFixedNumber, str is : " + str);
        Pattern p = Pattern.compile("\\d{" + length + "}");
        Matcher m = p.matcher(str);
        String result = null;
        if (m.find()) {
            int start = m.start();
            int end = start + m.group().length();
            result = str.substring(start, end);
        }

        return result;
    }

    public static int getLengthWithoutSpace(CharSequence s) {
        int len = s.length();

        int rlen = 0;
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) != ' ')
                rlen++;
        }

        return rlen;
    }

    /**
     * 获取控件的宽度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredWidth(TextView view) {
        // int height = view.getMeasuredHeight();
        // if(0 < height){
        // return height;
        // }
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(TextView view) {
        // int height = view.getMeasuredHeight();
        // if(0 < height){
        // return height;
        // }
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 测量控件的尺�?
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
        // int width =
        // View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        // int height =
        // View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        // view.measure(width,height);

        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }

    public static String getDisDsrc(float dis) {
        if (dis <= 0) {
            return "";
        }
        String disStr = null;
        if (dis > 1000) {
            disStr = (float) Math.round(dis / 1000 * 10) / 10 + "km";
        } else {
            disStr = dis + "m";
        }
        return disStr;
    }
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比�?2007/02/29会被接受，并转换�?2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或�?�NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }
}
