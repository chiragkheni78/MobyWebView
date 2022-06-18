package com.mobycashback.webview;

import com.google.gson.annotations.SerializedName;

import java.util.StringJoiner;
import java.util.stream.Stream;

public class CategoryRequest {
    @SerializedName("fsAction")
    private String action;

    @SerializedName("fsDeviceId")
    private String deviceId;

    public CategoryRequest(String action, String deviceId) {
        this.action = action;
        this.deviceId = deviceId;
    }
}
