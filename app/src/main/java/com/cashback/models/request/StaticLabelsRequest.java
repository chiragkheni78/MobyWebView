package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class StaticLabelsRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    public void setAction(String action) {
        this.action = action;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String validateData(Context foContext) {
        if (deviceId.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
