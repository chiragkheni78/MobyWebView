package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class DealOfTheDayResponse {

    @SerializedName("fiBannerId")
    private int fiBannerId;

    @SerializedName("fsImage")
    private String fsImage;

    @SerializedName("fiCategory")
    private int fiCategory;

    @SerializedName("fiAdId")
    private int fiAdId;

    public int getBannerId() {
        return fiBannerId;
    }

    public void setBannerId(int fiBannerId) {
        this.fiBannerId = fiBannerId;
    }

    public String getImage() {
        return fsImage;
    }

    public void setImage(String fsImage) {
        this.fsImage = fsImage;
    }

    public int getCategory() {
        return fiCategory;
    }

    public void setCategory(int fiCategory) {
        this.fiCategory = fiCategory;
    }

    public int getAdId() {
        return fiAdId;
    }

    public void setAdId(int fiAdId) {
        this.fiAdId = fiAdId;
    }
}
