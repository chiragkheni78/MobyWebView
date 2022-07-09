package com.cashback.models;

public class MapMarker {

    long adID;

    String adName;

    String adType;

    String pinColor;
    String discountUpTo;
    String flatCashBack;

    String productName;

    boolean engagedFlag;

    int quizReward;

    int secondReward;

    int normalRewardAmount;

    long locationID;

    double latitude;

    double longitude;

    String landmark;
    String adLogo;

    String providerCashLabel;

    public String getDiscountUpTo() {
        return discountUpTo;
    }

    public String getFlatCashBack() {
        return flatCashBack;
    }

    public String getProviderCashLabel() {
        return providerCashLabel;
    }

    public void setDiscountUpTo(String discountUpTo) {
        this.discountUpTo = discountUpTo;
    }

    public void setFlatCashBack(String flatCashBack) {
        this.flatCashBack = flatCashBack;
    }

    public void setProviderCashLabel(String providerCashLabel){
        this.providerCashLabel = providerCashLabel;
    }

    public String getAdLogo() {
        return adLogo;
    }

    public void setAdLogo(String adLogo) {
        this.adLogo = adLogo;
    }

    public long getAdID() {
        return adID;
    }

    public void setAdID(long adID) {
        this.adID = adID;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getPinColor() {
        return pinColor;
    }

    public void setPinColor(String pinColor) {
        this.pinColor = pinColor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isEngagedFlag() {
        return engagedFlag;
    }

    public void setEngagedFlag(boolean engagedFlag) {
        this.engagedFlag = engagedFlag;
    }

    public int getQuizReward() {
        return quizReward;
    }

    public void setQuizReward(int quizReward) {
        this.quizReward = quizReward;
    }

    public int getTotalReward() {
        if (getQuizReward() < getSecondReward()) {
            return getSecondReward() + getQuizReward();
        } else {
            return getQuizReward() + getQuizReward();
        }
    }

    public int getSecondReward() {
        return secondReward;
    }

    public void setSecondReward(int secondReward) {
        this.secondReward = secondReward;
    }

    public int getNormalRewardAmount() {
        return normalRewardAmount;
    }

    public void setNormalRewardAmount(int normalRewardAmount) {
        this.normalRewardAmount = normalRewardAmount;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
