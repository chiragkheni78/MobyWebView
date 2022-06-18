package com.mobycashback.webview;

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

public class APIClient {


    public static String BASE_URL = "https://mobyads.in/moby/";
    public static String IMAGE_URL = "https://mobyads.in/moby";
    public static String CATEGORY_URL =
            "https://app.mobyads.in/publisher/A01234567/?fsMobile=918140663133&fsEmail=johndeo@gmail.com&fsFirstName=Chirag&fsLastName=Kheni&fiDeviceType=0" + "&fiLoadCategory=";

    public static String MY_COUPONS = "https://app.mobyads.in/my-timeline";
    public static String MY_OFFERS = "https://app.mobyads.in/publisher/A01234567/?fsMobile=918140663133&fsEmail=johndeo@gmail.com&fsFirstName=Chirag&fsLastName=Kheni&fiDeviceType=0";
    public static String WALLET = "https://app.mobyads.in/wallet";

    public static AppServices getInterface() {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

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
