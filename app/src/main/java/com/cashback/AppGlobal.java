package com.cashback;

import android.app.Application;
import android.content.Context;

import com.cashback.models.Category;
import com.cashback.models.response.DealOfTheDayResponse;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AppGlobal extends Application {


    public static boolean isDisplayRewardNote = false;
    public static boolean isSearchButtonBlink = true;

    public static Context moContext;
    public static ArrayList<Category> moCategories = new ArrayList<>();
    public static DealOfTheDayResponse moDealOfTheDayResponse = new DealOfTheDayResponse();

    public static int getFiTotalBillVerified() {
        return fiTotalBillVerified;
    }

    public static void setFiTotalBillVerified(int fiTotalBillVerified) {
        AppGlobal.fiTotalBillVerified = fiTotalBillVerified;
    }

    public static int fiTotalBillVerified;

    @Override
    public void onCreate() {
        super.onCreate();
        moContext = this;
        configAdGyde();
        setDefaultReferrer();
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
//        AdGyde.init(this, Constants.ADGYDE_APP_KEY, "Organic");
//        AdGyde.setDebugEnabled(false);
    }

    public static FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getPhoneNumber() {
        if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        } else {
            return getPreferenceManager().getPhoneNumber();
        }
    }

    public static void logOutUser() {
        if (FirebaseAuth.getInstance() != null) {
            getPreferenceManager().clear();
            FirebaseAuth.getInstance().signOut();
        }
    }

    public static ArrayList<Category> getCategories() {
        return moCategories;
    }

    public static void setCategories(ArrayList<Category> loCategories) {
        moCategories = loCategories;
    }

    public static DealOfTheDayResponse getMoDealOfTheDayResponse() {
        return moDealOfTheDayResponse;
    }

    public static void setMoDealOfTheDayResponse(DealOfTheDayResponse moDealOfTheDayResponse) {
        AppGlobal.moDealOfTheDayResponse = moDealOfTheDayResponse;
    }
}
