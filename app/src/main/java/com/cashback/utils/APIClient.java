package com.cashback.utils;

import com.cashback.AppGlobal;
import com.cashback.interfaces.AppServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cashback.utils.Constants.BASE_URL;

public class APIClient {


    public static AppServices getInterface() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .serializeSpecialFloatingPointValues()
                .create();

//        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(20, TimeUnit.SECONDS)
//                .addInterceptor(logging)
//                .connectTimeout(20, TimeUnit.SECONDS)
//                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Device-Details", Common.getDeviceDetails(AppGlobal.getInstance()).toString())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit moRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        AppServices moAppService = moRetrofit.create(AppServices.class);

        return moAppService;
    }

}
