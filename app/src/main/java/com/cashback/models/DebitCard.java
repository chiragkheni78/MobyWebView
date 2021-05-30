package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class DebitCard {

    @SerializedName("fiCardId")
    int cardId;

    @SerializedName("fsBankName")
    String bankName;

    @SerializedName("fsCardType")
    String cardType;

    @SerializedName("fsCardProduct")
    String cardProduct;

    public int getCardId() {
        return cardId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardProduct() {
        return cardProduct;
    }
}