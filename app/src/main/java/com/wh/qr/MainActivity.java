package com.wh.qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;

import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private String TAG = "WH_" + getClass().getSimpleName();
    private TextView tv_qr_code_result;

    private static final int RequestCode_get_scan_result = 436;

    private static final String spf = "spf";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(spf, MODE_PRIVATE);
        SharedPreferences sharedPreferences_default = PreferenceManager.getDefaultSharedPreferences(this);

        tv_qr_code_result = findViewById(R.id.tv_qr_code_result);
        tv_qr_code_result.setOnClickListener(this);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        floatingActionButton.setOnLongClickListener(this);
        floatingActionButton.setVisibility(View.GONE);

        PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA)
                .onExplainRequestReason((scope, deniedList) -> {
                    String s = "QR need permission of Camera to function normally";
                    scope.showRequestReasonDialog(deniedList, s, "OK", "Cancel");
                }).request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        if (sharedPreferences_default.getBoolean("setting_start_scan_on_start", false)) {
            startScan();
        } else {
            tv_qr_code_result.setText(sharedPreferences.getString("last_scan_history", "have no history"));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCode_get_scan_result: {
                if (data != null) {
                    String d = data.getStringExtra("qr_scan_result");
                    tv_qr_code_result.setText(d);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("last_scan_history", d);
                    editor.apply();
                    tv_qr_code_result.callOnClick();
                }
            }
        }
    }

    private void startScan() {
        Intent intent = new Intent(this, QrScanTransparentActivity.class);
        intent.putExtra("forResult", "1");
        startActivityForResult(intent, RequestCode_get_scan_result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_qr_code_result: {
                String result = String.valueOf(tv_qr_code_result.getText());
                Matcher matcher = Singleton.getInstance().regex_pattern_is_url.matcher(result);
                if (matcher.matches()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("使用浏览器打开")
                            .setMessage(result)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("no", null);
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("复制到剪切板吗")
                            .setMessage(result)
                            .setPositiveButton("ok", (dialogInterface, i) -> {
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData mClipData = ClipData.newPlainText("scanResult", result);
                                clipboardManager.setPrimaryClip(mClipData);
                            })
                            .setNegativeButton("no", null);
                    builder.create().show();
                }
                break;
            }
            case R.id.floatingActionButton: {
                startScan();
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        EditText editText = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("输入文字")
                .setView(editText)
                .setPositiveButton("生成", (dialog, which) -> {
                    String s = editText.getText().toString();
                    if(s==null|| s.equals("")){
                        Toast.makeText(MainActivity.this,"Input null",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent i = new Intent(MainActivity.this,QrGenTransparentActivity.class);
                    i.setAction("gen_qr");
                    i.putExtra("text",s);
                    startActivity(i);
                }).setNegativeButton("取消", null).create().show();
        return true;
    }
}