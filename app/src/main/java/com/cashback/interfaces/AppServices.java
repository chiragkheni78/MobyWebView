package com.cashback.interfaces;


import com.cashback.models.request.ActivityDetailsRequest;
import com.cashback.models.request.ActivityMarkAsUsedRequest;
import com.cashback.models.request.FetchOffersRequest;
import com.cashback.models.request.GetProfileRequest;
import com.cashback.models.request.GetSettingRequest;
import com.cashback.models.request.ActivityListRequest;
import com.cashback.models.request.MessageListRequest;
import com.cashback.models.request.OfferDetailsRequest;
import com.cashback.models.request.OfferFilterRequest;
import com.cashback.models.request.QuizDetailsRequest;
import com.cashback.models.request.SaveMiniProfileRequest;
import com.cashback.models.request.SubmitQuizRequest;
import com.cashback.models.request.SyncTokenRequest;
import com.cashback.models.request.UpdateShopOnlineBlinkRequest;
import com.cashback.models.response.ActivityDetailsResponse;
import com.cashback.models.response.ActivityMarkAsUsedResponse;
import com.cashback.models.response.BillUploadResponse;
import com.cashback.models.response.GetSettingResponse;
import com.cashback.models.response.ActivityListResponse;
import com.cashback.models.response.OfferFilterResponse;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.response.GetProfileResponse;
import com.cashback.models.response.MessageListResponse;
import com.cashback.models.response.OfferDetailsResponse;
import com.cashback.models.response.QuizDetailsResponse;
import com.cashback.models.response.SaveProfileResponse;
import com.cashback.models.response.SubmitQuizResponse;
import com.cashback.models.response.SyncTokenResponse;
import com.cashback.models.response.UpdateShopOnlineBlinkResponse;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppServices {

    @POST("v2-apis/")
    Call<GetSettingResponse> getGlobalSetting(@Body GetSettingRequest foRequest);

    @POST("syncTokenToServer")
    Call<SyncTokenResponse> syncTokenToServer(@Body SyncTokenRequest foRequestObject);

    @POST("v2-apis/")
    Call<GetProfileResponse> getUserProfile(@Body GetProfileRequest foRequest);

    @POST("v2-apis/")
    Call<SaveProfileResponse> saveMiniProfile(@Body SaveMiniProfileRequest foRequest);

    @POST("v2-apis/")
    Call<FetchOffersResponse> getOfferList(@Body FetchOffersRequest foRequest);

    @POST("v2-apis/")
    Call<OfferFilterResponse> getOfferFilter(@Body OfferFilterRequest foRequest);

    @POST("v2-apis/")
    Call<OfferDetailsResponse> getOfferDetails(@Body OfferDetailsRequest foRequest);

    @POST("v2-apis/")
    Call<QuizDetailsResponse> getQuizDetails(@Body QuizDetailsRequest foRequest);

    @POST("v2-apis/")
    Call<SubmitQuizResponse> submitQuizAnswer(@Body SubmitQuizRequest foRequest);

    @POST("v2-apis/")
    Call<ActivityListResponse> getActivityList(@Body ActivityListRequest foRequest);

    @POST("v2-apis/")
    Call<ActivityDetailsResponse> getActivityDetails(@Body ActivityDetailsRequest foRequest);

    @POST("v2-apis/")
    Call<ActivityMarkAsUsedResponse> updateMarkAsUsed(@Body ActivityMarkAsUsedRequest foRequest);

    @Multipart
    @POST("v2-apis/")
    Call<BillUploadResponse> uploadTransactionBill(@Part("fsAction")RequestBody loAction, @Part("foTransactionData") RequestBody foRequest, @Part List<MultipartBody.Part> files);

    @POST("v2-apis/")
    Call<UpdateShopOnlineBlinkResponse> updateShopOnlineBlink(@Body UpdateShopOnlineBlinkRequest foRequest);

    @POST("v2-apis/")
    Call<MessageListResponse> getMessageList(@Body MessageListRequest foRequest);

}
