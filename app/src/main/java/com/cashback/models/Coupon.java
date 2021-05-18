package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Coupon {

    @SerializedName("fsCouponName")
    String couponName;

    @SerializedName("fsCouponLink")
    String couponLink;

    @SerializedName("fsCouponDescription")
    String details;

    public String getCouponName() {
        return couponName;
    }

    public String getCouponLink() {
        return couponLink;
    }

    public String getDetails() {
        return details;
    }
}
