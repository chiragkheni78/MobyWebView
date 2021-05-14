package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("fiCategoryId")
    int categoryId;
    @SerializedName("fsCategoryName")
    String categoryName;

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
