package com.cashback.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.cashback.databinding.ActivityQrScanBinding;
import com.cashback.databinding.ActivityVideoViewBinding;
import com.google.zxing.Result;



public class QrScannerActivity extends BaseActivity {

    private static final String TAG = "QrScannerActivity";
    ActivityQrScanBinding moBinding;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityQrScanBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

//        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
//            @Override
//            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                Log.d("TTT", "permission grant");
//                initializeContent();
//            }
//
//            @Override
//            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                //toastMessage("Give storage permission first");
//                Log.v("TTT", "Permission is revoked");
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                Log.d("TTT", "permission grant");
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();

        initializeContent();

    }

    private void initializeContent() {

        mCodeScanner = new CodeScanner(this, moBinding.scannerView);
        mCodeScanner.startPreview();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(QrScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        if (result != null && result.getText() != null) {
                            Log.d("TTT", "scal result..." + result.getText());

                        } else {
                            finish();
                        }
                    }
                });
            }
        });

        moBinding.scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

}

