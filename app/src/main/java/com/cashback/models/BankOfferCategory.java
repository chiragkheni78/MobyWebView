package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class BankOfferCategory {

    @SerializedName("fiCatId")
    int categoryId;
    @SerializedName("fsCatName")
    String categoryName;
    @SerializedName("fbIsSelected")
    boolean isSelected;

    public BankOfferCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
