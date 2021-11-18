package com.cashback.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;

import com.cashback.adapters.AdvertAdapter;
import com.cashback.adapters.EWalletAdapter;
import com.cashback.databinding.ActivityShortProfileBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.viewmodel.MiniProfileViewModel;
import com.cashback.models.UserDetails;
import com.cashback.models.response.GetMiniProfileResponse;
import com.cashback.models.response.SaveMiniProfileResponse;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;


import java.util.ArrayList;

public class ShortProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ShortProfileActivity.class.getSimpleName();
    ActivityShortProfileBinding moBinding;
    ProgressDialog loProgressDialog;
    EWalletAdapter loEWalletAdapter;

    MiniProfileViewModel moMiniProfileViewModel;

    private long mlOfferID = 0, miBannerID = 0;
    private int miCategoryId = 0;
    private ArrayList<EWallet> moWalletList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityShortProfileBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }

    private void initializeContent() {
        Common.hideKeyboard(this);
        initViewModel();
        moBinding.btnSaveProfile.setOnClickListener(this);

        moBinding.spinWallet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (moWalletList != null && moWalletList.size() > 0) {

                    if (moWalletList.get(position).getWalletId() == 2) {
                        moBinding.llUPI.setVisibility(View.VISIBLE);
                    } else moBinding.llUPI.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        moMiniProfileViewModel.getMiniProfile(getContext());
    }

    private void initViewModel() {
        moMiniProfileViewModel = new ViewModelProvider(this).get(MiniProfileViewModel.class);
        moMiniProfileViewModel.saveMiniProfileStatus.observe(this, saveProfileObserver);
        moMiniProfileViewModel.getMiniProfileStatus.observe(this, getProfileObserver);
    }

    Observer<GetMiniProfileResponse> getProfileObserver = new Observer<GetMiniProfileResponse>() {
        @Override
        public void onChanged(GetMiniProfileResponse loJsonObject) {
            if (!loJsonObject.isError()) {

                if (loJsonObject.isUserExist()) {
                    if (loJsonObject.getUserDetails() != null) {
                        UserDetails loUserDetails = loJsonObject.getUserDetails();
                        getPreferenceManager().setUserLogIn(true);
                        getPreferenceManager().setReferralCode(loUserDetails.getReferralCode());
                        getPreferenceManager().setReferralLink(loUserDetails.getReferralUrl());
                        getPreferenceManager().setPhoneNumber(loUserDetails.getMobileNumber());
                    }

                    Intent intent = new Intent(ShortProfileActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (loJsonObject.getWalletList() != null) {
                        moWalletList = loJsonObject.getWalletList();
                        loEWalletAdapter = new EWalletAdapter(ShortProfileActivity.this, loJsonObject.getWalletList());
                        moBinding.spinWallet.setAdapter(loEWalletAdapter);
                        moBinding.spinWallet.setSelection(moMiniProfileViewModel.getSelectedWalletPosition(loJsonObject.getWalletList()));
                    }

                    setImageSlider(loJsonObject.getAdvertisementList());
                    moBinding.llRoot.setVisibility(View.VISIBLE);
                }

            } else {
                Common.showErrorDialog(ShortProfileActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    Observer<SaveMiniProfileResponse> saveProfileObserver = new Observer<SaveMiniProfileResponse>() {
        @Override
        public void onChanged(SaveMiniProfileResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                UserDetails loUserDetails = loJsonObject.getUserDetails();
                if (loUserDetails != null) {
                    getPreferenceManager().setUserLogIn(true);
                    getPreferenceManager().setReferralCode(loUserDetails.getReferralCode());
                    getPreferenceManager().setReferralLink(loUserDetails.getReferralUrl());
                }

                //Adgyde Event - OPEN_REGISTER
                AdGydeEvents.saveProfile(getContext(), getAge(), getGender());
                Intent loIntent = new Intent(ShortProfileActivity.this, HomeActivity.class);
                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                loIntent.putExtra(Constants.IntentKey.OFFER_ID, mlOfferID);
                loIntent.putExtra(Constants.IntentKey.CATEGORY_ID, miCategoryId);
                loIntent.putExtra(Constants.IntentKey.BANNER_ID, miBannerID);
                startActivity(loIntent);
                finish();
            } else {
                Common.showErrorDialog(ShortProfileActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    ArrayList<Advertisement> moAdvertisementList;

    private void setImageSlider(ArrayList<Advertisement> foAdvertisementList) {
        ArrayList<Advertisement> loAdvertisementList = foAdvertisementList;

        if (loAdvertisementList != null && loAdvertisementList.size() > 0) {
            moAdvertisementList = loAdvertisementList;
            moBinding.cvBanner.setVisibility(View.VISIBLE);
            AppGlobal.isDisplayRewardNote = true;
            moBinding.imageSlider.setSliderAdapter(new AdvertAdapter(getContext(), loAdvertisementList, new AdvertAdapter.OnItemClick() {
                @Override
                public void onItemClick(Advertisement advertisement) {
                    moBinding.btnSaveProfile.performClick();
                }
            }));
        } else moBinding.cvBanner.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveProfile:
                saveShortProfile();
                break;
        }
    }

    private void saveShortProfile() {
        setBannerCategory();
        Common.hideKeyboard(this);
        int age = getAge();
        String lsGender = getGender();

        int eWalletId = ((EWallet) moBinding.spinWallet.getSelectedItem()).getWalletId();
        String lsUPIAddress = moBinding.etUpiID.getText().toString();

        loProgressDialog = Common.showProgressDialog(ShortProfileActivity.this);
        moMiniProfileViewModel.saveProfile(this, age, lsGender, eWalletId, lsUPIAddress);
    }

    private void setBannerCategory() {
        if (moAdvertisementList != null) {
            int liPosition = moBinding.imageSlider.getCurrentPagePosition();
            Advertisement loAdvertisement = moAdvertisementList.get(liPosition);
            miCategoryId = loAdvertisement.getCategoryID();
            mlOfferID = loAdvertisement.getAdID();
            miBannerID = loAdvertisement.getBannerID();
        }
    }

    private int getAge() {
        return moBinding.etAge.getText().length() == 0 ? 0 : Integer.parseInt(moBinding.etAge.getText().toString());
    }

    private String getGender() {
        int radioButtonID = moBinding.rgGender.getCheckedRadioButtonId();
        View radioButton = moBinding.rgGender.findViewById(radioButtonID);
        int idx = moBinding.rgGender.indexOfChild(radioButton);

        String gender = null;
        if (idx > -1) {
            gender = (idx == 1) ? Constants.Gender.MALE.getValue() : Constants.Gender.FEMALE.getValue(); //1-Male & 2-Female
        }
        return gender;
    }
}
