package com.mobycashback.webview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mobycashback.webview.databinding.ActivityCategoriesBinding;
import com.mobycashback.webview.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private static final String TEST_PAGE_URL = "https://app.mobyads.in/publisher/A01234567/?fsMobile=918140663133&fsEmail=johndeo@gmail.com&fsFirstName=Chirag&fsLastName=Kheni&fiDeviceType=0";
    //  private static final String TEST_PAGE_URL = "https://mobyads.in/webview.html";
    ActivityMainBinding moBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = moBinding.getRoot();
        setContentView(view);
        moBinding.btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobyWebViewBuilder builder = new MobyWebViewBuilder().
                        setAccessStorage(true).
                        setAccessGPS(true).
                        setUrl(TEST_PAGE_URL).
                        setCategories(true).
                        setPrimaryColor("#E91E63").
                        setSecondaryColor("#FFFFFF").
                        setPrimaryTextColor("#FFFFFF").
                        setSecondaryTextColor("#FFFFFF").
                        build();
                builder.loadWebView();
            }
        });
       moBinding.btnWebview.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               MobyWebViewBuilder builder = new MobyWebViewBuilder().
                       setAccessStorage(true).
                       setAccessGPS(true).
                       setUrl(TEST_PAGE_URL).
                       setCategories(false).
                       setPrimaryColor("#FF2401").
                       setSecondaryColor("#FFFFFF").
                       setPrimaryTextColor("#FFFFFF").
                       setSecondaryTextColor("#FFFFFF").
                       build();
               builder.loadWebView();
           }
       });
    }
}