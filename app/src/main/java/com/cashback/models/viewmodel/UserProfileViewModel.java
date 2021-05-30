package com.cashback.models.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.EWallet;
import com.cashback.models.request.DeleteCardRequest;
import com.cashback.models.request.GetUserProfileRequest;
import com.cashback.models.request.SaveUserProfileRequest;
import com.cashback.models.response.DeleteCardResponse;
import com.cashback.models.response.GetUserProfileResponse;
import com.cashback.models.response.SaveUserProfileResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserProfileViewModel extends ViewModel {

    public MutableLiveData<GetUserProfileResponse> getUserProfileStatus = new MutableLiveData<>();

    public void getUserProfile(Context foContext) {
        GetUserProfileRequest loGetUserProfileRequest = new GetUserProfileRequest();
        loGetUserProfileRequest.setAction(Constants.API.GET_USER_DETAILS.getValue());
        loGetUserProfileRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loGetUserProfileRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loGetUserProfileRequest.validateData(foContext);
        if (lsMessage != null) {
            getUserProfileStatus.postValue(new GetUserProfileResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<GetUserProfileResponse> loRequest = APIClient.getInterface().getUserProfile(loGetUserProfileRequest);
        Common.printReqRes(loRequest, "getUserProfile", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getUserProfile", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    GetUserProfileResponse loJsonObject = foResponse.body();
                    getUserProfileStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    getUserProfileStatus.postValue(new GetUserProfileResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                Common.printReqRes(t, "getUserProfile", Common.LogType.ERROR);
                getUserProfileStatus.postValue(new GetUserProfileResponse(true, t.getMessage()));
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

    public MutableLiveData<SaveUserProfileResponse> saveUserProfileStatus = new MutableLiveData<>();

    public void saveUserProfile(Context foContext, SaveUserProfileRequest foSaveUserProfileRequest) {
        foSaveUserProfileRequest.setAction(Constants.API.SAVE_USER_DETAILS.getValue());
        foSaveUserProfileRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        foSaveUserProfileRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = foSaveUserProfileRequest.validateData(foContext);
        if (lsMessage != null) {
            saveUserProfileStatus.postValue(new SaveUserProfileResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<SaveUserProfileResponse> loRequest = APIClient.getInterface().saveUserProfile(foSaveUserProfileRequest);
        Common.printReqRes(loRequest, "saveUserProfile", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<SaveUserProfileResponse>() {
            @Override
            public void onResponse(Call<SaveUserProfileResponse> call, Response<SaveUserProfileResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "saveUserProfile", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    SaveUserProfileResponse loJsonObject = foResponse.body();
                    saveUserProfileStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    saveUserProfileStatus.postValue(new SaveUserProfileResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<SaveUserProfileResponse> call, Throwable t) {
                Common.printReqRes(t, "saveUserProfile", Common.LogType.ERROR);
                saveUserProfileStatus.postValue(new SaveUserProfileResponse(true, t.getMessage()));
            }
        });
    }

    public int getBankOfferRadius(int fiPosition) {

        switch (fiPosition) {
            case 0:
                return 250;
            case 1:
                return 500;
            case 2:
                return 1000;
            case 3:
                return 3000;
            case 4:
                return 5000;
        }
        return 5000;
    }

    public MutableLiveData<DeleteCardResponse> deleteCardStatus = new MutableLiveData<>();

    public void deleteUserCard(Context foContext, int fiCardID) {

        DeleteCardRequest loDeleteCardRequest = new DeleteCardRequest(fiCardID);
        loDeleteCardRequest.setAction(Constants.API.DELETE_USER_CARD.getValue());
        loDeleteCardRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loDeleteCardRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loDeleteCardRequest.validateData(foContext);
        if (lsMessage != null) {
            deleteCardStatus.postValue(new DeleteCardResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<DeleteCardResponse> loRequest = APIClient.getInterface().deleteCard(loDeleteCardRequest);
        Common.printReqRes(loRequest, "deleteCard", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<DeleteCardResponse>() {
            @Override
            public void onResponse(Call<DeleteCardResponse> call, Response<DeleteCardResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "deleteCard", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    DeleteCardResponse loJsonObject = foResponse.body();
                    deleteCardStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    deleteCardStatus.postValue(new DeleteCardResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<DeleteCardResponse> call, Throwable t) {
                Common.printReqRes(t, "deleteCard", Common.LogType.ERROR);
                deleteCardStatus.postValue(new DeleteCardResponse(true, t.getMessage()));
            }
        });
    }
}
