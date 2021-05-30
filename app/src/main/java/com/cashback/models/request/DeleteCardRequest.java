package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class DeleteCardRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fiCardId")
    private int cardID;

    public DeleteCardRequest(int cardID) {
        this.cardID = cardID;
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
        if (cardID == 0) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
