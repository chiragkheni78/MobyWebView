package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class Advertisement {

    @SerializedName("fiBannerId")
    long bannerID;

    @SerializedName("fsImage")
    String imageUrl;

    @SerializedName("fiCategory")
    int categoryID;

    @SerializedName("fiAdId")
    long adID;

    public long getBannerID() {
        return bannerID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public long getAdID() {
        return adID;
    }
}
