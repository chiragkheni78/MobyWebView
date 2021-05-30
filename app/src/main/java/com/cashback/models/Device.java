package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("fiUserId")
    private long fiUserId;

    @SerializedName("fsDeviceName")
    private String fsDeviceName;

    public long getUserId() {
        return fiUserId;
    }

    public void setUserId(Integer fiUserId) {
        this.fiUserId = fiUserId;
    }

    public String getDeviceName() {
        return fsDeviceName;
    }

    public void setDeviceName(String fsDeviceName) {
        this.fsDeviceName = fsDeviceName;
    }

}
