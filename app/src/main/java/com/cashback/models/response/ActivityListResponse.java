package com.cashback.models.response;

import com.cashback.models.Activity;
import com.cashback.models.Ad;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ActivityListResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foActivityList")
    private ArrayList<Activity> activityList;

    @SerializedName("fbIsPendingBillUpload")
    private boolean isPendingBillUpload;

    @SerializedName("fiTotalBillVerified")
    private int totalVerifiedBill;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Activity> getActivityList() {
        return activityList;
    }

    public ActivityListResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isPendingBillUpload() {
        return isPendingBillUpload;
    }

    public int getTotalVerifiedBill() {
        return totalVerifiedBill;
    }
}
