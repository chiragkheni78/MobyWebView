package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class GetMiniProfileRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsReferrerCode")
    String referrer;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    public void setAction(String action) {
        this.action = action;
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

        if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
