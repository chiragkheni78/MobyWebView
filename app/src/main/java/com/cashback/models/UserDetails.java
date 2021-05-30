package com.cashback.models;

import android.content.Context;

import com.cashback.R;
import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class UserDetails {

    @SerializedName("fsFirstName")
    String firstName;

    @SerializedName("fsLastName")
    String lastName;

    @SerializedName("fsUserEmail")
    String email;

    @SerializedName("fsContactNo")
    String mobileNumber;

    @SerializedName("fiAge")
    int age;

    @SerializedName("fdDob")
    String birthDate;

    @SerializedName("fsGender")
    String gender;

    @SerializedName("fiEWalletId")
    int eWalletId;

    @SerializedName("fsReferralCode")
    String referralCode;

    @SerializedName("fsReferralLink")
    String referralUrl;

    @SerializedName("fsReferrerCode")
    String referrer;

    @SerializedName("fiBankOfferRadius")
    int bankOfferRadius;

    public UserDetails(int age, String gender, int eWalletId) {
        this.age = age;
        this.gender = gender;
        this.eWalletId = eWalletId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public int getAge() {
        return age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public int geteWalletId() {
        return eWalletId;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getReferralUrl() {
        return referralUrl;
    }

    public String getReferrer() {
        return referrer;
    }

    public int getBankOfferRadius() {
        return bankOfferRadius;
    }
}

