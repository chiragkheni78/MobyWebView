package com.cashback.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.databinding.ActivitySplashBinding;
import com.cashback.models.SplashViewModel;
import com.cashback.models.viewmodel.BillUploadViewModel;

import static com.cashback.utils.Constants.SPLASH_TIME;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    ActivitySplashBinding moBinding;
    SplashViewModel moSplashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moSplashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        moSplashViewModel.checkInstallReferrer(getContext());
        moSplashViewModel.retrieveFirebaseDeepLink(this, getIntent());

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                openActivity();
            }
        }, SPLASH_TIME);
    }

    private void openActivity() {

        Intent loIntent = null;
        if (getPreferenceManager().isUserLogin()) {
            loIntent = new Intent(SplashActivity.this, HomeActivity.class);
        } else {
            loIntent = new Intent(SplashActivity.this, ShortProfileActivity.class);
        }
        startActivity(loIntent);
        finish();
    }

}
