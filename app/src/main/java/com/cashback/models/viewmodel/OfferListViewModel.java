package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.OfferFilter;
import com.cashback.models.request.BypassQuizRequest;
import com.cashback.models.request.FetchOffersRequest;
import com.cashback.models.request.OfferFilterRequest;
import com.cashback.models.response.BypassQuizResponse;
import com.cashback.models.response.OfferFilterResponse;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferListViewModel extends ViewModel {

    public MutableLiveData<FetchOffersResponse> fetchOffersStatus = new MutableLiveData<>();

    public void fetchOffers(Context foContext, String mobileNumber, double latitude, double longitude, boolean isMarketingAd, boolean isBlockUser, int pageViewType, OfferFilter foOfferFilter) {
        FetchOffersRequest loFetchOffersRequest = new FetchOffersRequest(mobileNumber, latitude, longitude, isMarketingAd, isBlockUser, pageViewType);
        loFetchOffersRequest.setAction(Constants.API.GET_OFFER_LIST.getValue());
        loFetchOffersRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loFetchOffersRequest.setOfferFilter(foOfferFilter);
        loFetchOffersRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loFetchOffersRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchOffersStatus.postValue(new FetchOffersResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<FetchOffersResponse> loRequest = APIClient.getInterface().getOfferList(loFetchOffersRequest);
        Common.printReqRes(loRequest, "getOfferList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<FetchOffersResponse>() {
            @Override
            public void onResponse(Call<FetchOffersResponse> call, Response<FetchOffersResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getOfferList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    FetchOffersResponse loJsonObject = foResponse.body();
                    fetchOffersStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchOffersStatus.postValue(new FetchOffersResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<FetchOffersResponse> call, Throwable t) {
                Common.printReqRes(t, "getOfferList", Common.LogType.ERROR);
                fetchOffersStatus.postValue(new FetchOffersResponse(true, t.getMessage()));
            }
        });
    }

    public MutableLiveData<OfferFilterResponse> fetchCategoryStatus = new MutableLiveData<>();

    public void fetchCategory(Context foContext) {

        OfferFilterRequest loOfferFilterRequest = new OfferFilterRequest();
        loOfferFilterRequest.setAction(Constants.API.GET_OFFER_FILTER.getValue());
        loOfferFilterRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loOfferFilterRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        //API Call
        Call<OfferFilterResponse> loRequest = APIClient.getInterface().getOfferFilter(loOfferFilterRequest);
        Common.printReqRes(loRequest, "getOfferFilter", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<OfferFilterResponse>() {
            @Override
            public void onResponse(Call<OfferFilterResponse> call, Response<OfferFilterResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getOfferFilter", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    OfferFilterResponse loJsonObject = foResponse.body();
                    fetchCategoryStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchCategoryStatus.postValue(new OfferFilterResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<OfferFilterResponse> call, Throwable t) {
                Common.printReqRes(t, "getOfferFilter", Common.LogType.ERROR);
                fetchCategoryStatus.postValue(new OfferFilterResponse(true, t.getMessage()));
            }
        });
    }

    public MutableLiveData<BypassQuizResponse> bypassQuizStatus = new MutableLiveData<>();

    public void bypassQuiz(Context foContext, long llAdId) {

        BypassQuizRequest loBypassQuizRequest = new BypassQuizRequest();
        loBypassQuizRequest.setAction(Constants.API.BYPASS_QUIZ.getValue());
        loBypassQuizRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loBypassQuizRequest.setAdId(llAdId);
        loBypassQuizRequest.setDeviceUniqueId(1);
        loBypassQuizRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        //API Call
        Call<BypassQuizResponse> loRequest = APIClient.getInterface().bypassQuiz(loBypassQuizRequest);
        Common.printReqRes(loRequest, "bypassQuiz", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<BypassQuizResponse>() {
            @Override
            public void onResponse(Call<BypassQuizResponse> call, Response<BypassQuizResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "bypassQuiz", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    BypassQuizResponse loJsonObject = foResponse.body();
                    bypassQuizStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    bypassQuizStatus.postValue(new BypassQuizResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<BypassQuizResponse> call, Throwable t) {
                Common.printReqRes(t, "bypassQuiz", Common.LogType.ERROR);
                bypassQuizStatus.postValue(new BypassQuizResponse(true, t.getMessage()));
            }
        });
    }
}
