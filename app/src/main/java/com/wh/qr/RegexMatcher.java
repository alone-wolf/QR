package com.wh.qr;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
public class RegexMatcher {
    public static void Matcher(Activity activity, Context context, String result, String TAG) {
        String a = RegexUtils.matchTel1(result);
        if (a != null) {
            // 拨号
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(a));
            String finalA1 = a;
            new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("打开拨号器?\n" + a)
                    .setPositiveButton("打开", (dialog, which) -> {
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activity, "未找到拨号器", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setNeutralButton("复制", (dialog, which) -> {
                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("scanResult", finalA1);
                        clipboardManager.setPrimaryClip(mClipData);
                    })
                    .create()
                    .show();
            return;
        }
        a = RegexUtils.matchUrl(result);
        if (a != null) {
            // 打开网页
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
            String finalA = a;
            new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("打开链接?\n" + a)
                    .setPositiveButton("浏览器打开", (dialog, which) -> {
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activity, "未找到浏览器", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setNeutralButton("复制", (dialog, which) -> {
                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("scanResult", finalA);
                        clipboardManager.setPrimaryClip(mClipData);
                    })
                    .create()
                    .show();
            return;
        }
        a = RegexUtils.matchGeo1(result);
        if (a != null) {
            // 打开地图
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(a));
                String finalA1 = a;
                new AlertDialog.Builder(context)
                        .setTitle("提示")
                        .setMessage("打开地图位置?\n" + a)
                        .setPositiveButton("打开", (dialog, which) -> {
                            try{
                                context.startActivity(intent);
                            }catch (Exception e){
                                Toast.makeText(activity, "未找到地图软件", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .setNeutralButton("复制", (dialog, which) -> {
                            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("scanResult", finalA1);
                            clipboardManager.setPrimaryClip(mClipData);
                        })
                        .create()
                        .show();
            return;
        }
        String[] as = RegexUtils.matchSendSms(result);
        if (as != null && as.length == 3) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + as[1]));
            intent.putExtra("sms_body", as[2]);
            new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("打开短信?\n" + as[1]+":"+as[2])
                    .setPositiveButton("打开", (dialog, which) -> {
                        context.startActivity(intent);
                    })
                    .setNegativeButton("取消", null)
                    .setNeutralButton("复制", (dialog, which) -> {
                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("scanResult", result);
                        clipboardManager.setPrimaryClip(mClipData);
                    })
                    .create()
                    .show();
            return;
        }
        EditText editText = new EditText(context);
        editText.setText(result);
        new AlertDialog.Builder(context)
                .setTitle("未匹配到模式")
                .setView(editText)
                .setPositiveButton("复制", (dialog, which) -> {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("scanResult", result);
                    clipboardManager.setPrimaryClip(mClipData);
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }
}
