package com.cashback.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;

import com.cashback.adapters.EWalletAdapter;
import com.cashback.databinding.ActivityShortProfileBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.response.SawOurAdOn;
import com.cashback.models.viewmodel.MiniProfileViewModel;
import com.cashback.models.UserDetails;
import com.cashback.models.response.GetMiniProfileResponse;
import com.cashback.models.response.SaveMiniProfileResponse;
import com.cashback.models.viewmodel.ReferralTrackViewModel;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;


import java.util.ArrayList;

public class ShortProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ShortProfileActivity.class.getSimpleName();
    ActivityShortProfileBinding moBinding;
    ProgressDialog loProgressDialog;
    EWalletAdapter loEWalletAdapter;

    MiniProfileViewModel moMiniProfileViewModel;
    ReferralTrackViewModel moReferralTrackViewModel;

    private long mlOfferID = 0, miBannerID = 0;
    private int miCategoryId = 0;
    private ArrayList<EWallet> moWalletList;
    private ArrayList<SawOurAdOn> moShowOurAdsList;

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
        moBinding.checkedMobile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                moBinding.etPaytmNumber.requestFocus();
                moBinding.llPaytmNo.setVisibility(View.VISIBLE);
            } else {
                moBinding.llPaytmNo.setVisibility(View.GONE);
            }
        });

        // moBinding.etPaytmNumber.requestFocus();
        moBinding.spinWallet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (moWalletList != null && moWalletList.size() > 0) {

                    moBinding.llUPI.setVisibility(View.GONE);
                    moBinding.llPaytmCheck.setVisibility(View.GONE);
                    moBinding.llPaytmNo.setVisibility(View.GONE);
                    if (moWalletList.get(position).getWalletId() == 1) {
                        moBinding.llPaytmCheck.setVisibility(View.VISIBLE);
                        if (!moBinding.checkedMobile.isChecked()) {
                            moBinding.llPaytmNo.setVisibility(View.VISIBLE);
                            moBinding.etPaytmNumber.requestFocus();
                        }
                    } else if (moWalletList.get(position).getWalletId() == 2) {
                        moBinding.lblUPI.setText(getString(R.string.hint_upi_address));
                        moBinding.etUpiID.setHint(getString(R.string.enter_upi_id));
                        moBinding.llUPI.setVisibility(View.VISIBLE);
                    }
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

        moReferralTrackViewModel = new ViewModelProvider(this).get(ReferralTrackViewModel.class);
        moReferralTrackViewModel.retrieveFirebaseDeepLink(this, getIntent());
        moReferralTrackViewModel.checkInstallReferrer(getContext());
    }

    ArrayList<String> mStrAgeList = new ArrayList<>();

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

                    if (TextUtils.isEmpty(AppGlobal.getPreferenceManager().getAppDownloadCampaign())) {
                        if (loJsonObject.getSawOurAdOn() != null && loJsonObject.getSawOurAdOn().size() > 0) {
                            moShowOurAdsList = loJsonObject.getSawOurAdOn();
                            addAdsLayout();
                        }
                    }

                    setAgeArray();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ShortProfileActivity.this,
                            R.layout.row_age, R.id.tvRowTitle, mStrAgeList);
                    moBinding.spinAge.setAdapter(adapter);
                    moBinding.spinAge.setSelection(15);

                    Advertisement loAdvertisement = null;
                    ArrayList<Advertisement> loAdvertisementList = loJsonObject.getAdvertisementList();
                    if (loAdvertisementList != null && loAdvertisementList.size() > 0) {
                        int liPosition = moMiniProfileViewModel.getAdvertPosition(getContext(), loAdvertisementList);
                        loAdvertisement = loAdvertisementList.get(liPosition);

                        String lsURL = loAdvertisement.getImageUrl();
                        if (lsURL != null) {
                            Common.loadImage(moBinding.ivBanner, lsURL, null, null);
                            moBinding.ivBanner.setVisibility(View.VISIBLE);
                            AppGlobal.isDisplayRewardNote = true;
                            miCategoryId = loAdvertisement.getCategoryID();
                            mlOfferID = loAdvertisement.getAdID();
                            miBannerID = loAdvertisement.getBannerID();

                            moBinding.ivBanner.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    moBinding.btnSaveProfile.performClick();

                                    FirebaseEvents.trigger(ShortProfileActivity.this,
                                            null, FirebaseEvents.PICTURE_PRESS);

                                }
                            });
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

    private void addAdsLayout() {
        moBinding.rgAd.removeAllViews();
        moBinding.llAge.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen._35sdp));
        lParams.weight = 1;
        // lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        // lParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        for (int i = 0; i < moShowOurAdsList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            if (i != 0) {
                lParams.leftMargin = (int) getResources().getDimension(R.dimen.padding_mini);
            } else {
                //  radioButton.setChecked(true);
            }
            radioButton.setLayoutParams(lParams);
            radioButton.setId(i);
            radioButton.setText(moShowOurAdsList.get(i).getPromoName());
            radioButton.setButtonDrawable(null);
            radioButton.setBackground(getResources().getDrawable(R.drawable.bg_radio_gender_short_profile));
            //radioButton.setGravity(View.TEXT_ALIGNMENT_CENTER);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setPadding(0, 0, 0, 0);
            radioButton.setTextColor(getResources().getColorStateList(R.color.color_radio_txt));
            moBinding.rgAd.addView(radioButton);
        }
    }

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

                FirebaseEvents.trigger(ShortProfileActivity.this, null, FirebaseEvents.SAVE_PROFILE);

                AppGlobal.isNewUser = true;
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

    public void setAgeArray() {
        for (int i = 15; i <= 99; i++) {
            mStrAgeList.add(i + " Yr");
        }
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
        Common.hideKeyboard(this);
        //int age = getAge();
        String lsGender = getGender();
        String lsSawOurAds = getSawOurAds();

        if (TextUtils.isEmpty(lsSawOurAds) && moShowOurAdsList != null && moShowOurAdsList.size() > 0) {
            Toast.makeText(ShortProfileActivity.this, getResources().getString(R.string.valid_ads_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        int eWalletId;
        try {
            eWalletId = ((EWallet) moBinding.spinWallet.getSelectedItem()).getWalletId();
        } catch (NullPointerException e) {
            eWalletId = 1;
        }
        String age = (String) moBinding.spinAge.getSelectedItem();
        age = age.replace("Yr", "").trim();

        String lsUPIAddress = moBinding.etUpiID.getText().toString();
        String lsPaytmNumber = moBinding.etPaytmNumber.getText().toString();
        String lsPhoneNumber = moBinding.etPhoneNumber.getText().toString();

        loProgressDialog = Common.showProgressDialog(ShortProfileActivity.this);
        if (TextUtils.isEmpty(lsSawOurAds)) {
            lsSawOurAds = AppGlobal.getPreferenceManager().getAppDownloadCampaign();
        }
        moMiniProfileViewModel.saveProfile(this, Integer.parseInt(age), lsGender, eWalletId,
                lsUPIAddress, lsPaytmNumber, lsPhoneNumber, moBinding.checkedMobile.isChecked(), lsSawOurAds);
    }

    private void blinkText(View view) {
        AlphaAnimation moAnimBlink = new AlphaAnimation(0.0f, 1.0f);
        moAnimBlink.setDuration(700); //You can manage the blinking time with this parameter
        moAnimBlink.setStartOffset(20);
        moAnimBlink.setRepeatMode(Animation.REVERSE);
        moAnimBlink.setRepeatCount(Animation.INFINITE);
        view.startAnimation(moAnimBlink);
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

    private String getSawOurAds() {
        int radioButtonID = moBinding.rgAd.getCheckedRadioButtonId();
        View radioButton = moBinding.rgAd.findViewById(radioButtonID);
        int idx = moBinding.rgAd.indexOfChild(radioButton);

        String ads = null;
        if (idx > -1) {
            ads = moShowOurAdsList.get(idx).getPromoCode();
        }
        return ads;
    }
}