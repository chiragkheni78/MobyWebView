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
        params.put("user_id", "OPEN_REGISTER");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("OPEN_REGISTER", params); //eventid,params


        if (lsGender.equalsIgnoreCase(Constants.Gender.MALE.getValue())) {
            AdGyde.setGender(foContext, AdGyde.M);
        } else {
            AdGyde.setGender(foContext, AdGyde.F);
        }
        AdGyde.setAge(foContext, fiAge);
    }

    public static void offerEngaged(Context foContext, Ad foOffer) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putLong("offerID", foOffer.getAdID());
        bundle.putString("offerName", foOffer.getAdName());

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", "ENGAGED");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("ENGAGED", params); //eventid,params
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
        AdGyde.onPermanentUnique("shopOnlineClicked", params); //eventid, params
    }

    public static void purchased(Context foContext, long flOfferID) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        bundle.putString("deviceID", Common.getDeviceUniqueId(foContext));
        bundle.putLong("offerID", flOfferID);
       // bundle.putString("offerName", foOffer.getAdName());

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", "USER_SHOPPED");
        params.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("USER_SHOPPED", params); //eventid, params
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
        // bundle.putLong("offerID", flOfferID);
        // bundle.putString("offerName", foOffer.getAdName());

        Map<String, String> loParams = new HashMap<>();
        loParams.put("user_id", "otpVerified");
        loParams.put("miscellaneous", bundle.toString());
        AdGyde.onPermanentUnique("otpVerified", loParams);
    }


}
