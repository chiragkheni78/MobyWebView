package com.cashback.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.AdvertisementActivity;
import com.cashback.activities.HomeActivity;
import com.cashback.activities.PhoneLoginActivity;
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

import static android.app.Activity.RESULT_OK;
import static com.cashback.fragments.MapViewFragment.REQUEST_PHONE_LOGIN;
import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

public class ShareFragment extends BaseFragment implements View.OnClickListener {

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

//        String[] loURL = moSharedPreferenceManager.getShareBannerUrl();
//        if (loURL != null) {
            setImageSlider();
//        }
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
        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin();
            return;
        }

        switch (v.getId()) {
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

    private void setImageSlider() {
        moBinding.imageSlider.setSliderAdapter(new ShareBannerAdapter(getContext(), AppGlobal.moSharePageImages, new AdvertAdapter.OnItemClick() {
            @Override
            public void onItemClick(Advertisement advertisement) {
                Intent loIntent = new Intent(getActivity(), HomeActivity.class);
                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                loIntent.putExtra(Constants.IntentKey.OFFER_ID, advertisement.getAdID());
                loIntent.putExtra(Constants.IntentKey.CATEGORY_ID, advertisement.getCategoryID());
                loIntent.putExtra(Constants.IntentKey.BANNER_ID, advertisement.getBannerID());
                startActivity(loIntent);
                getActivity().finish();
            }
        }));
    }

    private void openPhoneLogin() {
        Intent loIntent = new Intent(getContext(), PhoneLoginActivity.class);
        loIntent.putExtra(SCREEN_TITLE, this.getResources().getString(R.string.msg_verify_phone_number));
        startActivityForResult(loIntent, REQUEST_PHONE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHONE_LOGIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), "Phone Verified", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
