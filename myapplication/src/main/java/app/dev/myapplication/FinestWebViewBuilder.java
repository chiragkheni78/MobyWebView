package app.dev.myapplication;

import android.content.Intent;
import android.util.Log;

public class FinestWebViewBuilder {
    String url;
    // Activity context;
    boolean isAccessGPS;
    boolean isAccessStorage;
    String setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor;

    public FinestWebViewBuilder(String url, boolean isLocationOn, boolean isWritePermission,
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

    public FinestWebViewBuilder() {

    }

   /* public FinestWebViewBuilder(Activity context) {
        this.context = context;
    }*/

    public FinestWebViewBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public FinestWebViewBuilder setPrimaryColor(String setPrimaryColor) {
        this.setPrimaryColor = setPrimaryColor;
        return this;
    }

    public FinestWebViewBuilder setSecondaryColor(String setSecondaryColor) {
        this.setSecondaryColor = setSecondaryColor;
        return this;
    }

    public FinestWebViewBuilder setPrimaryTextColor(String setPrimaryTextColor) {
        this.setPrimaryTextColor = setPrimaryTextColor;
        return this;
    }

    public FinestWebViewBuilder setSecondaryTextColor(String setSecondaryTextColor) {
        this.setSecondaryTextColor = setSecondaryTextColor;
        return this;
    }

    public FinestWebViewBuilder setAccessGPS(boolean accessGPS) {
        isAccessGPS = accessGPS;
        return this;
    }

    public FinestWebViewBuilder setAccessStorage(boolean accessStorage) {
        isAccessStorage = accessStorage;
        return this;
    }

    public FinestWebViewBuilder build() {
        return new FinestWebViewBuilder(url, isAccessGPS, isAccessStorage,
                setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor);
    }

    public void loadWebView() {
        if (LibApp.getContext() != null) {
            //Log.d("TTTT", "testttt.....");
            Intent intent = new Intent(LibApp.getContext(), WebActivity.class);
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
