package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class BillUploadResponse {

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

    public BillUploadResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
