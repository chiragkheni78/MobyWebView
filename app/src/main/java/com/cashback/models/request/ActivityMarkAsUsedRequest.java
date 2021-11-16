package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class ActivityMarkAsUsedRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fiActivityId")
    private long activityID;

    @SerializedName("fiAmount")
    int amount;

    public ActivityMarkAsUsedRequest(String mobileNumber, long activityID) {
        this.mobileNumber = mobileNumber;
        this.activityID = activityID;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setActivityID(long activityID) {
        this.activityID = activityID;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String validateData(Context foContext) {
        if (deviceId == null || deviceId.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
