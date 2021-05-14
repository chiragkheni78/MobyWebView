package com.cashback.models.response;

import com.cashback.models.Category;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfferFilterResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foCategoryList")
    private ArrayList<Category> categoryList;

    public OfferFilterResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Category> getCategoryList() {
        return categoryList;
    }
}
