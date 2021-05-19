package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class UpdateShopOnlineBlinkResponse {

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

    public UpdateShopOnlineBlinkResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
