package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HelpResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foFAQLists")
    private ArrayList<HelpModel> foFAQLists;

    public HelpResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<HelpModel> getFoFAQLists() {
        return foFAQLists;
    }

    public void setFoFAQLists(ArrayList<HelpModel> foFAQLists) {
        this.foFAQLists = foFAQLists;
    }

    public static class HelpModel {
        @SerializedName("fsTitle")
        private String fsTitle;

        @SerializedName("fsHtmlContent")
        private String fsHtmlContent;

        public String getFsTitle() {
            return fsTitle;
        }

        public void setFsTitle(String fsTitle) {
            this.fsTitle = fsTitle;
        }

        public String getFsHtmlContent() {
            return fsHtmlContent;
        }

        public void setFsHtmlContent(String fsHtmlContent) {
            this.fsHtmlContent = fsHtmlContent;
        }
    }

}
