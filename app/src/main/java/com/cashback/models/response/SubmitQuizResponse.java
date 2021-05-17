package com.cashback.models.response;

import com.cashback.models.Message;
import com.cashback.models.SuccessMessage;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubmitQuizResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("fiCreditAmount")
    private int creditAmount;

    @SerializedName("fiWalletAmount")
    private int walletAmount;

    @SerializedName("fbIsDisplayRateNow")
    private boolean isDisplayRateNow;

    @SerializedName("fbIsMarketingAd")
    private boolean isMarketingAd;

    @SerializedName("fsDisplayAdsMessage")
    SuccessMessage successMessage;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public int getCreditAmount() {
        return creditAmount;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public boolean isDisplayRateNow() {
        return isDisplayRateNow;
    }

    public boolean isMarketingAd() {
        return isMarketingAd;
    }

    public SuccessMessage getSuccessMessage() {
        return successMessage;
    }

    public SubmitQuizResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
