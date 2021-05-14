package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.gson.annotations.SerializedName;

public class MessageListRequest {

    @SerializedName("user_contact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String validateData(Context foContext) {
        if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
