package com.cashback.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Activity implements LifecycleOwner {

    @SerializedName("fiActivityId")
    long activityID;

    @SerializedName("fsFlatCashBack")
    String flatCashBack;

    @SerializedName("fsDiscountUpTo")
    String discountUpTo;

    @SerializedName("fbIsBlinkShopOnline")
    boolean isBlinkShopOnline;

    @SerializedName("fbIsVirtualCash")
    boolean isVirtualCash;

    @SerializedName("fbIsCouponUsed")
    boolean isCouponUsed;

    @SerializedName("fsCouponCode")
    String couponCode;

    @SerializedName("fsAdLogo")
    String fsAdLogo;

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

    @SerializedName("fdQuizEnggagTime")
    String quizEngageDateTime;

    @SerializedName("fsRemainDay")
    String remainDay;

    @SerializedName("fsFlatCashBackAmazon")
    String flatCbAmazon;

    public long getActivityID() {
        return activityID;
    }

    public String getFlatCashBack() {
        return flatCashBack;
    }

    public String getDiscountUpTo() {
        return discountUpTo;
    }

    public void setBlinkShopOnline(boolean clickShopOnline) {
        isBlinkShopOnline = clickShopOnline;
    }

    public boolean isBlinkShopOnline() {
        return isBlinkShopOnline;
    }

    public boolean isVirtualCash() {
        return isVirtualCash;
    }

    public boolean isCouponUsed() {
        return isCouponUsed;
    }

    public String getCouponCode() {
        return couponCode;
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

    public String getAdLogo() {
        return fsAdLogo;
    }

    @SerializedName("fsWalletName")
    String walletName;

    @SerializedName("fsCouponDescription")
    String couponDescription;

    @SerializedName("fsOfferDetails")
    String offerDetails;

    @SerializedName("fsCouponType")
    String couponType;

    @SerializedName("fsCouponPassword")
    String couponPassword;

    @SerializedName("fsShopOnlineLink")
    String shopOnlineLink;

    @SerializedName("fiVirtualCashTransferDays")
    int virtualCashTransferDays;

    @SerializedName("foCouponList")
    ArrayList<Coupon> couponList;

    @SerializedName("foLocationList")
    ArrayList<AdLocation> locationList;

    @SerializedName("fiAdCouponType")
    int adCouponType;

    @SerializedName("fiMarkIsUsedType")
    int markAsUsedType;

    @SerializedName("fsQrCodePasswordText")
    String qrCodeText;

    @SerializedName("fsAdditionLabel")
    String additionLabel;

    public String getWalletName() {
        return walletName;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public String getCouponType() {
        return couponType;
    }

    public String getCouponPassword() {
        return couponPassword;
    }

    public String getShopOnlineLink() {
        return shopOnlineLink;
    }

    public int getVirtualCashTransferDays() {
        return virtualCashTransferDays;
    }

    public ArrayList<Coupon> getCouponList() {
        return couponList;
    }

    public ArrayList<AdLocation> getLocationList() {
        return locationList;
    }

    public void setCouponUsed(boolean couponUsed) {
        isCouponUsed = couponUsed;
    }

    public void setBillUploaded(boolean billUploaded) {
        isBillUploaded = billUploaded;
    }

    public int getAdCouponType() {
        return adCouponType;
    }

    public int getMarkAsUsedType() {
        return markAsUsedType;
    }

    public String getQrCodeText() {
        return qrCodeText;
    }

    public String getAdditionLabel() {
        return additionLabel;
    }

    public String getFlatCbAmazon() {
        return flatCbAmazon;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}


