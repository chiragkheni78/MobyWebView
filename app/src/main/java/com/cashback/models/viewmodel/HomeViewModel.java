package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.request.GetProfileRequest;
import com.cashback.models.request.GetSettingRequest;
import com.cashback.models.request.SaveMiniProfileRequest;
import com.cashback.models.response.GetProfileResponse;
import com.cashback.models.response.GetSettingResponse;
import com.cashback.models.response.SaveProfileResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeViewModel extends ViewModel {

    public MutableLiveData<GetSettingResponse> getSettingStatus = new MutableLiveData<>();

    public void getGlobalSetting(Context foContext) {
        GetSettingRequest loGetSettingRequest = new GetSettingRequest();
        loGetSettingRequest.setAction(Constants.API.GET_GLOBAL_SETTING.getValue());
        loGetSettingRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loGetSettingRequest.setPhoneNumber("");

        String lsMessage = loGetSettingRequest.validateData(foContext);
        if (lsMessage != null) {
            getSettingStatus.postValue(new GetSettingResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<GetSettingResponse> loRequest = APIClient.getInterface().getGlobalSetting(loGetSettingRequest);
        Common.printReqRes(loRequest, "getGlobalSetting", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<GetSettingResponse>() {
            @Override
            public void onResponse(Call<GetSettingResponse> call, Response<GetSettingResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getGlobalSetting", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    GetSettingResponse loJsonObject = foResponse.body();
                    getSettingStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    getSettingStatus.postValue(new GetSettingResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<GetSettingResponse> call, Throwable t) {
                Common.printReqRes(t, "getGlobalSetting", Common.LogType.ERROR);
                getSettingStatus.postValue(new GetSettingResponse(true, t.getMessage()));
            }
        });
    }

    public int getSelectedWalletPosition(ArrayList<EWallet> foWalletList) {
        int liSize = foWalletList.size();
        for (int liCount = 0; liCount < liSize; liCount++) {
            if (foWalletList.get(liCount).isSelected()) {
                return liCount;
            }
        }
        return 0;
    }


    public String getAdvertImage(Context foContext, ArrayList<Advertisement> foAdvertisementList) {

        if (foAdvertisementList.size() > 0) {
            String lsBannerURL;
            int position = 0;

            SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);
            if (loSharedPreferenceManager.getAdvertBannerPosition() > -1) {
                int liOldPosition = loSharedPreferenceManager.getAdvertBannerPosition();
                position = liOldPosition + 1;
            }

            position = (position < foAdvertisementList.size())? position : 0;

            lsBannerURL = foAdvertisementList.get(position).getImageUrl();

            loSharedPreferenceManager.setAdvertBannerPosition(position);

            return lsBannerURL;
        }
        return null;
    }
}
