package com.cashback.models.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.request.UpdateUserSessionRequest;
import com.cashback.models.request.GetSettingRequest;
import com.cashback.models.response.GetUpdateUserSessionResponse;
import com.cashback.models.response.GetSettingResponse;
import com.cashback.services.MyFirebaseMessagingService;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;
import com.cashback.utils.SharedPreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();

    public MutableLiveData<GetSettingResponse> getSettingStatus = new MutableLiveData<>();
    public MutableLiveData<GetUpdateUserSessionResponse> getUpdateUserSession = new MutableLiveData<>();

    public void getGlobalSetting(Context foContext) {
        GetSettingRequest loGetSettingRequest = new GetSettingRequest();
        loGetSettingRequest.setAction(Constants.API.GET_GLOBAL_SETTING.getValue());
        loGetSettingRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loGetSettingRequest.setPhoneNumber(AppGlobal.getPhoneNumber());

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
                    Log.d("TTT", "Response..." + loJsonObject.isFbIsGpsEnInApp());
                    AppGlobal.setCategories(loJsonObject.getCategoryList());
                    AppGlobal.setMainStore(loJsonObject.getFoMainStore());
                    AppGlobal.setTotalBillVerified(loJsonObject.getTotalBillVerified());
                    AppGlobal.fbIsGpsEnInApp = loJsonObject.isFbIsGpsEnInApp();
                    AppGlobal.msProviderCashLabel = loJsonObject.getCashbackString();

                    //AppGlobal.fbIsGpsEnInApp = false;
                    if (loJsonObject.getMessageDetailImage() != null && loJsonObject.getMessageDetailImage().size() > 0) {
                        AppGlobal.setMessageDetailImage(loJsonObject.getMessageDetailImage());
                    }
                    if (loJsonObject.getDealsOfTheDay() != null && loJsonObject.getDealsOfTheDay().size() > 0) {
                        AppGlobal.setDealOfTheDayResponse(loJsonObject.getDealsOfTheDay());
                    }
                    if (loJsonObject.getShareScreenImages() != null && loJsonObject.getShareScreenImages().size() > 0) {
                        AppGlobal.setSharePageImages(loJsonObject.getShareScreenImages());
                    }
                    getSettingStatus.postValue(loJsonObject);
                    updateToken(foContext);
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

        UpdateUserSessionRequest loGetRequestFirstLoad = new UpdateUserSessionRequest();
        loGetRequestFirstLoad.setAction(Constants.API.UPDATE_USER_SESSION.getValue());
        loGetRequestFirstLoad.setAdId("0");
        loGetRequestFirstLoad.setPhoneNumber(AppGlobal.getPhoneNumber());
        loGetRequestFirstLoad.setEventType(Constants.IntentKey.CURRENT_SESSION_TIME);

        Call<GetUpdateUserSessionResponse> loRequestForFirstLoad = APIClient.getInterface().saveEvent(loGetRequestFirstLoad);
        Common.printReqRes(loGetRequestFirstLoad, "UpdateUserSessionRequest", Common.LogType.REQUEST);

        loRequestForFirstLoad.enqueue(new Callback<GetUpdateUserSessionResponse>() {
            @Override
            public void onResponse(Call<GetUpdateUserSessionResponse> call, Response<GetUpdateUserSessionResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "UpdateUserSessionRequest", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    //GetPageLoadEventResponse loJsonObject = foResponse.body();
                    Log.d("TTT", "Response..." + foResponse.body().toString());
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    getUpdateUserSession.postValue(new GetUpdateUserSessionResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<GetUpdateUserSessionResponse> call, Throwable t) {
                Common.printReqRes(t, "UpdateUserSessionRequest", Common.LogType.ERROR);
                getUpdateUserSession.postValue(new GetUpdateUserSessionResponse(true, t.getMessage()));
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

    private void updateToken(Context foContext) {
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getToken() failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = task.getResult().toString();
                    if (!loSharedPreferenceManager.isFcmTokenSynch() || !loSharedPreferenceManager.getFcmToken().equalsIgnoreCase(token)) {
                        LogV2.i(TAG, "New Token:: " + token);
                        loSharedPreferenceManager.setFcmToken(token);
                        MyFirebaseMessagingService.syncTokenToServer(foContext);
                    } else {
                        LogV2.i(TAG, "SAME TOKEN");
                    }
                });
    }
}
