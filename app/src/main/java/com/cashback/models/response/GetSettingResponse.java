package com.cashback.models.response;

import com.cashback.models.Advertisement;
import com.cashback.models.AppUpdate;
import com.cashback.models.Category;
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

    @SerializedName("fbIsUserExist")
    private boolean isUserExist;

    @SerializedName("foCategoryList")
    private ArrayList<Category> categoryList;

    @SerializedName("fiOfferListPageSize")
    int offerListPageSize;

    @SerializedName("fiTotalBillVerified")
    int totalBillVerified;


    @SerializedName("foDealOfTheDays")
    private ArrayList<DealOfTheDayResponse> dealsOfTheDay;

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

    public ArrayList<Category> getCategoryList() {
        return categoryList;
    }

    public int getOfferListPageSize() {
        return offerListPageSize;
    }

    public ArrayList<DealOfTheDayResponse> getDealsOfTheDay() {
        return dealsOfTheDay;
    }

    public void setDealsOfTheDay(ArrayList<DealOfTheDayResponse> foDealOfTheDays) {
        this.dealsOfTheDay = foDealOfTheDays;
    }

    public int getTotalBillVerified() {
        return totalBillVerified;
    }

    public void setTotalBillVerified(int fiTotalBillVerified) {
        this.totalBillVerified = fiTotalBillVerified;
    }

    public boolean isUserExist() {
        return isUserExist;
    }

    public void setUserExist(boolean userExist) {
        isUserExist = userExist;
    }
}
