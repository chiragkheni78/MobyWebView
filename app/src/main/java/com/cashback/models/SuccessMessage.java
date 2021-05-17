package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class SuccessMessage {

    @SerializedName("fsTitle")
    String title;
    @SerializedName("fsMessage")
    String message;

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
