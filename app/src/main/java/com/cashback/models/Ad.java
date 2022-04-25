package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Ad implements Serializable {

    @SerializedName("fiAdId")
    long adID;

    @SerializedName("fsAdName")
    String adName;

    @SerializedName("fsAdType")
    String adType;

    @SerializedName("fsPinColor")
    String pinColor;

    @SerializedName("fsProductName")
    String productName;

    @SerializedName("fbIsAdEngaged")
    boolean engagedFlag;

    @SerializedName("flCoverageRadius")
    long coverageRadius;

    @SerializedName("fsFlatCashBack")
    String flatCashBack;

    @SerializedName("fsDiscountUpTo")
    String discountUpTo;

    @SerializedName("fiQuizReward")
    int quizReward;

    @SerializedName("fiSecondReward")
    int secondReward;

    @SerializedName("fiNormalReward")
    int normalRewardAmount;

    @SerializedName("foLocationList")
    ArrayList<AdLocation> locationList;

    @SerializedName("fiOfferLeft")
    int offerLeft;

    @SerializedName("fbIsQuizFlow")
    boolean isQuizFlow;

    @SerializedName("fiAdCouponType")
    int adCouponType;

    public long getAdID() {
        return adID;
    }

    public String getAdName() {
        return adName;
    }

    public String getAdType() {
        return adType;
    }

    public void setEngagedFlag(boolean engagedFlag) {
        this.engagedFlag = engagedFlag;
    }

    public boolean getEngagedFlag() {
        return engagedFlag;
    }

    public String getProductName() {
        return productName;
    }

    public long getCoverageRadius() {
        return coverageRadius;
    }

    public String getFlatCashBack() {
        return flatCashBack;
    }

    public String getDiscountUpTo() {
        return discountUpTo;
    }

    public String getPinColor() {
        return pinColor;
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

    public String getDescription() {
        return description = (description != null) ? description : "";
    }

    public String getCampaignName() {
        return campaignName;
    }

    public ArrayList<AdLocation> getLocationList() {
        return locationList;
    }

    public int getOfferLeft() {
        return offerLeft;
    }

    /*details*/
    @SerializedName("fsDescription")
    String description;

    @SerializedName("fsOfferDetail")
    String offerDetails;

    @SerializedName("fsCompanyName")
    String campaignName;

    @SerializedName("fsAdLogo")
    String logoUrl;

    @SerializedName("fsAdBanner")
    String fsBannerURL;

    @SerializedName("fsDescription1")
    String desc1;

    @SerializedName("fsDescription2")
    String desc2;

    @SerializedName("fsDescription3")
    String desc3;

    @SerializedName("fsDescription4")
    String desc4;

    @SerializedName("fsCardType")
    String cardType;

    @SerializedName("fsCardName")
    String cardName;

    @SerializedName("fsCompanyUrl")
    String fsCompanyURL;

    @SerializedName("fsTC")
    String termsCondition;

    public String getCardType() {
        return cardType;
    }

    public String getOfferDetails() {
        return offerDetails = (offerDetails != null) ? offerDetails : "none";
    }

    public String getCardName() {
        return cardName;
    }

    public String getTermsCondition() {
        return termsCondition;
    }

    public String getDesc1() {
        return desc1.replace("<br/>", "\n");
    }

    public String getDesc2() {
        return desc2.replace("<br/>", "\n");
    }

    public String getDesc3() {
        return desc3.replace("<br/>", "\n");
    }

    public String getDesc4() {
        return desc4.replace("<br/>", "\n");
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getBannerUrl() {
        return fsBannerURL;
    }

    public boolean isQuizFlow() {
        return isQuizFlow;
    }

    public int getAdCouponType() {
        return adCouponType;
    }
}