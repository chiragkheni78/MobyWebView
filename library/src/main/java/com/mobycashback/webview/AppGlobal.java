package com.mobycashback.webview;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.mobycashback.model.Category;

import java.util.ArrayList;

public class AppGlobal extends Application {
    public static Context moContext;
    public static ArrayList<Category> moCategories = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        moContext = this;
    }

    public static Context getInstance() {
        return moContext;
    }

    public static ArrayList<Category> getCategories() {
        return moCategories;
    }

    public static void setCategories(ArrayList<Category> loCategories) {
        moCategories = loCategories;
    }

}
