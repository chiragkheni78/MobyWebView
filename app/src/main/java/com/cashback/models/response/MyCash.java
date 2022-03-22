package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MyCash {

    @SerializedName("fiTotalMyCash")
    private double totalMyCash;

    @SerializedName("fiUsedMyCash")
    private double usedMyCash;

    @SerializedName("fiAutoRedeemPer")
    private String autoRedeemPer;

    @SerializedName("foMyCashHistory")
    private ArrayList<MyCashHistory> myCashHistory;

    public double getTotalMyCash() {
        return totalMyCash;
    }

    public double getUsedMyCash() {
        return usedMyCash;
    }

    public String getAutoRedeemPer() {
        return autoRedeemPer;
    }

    public ArrayList<MyCashHistory> getMyCashHistory() {
        return myCashHistory;
    }
}
