package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class MyCashHistory {

    @SerializedName("fsTitle")
    private String title;

    @SerializedName("fsRemainAmount")
    private double remainAmount;

    @SerializedName("fsUsedAmount")
    private double usedAmount;

    @SerializedName("fsExpiryDate")
    private String expiryDate;

    public String getTitle() {
        return title;
    }

    public double getRemainAmount() {
        return remainAmount;
    }

    public double getUsedAmount() {
        return usedAmount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
