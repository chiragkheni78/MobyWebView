package com.cashback.models;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.request.OfferDetailsRequest;
import com.cashback.models.response.OfferDetailsResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferDetailsViewModel extends ViewModel {

    public MutableLiveData<OfferDetailsResponse> fetchOfferDetailsStatus = new MutableLiveData<>();

    public void fetchOfferDetails(Context foContext, String mobileNumber, long offerId, long locationId) {
        OfferDetailsRequest loOfferDetailsRequest = new OfferDetailsRequest(mobileNumber, offerId, locationId);
        loOfferDetailsRequest.setAction(Constants.API.GET_OFFER_DETAILS.getValue());
        loOfferDetailsRequest.setDeviceId(Common.getDeviceUniqueId(foContext));

        String lsMessage = loOfferDetailsRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchOfferDetailsStatus.postValue(new OfferDetailsResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<OfferDetailsResponse> loRequest = APIClient.getInterface().getOfferDetails(loOfferDetailsRequest);
        Common.printReqRes(loRequest, "fetchOfferDetails", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<OfferDetailsResponse>() {
            @Override
            public void onResponse(Call<OfferDetailsResponse> call, Response<OfferDetailsResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "fetchOfferDetails", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    OfferDetailsResponse loJsonObject = foResponse.body();
                    fetchOfferDetailsStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchOfferDetailsStatus.postValue(new OfferDetailsResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<OfferDetailsResponse> call, Throwable t) {
                Common.printReqRes(t, "fetchOfferDetails", Common.LogType.ERROR);
                fetchOfferDetailsStatus.postValue(new OfferDetailsResponse(true, t.getMessage()));
            }
        });
    }
}
