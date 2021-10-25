package com.cashback.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.activities.AdvertisementActivity;
import com.cashback.activities.HomeActivity;
import com.cashback.adapters.AdvertAdapter;
import com.cashback.adapters.HelpListAdapter;
import com.cashback.adapters.ShareBannerAdapter;
import com.cashback.databinding.ActivityHelpBinding;
import com.cashback.databinding.ActivityReferEarnBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.response.HelpResponse;
import com.cashback.models.viewmodel.HelpViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShareFragment extends BaseFragment implements View.OnClickListener{

    public ShareFragment() {

    }

    ActivityReferEarnBinding moBinding;
    SharedPreferenceManager moSharedPreferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = ActivityReferEarnBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeContent();
    }

    private void initializeContent() {
        moSharedPreferenceManager = new SharedPreferenceManager(getContext());
        moBinding.tvWhatsapp.setOnClickListener(this);
        moBinding.tvMessenger.setOnClickListener(this);
        moBinding.tvSMS.setOnClickListener(this);
        moBinding.tvEmail.setOnClickListener(this);

        String[] loURL = moSharedPreferenceManager.getShareBannerUrl();
        if (loURL != null) {
            setImageSlider(loURL);
        }
    }

    private String getMessage() {
        if (!getPreferenceManager().getReferralLink().isEmpty()) {
            String lsMessage = Common.getDynamicText(getContext(), "share_message");
            if (getPreferenceManager().getReferralCode() != null)
                lsMessage = lsMessage.replace("CCCCC", getPreferenceManager().getReferralCode());
            lsMessage = lsMessage.replace("XXXXXX", getPreferenceManager().getReferralLink());
            return lsMessage;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvWhatsapp:
                Common.openWhatsapp(getContext(), getMessage());
                break;
            case R.id.tvMessenger:
                Common.openMessenger(getContext(), getMessage());
                break;
            case R.id.tvSMS:
                Common.openSMS(getContext(), getMessage());
                break;
            case R.id.tvEmail:
                Common.openEmail(getContext(), getMessage());
                break;
        }
    }

    private void setImageSlider(String[] foUrls) {
        moBinding.imageSlider.setSliderAdapter(new ShareBannerAdapter(getContext(), foUrls));
    }
}
