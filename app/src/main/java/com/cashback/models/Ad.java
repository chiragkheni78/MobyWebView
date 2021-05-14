package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Ad {

    @SerializedName("fiAdId")
    int adID;

    @SerializedName("fsAdName")
    String adName;

    @SerializedName("fsAdType")
    String adType;

    @SerializedName("fbIsAdEngaged")
    int engagedFlag;

    @SerializedName("fsProductName")
    String productName;

    @SerializedName("fsCompanyLogo")
    String logoUrl;

    @SerializedName("fsCompanyBanner")
    String bannerUrl;

    @SerializedName("flCoverageRadius")
    String coverageRadius;

    @SerializedName("fsFlatCashBack")
    String flatCashBack;

    @SerializedName("fsDiscountUpTo")
    String discountUpTo;

    @SerializedName("ffAdDistance")
    double distance;

    @SerializedName("fsPinColor")
    String pinColor;

    @SerializedName("fiQuizReward")
    int quizReward;

    @SerializedName("fiSecondReward")
    int secondReward;

    @SerializedName("fiNormalReward")
    int normalRewardAmount;

    @SerializedName("description")
    String description;

    @SerializedName("campaign_name")
    String campaignName;

    @SerializedName("desc_1")
    String desc1;

    @SerializedName("desc_2")
    String desc2;

    @SerializedName("desc_3")
    String desc3;

    @SerializedName("desc_4")
    String desc4;


    @SerializedName("foLocationList")
    ArrayList<AdLocation> locationList;

    @SerializedName("foLocation")
    AdLocation location;

    public int getAdID() {
        return adID;
    }

    public String getAdName() {
        return adName;
    }

    public String getAdType() {
        return adType;
    }

    public int getEngagedFlag() {
        return engagedFlag;
    }

    public String getProductName() {
        return productName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
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

    public double getDistance() {
        return distance;
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

    public String getDesc1() {
        return desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public String getDesc3() {
        return desc3;
    }

    public String getDesc4() {
        return desc4;
    }

    public ArrayList<AdLocation> getLocationList() {
        return locationList;
    }

    public AdLocation getLocation() {
        return location;
    }



    /*details*/

    @SerializedName("cardType")
    String cardType;
    @SerializedName("cardName")
    String cardName;
    @SerializedName("terms_conditions_URL")
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
}

