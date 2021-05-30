package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class WebviewDataRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsPage")
    private String pageName;


    public WebviewDataRequest(String fsPageName) {
        this.pageName = fsPageName;
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

    public String validateData(Context foContext) {
        if (deviceId == null || deviceId.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
