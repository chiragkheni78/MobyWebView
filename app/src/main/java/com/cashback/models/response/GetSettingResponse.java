package com.cashback.models.response;

import com.cashback.models.Advertisement;
import com.cashback.models.AppUpdate;
import com.cashback.models.EWallet;
import com.cashback.models.UserDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetSettingResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foAdvertisementList")
    private ArrayList<Advertisement> advertisementList;

    @SerializedName("foAppUpdates")
    private AppUpdate appUpdate;

    @SerializedName("fiMessageUpdateVersion")
    private int messageUpdateVersion;

    @SerializedName("fiQuizTimeInterval")
    private int quizTimeInterval;

    @SerializedName("fbIsAdsSliderReset")
    private boolean isResetAdvertisementSlider;

    @SerializedName("fsReferralCode")
    String referralCode;

    @SerializedName("fsReferralLink")
    String referralUrl;

    @SerializedName("fIZoomAndroid")
    int zoomLevel;

    @SerializedName("fiUnreadCount")
    int unreadMessageCount;

    @SerializedName("fsFirstTimeAlertMsg")
    String firstTimeAlertMsg;

    @SerializedName("fsFirstTimeAlertTitle")
    String firstTimeAlertTitle;

    @SerializedName("fbIsDeviceExist")
    private boolean isDeviceExist;

    public GetSettingResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Advertisement> getAdvertisementList() {
        return advertisementList;
    }

    public AppUpdate getAppUpdate() {
        return appUpdate;
    }

    public int getMessageUpdateVersion() {
        return messageUpdateVersion;
    }

    public int getQuizTimeInterval() {
        return quizTimeInterval;
    }

    public boolean isResetAdvertisementSlider() {
        return isResetAdvertisementSlider;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getReferralUrl() {
        return referralUrl;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public String getFirstTimeAlertMsg() {
        return firstTimeAlertMsg;
    }

    public String getFirstTimeAlertTitle() {
        return firstTimeAlertTitle;
    }

    public boolean isDeviceExist() {
        return isDeviceExist;
    }
}
