package com.cashback.models.response;

import com.cashback.models.Ad;
import com.cashback.models.Quiz;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuizDetailsResponse {

    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foQuizList")
    private ArrayList<Quiz> quizList;

    @SerializedName("fsAdLogo")
    String logoUrl;

    @SerializedName("fsAdBanner")
    String fsBannerURL;

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Quiz> getQuizList() {
        return quizList;
    }

    public QuizDetailsResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getBannerUrl() {
        return fsBannerURL;
    }

}