package com.cashback.models.request;

import com.cashback.models.Category;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfferFilterRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
