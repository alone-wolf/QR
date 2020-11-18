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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "WH_" + getClass().getSimpleName();
    private TextView tv_qr_code_result;
    private Pattern pattern;

    private static final int RequestCode_get_scan_result = 436;
    private static final int RequestCode_get_permission_camera = 144;

    private static final String spf = "spf";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(spf, MODE_PRIVATE);
        SharedPreferences sharedPreferences_default = PreferenceManager.getDefaultSharedPreferences(this);

        String isAddress = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_+.~#?&//=]*)";
        pattern = Pattern.compile(isAddress);
        tv_qr_code_result = findViewById(R.id.tv_qr_code_result);
        tv_qr_code_result.setOnClickListener(this);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);

        if (sharedPreferences_default.getBoolean("setting_start_scan_on_start", false)) {
            startScan();
        }else {
            tv_qr_code_result.setText(sharedPreferences.getString("last_scan_history","have no history"));
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, RequestCode_get_permission_camera);
            }
        } else {
            Intent intent = new Intent(this, QrScanTransparentActivity.class);
            intent.putExtra("forResult", "1");
            startActivityForResult(intent, RequestCode_get_scan_result);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_qr_code_result: {
                String result = String.valueOf(tv_qr_code_result.getText());
                Matcher matcher = pattern.matcher(result);
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
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData mClipData = ClipData.newPlainText("scanResult", result);
                                    clipboardManager.setPrimaryClip(mClipData);
                                }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestCode_get_permission_camera: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                }
                break;
            }
        }
    }

}