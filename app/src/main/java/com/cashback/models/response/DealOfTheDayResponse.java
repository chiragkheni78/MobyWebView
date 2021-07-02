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

    public int getFiBannerId() {
        return fiBannerId;
    }

    public void setFiBannerId(int fiBannerId) {
        this.fiBannerId = fiBannerId;
    }

    public String getFsImage() {
        return fsImage;
    }

    public void setFsImage(String fsImage) {
        this.fsImage = fsImage;
    }

    public int getFiCategory() {
        return fiCategory;
    }

    public void setFiCategory(int fiCategory) {
        this.fiCategory = fiCategory;
    }

    public int getFiAdId() {
        return fiAdId;
    }

    public void setFiAdId(int fiAdId) {
        this.fiAdId = fiAdId;
    }
}
