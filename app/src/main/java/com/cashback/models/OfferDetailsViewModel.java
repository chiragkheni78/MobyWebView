package com.cashback.models;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.request.FetchOffersRequest;
import com.cashback.models.request.OfferDetailsRequest;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.response.OffersDetailsResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferDetailsViewModel extends ViewModel {

    public MutableLiveData<OffersDetailsResponse> fetchOfferDetailsStatus = new MutableLiveData<>();

    public void fetchOfferDetails(Context foContext, String mobileNumber, long offerId, long locationId) {
        OfferDetailsRequest loOfferDetailsRequest = new OfferDetailsRequest(mobileNumber, offerId, locationId);
        loOfferDetailsRequest.setDeviceId(Common.getDeviceUniqueId(foContext));

        String lsMessage = loOfferDetailsRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchOfferDetailsStatus.postValue(new OffersDetailsResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<OffersDetailsResponse> loRequest = APIClient.getInterface().fetchOfferDetails(loOfferDetailsRequest);
        Common.printReqRes(loRequest, "fetchOfferDetails", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<OffersDetailsResponse>() {
            @Override
            public void onResponse(Call<OffersDetailsResponse> call, Response<OffersDetailsResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "fetchOfferDetails", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    OffersDetailsResponse loJsonObject = foResponse.body();
                    fetchOfferDetailsStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchOfferDetailsStatus.postValue(new OffersDetailsResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<OffersDetailsResponse> call, Throwable t) {
                Common.printReqRes(t, "fetchOfferDetails", Common.LogType.ERROR);
                fetchOfferDetailsStatus.postValue(new OffersDetailsResponse(true, t.getMessage()));
            }
        });
    }
}
