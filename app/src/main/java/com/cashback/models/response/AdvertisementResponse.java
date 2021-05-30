package com.cashback.models.response;

import com.cashback.models.Advertisement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdvertisementResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foAdvertisementList")
    private ArrayList<Advertisement> advertisementList;

    public AdvertisementResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Advertisement> getAdvertisementList() {
        return advertisementList;
    }

}
