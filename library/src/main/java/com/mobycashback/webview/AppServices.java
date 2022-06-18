package com.mobycashback.webview;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppServices {

    @POST("v3/")
    Call<GetCategoryResponse> getCategory(@Body CategoryRequest loCategoryRequest);

}
