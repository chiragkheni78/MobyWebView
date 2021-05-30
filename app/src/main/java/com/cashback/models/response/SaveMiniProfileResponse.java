package com.cashback.models.response;

import com.cashback.models.UserDetails;
import com.google.gson.annotations.SerializedName;

public class SaveMiniProfileResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foUserDetails")
    private UserDetails userDetails;

    public SaveMiniProfileResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}
