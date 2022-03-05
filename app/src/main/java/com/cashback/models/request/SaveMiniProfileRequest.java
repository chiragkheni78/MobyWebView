package com.cashback.models.request;

import android.content.Context;
import android.text.TextUtils;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

import kotlin.text.Regex;

public class SaveMiniProfileRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsUserContact")
    private String userContact;

    @SerializedName("fiAge")
    int age;

    @SerializedName("fsGender")
    String gender;

    @SerializedName("fiEWalletId")
    int eWalletId;

    @SerializedName("fsUPILink")
    String upiLink;

    @SerializedName("fsPaytmMobile")
    String paytmMobile;

    boolean isCheckedMobile;

    @SerializedName("fsReferrerCode")
    String referrer;

    @SerializedName("fsUtmSource")
    String source;

    @SerializedName("fsUtmMedium")
    String medium;

    public SaveMiniProfileRequest(int age, String gender, int eWalletId, String upiLink,
                                  String paytmNumber, String phoneNumber, boolean isCheckedMobile, String lsSawOurAds) {
        this.age = age;
        this.gender = gender;
        this.eWalletId = eWalletId;
        this.upiLink = upiLink;
        this.paytmMobile = paytmNumber;
        this.userContact = phoneNumber;
        this.isCheckedMobile = isCheckedMobile;
        this.referrer = lsSawOurAds;
    }

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

    public void setUtmSource(String source) {
        this.source = source;
    }

    public void setUtmMedium(String medium) {
        this.medium = medium;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public void setPaytmMobile(String paytmMobile) {
        this.paytmMobile = paytmMobile;
    }

    public String validateData(Context foContext) {
        Regex reg = new Regex("^[0-9]{10}$");
        if (TextUtils.isEmpty(userContact) || !reg.matches(userContact)) {
            return "Please enter valid mobile number";
        } else if (age <= 0) {
            return Common.getDynamicText(foContext, "validate_age");
        } else if (gender == null) {
            return Common.getDynamicText(foContext, "validate_gender");
        } else if (eWalletId <= 0) {
            return Common.getDynamicText(foContext, "validate_eWallet");
        } else if (eWalletId == 2 && (upiLink.isEmpty() || !Common.validateUPI(upiLink))) {
            return Common.getDynamicText(foContext, "UPI_alert");
        }/* else if (eWalletId == 1 && (paytmMobile.isEmpty() || paytmMobile.length() < 10)) {
            return Common.getDynamicText(foContext, "UPI_alert");
        } */ else if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        } else if (eWalletId == 1 && !isCheckedMobile) {
            if (TextUtils.isEmpty(paytmMobile) || !reg.matches(paytmMobile)) {
                return "Please enter valid paytm mobile number";// TODO: 02-03-2022
            }
        }
        return null;
    }
}