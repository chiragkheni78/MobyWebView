package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("fiMessageId")
    long messageID;

    @SerializedName("fsTitle")
    String title;

    @SerializedName("fsContent")
    String content;

    @SerializedName("fdTimestamp")
    String dateTime;

    @SerializedName("fdIsMessageSeen")
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