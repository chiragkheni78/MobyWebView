package com.cashback.activities;

import android.os.Bundle;
import android.view.View;

import com.cashback.databinding.ActivityDashboardBinding;
import com.cashback.databinding.ActivityMyCouponsBinding;

public class MyCouponsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MyCouponsActivity.class.getSimpleName();
    ActivityMyCouponsBinding moBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMyCouponsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }
}
