package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class HelpRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsPage")
    private String fsPage;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsScreenType")
    private String screenType;

    public HelpRequest(String screenType) {
        this.screenType = screenType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setFsPage(String fsPage) {
        this.fsPage = fsPage;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setActivityID(String screenType) {
        this.screenType = screenType;
    }

    public String validateData(Context foContext) {
        if (screenType == null || screenType.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
