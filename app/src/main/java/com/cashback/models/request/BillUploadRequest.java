package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BillUploadRequest implements Serializable {

    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fiActivityId")
    long activityId;

    @SerializedName("fdTransactionDate")
    String transactionDate;

    @SerializedName("fiAmount")
    int amount;

    public BillUploadRequest(String mobileNumber, long activityId, String transactionDate, int amount) {
        this.mobileNumber = mobileNumber;
        this.activityId = activityId;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String validateData(Context foContext) {
        if (activityId == 0) {
            return Common.getDynamicText(foContext, "contact_support");
        } else if (transactionDate == null || transactionDate.isEmpty()) {
            return Common.getDynamicText(foContext, "validate_transaction_date");
        } else if (amount <= 0) {
            return Common.getDynamicText(foContext, "validate_transaction_amount");
        }
        return null;
    }
}
