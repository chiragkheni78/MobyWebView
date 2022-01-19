package com.cashback;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.adgyde.android.AdGyde;
import com.cashback.models.Advertisement;
import com.cashback.models.Category;
import com.cashback.models.response.DealOfTheDayResponse;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

public class AppGlobal extends Application {


    public static boolean isDisplayRewardNote = false;
    public static boolean isSearchButtonBlink = true;
    public static boolean isDealBannerClosed = false;
    public static boolean isNewUser = false;

    public static Context moContext;
    public static ArrayList<Category> moCategories = new ArrayList<>();
    public static ArrayList<Category> moMainStore = new ArrayList<>();
    public static ArrayList<DealOfTheDayResponse> moDealOfTheDayResponse;
    public static ArrayList<Advertisement> moSharePageImages;
    public static Location moLocation;

    public static int getTotalBillVerified() {
        return fiTotalBillVerified;
    }

    public static void setTotalBillVerified(int fiTotalBillVerified) {
        AppGlobal.fiTotalBillVerified = fiTotalBillVerified;
    }

    private static FirebaseAnalytics moFirebaseAnalytics;
    public static int fiTotalBillVerified;

    @Override
    public void onCreate() {
        super.onCreate();
        moContext = this;
        moFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initFacebookSDK();
        enableCrashlytics();
        configAdGyde();
        setDefaultReferrer();
    }

    private void initFacebookSDK() {
        FacebookSdk.fullyInitialize();
//        FacebookSdk.setIsDebugEnabled(true);
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return moFirebaseAnalytics;
    }

    private void enableCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        //enabled only for signed builds
    }

    private void setDefaultReferrer() {
        getPreferenceManager().setAppDownloadCampaign(Constants.DEFAULT_REFERRAL_CODE);
    }

    public static Context getInstance() {
        return moContext;
    }

    public static SharedPreferenceManager getPreferenceManager() {
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(getInstance());
        return loSharedPreferenceManager;
    }

    private void configAdGyde() {
        if (!Constants.DEFAULT_REFERRAL_CODE.equalsIgnoreCase("SAMSUNG")) {
            AdGyde.init(this, Constants.ADGYDE_APP_KEY, "Organic");
            AdGyde.setDebugEnabled(false);
        }
    }

    public static String getPhoneNumber() {
        return getPreferenceManager().getPhoneNumber();
    }

    public static void logOutUser() {
        getPreferenceManager().clear();
    }

    public static ArrayList<Category> getCategories() {
        return moCategories;
    }

    public static ArrayList<Category> getMoMainStore() {
        return moMainStore;
    }

    public static void setCategories(ArrayList<Category> loCategories) {
        moCategories = loCategories;
    }

    public static void setMainStore(ArrayList<Category> loCategories) {
        moMainStore = loCategories;
    }

    public static ArrayList<DealOfTheDayResponse> getDealOfTheDayResponse() {
        return moDealOfTheDayResponse;
    }

    public static void setDealOfTheDayResponse(ArrayList<DealOfTheDayResponse> moDealOfTheDayResponse) {
        AppGlobal.moDealOfTheDayResponse = moDealOfTheDayResponse;
    }

    public static ArrayList<Advertisement> getSharePageImages() {
        return moSharePageImages;
    }

    public static void setSharePageImages(ArrayList<Advertisement> foImageList) {
        AppGlobal.moSharePageImages = foImageList;
    }

    public static Location getLocation() {
        return moLocation;
    }

    public static void setLocation(Location location) {
        AppGlobal.moLocation = location;
    }
}
