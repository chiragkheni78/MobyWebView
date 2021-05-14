package com.cashback.interfaces;


import com.cashback.models.request.FetchOffersRequest;
import com.cashback.models.request.GetProfileRequest;
import com.cashback.models.request.MessageListRequest;
import com.cashback.models.request.OfferDetailsRequest;
import com.cashback.models.request.OfferFilterRequest;
import com.cashback.models.request.SaveMiniProfileRequest;
import com.cashback.models.response.OfferFilterResponse;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.response.GetProfileResponse;
import com.cashback.models.response.MessageListResponse;
import com.cashback.models.response.OffersDetailsResponse;
import com.cashback.models.response.SaveProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AppServices {

    @POST("v2-apis/")
    Call<GetProfileResponse> getUserProfile(@Body GetProfileRequest foRequest);

    @POST("v2-apis/")
    Call<SaveProfileResponse> saveMiniProfile(@Body SaveMiniProfileRequest foRequest);

    @POST("v2-apis/")
    Call<FetchOffersResponse> getOfferList(@Body FetchOffersRequest foRequest);

    @POST("v2-apis/")
    Call<OfferFilterResponse> getOfferFilter(@Body OfferFilterRequest foRequest);

    @POST("v2-apis/")
    Call<OffersDetailsResponse> fetchOfferDetails(@Body OfferDetailsRequest foRequest);

    @POST("v2-apis/")
    Call<MessageListResponse> getMessageList(@Body MessageListRequest foRequest);

}
