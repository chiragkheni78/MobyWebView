package com.cashback.models.request;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class SyncTokenRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("user_contact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsToken")
    public String fcmToken;

    public void setAction(String action) {
        this.action = action;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
