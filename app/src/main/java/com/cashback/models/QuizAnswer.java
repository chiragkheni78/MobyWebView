package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class QuizAnswer {

    @SerializedName("fiQuizId")
    long quizId;

    @SerializedName("fiAnsInterval")
    long ansInterval;

    @SerializedName("fsAnswer")
    String answer;

    public QuizAnswer(long quizId, int ansInterval, String answer) {
        this.quizId = quizId;
        this.ansInterval = ansInterval;
        this.answer = answer;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public void setAnsInterval(long ansInterval) {
        this.ansInterval = ansInterval;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
