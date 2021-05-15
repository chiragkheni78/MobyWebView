package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Quiz {

    @SerializedName("fiQuizId")
    long quizID;

    @SerializedName("fsQuestion")
    String question;

    @SerializedName("foOptionsWithIndexes")
    ArrayList<QuizOption> optionList;

    @SerializedName("answerType")
    String answerType;

    public long getQuizID() {
        return quizID;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<QuizOption> getOptionList() {
        return optionList;
    }

    public String getAnswerType() {
        return answerType;
    }
}