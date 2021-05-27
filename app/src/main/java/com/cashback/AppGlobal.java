package com.cashback;

import android.app.Application;
import android.content.Context;

import com.adgyde.android.AdGyde;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppGlobal extends Application {


    public static boolean isDisplayRewardNote = false;
    public static boolean isSearchButtonBlink = true;

    public static Context moContext;

    @Override
    public void onCreate() {
        super.onCreate();
        moContext = this;
        configAdGyde();
    }

    public static Context getInstance(){
        return moContext;
    }

    private void configAdGyde() {
//        AdGyde.init(this, Constants.ADGYDE_APP_KEY, "Organic");
//        AdGyde.setDebugEnabled(true);
    }

    public static FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getPhoneNumber() {
        if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null){
            return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        }
        return "";
    }

    public static SharedPreferenceManager getPreferenceManager(){
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(getInstance());
        return  loSharedPreferenceManager;
    }

    public static void logOutUser() {
        if (FirebaseAuth.getInstance() != null){
            getPreferenceManager().clear();
            FirebaseAuth.getInstance().signOut();
        }
    }
}
