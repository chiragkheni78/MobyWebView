package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.MessageDetailsRequest;
import com.cashback.models.request.MessageListRequest;
import com.cashback.models.response.MessageDetailsResponse;
import com.cashback.models.response.MessageListResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesViewModel extends ViewModel {

    public MutableLiveData<MessageListResponse> fetchMessageStatus = new MutableLiveData<>();

    public void fetchMessageList(Context foContext) {
        MessageListRequest loMessageListRequest = new MessageListRequest();
        loMessageListRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loMessageListRequest.setAction(Constants.API.GET_MESSAGE_LIST.getValue());
        loMessageListRequest.setMobileNumber(AppGlobal.getPhoneNumber());

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


    public MutableLiveData<MessageDetailsResponse> seenMessageDetailStatus = new MutableLiveData<>();

    public void updateMessageSeen(Context foContext, String msgid) {
        MessageDetailsRequest loMessageListRequest = new MessageDetailsRequest();
        loMessageListRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loMessageListRequest.setAction(Constants.API.UPDATE_MESSAGE_AS_READ.getValue());
        loMessageListRequest.setmessageId(msgid);
        loMessageListRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loMessageListRequest.validateData(foContext);
        if (lsMessage != null) {
            seenMessageDetailStatus.postValue(new MessageDetailsResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<MessageDetailsResponse> loRequest = APIClient.getInterface().getMessageDetails(loMessageListRequest);
        Common.printReqRes(loRequest, "getMessageDetails", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<MessageDetailsResponse>() {
            @Override
            public void onResponse(Call<MessageDetailsResponse> call, Response<MessageDetailsResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getMessageDetails", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    MessageDetailsResponse loJsonObject = foResponse.body();
                    seenMessageDetailStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    seenMessageDetailStatus.postValue(new MessageDetailsResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<MessageDetailsResponse> call, Throwable t) {
                Common.printReqRes(t, "getMessageDetails", Common.LogType.ERROR);
                seenMessageDetailStatus.postValue(new MessageDetailsResponse(true, t.getMessage()));
            }
        });
    }

}
