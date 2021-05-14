package com.cashback.activities;

import android.os.Bundle;
import android.view.View;

import com.cashback.databinding.ActivityDashboardBinding;

public class DashboardActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    ActivityDashboardBinding moBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
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
