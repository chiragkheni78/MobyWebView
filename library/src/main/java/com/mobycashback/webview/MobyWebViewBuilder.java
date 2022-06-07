package com.mobycashback.webview;

import android.content.Intent;

public class MobyWebViewBuilder {
    String url;
    // Activity context;
    boolean isAccessGPS;
    boolean isAccessStorage;
    String setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor;

    public MobyWebViewBuilder(String url, boolean isLocationOn, boolean isWritePermission,
                              String setPrimaryColor, String setSecondaryColor,
                              String setPrimaryTextColor, String setSecondaryTextColor) {
        this.url = url;
        this.isAccessGPS = isLocationOn;
        this.isAccessStorage = isWritePermission;
        this.setPrimaryColor = setPrimaryColor;
        this.setSecondaryColor = setSecondaryColor;
        this.setPrimaryTextColor = setPrimaryTextColor;
        this.setSecondaryTextColor = setSecondaryTextColor;
    }

    public MobyWebViewBuilder() {

    }

   /* public FinestWebViewBuilder(Activity context) {
        this.context = context;
    }*/

    public MobyWebViewBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public MobyWebViewBuilder setPrimaryColor(String setPrimaryColor) {
        this.setPrimaryColor = setPrimaryColor;
        return this;
    }

    public MobyWebViewBuilder setSecondaryColor(String setSecondaryColor) {
        this.setSecondaryColor = setSecondaryColor;
        return this;
    }

    public MobyWebViewBuilder setPrimaryTextColor(String setPrimaryTextColor) {
        this.setPrimaryTextColor = setPrimaryTextColor;
        return this;
    }

    public MobyWebViewBuilder setSecondaryTextColor(String setSecondaryTextColor) {
        this.setSecondaryTextColor = setSecondaryTextColor;
        return this;
    }

    public MobyWebViewBuilder setAccessGPS(boolean accessGPS) {
        isAccessGPS = accessGPS;
        return this;
    }

    public MobyWebViewBuilder setAccessStorage(boolean accessStorage) {
        isAccessStorage = accessStorage;
        return this;
    }

    public MobyWebViewBuilder build() {
        return new MobyWebViewBuilder(url, isAccessGPS, isAccessStorage,
                setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor);
    }

    public void loadWebView() {
        if (LibApp.getContext() != null) {
            //Log.d("TTTT", "testttt.....");
            Intent intent = new Intent(LibApp.getContext(), MobyWebActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("isAccessGPS", isAccessGPS);
            intent.putExtra("isAccessStorage", isAccessStorage);
            intent.putExtra("setPrimaryColor", setPrimaryColor);
            intent.putExtra("setSecondaryColor", setSecondaryColor);
            intent.putExtra("setPrimaryTextColor", setPrimaryTextColor);
            intent.putExtra("setSecondaryTextColor", setSecondaryTextColor);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LibApp.getContext().startActivity(intent);
        }
    }
}
