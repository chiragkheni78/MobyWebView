package com.cashback.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.adapters.AdvertAdapter;
import com.cashback.adapters.ImageSliderAdapter;
import com.cashback.databinding.ActivityImageSlidderBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.response.AdvertisementResponse;
import com.cashback.models.viewmodel.AdvertisementViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.custom.CircularViewPagerHandler;

import java.util.ArrayList;

public class FragmentHelp extends BaseFragment {

    public FragmentHelp() {

    }

    private static final String TAG = FragmentHelp.class.getSimpleName();
    ActivityImageSlidderBinding moBinding;
    AdvertisementViewModel moProfileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = ActivityImageSlidderBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeContent();
    }


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityImageSlidderBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }*/

    private void initializeContent() {
        Common.hideKeyboard(getActivity());

       initViewModel();

       if (getArguments()!= null){
           String msScreenType = getArguments().getString(Constants.IntentKey.ADVERT_SCREEN_TYPE);
           String lsTitle = getArguments().getString(Constants.IntentKey.SCREEN_TITLE);

           showProgressDialog();
           moProfileViewModel.fetAdvertList(getContext(), msScreenType);

           setToolbar(lsTitle);
       }
    }

    private void setToolbar(String fsTitle) {

        moBinding.toolbar.toolbar.setVisibility(View.GONE);

        /*Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);

        ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(fsTitle);*/
    }

    private void initViewModel() {
        moProfileViewModel = new ViewModelProvider(this).get(AdvertisementViewModel.class);
        moProfileViewModel.advertListStatus.observe(getActivity(), advertImageObserver);
    }

    Observer<AdvertisementResponse> advertImageObserver = new Observer<AdvertisementResponse>() {
        @Override
        public void onChanged(AdvertisementResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                setImageSlider(loJsonObject.getAdvertisementList());
            } else {
                Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    private void setImageSlider(ArrayList<Advertisement> foAdvertisementList) {
        /*ImageSliderAdapter loSliderAdapter = new ImageSliderAdapter(getContext(), foAdvertisementList);
        moBinding.vpAdvert.setAdapter(loSliderAdapter);
        moBinding.vpAdvert.addOnPageChangeListener(new CircularViewPagerHandler(moBinding.vpAdvert));
        moBinding.vpAdvert.addOnPageChangeListener(new CircularViewPagerHandler(moBinding.vpAdvert));

        moBinding.indicator.setViewPager(moBinding.vpAdvert);
        moBinding.indicator.setVisibility(View.VISIBLE);

        moBinding.imageSlider.setVisibility(View.GONE);
        moBinding.flSlider.setVisibility(View.VISIBLE);*/

        moBinding.imageSlider.setVisibility(View.VISIBLE);
        moBinding.flSlider.setVisibility(View.GONE);
        moBinding.imageSlider.setSliderAdapter(new AdvertAdapter(getContext(), foAdvertisementList));
    }

}
