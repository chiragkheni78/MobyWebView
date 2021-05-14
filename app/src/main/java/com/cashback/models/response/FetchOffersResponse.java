package com.cashback.models.response;

import com.cashback.models.Ad;
import com.cashback.models.EWallet;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FetchOffersResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foOfferList")
    private ArrayList<Ad> offerList;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Ad> getOfferList() {
        return offerList;
    }

    public FetchOffersResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
