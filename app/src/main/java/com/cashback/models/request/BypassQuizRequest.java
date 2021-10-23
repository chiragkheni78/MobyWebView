package com.cashback.models.request;

import com.google.gson.annotations.SerializedName;

public class BypassQuizRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fiAdId")
    private long adId;

    @SerializedName("device_unique_id")
    private int deviceUniqueId;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public void setDeviceUniqueId(int deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }
}
