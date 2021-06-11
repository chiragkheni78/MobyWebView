package com.cashback.models.viewmodel;

import android.content.Context;
import android.os.Bundle;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adgyde.android.AdGyde;
import com.cashback.AppGlobal;
import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.request.GetMiniProfileRequest;
import com.cashback.models.request.SaveMiniProfileRequest;
import com.cashback.models.response.GetMiniProfileResponse;
import com.cashback.models.response.SaveMiniProfileResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MiniProfileViewModel extends ViewModel {

    public MutableLiveData<GetMiniProfileResponse> getMiniProfileStatus = new MutableLiveData<>();

    public void getMiniProfile(Context foContext) {
        GetMiniProfileRequest loGetMiniProfileRequest = new GetMiniProfileRequest();
        loGetMiniProfileRequest.setAction(Constants.API.GET_MINI_PROFILE.getValue());
        loGetMiniProfileRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loGetMiniProfileRequest.setReferrer(AppGlobal.getPreferenceManager().getAppDownloadCampaign());
        loGetMiniProfileRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loGetMiniProfileRequest.validateData(foContext);
        if (lsMessage != null) {
            getMiniProfileStatus.postValue(new GetMiniProfileResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<GetMiniProfileResponse> loRequest = APIClient.getInterface().getMiniProfile(loGetMiniProfileRequest);
        Common.printReqRes(loRequest, "getProfile", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<GetMiniProfileResponse>() {
            @Override
            public void onResponse(Call<GetMiniProfileResponse> call, Response<GetMiniProfileResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getProfile", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    GetMiniProfileResponse loJsonObject = foResponse.body();
                    getMiniProfileStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    getMiniProfileStatus.postValue(new GetMiniProfileResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<GetMiniProfileResponse> call, Throwable t) {
                Common.printReqRes(t, "getProfile", Common.LogType.ERROR);
                getMiniProfileStatus.postValue(new GetMiniProfileResponse(true, t.getMessage()));
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


    public int getAdvertPosition(Context foContext, ArrayList<Advertisement> foAdvertisementList) {

        if (foAdvertisementList.size() > 0) {
            String lsBannerURL;
            int position = 0;

            SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);
            if (loSharedPreferenceManager.getAdvertBannerPosition() > -1) {
                int liOldPosition = loSharedPreferenceManager.getAdvertBannerPosition();
                position = liOldPosition + 1;
            }

            position = (position < foAdvertisementList.size()) ? position : 0;

            lsBannerURL = foAdvertisementList.get(position).getImageUrl();

            loSharedPreferenceManager.setAdvertBannerPosition(position);

            return position;
        }
        return 0;
    }

    public MutableLiveData<SaveMiniProfileResponse> saveMiniProfileStatus = new MutableLiveData<>();

    public void saveProfile(Context foContext, int fiAge, String fsGender, int fiEWalletId, String fsUPIAddress) {
        SaveMiniProfileRequest loSaveMiniProfileRequest = new SaveMiniProfileRequest(fiAge, fsGender, fiEWalletId, fsUPIAddress);
        loSaveMiniProfileRequest.setAction(Constants.API.SAVE_MINI_PROFILE.getValue());
        loSaveMiniProfileRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loSaveMiniProfileRequest.setReferrer(AppGlobal.getPreferenceManager().getAppDownloadCampaign());
        loSaveMiniProfileRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loSaveMiniProfileRequest.validateData(foContext);
        if (lsMessage != null) {
            saveMiniProfileStatus.postValue(new SaveMiniProfileResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<SaveMiniProfileResponse> loRequest = APIClient.getInterface().saveMiniProfile(loSaveMiniProfileRequest);
        Common.printReqRes(loRequest, "saveMiniProfile", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<SaveMiniProfileResponse>() {
            @Override
            public void onResponse(Call<SaveMiniProfileResponse> call, Response<SaveMiniProfileResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "saveMiniProfile", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    SaveMiniProfileResponse loJsonObject = foResponse.body();
                    saveMiniProfileStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    saveMiniProfileStatus.postValue(new SaveMiniProfileResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<SaveMiniProfileResponse> call, Throwable t) {
                Common.printReqRes(t, "saveMiniProfile", Common.LogType.ERROR);
                saveMiniProfileStatus.postValue(new SaveMiniProfileResponse(true, t.getMessage()));
            }
        });
    }


}
