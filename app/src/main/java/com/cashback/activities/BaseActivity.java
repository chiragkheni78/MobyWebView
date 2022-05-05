package com.cashback.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    protected Context getContext() {
        return moContext;
    }

    protected View getContentView(ViewBinding binding) {
        this.binding = binding;
        View view = binding.getRoot();
        return view;
    }

    protected SharedPreferenceManager getPreferenceManager() {
        return moSharedPreferenceManager;
    }

    protected void showProgressDialog() {
        loProgressDialog = Common.showProgressDialog(BaseActivity.this);
    }

    protected void dismissProgressDialog() {
        if (!isFinishing())
            Common.dismissProgressDialog(loProgressDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }


        /*if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }*/
        return super.dispatchTouchEvent(ev);
    }
}
