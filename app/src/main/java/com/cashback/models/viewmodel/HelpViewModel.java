package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.AdvertisementRequest;
import com.cashback.models.request.HelpRequest;
import com.cashback.models.response.AdvertisementResponse;
import com.cashback.models.response.HelpResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpViewModel extends ViewModel {
    private static final String TAG = HelpViewModel.class.getSimpleName();

    public MutableLiveData<HelpResponse> advertListStatus = new MutableLiveData<>();

    public void fetAdvertList(Context foContext, String fsScreenType) {
        HelpRequest loAdvertisementRequest = new HelpRequest(fsScreenType);
        loAdvertisementRequest.setAction(Constants.API.GET_LOAD_WEBVIEW.getValue());
        loAdvertisementRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loAdvertisementRequest.setFsPage(Constants.API.GET_LOAD_WEBVIEW_FAQ.getValue());

        String lsMessage = loAdvertisementRequest.validateData(foContext);
        if (lsMessage != null) {
            advertListStatus.postValue(new HelpResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<HelpResponse> loRequest = APIClient.getInterface().getHelpList(loAdvertisementRequest);
        Common.printReqRes(loRequest, "loadWebView", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<HelpResponse>() {
            @Override
            public void onResponse(Call<HelpResponse> call, Response<HelpResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "loadWebView", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    HelpResponse loJsonObject = foResponse.body();
                    advertListStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    advertListStatus.postValue(new HelpResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<HelpResponse> call, Throwable t) {
                Common.printReqRes(t, "loadWebView", Common.LogType.ERROR);
                advertListStatus.postValue(new HelpResponse(true, t.getMessage()));
            }
        });
    }

}
