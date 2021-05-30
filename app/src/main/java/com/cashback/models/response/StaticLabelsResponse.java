package com.cashback.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class StaticLabelsResponse {

    @SerializedName("foLabels")
    @Expose
    private JSONObject foLabels;
    @SerializedName("fbIsError")
    @Expose
    private Boolean fbIsError;
    @SerializedName("fsMessage")
    @Expose
    private String fsMessage;

    public JSONObject getLabels() {
        return foLabels;
    }

    public void setLabels(JSONObject foLabels) {
        this.foLabels = foLabels;
    }

    public Boolean isError() {
        return fbIsError;
    }

    public void setIsError(Boolean fbIsError) {
        this.fbIsError = fbIsError;
    }

    public String getMessage() {
        return fsMessage;
    }

    public void setMessage(String fsMessage) {
        this.fsMessage = fsMessage;
    }

    public StaticLabelsResponse(boolean isError, String message) {
        this.fbIsError = isError;
        this.fsMessage = message;
    }

}
