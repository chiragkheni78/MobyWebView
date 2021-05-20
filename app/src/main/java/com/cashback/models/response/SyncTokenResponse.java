package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class SyncTokenResponse {

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

}
