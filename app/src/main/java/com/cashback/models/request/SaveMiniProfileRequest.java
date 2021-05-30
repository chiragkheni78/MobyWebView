package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class SaveMiniProfileRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fiAge")
    int age;

    @SerializedName("fsGender")
    String gender;

    @SerializedName("fiEWalletId")
    int eWalletId;

    public SaveMiniProfileRequest(int age, String gender, int eWalletId) {
        this.age = age;
        this.gender = gender;
        this.eWalletId = eWalletId;
    }

    @SerializedName("fsReferrerCode")
    String referrer;

    public void setAction(String action) {
        this.action = action;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void seteWalletId(int eWalletId) {
        this.eWalletId = eWalletId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String validateData(Context foContext) {

        if (age <= 0) {
            return Common.getDynamicText(foContext, "validate_age");
        } else if (eWalletId <= 0) {
            return Common.getDynamicText(foContext, "validate_eWallet");
        } else if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}