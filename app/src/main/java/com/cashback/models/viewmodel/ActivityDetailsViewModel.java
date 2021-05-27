package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.ActivityDetailsRequest;
import com.cashback.models.request.ActivityMarkAsUsedRequest;
import com.cashback.models.request.UpdateShopOnlineBlinkRequest;
import com.cashback.models.response.ActivityDetailsResponse;
import com.cashback.models.response.ActivityMarkAsUsedResponse;
import com.cashback.models.response.UpdateShopOnlineBlinkResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityDetailsViewModel extends ViewModel {

    public MutableLiveData<ActivityDetailsResponse> fetchActivityStatus = new MutableLiveData<>();

    public void getActivityDetails(Context foContext, String mobileNumber, long fiActivityID) {
        ActivityDetailsRequest loActivityDetailsRequest = new ActivityDetailsRequest(mobileNumber, fiActivityID);
        loActivityDetailsRequest.setAction(Constants.API.GET_ACTIVITY_DETAILS.getValue());
        loActivityDetailsRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loActivityDetailsRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loActivityDetailsRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchActivityStatus.postValue(new ActivityDetailsResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<ActivityDetailsResponse> loRequest = APIClient.getInterface().getActivityDetails(loActivityDetailsRequest);
        Common.printReqRes(loRequest, "getActivityDetails", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<ActivityDetailsResponse>() {
            @Override
            public void onResponse(Call<ActivityDetailsResponse> call, Response<ActivityDetailsResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getActivityDetails", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    ActivityDetailsResponse loJsonObject = foResponse.body();
                    fetchActivityStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchActivityStatus.postValue(new ActivityDetailsResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<ActivityDetailsResponse> call, Throwable t) {
                Common.printReqRes(t, "getActivityDetails", Common.LogType.ERROR);
                fetchActivityStatus.postValue(new ActivityDetailsResponse(true, t.getMessage()));
            }
        });
    }


    public MutableLiveData<ActivityMarkAsUsedResponse> updateMarkAsUsedStatus = new MutableLiveData<>();

    public void updateMarkAsUsed(Context foContext, String mobileNumber, long fiActivityID) {
        ActivityMarkAsUsedRequest loActivityMarkAsUsedRequest = new ActivityMarkAsUsedRequest(mobileNumber, fiActivityID);
        loActivityMarkAsUsedRequest.setAction(Constants.API.COUPON_MARK_AS_USED.getValue());
        loActivityMarkAsUsedRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loActivityMarkAsUsedRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loActivityMarkAsUsedRequest.validateData(foContext);
        if (lsMessage != null) {
            updateMarkAsUsedStatus.postValue(new ActivityMarkAsUsedResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<ActivityMarkAsUsedResponse> loRequest = APIClient.getInterface().updateMarkAsUsed(loActivityMarkAsUsedRequest);
        Common.printReqRes(loRequest, "updateMarkAsUsed", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<ActivityMarkAsUsedResponse>() {
            @Override
            public void onResponse(Call<ActivityMarkAsUsedResponse> call, Response<ActivityMarkAsUsedResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "updateMarkAsUsed", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    ActivityMarkAsUsedResponse loJsonObject = foResponse.body();
                    updateMarkAsUsedStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    updateMarkAsUsedStatus.postValue(new ActivityMarkAsUsedResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<ActivityMarkAsUsedResponse> call, Throwable t) {
                Common.printReqRes(t, "updateMarkAsUsed", Common.LogType.ERROR);
                updateMarkAsUsedStatus.postValue(new ActivityMarkAsUsedResponse(true, t.getMessage()));
            }
        });
    }


    public MutableLiveData<UpdateShopOnlineBlinkResponse> updateShopOnlineBlinkStatus = new MutableLiveData<>();

    public void updateShopOnlineBlink(Context foContext, String mobileNumber, long fiActivityID) {
        UpdateShopOnlineBlinkRequest loUpdateShopOnlineBlinkRequest = new UpdateShopOnlineBlinkRequest(mobileNumber, fiActivityID);
        loUpdateShopOnlineBlinkRequest.setAction(Constants.API.UPLOAD_SHOP_ONLINE_BLINK.getValue());
        loUpdateShopOnlineBlinkRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loUpdateShopOnlineBlinkRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loUpdateShopOnlineBlinkRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchActivityStatus.postValue(new ActivityDetailsResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<UpdateShopOnlineBlinkResponse> loRequest = APIClient.getInterface().updateShopOnlineBlink(loUpdateShopOnlineBlinkRequest);
        Common.printReqRes(loRequest, "updateShopOnlineBlink", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<UpdateShopOnlineBlinkResponse>() {
            @Override
            public void onResponse(Call<UpdateShopOnlineBlinkResponse> call, Response<UpdateShopOnlineBlinkResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "updateShopOnlineBlink", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    UpdateShopOnlineBlinkResponse loJsonObject = foResponse.body();
                    updateShopOnlineBlinkStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    updateShopOnlineBlinkStatus.postValue(new UpdateShopOnlineBlinkResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<UpdateShopOnlineBlinkResponse> call, Throwable t) {
                Common.printReqRes(t, "updateShopOnlineBlink", Common.LogType.ERROR);
                updateShopOnlineBlinkStatus.postValue(new UpdateShopOnlineBlinkResponse(true, t.getMessage()));
            }
        });
    }
}
