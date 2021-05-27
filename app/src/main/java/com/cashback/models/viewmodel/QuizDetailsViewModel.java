package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.QuizAnswer;
import com.cashback.models.request.QuizDetailsRequest;
import com.cashback.models.request.SubmitQuizRequest;
import com.cashback.models.response.QuizDetailsResponse;
import com.cashback.models.response.SubmitQuizResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizDetailsViewModel extends ViewModel {

    public MutableLiveData<QuizDetailsResponse> fetchQuizDetailsStatus = new MutableLiveData<>();

    public void fetchQuizDetails(Context foContext, String mobileNumber, long offerId, long locationId) {
        QuizDetailsRequest loQuizDetailsRequest = new QuizDetailsRequest(mobileNumber, offerId, locationId);
        loQuizDetailsRequest.setAction(Constants.API.GET_QUIZ_DETAILS.getValue());
        loQuizDetailsRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loQuizDetailsRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loQuizDetailsRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchQuizDetailsStatus.postValue(new QuizDetailsResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<QuizDetailsResponse> loRequest = APIClient.getInterface().getQuizDetails(loQuizDetailsRequest);
        Common.printReqRes(loRequest, "getQuizDetails", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<QuizDetailsResponse>() {
            @Override
            public void onResponse(Call<QuizDetailsResponse> call, Response<QuizDetailsResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getQuizDetails", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    QuizDetailsResponse loJsonObject = foResponse.body();
                    fetchQuizDetailsStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchQuizDetailsStatus.postValue(new QuizDetailsResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<QuizDetailsResponse> call, Throwable t) {
                Common.printReqRes(t, "getQuizDetails", Common.LogType.ERROR);
                fetchQuizDetailsStatus.postValue(new QuizDetailsResponse(true, t.getMessage()));
            }
        });
    }


    public MutableLiveData<SubmitQuizResponse> submitQuizAnswerStatus = new MutableLiveData<>();

    public void submitQuizAnswer(Context foContext, String mobileNumber, long offerId, long locationId, ArrayList<QuizAnswer> foQuizAnswerList) {
        SubmitQuizRequest loQuizDetailsRequest = new SubmitQuizRequest(mobileNumber, offerId, locationId);
        loQuizDetailsRequest.setAction(Constants.API.SUBMIT_QUIZ_ANSWER.getValue());
        loQuizDetailsRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loQuizDetailsRequest.setQuizAnswerList(foQuizAnswerList);
        loQuizDetailsRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loQuizDetailsRequest.validateData(foContext);
        if (lsMessage != null) {
            submitQuizAnswerStatus.postValue(new SubmitQuizResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<SubmitQuizResponse> loRequest = APIClient.getInterface().submitQuizAnswer(loQuizDetailsRequest);
        Common.printReqRes(loRequest, "submitQuiz", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<SubmitQuizResponse>() {
            @Override
            public void onResponse(Call<SubmitQuizResponse> call, Response<SubmitQuizResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "submitQuiz", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    SubmitQuizResponse loJsonObject = foResponse.body();
                    submitQuizAnswerStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    submitQuizAnswerStatus.postValue(new SubmitQuizResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<SubmitQuizResponse> call, Throwable t) {
                Common.printReqRes(t, "submitQuiz", Common.LogType.ERROR);
                submitQuizAnswerStatus.postValue(new SubmitQuizResponse(true, t.getMessage()));
            }
        });
    }
}
