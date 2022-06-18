package com.mobycashback.webview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mobycashback.model.Category;
import com.mobycashback.utils.Common;
import com.mobycashback.webview.databinding.ActivityCategoriesBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesActivity extends AppCompatActivity {
    ActivityCategoriesBinding moBinding;

    public MutableLiveData<GetCategoryResponse> fetchCategory = new MutableLiveData<>();
    ArrayList<Category> moCategories = new ArrayList<>();
    CategoryAdapter moCategoryAdapter;
    String url, setPrimaryColor, setSecondaryColor, setPrimaryTextColor, setSecondaryTextColor;
    boolean isAccessGPS, isAccessStorage;
    public static MobyWebViewBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        View view = moBinding.getRoot();
        setContentView(view);

        initView();
        fetchCategory();
        builder = new MobyWebViewBuilder();

        if (getIntent() != null) {
            url = getIntent().getStringExtra(Common.URL);
            isAccessGPS = getIntent().getBooleanExtra(Common.ACCESS_GPS, false);
            isAccessStorage = getIntent().getBooleanExtra(Common.ACCESS_STORAGE, false);
            setPrimaryColor = getIntent().getStringExtra(Common.PRIMARY_COLOR);
            setSecondaryColor = getIntent().getStringExtra(Common.SECONDARY_COLOR);
            setPrimaryTextColor = getIntent().getStringExtra(Common.PRIMARY_TEXT_COLOR);
            setSecondaryTextColor = getIntent().getStringExtra(Common.SECONDARY_TEXT_COLOR);

        }

        moBinding.ivBack.setColorFilter(Color.parseColor(setSecondaryTextColor));
        moBinding.llTop.setBackgroundColor(Color.parseColor(setPrimaryColor));
        moBinding.ivMyOffer.setColorFilter(Color.parseColor(setSecondaryTextColor));
        moBinding.tvMyOffer.setTextColor(Color.parseColor(setSecondaryTextColor));

        moBinding.ivCoupon.setColorFilter(Color.parseColor(setSecondaryTextColor));
        moBinding.tvCoupon.setTextColor(Color.parseColor(setSecondaryTextColor));

        moBinding.ivWallet.setColorFilter(Color.parseColor(setSecondaryTextColor));
        moBinding.tvWallet.setTextColor(Color.parseColor(setSecondaryTextColor));



        moBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        moBinding.llOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIClient.MY_OFFERS;
                setWebview();
                builder.setUrl(url);
            }
        });
        moBinding.llCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIClient.MY_COUPONS;
                setWebview();
                builder.setUrl(url);
            }
        });
        moBinding.llWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = APIClient.WALLET;
                setWebview();
                builder.setUrl(url);
            }
        });
    }

    private void initView() {
        final LinearLayoutManager loLayoutManager = new GridLayoutManager(this, 3);
        moBinding.rvCategory.setLayoutManager(loLayoutManager);
        moCategoryAdapter = new CategoryAdapter(this, moCategories);
        moBinding.rvCategory.setAdapter(moCategoryAdapter);

        moCategoryAdapter.setOnItemClickListener(new CategoryAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                url = APIClient.CATEGORY_URL + moCategories.get(position).getCategoryId();
                setWebview();
                builder.setUrl(url);
            }
        });
    }

    public void fetchCategory() {
        CategoryRequest loCategoryRequest = new CategoryRequest("getCategoryList", LibApp.getDeviceUniqueId());
        Call<GetCategoryResponse> loRequest = APIClient.getInterface().getCategory(loCategoryRequest);

        loRequest.enqueue(new Callback<GetCategoryResponse>() {
            @Override
            public void onResponse(Call<GetCategoryResponse> call, Response<GetCategoryResponse> foResponse) {
//                Common.printReqRes(foResponse.body(), "getOfferList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    GetCategoryResponse loGetCategoryResponse = foResponse.body();
                    moCategories = loGetCategoryResponse.getCategoryList();
                    setCategoryView();
                    return;
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchCategory.postValue(new GetCategoryResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<GetCategoryResponse> call, Throwable t) {
//                Common.printReqRes(t, "getOfferList", Common.LogType.ERROR);
                fetchCategory.postValue(new GetCategoryResponse(true, t.getMessage()));
            }
        });
    }

    private void setCategoryView() {
        if (moCategories != null && moCategories.size() > 0) {
            moCategoryAdapter.addAll(moCategories);
            moCategoryAdapter.notifyDataSetChanged();
        }
    }

    public void setWebview() {
        builder.setAccessStorage(isAccessStorage)
                .setAccessGPS(isAccessGPS)
                .setUrl(url)
                .setPrimaryColor(setPrimaryColor)
                .setSecondaryColor(setSecondaryColor)
                .setPrimaryTextColor(setPrimaryTextColor)
                .setSecondaryTextColor(setSecondaryTextColor)
                .setFromCategory(true)
                .build();
        builder.loadWebView();
    }
}
