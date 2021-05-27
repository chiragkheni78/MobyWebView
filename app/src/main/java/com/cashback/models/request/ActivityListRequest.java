package com.cashback.models.request;

import android.content.Context;

import com.cashback.models.OfferFilter;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.gson.annotations.SerializedName;

public class ActivityListRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsOrderBy")
    private String orderBy;

    @SerializedName("fsFilter")
    private String filter;

    public ActivityListRequest(String mobileNumber, String orderBy, String fsFilter) {
        this.mobileNumber = mobileNumber;
        this.orderBy = orderBy;
        this.filter = fsFilter;
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

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String validateData(Context foContext) {
        if (deviceId == null || deviceId.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
