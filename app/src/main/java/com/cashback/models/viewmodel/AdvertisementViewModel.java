package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.AdvertisementRequest;
import com.cashback.models.request.GetSettingRequest;
import com.cashback.models.response.AdvertisementResponse;
import com.cashback.models.response.GetSettingResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvertisementViewModel extends ViewModel {
    private static final String TAG = AdvertisementViewModel.class.getSimpleName();

    public MutableLiveData<AdvertisementResponse> advertListStatus = new MutableLiveData<>();

    public void fetAdvertList(Context foContext, String fsScreenType) {
        AdvertisementRequest loAdvertisementRequest = new AdvertisementRequest(fsScreenType);
        loAdvertisementRequest.setAction(Constants.API.GET_ADVERT_IMAGES.getValue());
        loAdvertisementRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loAdvertisementRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loAdvertisementRequest.validateData(foContext);
        if (lsMessage != null) {
            advertListStatus.postValue(new AdvertisementResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<AdvertisementResponse> loRequest = APIClient.getInterface().getAdvertList(loAdvertisementRequest);
        Common.printReqRes(loRequest, "getAdvertList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<AdvertisementResponse>() {
            @Override
            public void onResponse(Call<AdvertisementResponse> call, Response<AdvertisementResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getAdvertList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    AdvertisementResponse loJsonObject = foResponse.body();
                    advertListStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    advertListStatus.postValue(new AdvertisementResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<AdvertisementResponse> call, Throwable t) {
                Common.printReqRes(t, "getAdvertList", Common.LogType.ERROR);
                advertListStatus.postValue(new AdvertisementResponse(true, t.getMessage()));
            }
        });
    }

}
