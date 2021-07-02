package com.cashback.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.adapters.AdvertAdapter;
import com.cashback.adapters.ImageSliderAdapter;
import com.cashback.databinding.ActivityImageSlidderBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.response.AdvertisementResponse;
import com.cashback.models.viewmodel.AdvertisementViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.custom.CirclePageIndicator;
import com.cashback.utils.custom.CircularViewPagerHandler;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class AdvertisementActivity extends BaseActivity{

    private static final String TAG = AdvertisementActivity.class.getSimpleName();
    ActivityImageSlidderBinding moBinding;
    AdvertisementViewModel moProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityImageSlidderBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }

    private void initializeContent() {
        Common.hideKeyboard(this);

       initViewModel();

       if (getIntent()!= null){
           String msScreenType = getIntent().getStringExtra(Constants.IntentKey.ADVERT_SCREEN_TYPE);
           String lsTitle = getIntent().getStringExtra(Constants.IntentKey.SCREEN_TITLE);

           showProgressDialog();
           moProfileViewModel.fetAdvertList(getContext(), msScreenType);

           setToolbar(lsTitle);
       }
    }

    private void setToolbar(String fsTitle) {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);

        ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(fsTitle);
    }

    private void initViewModel() {
        moProfileViewModel = new ViewModelProvider(this).get(AdvertisementViewModel.class);
        moProfileViewModel.advertListStatus.observe(this, advertImageObserver);
    }

    Observer<AdvertisementResponse> advertImageObserver = new Observer<AdvertisementResponse>() {
        @Override
        public void onChanged(AdvertisementResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                setImageSlider(loJsonObject.getAdvertisementList());
            } else {
                Common.showErrorDialog(AdvertisementActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    private void setImageSlider(ArrayList<Advertisement> foAdvertisementList) {
//        ImageSliderAdapter loSliderAdapter = new ImageSliderAdapter(getContext(), foAdvertisementList);
//        moBinding.vpAdvert.setAdapter(loSliderAdapter);
//        moBinding.vpAdvert.addOnPageChangeListener(new CircularViewPagerHandler(moBinding.vpAdvert));
//        moBinding.vpAdvert.addOnPageChangeListener(new CircularViewPagerHandler(moBinding.vpAdvert));
//
//        moBinding.indicator.setViewPager(moBinding.vpAdvert);
//        moBinding.indicator.setVisibility(View.VISIBLE);

        moBinding.imageSlider.setVisibility(View.VISIBLE);
        moBinding.flSlider.setVisibility(View.GONE);
        moBinding.imageSlider.setSliderAdapter(new AdvertAdapter(getContext(), foAdvertisementList, new AdvertAdapter.OnItemClick() {
            @Override
            public void onItemClick(Advertisement advertisement) {
                Intent loIntent = new Intent(AdvertisementActivity.this, HomeActivity.class);
                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                loIntent.putExtra(Constants.IntentKey.OFFER_ID, advertisement.getAdID());
                loIntent.putExtra(Constants.IntentKey.CATEGORY_ID, advertisement.getCategoryID());
                loIntent.putExtra(Constants.IntentKey.BANNER_ID, advertisement.getBannerID());
                startActivity(loIntent);
                finish();
            }
        }));
    }

}
