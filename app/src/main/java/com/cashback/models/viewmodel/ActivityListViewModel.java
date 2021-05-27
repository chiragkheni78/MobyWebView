package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.request.ActivityListRequest;
import com.cashback.models.response.ActivityListResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityListViewModel extends ViewModel {

    public MutableLiveData<ActivityListResponse> fetchActivityStatus = new MutableLiveData<>();

    public void fetchActivityList(Context foContext, String mobileNumber, String orderBy, String fsFilter) {
        ActivityListRequest loActivityListRequest = new ActivityListRequest(mobileNumber, orderBy, fsFilter);
        loActivityListRequest.setAction(Constants.API.GET_ACTIVITY_LIST.getValue());
        loActivityListRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loActivityListRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loActivityListRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchActivityStatus.postValue(new ActivityListResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<ActivityListResponse> loRequest = APIClient.getInterface().getActivityList(loActivityListRequest);
        Common.printReqRes(loRequest, "getOfferList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<ActivityListResponse>() {
            @Override
            public void onResponse(Call<ActivityListResponse> call, Response<ActivityListResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getOfferList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    ActivityListResponse loJsonObject = foResponse.body();
                    fetchActivityStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchActivityStatus.postValue(new ActivityListResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<ActivityListResponse> call, Throwable t) {
                Common.printReqRes(t, "getOfferList", Common.LogType.ERROR);
                fetchActivityStatus.postValue(new ActivityListResponse(true, t.getMessage()));
            }
        });
    }
}
