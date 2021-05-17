package com.cashback.models.request;

import android.content.Context;

import com.cashback.models.OfferFilter;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.gson.annotations.SerializedName;

public class GetTimelineRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("user_contact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsOrderBy")
    private String orderBy;

    @SerializedName("fsFilter")
    private String filter;


    public String validateData(Context foContext) {
        if (deviceId == null || deviceId.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
