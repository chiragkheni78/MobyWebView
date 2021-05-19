package com.cashback.models.response;

import com.cashback.models.Activity;
import com.google.gson.annotations.SerializedName;

public class ActivityMarkAsUsedResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ActivityMarkAsUsedResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
