package com.cashback.activities;

import static com.cashback.utils.Constants.SPLASH_TIME;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.databinding.ActivitySplashBinding;
import com.cashback.models.response.StaticLabelsResponse;
import com.cashback.models.viewmodel.ReferralTrackViewModel;
import com.cashback.models.viewmodel.SplashViewModel;
import com.cashback.utils.SharedPreferenceManager;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    ActivitySplashBinding moBinding;
    SplashViewModel moSplashViewModel;
    ReferralTrackViewModel moReferralTrackViewModel;

    Observer<StaticLabelsResponse> fetchStaticLabelsObserver = new Observer<StaticLabelsResponse>() {
        @Override
        public void onChanged(StaticLabelsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getLabels() != null) {
                    //handle Result here
                }
            } else {
                //handle Error here
                //Common.showErrorDialog(getContext(), loJsonObject.getFsMessage(), false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moSplashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        moSplashViewModel.fetchStaticLabels.observe(this, fetchStaticLabelsObserver);
        moSplashViewModel.fetchStaticLabelsList(getContext());

        moReferralTrackViewModel = new ViewModelProvider(this).get(ReferralTrackViewModel.class);
        moReferralTrackViewModel.retrieveFirebaseDeepLink(this, getIntent());
        moReferralTrackViewModel.checkInstallReferrer(getContext());

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                openActivity();
            }
        }, SPLASH_TIME);
    }

    private void openActivity() {
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(this);

        Intent loIntent = null;
        if (getPreferenceManager().isUserLogin()) {
            if (AppGlobal.walletRefferal.equalsIgnoreCase("wallet")) {
                loIntent = new Intent(SplashActivity.this, WalletActivity.class);
                //loSharedPreferenceManager.setAppDownloadCampaign("");
            } else
                loIntent = new Intent(SplashActivity.this, HomeActivity.class);
        } else {
            loIntent = new Intent(SplashActivity.this, ShortProfileActivity.class);
        }
        startActivity(loIntent);
        finish();
    }

}
