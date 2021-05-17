package com.cashback.models.response;

import com.cashback.models.Ad;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfferDetailsResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("fbIsEngageLimitOver")
    boolean isAdEngageLimitOver;

    @SerializedName("fsMsgEngageLimitOver")
    String engageLimitOverMessage;

    @SerializedName("foOfferDetails")
    private Ad offer;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public Ad getOffer() {
        return offer;
    }

    public boolean isAdEngageLimitOver() {
        return isAdEngageLimitOver;
    }

    public String getEngageLimitOverMessage() {
        return engageLimitOverMessage;
    }

    public OfferDetailsResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}