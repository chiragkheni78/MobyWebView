package com.cashback.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.cashback.utils.Common;
import com.cashback.utils.SharedPreferenceManager;

public class BaseActivity extends AppCompatActivity {

    private SharedPreferenceManager moSharedPreferenceManager;

    ViewBinding binding;
    ProgressDialog loProgressDialog;

    Context moContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moSharedPreferenceManager = new SharedPreferenceManager(this);
        moContext = this;
    }

    protected Context getContext(){
        return moContext;
    }

    protected View getContentView(ViewBinding binding){
        this.binding = binding;
        View view = binding.getRoot();
        return view;
    }

    protected SharedPreferenceManager getPreferenceManager(){
        return  moSharedPreferenceManager;
    }

    protected void showProgressDialog(){
        loProgressDialog = Common.showProgressDialog(this);
    }

    protected void dismissProgressDialog(){
        Common.dismissProgressDialog(loProgressDialog);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
