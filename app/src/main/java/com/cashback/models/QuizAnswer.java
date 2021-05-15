package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class QuizAnswer {

    @SerializedName("fiQuizId")
    long quizId;

    @SerializedName("fiAnsInterval")
    int ansInterval;

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

    public void setAnsInterval(int ansInterval) {
        this.ansInterval = ansInterval;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
