package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class Activity {

    @SerializedName("fiActivityId")
    long activityID;

    @SerializedName("fsFlatCashBack")
    String flatCashBack;

    @SerializedName("fsDiscountUpTo")
    String discountUpTo;

    @SerializedName("fbIsBlinkShopOnline")
    boolean isClickShopOnline;

    @SerializedName("fbIsVirtualCash")
    boolean isVirtualCash;

    @SerializedName("fbIsVerifiedCash")
    boolean isVerifiedCash;

    @SerializedName("fbIsCouponUsed")
    boolean isCouponUsed;

    @SerializedName("fsCouponCode")
    String fsCouponCode;

    @SerializedName("fbIsCouponExpired")
    boolean isCouponExpired;

    @SerializedName("fbIsBillUploaded")
    boolean isBillUploaded;

    @SerializedName("fbIsBillUploadEnable")
    boolean isBillUploadEnable;

    @SerializedName("fiQuizReward")
    int quizReward;

    @SerializedName("fiSecondReward")
    int secondReward;

    @SerializedName("fiNormalReward")
    int normalRewardAmount;

    @SerializedName("fiAdId")
    long adID;

    @SerializedName("fsAdName")
    String adName;

    @SerializedName("fsPinColor")
    String pinColor;

    @SerializedName("fbIsMarketingAd")
    boolean isMarketingAd;

    @SerializedName("fdQuizEngageTime")
    String quizEngageDateTime;

    @SerializedName("fsRemainDay")
    String remainDay;

    public long getActivityID() {
        return activityID;
    }

    public String getFlatCashBack() {
        return flatCashBack;
    }

    public String getDiscountUpTo() {
        return discountUpTo;
    }

    public boolean isClickShopOnline() {
        return isClickShopOnline;
    }

    public boolean isVirtualCash() {
        return isVirtualCash;
    }

    public boolean isVerifiedCash() {
        return isVerifiedCash;
    }

    public boolean isCouponUsed() {
        return isCouponUsed;
    }

    public String getFsCouponCode() {
        return fsCouponCode;
    }

    public boolean isCouponExpired() {
        return isCouponExpired;
    }

    public boolean isBillUploaded() {
        return isBillUploaded;
    }

    public boolean isBillUploadEnable() {
        return isBillUploadEnable;
    }

    public int getQuizReward() {
        return quizReward;
    }

    public int getSecondReward() {
        return secondReward;
    }

    public int getNormalRewardAmount() {
        return normalRewardAmount;
    }

    public long getAdID() {
        return adID;
    }

    public String getAdName() {
        return adName;
    }

    public String getPinColor() {
        return pinColor;
    }

    public boolean isMarketingAd() {
        return isMarketingAd;
    }

    public String getQuizEngageDateTime() {
        return quizEngageDateTime;
    }

    public String getRemainDay() {
        return remainDay;
    }
}
