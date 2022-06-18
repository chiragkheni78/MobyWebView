package com.mobycashback.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("fiCategoryId")
    int categoryId;
    @SerializedName("fsCategoryName")
    String categoryName;
    @SerializedName("fsCategoryImage")
    String fsCategoryImage;

    float blurScale = 1;
    boolean isSelected;

    public Category(int categoryId, String categoryName, String fsCategoryImage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.fsCategoryImage = fsCategoryImage;
    }

    public float getBlurScale() {
        return blurScale;
    }

    public void setBlurScale(float blurScale) {
        this.blurScale = blurScale;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public String getFsCategoryImage() {
        return fsCategoryImage;
    }

    public void setFsCategoryImage(String fsCategoryImage) {
        this.fsCategoryImage = fsCategoryImage;
    }
}
