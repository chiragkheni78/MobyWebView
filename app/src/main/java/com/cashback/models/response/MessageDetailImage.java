package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class MessageDetailImage {

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
