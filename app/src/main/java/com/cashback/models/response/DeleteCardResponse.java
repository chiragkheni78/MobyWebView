package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class DeleteCardResponse {

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

    public DeleteCardResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
