package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class AppUpdate {

    @SerializedName("fiStatus")
    int status;
    @SerializedName("fsMessage")
    String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
