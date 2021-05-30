package com.cashback.models.response;

import com.cashback.models.Device;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MobileDeviceResponse {

    @SerializedName("fbIsError")
    private Boolean fbIsError;

    @SerializedName("fsMessage")
    private String fsMessage;

    @SerializedName("foDevicesList")
    private ArrayList<Device> foDeviceList;
    {
        foDeviceList = null;
    }

    public MobileDeviceResponse(boolean fbIsError, String fsMessage) {

        this.fbIsError = fbIsError;
        this.fsMessage = fsMessage;
    }

    public Boolean isError() {
        return fbIsError;
    }


    public String getMessage() {
        return fsMessage;
    }


    public ArrayList<Device> getDevicesList() {
        return foDeviceList;
    }


}
