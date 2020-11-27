package com.wh.qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bingoogolapple.qrcode.core.QRCodeView;

public class QrScanTransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomSheet.getInstance().show(getSupportFragmentManager(), null);
    }

    public static class BottomSheet extends BottomSheetDialogFragment implements QRCodeView.Delegate {
        private final String TAG = "WH_" + getClass().getSimpleName();
        private QRCodeView qrCode;
        private static final int RequestCode_get_scan_result = 436;


        public static BottomSheet getInstance() {
            return new BottomSheet();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_buttom_sheet_scan, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            qrCode = view.findViewById(R.id.qrcode1);
            qrCode.setDelegate(this);
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d(TAG, "onStart");
            qrCode.startSpotAndShowRect();
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop");
            qrCode.stopCamera();
            super.onStop();
        }

        @Override
        public void onDestroy() {
            qrCode.onDestroy();
            super.onDestroy();
            requireActivity().finish();
        }

        @Override
        public void onScanQRCodeSuccess(String result) {
            qrCode.stopCamera();
            Log.d(TAG, "onScanQRCodeSuccess: 1");

            Intent order_intent = requireActivity().getIntent();
            String for_result = order_intent.getStringExtra("forResult");
            if ("1".equals(for_result)) {
                Intent intent = new Intent();
                intent.putExtra("qr_scan_result", result);
                requireActivity().setResult(RequestCode_get_scan_result, intent);
                requireActivity().finish();
                return;
            }
            if (Singleton.getInstance().regex_pattern_is_url.matcher(result).matches()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Open URL?")
                        .setMessage(result)
                        .setPositiveButton("ok", (dialog, which) -> {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
                            requireActivity().finish();
                        })
                        .setNeutralButton("share", (dialog, which) -> {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, result);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, "Share URL");
                            startActivity(shareIntent);
                            requireActivity().finish();
                        })
                        .setNegativeButton("cancel", (dialog, which) -> requireActivity().finish())
                        .create()
                        .show();
            }

            if (result.startsWith("geo:") || result.startsWith("GEO:")) {
                String result_tmp = result.substring(4);
                String[] position = result_tmp.split(",");
                if (position.length != 2 && position.length != 3) {
                    requireActivity().finish();
                    return;
                }
                String latitude = position[0];
                String longitude = position[1];
                new AlertDialog.Builder(requireContext())
                        .setTitle("Operation for position?")
                        .setMessage(position[0] + " " + position[1])
                        .setOnDismissListener(dialog -> requireActivity().finish())
                        .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, result);
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, "Share Position");
                                startActivity(shareIntent);
                                requireActivity().finish();
                            }
                        })
                        .setPositiveButton("Open", (dialog, which) -> {
                            PackageManager manager = requireContext().getPackageManager();
                            Intent intent_to_map;
                            intent_to_map = manager.getLaunchIntentForPackage("com.tencent.map");
                            if (intent_to_map != null) {
                                Intent intent1 = new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                                new UrlBuilder()
                                                        .setBaseUrl("qqmap://map")
                                                        .addPath("routeplan")
                                                        .addFirstArg("type", "drive")
                                                        .addMoreArg("fromcoord", "CurrentLocation")
                                                        .addMoreArg("tocoord", latitude + "," + longitude)
                                                        .addMoreArg("refer", "7FMBZ-43FWW-B2DRX-OV7TJ-GXRVZ-A3BIP")
                                                        .getString(TAG)));
                                startActivity(intent1);
                                requireActivity().finish();
                                return;
                            }

                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Please install tencent map!")
                                    .setMessage("amap baidumap are poor!!!!")
                                    .setOnDismissListener(dialog1 -> requireActivity().finish()).create().show();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> requireActivity().finish())
                        .create()
                        .show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Operations?")
                        .setMessage(result)
                        .setPositiveButton("copy", (dialog, which) -> {
                            ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("scanResult", result);
                            clipboardManager.setPrimaryClip(mClipData);
                            requireActivity().finish();
                        })
                        .setNeutralButton("share", (dialog, which) -> {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, result);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, "Share QRCode Content");
                            startActivity(shareIntent);
                            requireActivity().finish();
                        })
                        .setNegativeButton("cancel", (dialog, which) -> requireActivity().finish())
                        .create()
                        .show();
            }
        }

        @Override
        public void onScanQRCodeOpenCameraError() {
            Log.e(TAG, "onScanQRCodeOpenCameraError");
            qrCode.startSpotAndShowRect();
        }
    }
}