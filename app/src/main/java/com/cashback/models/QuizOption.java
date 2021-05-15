package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class QuizOption {

    @SerializedName("fiIndex")
    int index;

    @SerializedName("fsValue")
    String value;

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }
}
