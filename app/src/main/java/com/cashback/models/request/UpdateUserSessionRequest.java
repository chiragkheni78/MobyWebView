package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class UpdateUserSessionRequest {

    @SerializedName("fsEventType")
    String eventType;

    @SerializedName("fsAction")
    String action;

    @SerializedName("fiAdId")
    String adId;

    @SerializedName("fsUserContact")
    String phoneNumber;

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String validateData(Context foContext) {

        if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
