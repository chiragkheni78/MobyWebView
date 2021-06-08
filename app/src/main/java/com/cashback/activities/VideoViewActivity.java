package com.cashback.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.cashback.databinding.ActivityVideoViewBinding;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;


public class VideoViewActivity extends BaseActivity {

    private static final String TAG = VideoViewActivity.class.getSimpleName();
    ActivityVideoViewBinding moBinding;
    private String fsUrl, adName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityVideoViewBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        setSupportActionBar(moBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            fsUrl = getIntent().getStringExtra(Constants.IntentKey.VIDEO_URL);
            adName = getIntent().getStringExtra(Constants.IntentKey.SCREEN_TITLE);

            if (adName != null && !adName.isEmpty())
                getSupportActionBar().setTitle(adName);

            if (fsUrl != null && !fsUrl.isEmpty())
                startWebView(fsUrl);
            // initContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startWebView(String url) {

        WebSettings settings = moBinding.webview.getSettings();

        settings.setJavaScriptEnabled(true);
        moBinding.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        moBinding.webview.getSettings().setBuiltInZoomControls(true);
        moBinding.webview.getSettings().setUseWideViewPort(true);
        moBinding.webview.getSettings().setLoadWithOverviewMode(true);

        showProgressDialog();

        moBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissProgressDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogV2.i(TAG, "Error:" + description);
            }
        });
        moBinding.webview.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            Runtime.getRuntime().gc();
            System.gc();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
