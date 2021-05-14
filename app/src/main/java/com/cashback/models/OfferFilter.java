package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class OfferFilter {

    @SerializedName("fsSearchString")
    private String searchString;

    @SerializedName("fiCurrentPage")
    private int currentPage;

    @SerializedName("fiCategoryId")
    private int categoryId;

    @SerializedName("fsAdRewardAmount")
    private String adRewardAmount;

    @SerializedName("fiAdType")
    private int adType;

    @SerializedName("fsPinColor")
    private String pinColors;

    public OfferFilter(String searchString, int currentPage, int categoryId) {
        this.searchString = searchString;
        this.currentPage = currentPage;
        this.categoryId = categoryId;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setAdRewardAmount(String adRewardAmount) {
        this.adRewardAmount = adRewardAmount;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public void setPinColors(String pinColors) {
        this.pinColors = pinColors;
    }
}