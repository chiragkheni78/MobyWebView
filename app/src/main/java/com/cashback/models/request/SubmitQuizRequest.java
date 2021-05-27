package com.cashback.models.request;

import android.content.Context;

import com.cashback.models.QuizAnswer;
import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubmitQuizRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    private String deviceId;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fiAdId")
    private long offerId;

    @SerializedName("fiLocationId")
    private long locationId;

    @SerializedName("foQuizAnswerList")
    ArrayList<QuizAnswer> quizAnswerList;



    public SubmitQuizRequest(String deviceId, long offerId, long locationId) {
        this.deviceId = deviceId;
        this.offerId = offerId;
        this.locationId = locationId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public void setQuizAnswerList(ArrayList<QuizAnswer> quizAnswerList) {
        this.quizAnswerList = quizAnswerList;
    }

    public String validateData(Context foContext) {

        if (Common.getDeviceUniqueId(foContext).isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        }
        return null;
    }
}