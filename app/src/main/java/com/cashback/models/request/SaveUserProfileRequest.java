package com.cashback.models.request;

import android.content.Context;

import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

public class SaveUserProfileRequest {

    @SerializedName("fsAction")
    String action;

    @SerializedName("fsDeviceId")
    String deviceId;

    @SerializedName("fsUserContact")
    private String mobileNumber;

    @SerializedName("fsFirstName")
    String firstName;

    @SerializedName("fsLastName")
    String lastName;

    @SerializedName("fsEmail")
    String email;

    @SerializedName("fiSelectedNGOId")
    int selectedNgoId;

    @SerializedName("fiEWalletId")
    int eWalletId;

    @SerializedName("fiBankOfferDistance")
    int bankOfferDistance;

    @SerializedName("fsBankOfferCategories")
    String bankOfferCategories;

    @SerializedName("fiPaymentMode")
    int paymentMode;

    @SerializedName("fsUPILink")
    String upiLink;

    @SerializedName("fsAccountNo")
    String fsAccountNo;

    @SerializedName("fsIFSCCode")
    String fsIFSCCode;

    @SerializedName("fbPrivacyChecked")
    boolean isTCAccepted;

    @SerializedName("fdDOB")
    String birthDate;

    @SerializedName("fsGender")
    String gender;

    @SerializedName("fsReferrerCode")
    String referrer;

    public void setAction(String action) {
        this.action = action;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSelectedNgoId(int selectedNgoId) {
        this.selectedNgoId = selectedNgoId;
    }

    public void seteWalletId(int eWalletId) {
        this.eWalletId = eWalletId;
    }

    public void setBankOfferDistance(int bankOfferDistance) {
        this.bankOfferDistance = bankOfferDistance;
    }

    public void setBankOfferCategories(String bankOfferCategories) {
        this.bankOfferCategories = bankOfferCategories;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setUpiLink(String upiLink) {
        this.upiLink = upiLink;
    }

    public void setTCAccepted(boolean TCAccepted) {
        isTCAccepted = TCAccepted;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void setFsAccountNo(String fsAccountNo) {
        this.fsAccountNo = fsAccountNo;
    }

    public void setFsIFSCCode(String fsIFSCCode) {
        this.fsIFSCCode = fsIFSCCode;
    }

    public String validateData(Context foContext) {

        if (birthDate.isEmpty()) {
            return Common.getDynamicText(foContext, "validate_age");
        } else if (eWalletId <= 0) {
            return Common.getDynamicText(foContext, "validate_eWallet");
        } else if (mobileNumber.isEmpty() || gender.isEmpty()) {
            return Common.getDynamicText(foContext, "contact_support");
        } else if (paymentMode == 2 && upiLink.isEmpty()) {
            return Common.getDynamicText(foContext, "UPI_alert");
        } else if (paymentMode == 4 && fsAccountNo.isEmpty()) {
            return Common.getDynamicText(foContext, "account_alert");
        } else if (paymentMode == 4 && fsIFSCCode.isEmpty()) {
            return Common.getDynamicText(foContext, "ifsc_alert");
        } else if (!isTCAccepted) {
            return Common.getDynamicText(foContext, "error_term_condition");
        }
        return null;
    }
}