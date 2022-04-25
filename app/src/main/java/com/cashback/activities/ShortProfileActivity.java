package com.cashback.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.EWalletAdapter;
import com.cashback.databinding.ActivityShortProfileBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.EWallet;
import com.cashback.models.UserDetails;
import com.cashback.models.response.GetMiniProfileResponse;
import com.cashback.models.response.SaveMiniProfileResponse;
import com.cashback.models.response.SawOurAdOn;
import com.cashback.models.viewmodel.MiniProfileViewModel;
import com.cashback.models.viewmodel.ReferralTrackViewModel;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortProfileActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ShortProfileActivity.class.getSimpleName();
    ActivityShortProfileBinding moBinding;
    ProgressDialog loProgressDialog;
    EWalletAdapter loEWalletAdapter;

    MiniProfileViewModel moMiniProfileViewModel;
    ReferralTrackViewModel moReferralTrackViewModel;
    private Animation moAnim;

    private long mlOfferID = 0, miBannerID = 0;
    private int miCategoryId = 0;
    private ArrayList<EWallet> moWalletList;
    private ArrayList<SawOurAdOn> moShowOurAdsList;
    ArrayList<String> mStrAgeList = new ArrayList<>();
    private int CREDENTIAL_PICKER_REQUEST = 1111;
    public static final int REQUEST_PHONE_PERMISSIONS = 2222;

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
        moBinding.rgAd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                stopAnim();
            }
        });

    }

    public void blinkAnimation(View foView) {
        Animation moAnim = new AlphaAnimation(1, 0.5f);
        moAnim.setDuration(1000); //You can manage the blinking time with this parameter
        moAnim.setStartOffset(20);
        moAnim.setRepeatMode(Animation.REVERSE);
        moAnim.setRepeatCount(Animation.INFINITE);
        foView.startAnimation(moAnim);
    }

    public void stopAnim() {
        /*if (moAnim != null) {
            moAnim.cancel();
        }*/
        int count = moBinding.rgAd.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = moBinding.rgAd.getChildAt(i);
            if (o instanceof RadioButton) {
                o.clearAnimation();
            }

        }
    }

    private void phoneSelection() {
        //To retrieve the Phone Number hints, first, configure the hint selector dialog by creating a HintRequest object.
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(ShortProfileActivity.this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();

        //Then, pass the HintRequest object to credentialsClient.getHintPickerIntent()
        // to get an intent to prompt the user to choose a phone number.
        CredentialsClient credentialsClient = Credentials.getClient(this, options);
        // PendingIntent intent = credentialsClient.getHintPickerIntent(hintRequest);
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(
                    intent.getIntentSender(),
                    CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, new Bundle()
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            // get data from the dialog which is of type Credential
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            moBinding.etPhoneNumber.setText(credential.getId().replaceAll("[\\D]", "").
                    replaceFirst("91", ""));
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        int readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int phoneNumber = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (phoneNumber != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_NUMBERS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_PHONE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                Map<String, Integer> perm = new HashMap<>();
                perm.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.READ_PHONE_NUMBERS, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perm.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perm.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                            perm.get(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                        phoneSelection();
                    } else {
                        Log.i(TAG, "Some permissions are not granted ask again ");
                        Toast.makeText(ShortProfileActivity.this, "Go to app settings and enable permissions", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
        }
    }

    private void initViewModel() {
        moMiniProfileViewModel = new ViewModelProvider(this).get(MiniProfileViewModel.class);
        moMiniProfileViewModel.saveMiniProfileStatus.observe(this, saveProfileObserver);
        moMiniProfileViewModel.getMiniProfileStatus.observe(this, getProfileObserver);

        moReferralTrackViewModel = new ViewModelProvider(this).get(ReferralTrackViewModel.class);
        moReferralTrackViewModel.retrieveFirebaseDeepLink(this, getIntent());
        moReferralTrackViewModel.checkInstallReferrer(getContext());
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
                    if (checkAndRequestPermissions()) {
                        phoneSelection();
                    }

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
        for (int i = 0; i < moShowOurAdsList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            if (i != 0) {
                lParams.leftMargin = (int) getResources().getDimension(R.dimen.padding_mini);
            }

            radioButton.setLayoutParams(lParams);
            radioButton.setId(i);
            radioButton.setText(moShowOurAdsList.get(i).getPromoName());
            radioButton.setButtonDrawable(null);
            radioButton.setBackground(getResources().getDrawable(R.drawable.bg_radio_gender_short_profile));
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setPadding(0, 0, 0, 0);
            radioButton.setTextColor(getResources().getColorStateList(R.color.color_radio_txt));
            moBinding.rgAd.addView(radioButton);
            blinkAnimation(radioButton);
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

          /*  int count = moBinding.rgAd.getChildCount();
            for (int i = 0; i < count; i++) {
                if (i == 0) {
                    View o = moBinding.rgAd.getChildAt(i);
                    if (o instanceof RadioButton) {
                        blinkAnimation(o);
                    }
                }
            }*/
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}