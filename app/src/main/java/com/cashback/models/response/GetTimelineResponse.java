package com.cashback.models.response;

import com.cashback.models.Activity;
import com.cashback.models.Ad;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetTimelineResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foTimelineList")
    private ArrayList<Activity> activityList;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Activity> getActivityList() {
        return activityList;
    }

    public GetTimelineResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
