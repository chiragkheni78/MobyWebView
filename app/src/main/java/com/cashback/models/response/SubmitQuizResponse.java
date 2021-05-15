package com.cashback.models.response;

import com.cashback.models.Message;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubmitQuizResponse {

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

    public SubmitQuizResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
