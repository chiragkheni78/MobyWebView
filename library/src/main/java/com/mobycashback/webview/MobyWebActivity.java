package com.mobycashback.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.mobycashback.webview.databinding.ActivityWebBinding;


public class MobyWebActivity extends AppCompatActivity implements MobyWebviewPermission.Listener, View.OnClickListener {
    String url, setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor;
    boolean isAccessStorage, isAccessGPS;
    ActivityWebBinding moBinding;
    ViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_web);

        moBinding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        if (getIntent() != null) {
            url = getIntent().getStringExtra("url");
            isAccessGPS = getIntent().getBooleanExtra("isAccessGPS", false);
            isAccessStorage = getIntent().getBooleanExtra("isAccessStorage", false);
            setPrimaryColor = getIntent().getStringExtra("setPrimaryColor");
            setSecondaryColor = getIntent().getStringExtra("setSecondaryColor");
            setPrimaryTextColor = getIntent().getStringExtra("setPrimaryTextColor");
            setSecondaryTextColor = getIntent().getStringExtra("setSecondaryTextColor");
        }


        moBinding.llTop.setBackgroundColor(Color.parseColor(setPrimaryColor));
        moBinding.ivWallet.setColorFilter(Color.parseColor(setSecondaryColor), android.graphics.PorterDuff.Mode.MULTIPLY);
        moBinding.ivCoupon.setColorFilter(Color.parseColor(setSecondaryColor), android.graphics.PorterDuff.Mode.MULTIPLY);
        moBinding.ivMyOffer.setColorFilter(Color.parseColor(setSecondaryColor), android.graphics.PorterDuff.Mode.MULTIPLY);

        moBinding.tvWallet.setTextColor(Color.parseColor(setPrimaryTextColor));
        moBinding.tvCoupon.setTextColor(Color.parseColor(setPrimaryTextColor));
        moBinding.tvMyOffer.setTextColor(Color.parseColor(setPrimaryTextColor));

        moBinding.ivBack.setOnClickListener(this);
        moBinding.llCoupon.setOnClickListener(this);
        moBinding.llOffer.setOnClickListener(this);
        moBinding.llWallet.setOnClickListener(this);


        moBinding.webview.setListener(this, this);
        moBinding.webview.setGeolocationEnabled(isAccessGPS);
        moBinding.webview.setAccessStorage(isAccessStorage);
        moBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(MobyWebActivity.this, "Finished loading", Toast.LENGTH_SHORT).show();
            }

        });
        moBinding.webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Toast.makeText(MobyWebActivity.this, title, Toast.LENGTH_SHORT).show();
            }

        });
        //mWebView.addHttpHeader("Referer", "https://mobyads.in/");
        // TODO: 11-03-2022 payal
        // moBinding.webview.loadUrl(url + "&fbIsWebView=true");
        moBinding.webview.loadUrl(url);
    }

    protected View getContentView(ViewBinding binding) {
        this.binding = binding;
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        //Log.d("TTT", "onResume call...");
        moBinding.webview.onResume();
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        moBinding.webview.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        moBinding.webview.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivBack) {
            onBackPressed();
        } else if (id == R.id.llCoupon) {
            moBinding.webview.loadJavaScript("openMenuFromNative('my-coupons')");
        } else if (id == R.id.llOffer) {
            moBinding.webview.loadJavaScript("openMenuFromNative('my-offers')");
        } else if (id == R.id.llWallet) {
            moBinding.webview.loadJavaScript("openMenuFromNative('my-wallet')");
        }
    }
}