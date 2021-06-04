package com.cashback.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.BankOfferCategoryAdapter;
import com.cashback.adapters.DebitCardAdapter;
import com.cashback.adapters.EWalletAdapter;
import com.cashback.databinding.ActivityMyProfileBinding;
import com.cashback.models.EWallet;
import com.cashback.models.UserDetails;
import com.cashback.models.request.SaveUserProfileRequest;
import com.cashback.models.response.DeleteCardResponse;
import com.cashback.models.response.GetUserProfileResponse;
import com.cashback.models.response.SaveUserProfileResponse;
import com.cashback.models.viewmodel.UserProfileViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import static android.view.View.GONE;
import static com.cashback.fragments.MapViewFragment.REQUEST_PHONE_LOGIN;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    ActivityMyProfileBinding moBinding;

    UserProfileViewModel moUserProfileViewModel;
    GetUserProfileResponse moGetUserProfileResponse;

    EWalletAdapter moEWalletAdapter;
    BankOfferCategoryAdapter moBankOfferCategoryAdapter;
    DebitCardAdapter moDebitCardAdapter;
    private int miDeletePosition;

    private int miPaymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }

    private void initializeContent() {
        Common.hideKeyboard(this);
        initViewModel();
        setToolbar();
        moBinding.btnSaveProfile.setOnClickListener(this);

        if (AppGlobal.getFirebaseUser() == null) {
            errorButtonPressed();
            moBinding.btnError.setOnClickListener(this);
            moBinding.llErrorMessage.setVisibility(View.VISIBLE);
            moBinding.tvErrorTitle.setText(Common.getDynamicText(getContext(), "text_verify_phone_no"));
            moBinding.tvErrorMessage.setText(Common.getDynamicText(getContext(), "msg_verify_phone_number"));
        } else {
            loadView();
        }
    }

    private void loadView() {
        moBinding.llErrorMessage.setVisibility(View.GONE);
        getUserProfile();
    }

    private void getUserProfile() {
        showProgressDialog();
        moUserProfileViewModel.getUserProfile(getContext());
    }

    private void setToolbar() {

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
        loTvToolbarTitle.setText(getString(R.string.my_profile));
    }

    private void initViewModel() {
        moUserProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        moUserProfileViewModel.saveUserProfileStatus.observe(this, saveProfileObserver);
        moUserProfileViewModel.getUserProfileStatus.observe(this, getProfileObserver);
        moUserProfileViewModel.deleteCardStatus.observe(this, deleteCardObserver);
    }

    Observer<GetUserProfileResponse> getProfileObserver = new Observer<GetUserProfileResponse>() {
        @Override
        public void onChanged(GetUserProfileResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                moGetUserProfileResponse = loJsonObject;
                viewData();
            } else {
                Common.showErrorDialog(UserProfileActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    Observer<SaveUserProfileResponse> saveProfileObserver = new Observer<SaveUserProfileResponse>() {
        @Override
        public void onChanged(SaveUserProfileResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Common.showErrorDialog(UserProfileActivity.this, loJsonObject.getMessage(), false);
            }
            Common.dismissProgressDialog(loProgressDialog);
        }
    };

    Observer<DeleteCardResponse> deleteCardObserver = new Observer<DeleteCardResponse>() {
        @Override
        public void onChanged(DeleteCardResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                moGetUserProfileResponse.getDebitCardList().remove(miDeletePosition);
                miDeletePosition = -1;
                moDebitCardAdapter.notifyDataSetChanged();
            } else {
                Common.showErrorDialog(UserProfileActivity.this, loJsonObject.getMessage(), false);
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
            case R.id.btnError:
                errorButtonPressed();
                break;
        }
    }

    private void errorButtonPressed() {
        if (AppGlobal.getFirebaseUser() == null) {
            Intent loIntent = new Intent(getContext(), PhoneLoginActivity.class);
            startActivityForResult(loIntent, REQUEST_PHONE_LOGIN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHONE_LOGIN) {
            if (resultCode == RESULT_OK) {
                loadView();
            }
        }
    }

    private void viewData() {
        if (moGetUserProfileResponse.getUserDetails() != null) {
            UserDetails loUserDetails = moGetUserProfileResponse.getUserDetails();
            moBinding.etFirstName.setText(loUserDetails.getFirstName());
            moBinding.etLastName.setText(loUserDetails.getLastName());
            moBinding.etEmail.setText(loUserDetails.getEmail());
            moBinding.etDob.setText(loUserDetails.getBirthDate());
            moBinding.etPhoneNo.setText(loUserDetails.getMobileNumber().replace("+91", ""));
            setBankOfferRadiusBox(loUserDetails.getBankOfferRadius());
        }

        if (moGetUserProfileResponse.getWalletList() != null) {
            moEWalletAdapter = new EWalletAdapter(UserProfileActivity.this, moGetUserProfileResponse.getWalletList());
            moBinding.spinWallet.setAdapter(moEWalletAdapter);
            moBinding.spinWallet.setSelection(moUserProfileViewModel.getSelectedWalletPosition(moGetUserProfileResponse.getWalletList()));
        }

        if (moGetUserProfileResponse.getBankOfferCategoryList() != null) {
            moBinding.rvBankOfferCategory.setLayoutManager(new GridLayoutManager(UserProfileActivity.this, 2));
            moBankOfferCategoryAdapter = new BankOfferCategoryAdapter(getContext(), moGetUserProfileResponse.getBankOfferCategoryList());
            moBinding.rvBankOfferCategory.setAdapter(moBankOfferCategoryAdapter);
        }

        if (moGetUserProfileResponse.getPaymentDetails() != null) {
            GetUserProfileResponse.PaymentDetails loPaymentDetails = moGetUserProfileResponse.getPaymentDetails();
            if (loPaymentDetails.getPaymentMode() == 1) {
                moBinding.llUPI.setVisibility(GONE);
                miPaymentMode = 1;
                moBinding.rbPaytm.setChecked(true);
            } else if (loPaymentDetails.getPaymentMode() == 2) {
                moBinding.llUPI.setVisibility(View.VISIBLE);
                moBinding.etUpiID.setText(loPaymentDetails.getUpiLink());
                miPaymentMode = 2;
                moBinding.rbUPI.setChecked(true);
            }

            moBinding.rbPaytm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        moBinding.llUPI.setVisibility(GONE);
                        miPaymentMode = 1;
                    } else {
                        moBinding.llUPI.setVisibility(View.VISIBLE);
                        miPaymentMode = 2;
                    }
                }
            });
        }
        setCardList();
    }

    private void setCardList() {

        if (moGetUserProfileResponse.getDebitCardList() != null) {
            moBinding.rvBankCard.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            moBinding.rvBankCard.setLayoutManager(linearLayoutManager);
            moDebitCardAdapter = new DebitCardAdapter(getContext(), moGetUserProfileResponse.getDebitCardList(), new DebitCardAdapter.CancelClick() {

                @Override
                public void onClick(int pos, String bank, String card_type, String card_product, int list_size) {
                    if (list_size <= 1) {
                        Common.showErrorDialog(UserProfileActivity.this, Common.getDynamicText(getContext(), "all_delete_card_validation"), false);

                    } else {
                        //Delete Card API
                        miDeletePosition = pos;
                        int liCardID = moGetUserProfileResponse.getDebitCardList().get(pos).getCardId();
                        deleteCard(liCardID);
                    }
                }
            });
            moBinding.rvBankCard.setAdapter(moDebitCardAdapter);
        }
    }

    private void deleteCard(int fiCardID) {
        showProgressDialog();
        moUserProfileViewModel.deleteUserCard(getContext(), fiCardID);
    }

    private void setBankOfferRadiusBox(int bankOfferRadius) {
        switch (bankOfferRadius) {
            case 250:
                moBinding.rb250m.setChecked(true);
                break;
            case 500:
                moBinding.rb500m.setChecked(true);
                break;
            case 1000:
                moBinding.rb1km.setChecked(true);
                break;
            case 3000:
                moBinding.rb2km.setChecked(true);
                break;
            case 5000:
                moBinding.rb5km.setChecked(true);
                break;
        }
    }

    private void saveShortProfile() {

        String lsFirstName = moBinding.etFirstName.getText().toString();
        String lsLastName = moBinding.etLastName.getText().toString();
        String lsEmail = moBinding.etEmail.getText().toString();
        String lsUPILink = moBinding.etUpiID.getText().toString();
        String lsBirthDate = moGetUserProfileResponse.getUserDetails().getBirthDate();

        int liBankOfferRadius = moUserProfileViewModel.getBankOfferRadius(getRadioGroupSelectedPosition(moBinding.rgRange));

        int liIndex = getRadioGroupSelectedPosition(moBinding.rgGender);
        String lsGender = (liIndex == 1) ? Constants.Gender.MALE.getValue() : Constants.Gender.FEMALE.getValue(); //1-Male & 2-Female

        int eWalletId = ((EWallet) moBinding.spinWallet.getSelectedItem()).getWalletId();

        String lsCategoryIds = moBankOfferCategoryAdapter.getSelectedCategory();

        boolean isTermsAccepted = moBinding.cbTerms.isChecked();

        String lsReferrer = moGetUserProfileResponse.getUserDetails().getReferrer();

        showProgressDialog();

        SaveUserProfileRequest loSaveUserProfileRequest = new SaveUserProfileRequest();
        loSaveUserProfileRequest.setFirstName(lsFirstName);
        loSaveUserProfileRequest.setLastName(lsLastName);
        loSaveUserProfileRequest.setEmail(lsEmail);
        loSaveUserProfileRequest.setGender(lsGender);
        loSaveUserProfileRequest.setBankOfferDistance(liBankOfferRadius);
        loSaveUserProfileRequest.setBankOfferCategories(lsCategoryIds);
        loSaveUserProfileRequest.setReferrer(lsReferrer);
        loSaveUserProfileRequest.setTCAccepted(isTermsAccepted);
        loSaveUserProfileRequest.setPaymentMode(miPaymentMode);
        loSaveUserProfileRequest.setUpiLink(lsUPILink);
        loSaveUserProfileRequest.setBirthDate(lsBirthDate);
        loSaveUserProfileRequest.seteWalletId(eWalletId);
        moUserProfileViewModel.saveUserProfile(this, loSaveUserProfileRequest);
    }

    private int getRadioGroupSelectedPosition(RadioGroup radiogroup) {
        int radioButtonID = radiogroup.getCheckedRadioButtonId();
        View radioButton = radiogroup.findViewById(radioButtonID);
        int liPosition = radiogroup.indexOfChild(radioButton);
        return liPosition;
    }
}