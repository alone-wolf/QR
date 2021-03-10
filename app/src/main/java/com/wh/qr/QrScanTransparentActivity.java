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

import java.util.Arrays;

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
            Log.d(TAG, "onScanQRCodeSuccess: "+result);
            qrCode.stopCamera();

            Intent order_intent = requireActivity().getIntent();
            String for_result = order_intent.getStringExtra("forResult");
            if ("1".equals(for_result)) {
                Intent intent = new Intent();
                intent.putExtra("qr_scan_result", result);
                requireActivity().setResult(RequestCode_get_scan_result, intent);
                requireActivity().finish();
                return;
            }
            String a = RegexUtils.matchTel1(result);
            if (a != null) {
                // 拨号
                String finalA = a;
                new AlertDialog
                        .Builder(requireContext())
                        .setTitle("提示")
                        .setMessage("要打开拨号器吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(finalA));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                return;
            }
            a = RegexUtils.matchUrl(result);
            if (a != null) {
                // 打开网页
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    new AlertDialog
                            .Builder(requireContext())
                            .setTitle("提示")
                            .setMessage("要打开链接吗？\n" + a)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                } else {
                    Toast.makeText(requireActivity(), "未找到浏览器", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            a = RegexUtils.matchGeo1(result);
            if (a != null) {
                // 打开地图
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("提示")
                            .setMessage("要打开地图吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                } else {
                    Toast.makeText(requireActivity(), "未找到地图软件", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            String[] as = RegexUtils.matchSendSms(result);
            Log.d(TAG, "onActivityResult: "+ Arrays.toString(as));
            if (as != null && as.length == 3) {
                // 发送短信
                Log.d(TAG, "onClick: " + Arrays.toString(as));
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + as[1]));
                intent.putExtra("sms_body", as[2]);
                new AlertDialog.Builder(requireContext())
                        .setTitle("提示")
                        .setMessage("要打开短信吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
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