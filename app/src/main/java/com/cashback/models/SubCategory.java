package com.cashback.models;

public class SubCategory {
    // @SerializedName("fiIndex")
    String category;
    String price;

    public SubCategory(String category, String price) {
        this.category = category;
        this.price = price;
    }

    public SubCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
