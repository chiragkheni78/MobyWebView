package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class GetMiniProfileRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsReferrelCode")
    String referralCode;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    public void setAction(String action) {
        this.action = action;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String validateData(Context foContext) {

        if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
