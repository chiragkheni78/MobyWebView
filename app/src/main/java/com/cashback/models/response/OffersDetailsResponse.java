package com.cashback.models.response;

import com.cashback.models.Ad;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OffersDetailsResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foAdOffer")
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

    public OffersDetailsResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
