package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.request.ActivityDetailsRequest;
import com.cashback.models.response.ActivityDetailsResponse;
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
}
