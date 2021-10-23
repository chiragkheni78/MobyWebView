package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class BypassQuizResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("fsLastActivityId")
    private long activityID;

    public BypassQuizResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public long getActivityID() {
        return activityID;
    }
}
