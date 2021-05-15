package com.cashback.models.response;

import com.cashback.models.Ad;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfferDetailsResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

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

    public OfferDetailsResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}