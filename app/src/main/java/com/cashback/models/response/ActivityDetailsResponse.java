package com.cashback.models.response;

import com.cashback.models.Activity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ActivityDetailsResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foActivity")
    private Activity activity;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public Activity getActivity() {
        return activity;
    }

    public ActivityDetailsResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
