package com.cashback.models.response;

import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.UserDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetMiniProfileResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("fbIsUserExist")
    private boolean isUserExist;

    @SerializedName("foWalletList")
    private ArrayList<EWallet> walletList;

    @SerializedName("foAdvertisementList")
    private ArrayList<Advertisement> advertisementList;

    @SerializedName("foUserDetails")
    private UserDetails userDetails;

    public GetMiniProfileResponse(boolean isError, String message) {
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

    public boolean isUserExist() {
        return isUserExist;
    }

    public ArrayList<EWallet> getWalletList() {
        return walletList;
    }

    public ArrayList<Advertisement> getAdvertisementList() {
        return advertisementList;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}
