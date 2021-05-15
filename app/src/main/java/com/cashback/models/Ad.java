package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Ad {

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
    String coverageRadius;

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

    public long getAdID() {
        return adID;
    }

    public String getAdName() {
        return adName;
    }

    public String getAdType() {
        return adType;
    }

    public boolean getEngagedFlag() {
        return engagedFlag;
    }

    public String getProductName() {
        return productName;
    }

    public String getCoverageRadius() {
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
        return description;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public ArrayList<AdLocation> getLocationList() {
        return locationList;
    }


    /*details*/
    @SerializedName("description")
    String description;

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

    @SerializedName("cardType")
    String cardType;

    @SerializedName("cardName")
    String cardName;

    @SerializedName("fbIsAdEngageLimitOver")
    String isAdEngageLimitOver;

    @SerializedName("fsMsgEngageLimitOver")
    String engageLimitOverMessage;

    @SerializedName("fsCompanyUrl")
    String fsCompanyURL;

    @SerializedName("fsTC")
    String termsCondition;

    public String getCardType() {
        return cardType;
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
}