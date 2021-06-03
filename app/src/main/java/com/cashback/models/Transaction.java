package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Transaction {

    @SerializedName("fsActivityType")
    String activityType;

    @SerializedName("fiActivityId")
    long activityID;

    @SerializedName("fbIsVirtualCash")
    boolean isVirtualCash;

    @SerializedName("fbIsVerifiedCash")
    boolean isVerifiedCash;

    @SerializedName("fbIsCashback")
    boolean isCashback;

    @SerializedName("fiQuizReward")
    int quizReward;

    @SerializedName("fiSecondReward")
    int secondReward;

    @SerializedName("fiNormalReward")
    int normalRewardAmount;

    @SerializedName("fiAdId")
    long adID;

    @SerializedName("fsAdName")
    String adName;

    @SerializedName("fsPinColor")
    String pinColor;

    @SerializedName("timestamp")
    String quizEngageDateTime;

    @SerializedName("fsWalletName")
    String walletName;

    @SerializedName("fsTrasactionType")
    String transactionType;

    @SerializedName("fiTransactionAmount")
    int transactionAmount;

    public String getActivityType() {
        return activityType;
    }

    public long getActivityID() {
        return activityID;
    }

    public boolean isVirtualCash() {
        return isVirtualCash;
    }

    public boolean isVerifiedCash() {
        return isVerifiedCash;
    }

    public boolean isCashback() {
        return isCashback;
    }

    public int getQuizReward() {
        return quizReward;
    }

    public int getSecondReward() {
        return secondReward;
    }

    public int getNormalRewardAmount() {
        return normalRewardAmount;
    }

    public long getAdID() {
        return adID;
    }

    public String getAdName() {
        return adName;
    }

    public String getPinColor() {
        return pinColor;
    }

    public String getQuizEngageDateTime() {
        return quizEngageDateTime;
    }

    public String getWalletName() {
        return walletName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }
}
