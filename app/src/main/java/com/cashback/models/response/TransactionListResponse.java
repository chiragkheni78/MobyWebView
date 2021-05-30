package com.cashback.models.response;

import com.cashback.models.Activity;
import com.cashback.models.Transaction;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TransactionListResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("fsUserWalletName")
    private String walletName;

    @SerializedName("fbIsActivityCouponExist")
    private boolean isActivityCouponExist;

    @SerializedName("foTransactionList")
    private ArrayList<Transaction> transactionList;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public String getWalletName() {
        return walletName;
    }

    public ArrayList<Transaction> getTransactionList() {
        return transactionList;
    }

    public boolean isActivityCouponExist() {
        return isActivityCouponExist;
    }

    public TransactionListResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

}
