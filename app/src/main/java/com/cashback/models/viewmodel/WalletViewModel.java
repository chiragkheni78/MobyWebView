package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.TransactionListRequest;
import com.cashback.models.response.TransactionListResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletViewModel extends ViewModel {

    public MutableLiveData<TransactionListResponse> fetchTransactionStatus = new MutableLiveData<>();

    public void fetchTransactionList(Context foContext) {
        TransactionListRequest loTransactionListRequest = new TransactionListRequest();
        loTransactionListRequest.setAction(Constants.API.GET_USER_TRANSACTION.getValue());
        loTransactionListRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loTransactionListRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loTransactionListRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchTransactionStatus.postValue(new TransactionListResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<TransactionListResponse> loRequest = APIClient.getInterface().getTransactionList(loTransactionListRequest);
        Common.printReqRes(loRequest, "getTransactionList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<TransactionListResponse>() {
            @Override
            public void onResponse(Call<TransactionListResponse> call, Response<TransactionListResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getTransactionList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    TransactionListResponse loJsonObject = foResponse.body();
                    fetchTransactionStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchTransactionStatus.postValue(new TransactionListResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<TransactionListResponse> call, Throwable t) {
                Common.printReqRes(t, "getTransactionList", Common.LogType.ERROR);
                fetchTransactionStatus.postValue(new TransactionListResponse(true, t.getMessage()));
            }
        });
    }
}
