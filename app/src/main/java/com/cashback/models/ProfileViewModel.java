package com.cashback.models;

import android.content.Context;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.models.request.GetProfileRequest;
import com.cashback.models.request.SaveMiniProfileRequest;
import com.cashback.models.response.GetProfileResponse;
import com.cashback.models.response.SaveProfileResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileViewModel extends ViewModel {

    public MutableLiveData<GetProfileResponse> getProfileStatus = new MutableLiveData<>();

    public void getProfile(Context foContext) {
        GetProfileRequest loGetProfileRequest = new GetProfileRequest();
        loGetProfileRequest.setAction(Constants.API.GET_USER_PROFILE.getValue());
        loGetProfileRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loGetProfileRequest.setReferralCode("OTHER");

        String lsMessage = loGetProfileRequest.validateData(foContext);
        if (lsMessage != null) {
            getProfileStatus.postValue(new GetProfileResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<GetProfileResponse> loRequest = APIClient.getInterface().getUserProfile(loGetProfileRequest);
        Common.printReqRes(loRequest, "getProfile", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<GetProfileResponse>() {
            @Override
            public void onResponse(Call<GetProfileResponse> call, Response<GetProfileResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getProfile", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    GetProfileResponse loJsonObject = foResponse.body();
                    getProfileStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    getProfileStatus.postValue(new GetProfileResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                Common.printReqRes(t, "getProfile", Common.LogType.ERROR);
                getProfileStatus.postValue(new GetProfileResponse(true, t.getMessage()));
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

    public MutableLiveData<SaveProfileResponse> saveProfileStatus = new MutableLiveData<>();

    public void saveProfile(Context foContext, int fiAge, String fsGender, int fiEWalletId) {
        SaveMiniProfileRequest loSaveMiniProfileRequest = new SaveMiniProfileRequest(fiAge, fsGender, fiEWalletId);
        loSaveMiniProfileRequest.setAction(Constants.API.SAVE_MINI_PROFILE.getValue());
        loSaveMiniProfileRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loSaveMiniProfileRequest.setReferralCode("OTHER");

        String lsMessage = loSaveMiniProfileRequest.validateData(foContext);
        if (lsMessage != null) {
            saveProfileStatus.postValue(new SaveProfileResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<SaveProfileResponse> loRequest = APIClient.getInterface().saveMiniProfile(loSaveMiniProfileRequest);
        Common.printReqRes(loRequest, "saveMiniProfile", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<SaveProfileResponse>() {
            @Override
            public void onResponse(Call<SaveProfileResponse> call, Response<SaveProfileResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "saveMiniProfile", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    SaveProfileResponse loJsonObject = foResponse.body();
                    saveProfileStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    saveProfileStatus.postValue(new SaveProfileResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<SaveProfileResponse> call, Throwable t) {
                Common.printReqRes(t, "saveMiniProfile", Common.LogType.ERROR);
                saveProfileStatus.postValue(new SaveProfileResponse(true, t.getMessage()));
            }
        });
    }
}
