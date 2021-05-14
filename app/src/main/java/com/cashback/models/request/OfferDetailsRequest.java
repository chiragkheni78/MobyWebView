package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class OfferDetailsRequest {

    @SerializedName("user_contact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("flAdId")
    private long offerId;

    @SerializedName("flLocationId")
    private long locationId;

    public OfferDetailsRequest(String mobileNumber, long offerId, long locationId) {
        this.mobileNumber = mobileNumber;
        this.offerId = offerId;
        this.locationId = locationId;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String validateData(Context foContext) {
        if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        } else if (offerId == 0 || locationId == 0){
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}
