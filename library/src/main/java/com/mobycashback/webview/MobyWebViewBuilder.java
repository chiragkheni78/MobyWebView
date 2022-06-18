package com.mobycashback.webview;

import android.content.Intent;

import com.mobycashback.utils.Common;

public class MobyWebViewBuilder {
    String url;
    boolean isAccessGPS;
    boolean isAccessStorage;
    boolean isCategories, isFromCategory = false;
    String setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor;

    public MobyWebViewBuilder(String url, boolean isLocationOn, boolean isWritePermission, boolean isCategories,
                              String setPrimaryColor, String setSecondaryColor,
                              String setPrimaryTextColor, String setSecondaryTextColor, boolean isFromCategory) {
        this.url = url;
        this.isAccessGPS = isLocationOn;
        this.isAccessStorage = isWritePermission;
        this.setPrimaryColor = setPrimaryColor;
        this.setSecondaryColor = setSecondaryColor;
        this.setPrimaryTextColor = setPrimaryTextColor;
        this.setSecondaryTextColor = setSecondaryTextColor;
        this.isCategories = isCategories;
        this.isFromCategory = isFromCategory;
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

    public MobyWebViewBuilder setCategories(boolean categories) {
        isCategories = categories;
        return this;
    }

    public MobyWebViewBuilder setFromCategory(boolean isFromCategory) {
        this.isFromCategory = isFromCategory;
        return this;
    }

    public MobyWebViewBuilder build() {
        return new MobyWebViewBuilder(url, isAccessGPS, isAccessStorage, isCategories,
                setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor, isFromCategory);
    }

    public void loadWebView() {
        if (LibApp.getContext() != null) {
            if (isCategories) {
                Intent intent = new Intent(LibApp.getContext(), CategoriesActivity.class);
                intent.putExtra(Common.URL, url);
                intent.putExtra(Common.ACCESS_GPS, isAccessGPS);
                intent.putExtra(Common.ACCESS_STORAGE, isAccessStorage);
                intent.putExtra(Common.PRIMARY_COLOR, setPrimaryColor);
                intent.putExtra(Common.SECONDARY_COLOR, setSecondaryColor);
                intent.putExtra(Common.PRIMARY_TEXT_COLOR, setPrimaryTextColor);
                intent.putExtra(Common.SECONDARY_TEXT_COLOR, setSecondaryTextColor);
                intent.putExtra(Common.CATEGORY, isCategories);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LibApp.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(LibApp.getContext(), MobyWebActivity.class);
                intent.putExtra(Common.URL, String.valueOf(url));
                intent.putExtra(Common.ACCESS_GPS, String.valueOf(isAccessGPS));
                intent.putExtra(Common.ACCESS_STORAGE, String.valueOf(isAccessStorage));
                intent.putExtra(Common.PRIMARY_COLOR, String.valueOf(setPrimaryColor));
                intent.putExtra(Common.SECONDARY_COLOR, String.valueOf(setSecondaryColor));
                intent.putExtra(Common.PRIMARY_TEXT_COLOR, String.valueOf(setPrimaryTextColor));
                intent.putExtra(Common.SECONDARY_TEXT_COLOR, String.valueOf(setSecondaryTextColor));
                intent.putExtra(Common.CATEGORY, isCategories);
                if (!isFromCategory) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LibApp.getContext().startActivity(intent);
            }
        }
    }
}
