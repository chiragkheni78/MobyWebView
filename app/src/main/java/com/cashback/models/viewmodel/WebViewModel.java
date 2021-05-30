package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.TransactionListRequest;
import com.cashback.models.request.WebviewDataRequest;
import com.cashback.models.response.WebViewDataResponse;
import com.cashback.models.response.WebViewDataResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewModel extends ViewModel {

    public MutableLiveData<WebViewDataResponse> loadWebViewDataStatus = new MutableLiveData<>();

    public void loadWebViewData(Context foContext, String fsPageName) {
        WebviewDataRequest loWebviewDataRequest = new WebviewDataRequest(fsPageName);
        loWebviewDataRequest.setAction(Constants.API.LOAD_WEB_VIEW_DATA.getValue());
        loWebviewDataRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loWebviewDataRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loWebviewDataRequest.validateData(foContext);
        if (lsMessage != null) {
            loadWebViewDataStatus.postValue(new WebViewDataResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<WebViewDataResponse> loRequest = APIClient.getInterface().loadWebViewData(loWebviewDataRequest);
        Common.printReqRes(loRequest, "loadWebViewData", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<WebViewDataResponse>() {
            @Override
            public void onResponse(Call<WebViewDataResponse> call, Response<WebViewDataResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "loadWebViewData", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    WebViewDataResponse loJsonObject = foResponse.body();
                    loadWebViewDataStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    loadWebViewDataStatus.postValue(new WebViewDataResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<WebViewDataResponse> call, Throwable t) {
                Common.printReqRes(t, "loadWebViewData", Common.LogType.ERROR);
                loadWebViewDataStatus.postValue(new WebViewDataResponse(true, t.getMessage()));
            }
        });
    }
}
