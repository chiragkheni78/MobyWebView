package com.mobycashback.webview;

import com.google.gson.annotations.SerializedName;
import com.mobycashback.model.Category;

import java.util.ArrayList;

public class GetCategoryResponse {
    @SerializedName("fbIsError")
    private boolean isError;

    @SerializedName("fsMessage")
    private String message;

    @SerializedName("foCategoryList")
    private ArrayList<Category> categoryList;

    public String getMessage() {

        return message;
    }
    public GetCategoryResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }
    public boolean isError() {
        return isError;
    }

    public ArrayList<Category>  getCategoryList() {
        return categoryList;
    }
}
