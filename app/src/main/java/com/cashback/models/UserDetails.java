package com.cashback.models;

import android.content.Context;

import com.cashback.R;
import com.cashback.utils.Common;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class UserDetails {

    @SerializedName("fsUserName")
    String userName;

    @SerializedName("fsUserEmail")
    String email;

    @SerializedName("fiAge")
    int age;

    @SerializedName("fdDob")
    String birthDate;

    @SerializedName("fsGender")
    String gender;

    @SerializedName("fiEWalletId")
    int eWalletId;

    @SerializedName("fsShareCode")
    String referralCode;

    @SerializedName("fsShareFirebaseUrl")
    String referralUrl;

    public UserDetails(int age, String gender, int eWalletId) {
        this.age = age;
        this.gender = gender;
        this.eWalletId = eWalletId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getEWalletId() {
        return eWalletId;
    }

    public void setEWalletId(int eWalletId) {
        this.eWalletId = eWalletId;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferralUrl() {
        return referralUrl;
    }

    public void setReferralUrl(String referralUrl) {
        this.referralUrl = referralUrl;
    }

}
