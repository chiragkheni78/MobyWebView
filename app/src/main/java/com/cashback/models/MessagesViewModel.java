package com.cashback.models;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.request.MessageListRequest;
import com.cashback.models.response.MessageListResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesViewModel extends ViewModel {

    public MutableLiveData<MessageListResponse> fetchMessageStatus = new MutableLiveData<>();

    public void fetchMessageList(Context foContext) {
        MessageListRequest loMessageListRequest = new MessageListRequest();
        loMessageListRequest.setDeviceId(Common.getDeviceUniqueId(foContext));

        String lsMessage = loMessageListRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchMessageStatus.postValue(new MessageListResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<MessageListResponse> loRequest = APIClient.getInterface().getMessageList(loMessageListRequest);
        Common.printReqRes(loRequest, "getMessageList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<MessageListResponse>() {
            @Override
            public void onResponse(Call<MessageListResponse> call, Response<MessageListResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getMessageList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    MessageListResponse loJsonObject = foResponse.body();
                    fetchMessageStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchMessageStatus.postValue(new MessageListResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<MessageListResponse> call, Throwable t) {
                Common.printReqRes(t, "getMessageList", Common.LogType.ERROR);
                fetchMessageStatus.postValue(new MessageListResponse(true, t.getMessage()));
            }
        });
    }

}
