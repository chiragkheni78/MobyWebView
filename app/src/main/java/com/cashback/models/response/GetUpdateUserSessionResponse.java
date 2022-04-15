package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class GetUpdateUserSessionResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    public GetUpdateUserSessionResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }
}
