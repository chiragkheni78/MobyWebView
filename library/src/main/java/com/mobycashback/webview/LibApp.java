package com.mobycashback.webview;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

public class LibApp extends Application {

    /**
     * Instance of the current application.
     */
    private static LibApp instance;

    /**
     * Constructor.
     */
    public LibApp() {
        instance = this;
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    public static Context getContext() {
        return instance;
    }

    public static String getDeviceUniqueId() {
        return Settings.Secure.getString(LibApp.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}