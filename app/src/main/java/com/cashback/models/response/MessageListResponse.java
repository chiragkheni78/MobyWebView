package com.cashback.models.response;

import com.cashback.models.Ad;
import com.cashback.models.Message;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MessageListResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foMessageList")
    private ArrayList<Message> messageList;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Message> getMessageList() {
        return messageList;
    }

    public MessageListResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
