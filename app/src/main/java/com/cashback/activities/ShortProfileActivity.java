package com.cashback.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;

import com.cashback.adapters.EWalletAdapter;
import com.cashback.databinding.ActivityShortProfileBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.viewmodel.ProfileViewModel;
import com.cashback.models.UserDetails;
import com.cashback.models.response.GetProfileResponse;
import com.cashback.models.response.SaveProfileResponse;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

public class ShortProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ShortProfileActivity.class.getSimpleName();
    ActivityShortProfileBinding moBinding;
    ProgressDialog loProgressDialog;
    EWalletAdapter loEWalletAdapter;

    ProfileViewModel moProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityShortProfileBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }

    private void initializeContent() {
        Common.hideKeyboard(this);
        moProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        moProfileViewModel.saveProfileStatus.observe(this, saveProfileObserver);
        moProfileViewModel.getProfileStatus.observe(this, getProfileObserver);
        moBinding.btnSaveProfile.setOnClickListener(this);

        moProfileViewModel.getProfile(getContext());
    }

    Observer<GetProfileResponse> getProfileObserver = new Observer<GetProfileResponse>() {
        @Override
        public void onChanged(GetProfileResponse loJsonObject) {
            if (!loJsonObject.isError()) {


                if (loJsonObject.isUserExist()) {
                    if (loJsonObject.getUserDetails() != null) {
                        UserDetails loUserDetails = loJsonObject.getUserDetails();
                        getPreferenceManager().setUserLogIn(true);
                        getPreferenceManager().setReferralCode(loUserDetails.getReferralCode());
                        getPreferenceManager().setReferralLink(loUserDetails.getReferralUrl());
                    }

                    Intent intent = new Intent(ShortProfileActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {

                    if (loJsonObject.getWalletList() != null) {
                        loEWalletAdapter = new EWalletAdapter(ShortProfileActivity.this, loJsonObject.getWalletList());
                        moBinding.spinWallet.setAdapter(loEWalletAdapter);
                        moBinding.spinWallet.setSelection(moProfileViewModel.getSelectedWalletPosition(loJsonObject.getWalletList()));
                    }

                    ArrayList<Advertisement> loAdvertisementList = loJsonObject.getAdvertisementList();
                    if (loAdvertisementList != null && loAdvertisementList.size() > 0) {
                        String lsURL = moProfileViewModel.getAdvertImage(getContext(), loAdvertisementList);
                        if (lsURL != null) {
                            Common.loadImage(moBinding.ivBanner, lsURL, null, null);
                            moBinding.ivBanner.setVisibility(View.VISIBLE);
                        }
                    } else moBinding.ivBanner.setVisibility(View.GONE);

                    moBinding.llRoot.setVisibility(View.VISIBLE);
                }

            } else {
                Common.showErrorDialog(ShortProfileActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    Observer<SaveProfileResponse> saveProfileObserver = new Observer<SaveProfileResponse>() {
        @Override
        public void onChanged(SaveProfileResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                UserDetails loUserDetails = loJsonObject.getUserDetails();
                if (loUserDetails != null) {
                    getPreferenceManager().setUserLogIn(true);
                    getPreferenceManager().setReferralCode(loUserDetails.getReferralCode());
                    getPreferenceManager().setReferralLink(loUserDetails.getReferralUrl());
                }

                Intent intent = new Intent(ShortProfileActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Common.showErrorDialog(ShortProfileActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveProfile:
                saveShortProfile();
                break;
        }
    }

    private void saveShortProfile() {
        int age = moBinding.etAge.getText().length() == 0 ? 0 : Integer.parseInt(moBinding.etAge.getText().toString());

        int radioButtonID = moBinding.rgGender.getCheckedRadioButtonId();
        View radioButton = moBinding.rgGender.findViewById(radioButtonID);
        int idx = moBinding.rgGender.indexOfChild(radioButton);

        String gender = (idx == 1) ? Constants.Gender.MALE.getValue() : Constants.Gender.FEMALE.getValue(); //1-Male & 2-Female
        int eWalletId = ((EWallet) moBinding.spinWallet.getSelectedItem()).getWalletId();

        loProgressDialog = Common.showProgressDialog(ShortProfileActivity.this);
        moProfileViewModel.saveProfile(this, age, gender, eWalletId);
    }
}
