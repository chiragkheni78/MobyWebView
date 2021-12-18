package com.cashback.utils;

import android.content.Context;
import android.os.Bundle;

import com.adgyde.android.AdGyde;
import com.cashback.AppGlobal;
import com.cashback.models.Activity;
import com.cashback.models.Ad;

import java.util.HashMap;
import java.util.Map;

public class AdGydeEvents {

    public static void saveProfile(Context foContext, int fiAge, String lsGender) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putInt("age", fiAge);
        bundle.putString("gender", lsGender);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", "saveProfile");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("saveProfile", params); //eventid,params

        if (lsGender.equalsIgnoreCase(Constants.Gender.MALE.getValue())) {
            AdGyde.setGender(foContext, AdGyde.M);
        } else {
            AdGyde.setGender(foContext, AdGyde.F);
        }
        AdGyde.setAge(foContext, fiAge);
    }

    public static void offerLoaded(Context foContext) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        // bundle.putLong("offerID", flOfferID);
        // bundle.putString("offerName", foOffer.getAdName());

        Map<String, String> loParams = new HashMap<>();
        loParams.put("user_id", "offerLoaded");
        loParams.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("offerLoaded", loParams);
    }

    public static void otpVerified(Context foContext) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));

        Map<String, String> loParams = new HashMap<>();
        loParams.put("user_id", "otpVerified");
        loParams.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("otpVerified", loParams);
    }

    public static void shareApp(Context foContext, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putString("shareApp", type);

        Map<String, String> loParams = new HashMap<>();
        loParams.put("user_id", "shareApp");
        loParams.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("shareApp", loParams);
    }

    public static void billTracked(Context foContext) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));

        Map<String, String> loParams = new HashMap<>();
        loParams.put("user_id", "billTracked");
        loParams.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("billTracked", loParams);
    }

    public static void shopOnlineClicked(Context foContext, Activity foActivity) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putLong("offerID", foActivity.getAdID());
        bundle.putString("offerName", foActivity.getAdName());
        bundle.putLong("activityID", foActivity.getActivityID());

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", "shopOnlineClicked");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("shopOnlineClicked", params);
    }

    public static void getOfferClicked(Context foContext, Activity foActivity) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putLong("offerID", foActivity.getAdID());
        bundle.putString("offerName", foActivity.getAdName());
        bundle.putLong("activityID", foActivity.getActivityID());

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", "getOfferClicked");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("getOfferClicked", params);
    }

    public static void redirectToURL(Context foContext, String fsURL) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putString("url", fsURL);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", "redirectToURL");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("redirectToURL", params);
    }
}
