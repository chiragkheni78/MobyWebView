package com.cashback.models.response;

import com.google.gson.annotations.SerializedName;

public class SawOurAdOn {

    @SerializedName("promo_code")
    String promoCode;

    @SerializedName("promo_name")
    String promoName;

    public String getPromoCode() {
        return promoCode;
    }

    public String getPromoName() {
        return promoName;
    }
}
