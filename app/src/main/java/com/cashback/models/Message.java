package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("message_id")
    long messageID;
    @SerializedName("title")
    String title;
    @SerializedName("content")
    String content;
    @SerializedName("datetime")
    String dateTime;
    @SerializedName("message_seen")
    boolean isSeen;

    public long getMessageID() {
        return messageID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public boolean isSeen() {
        return isSeen;
    }
}