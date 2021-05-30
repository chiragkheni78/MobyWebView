package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DonationDetails {

    @SerializedName("fbIsNGOSelectedOnApp")
    private boolean isNgoSelected;

    @SerializedName("fsDonateMsg")
    private String donateMessage;

    @SerializedName("fsLockedErrorMsg")
    private String lockedErrorMessage;

    @SerializedName("fbIsEnableNGOSection")
    private boolean isNGOEnabled;

    @SerializedName("fbIsPayToNGO")
    private String isPayToNGO;

    @SerializedName("foNgoList")
    private ArrayList<NGO> ngoList;

    public boolean isNgoSelected() {
        return isNgoSelected;
    }

    public String getDonateMessage() {
        return donateMessage;
    }

    public String getLockedErrorMessage() {
        return lockedErrorMessage;
    }

    public boolean isNGOEnabled() {
        return isNGOEnabled;
    }

    public String getIsPayToNGO() {
        return isPayToNGO;
    }

    public ArrayList<NGO> getNgoList() {
        return ngoList;
    }
}

