package com.cashback.models.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adgyde.android.AdGyde;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.cashback.AppGlobal;
import com.cashback.activities.SplashActivity;
import com.cashback.models.request.StaticLabelsRequest;
import com.cashback.models.response.StaticLabelsResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;
import com.cashback.utils.SharedPreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashViewModel extends ViewModel {
    private static final String TAG = SplashViewModel.class.getSimpleName();

    InstallReferrerClient referrerClient;

    public void checkInstallReferrer(Context foContext) {

        referrerClient = InstallReferrerClient.newBuilder(foContext).build();

        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        try {
                            getReferralUser(foContext);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        LogV2.i(TAG, "InstallReferrer FEATURE_NOT_SUPPORTED");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        LogV2.i(TAG, "InstallReferrer SERVICE_UNAVAILABLE");
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                LogV2.i(TAG, "InstallReferrer onInstallReferrerServiceDisconnected()");
            }
        });
    }

    private void getReferralUser(Context foContext) throws RemoteException {
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);
        if (referrerClient != null && referrerClient.isReady()) {
            ReferrerDetails response = referrerClient.getInstallReferrer();
            String referrerData = response.getInstallReferrer();
            Log.e(TAG, "Install referrer:" + response.getInstallReferrer());

            try {
                HashMap<String, String> values = new HashMap<>();
                if (referrerData != null) {
                    String referrers[] = referrerData.split("&");

                    for (String referrerValue : referrers) {
                        String keyValue[] = referrerValue.split("=");
                        values.put(URLDecoder.decode(keyValue[0], "UTF-8"), (keyValue.length > 1) ? URLDecoder.decode(keyValue[1], "UTF-8") : "");
                    }

                    if (values.containsKey("utm_campaign")) {
                        String lsCampaign = values.get("utm_campaign");
                        Log.e(TAG, "UTM campaign:" + lsCampaign);
                        if (!lsCampaign.isEmpty())
                            loSharedPreferenceManager.setAppDownloadCampaign(lsCampaign);
                    }

                    if (values.containsKey("utm_medium")) {
                        String lsMedium = values.get("utm_medium");
                        Log.e(TAG, "UTM medium:" + lsMedium);
                        loSharedPreferenceManager.setAppDownloadMedium(lsMedium);
                    }

                    if (values.containsKey("utm_source")) {
                        String lsSource = values.get("utm_source");
                        Log.e(TAG, "UTM source:" + lsSource);
                        loSharedPreferenceManager.setAppDownloadSource(lsSource);
                    }
                }
            } catch (Exception e) {
                LogV2.logException(TAG, e);
                Log.e(TAG, "UTM ERROR:" + e.getMessage());
            } finally {
                if (referrerClient != null)
                    referrerClient.endConnection();

                if (loSharedPreferenceManager.getAppDownloadCampaign().isEmpty()) {
                    String lsCampaignName = AdGyde.getCampaignName();
                    if (!lsCampaignName.isEmpty()) {
                        Log.e(TAG, "AdGyde:: UTM campaign:" + lsCampaignName);
                        loSharedPreferenceManager.setAppDownloadCampaign(lsCampaignName);
                    }
                }
                Log.e(TAG, "UTM FINISH: " + loSharedPreferenceManager.getAppDownloadCampaign());
            }
        }
    }


    public void retrieveFirebaseDeepLink(Context foContext, Intent foIntent) {
        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(foContext);

        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(foIntent)
                .addOnSuccessListener((SplashActivity) foContext, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                        try {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {

                                // Handle the deep link. For example, open the linked
                                // content, or apply promotional credit to the user's
                                // account.
                                // ...

                                deepLink = pendingDynamicLinkData.getLink();

                                Bundle loBundle = pendingDynamicLinkData.getExtensions();
                                Bundle scionData = loBundle.getBundle("scionData");
                                Bundle _cmp = scionData.getBundle("_cmp");
                                String lsSource = _cmp.getString("source");
                                String lsCampaign = _cmp.getString("campaign");
                                String lsMedium = _cmp.getString("medium");
                                loSharedPreferenceManager.setAppDownloadCampaign(lsCampaign);
                                loSharedPreferenceManager.setAppDownloadMedium(lsMedium);
                                loSharedPreferenceManager.setAppDownloadSource(lsSource);
                            }

                            // [START_EXCLUDE]

                            // Display deep link in the UI
                            if (deepLink != null) {
                                LogV2.i(TAG, "Found deep link!:: " + deepLink);
                            } else {
                                LogV2.i(TAG, "getDynamicLink: no link found");
                            }
                            // [END_EXCLUDE]

                        } catch (Exception e) {
                            LogV2.logException(TAG, e);
                        }
                    }
                })
                .addOnFailureListener((SplashActivity) foContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LogV2.logException(TAG, e);
                    }
                });
        // [END get_deep_link]
    }

    public MutableLiveData<StaticLabelsResponse> fetchStaticLabels = new MutableLiveData<>();

    public void fetchStaticLabelsList(Context foContext) {
        StaticLabelsRequest loStaticLabelsRequest = new StaticLabelsRequest();
        loStaticLabelsRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loStaticLabelsRequest.setAction(Constants.API.GET_ALL_STATIC_LABEL.getValue());
        loStaticLabelsRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsLabels = loStaticLabelsRequest.validateData(foContext);
        if (lsLabels != null) {
            fetchStaticLabels.postValue(new StaticLabelsResponse(true, lsLabels));
            return;
        }

        //API Call
        Call<StaticLabelsResponse> loRequest = APIClient.getInterface().getAllStaticLabelsList(loStaticLabelsRequest);
        Common.printReqRes(loRequest, "getAllStaticLabels", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<StaticLabelsResponse>() {
            @Override
            public void onResponse(Call<StaticLabelsResponse> call, Response<StaticLabelsResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getAllStaticLabels", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    StaticLabelsResponse loJsonObject = foResponse.body();

                    JSONObject request = loJsonObject.getLabels();
                    Common.setDynamicText(foContext, request);

                    fetchStaticLabels.postValue(loJsonObject);
                } else {
                    String fsLabels = Common.getErrorMessage(foResponse);
                    fetchStaticLabels.postValue(new StaticLabelsResponse(true, fsLabels));
                }
            }

            @Override
            public void onFailure(Call<StaticLabelsResponse> call, Throwable t) {
                Common.printReqRes(t, "getAllStaticLabels", Common.LogType.ERROR);
                fetchStaticLabels.postValue(new StaticLabelsResponse(true, t.getMessage()));
            }
        });
    }
}
