package com.cashback.models.request;

import android.content.Context;

import com.cashback.models.EWallet;
import com.cashback.models.OfferFilter;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FetchOffersRequest {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("ffUserLat")
    private double latitude;

    @SerializedName("ffUserLng")
    private double longitude;

    @SerializedName("isMarketingAds")
    private boolean isMarketingAd;

    @SerializedName("fbIsBlockUser")
    private boolean isBlockUser;

    @SerializedName("fiPageViewType")
    private int pageViewType;

    @SerializedName("foFilterParams")
    OfferFilter offerFilter;


    public FetchOffersRequest(String mobileNumber, double latitude, double longitude, boolean isMarketingAd, boolean isBlockUser, int pageViewType) {
        this.mobileNumber = mobileNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isMarketingAd = isMarketingAd;
        this.isBlockUser = isBlockUser;
        this.pageViewType = pageViewType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setOfferFilter(OfferFilter offerFilter) {
        this.offerFilter = offerFilter;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMarketingAd(boolean marketingAd) {
        isMarketingAd = marketingAd;
    }

    public void setBlockUser(boolean blockUser) {
        isBlockUser = blockUser;
    }

    public void setPageViewType(int pageViewType) {
        this.pageViewType = pageViewType;
    }

    public String validateData(Context foContext) {
        if (deviceId == null || deviceId.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        } else if (pageViewType == Constants.OfferPage.MAP_VIEW.getValue() && (latitude <= 0 || longitude <= 0)) {
            return Common.getDynamicText(foContext, "validate_gps_coordinates");
        }
        return null;
    }
}
