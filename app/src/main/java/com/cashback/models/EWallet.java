package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class EWallet {

    @SerializedName("fiId")
    int walletId;

    @SerializedName("fsName")
    String walletName;

    @SerializedName("fbIsSelected")
    boolean isSelected;

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
