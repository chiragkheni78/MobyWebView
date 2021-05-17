package com.cashback;

import android.app.Application;
import android.content.Context;

public class AppGlobal extends Application {


    public static boolean isDisplayRewardNote = false;
    public static boolean isSearchButtonBlink = true;

    public static Context moContext;

    @Override
    public void onCreate() {
        super.onCreate();
        moContext = this;
    }

    public static Context getInstance(){
        return moContext;
    }
}
