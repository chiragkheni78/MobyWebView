package com.cashback.activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.cashback.R;
import com.cashback.databinding.ActivityHelpBinding;
import com.cashback.databinding.ActivityReferEarnBinding;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

public class HelpActivity extends BaseActivity {

    private static final String TAG = HelpActivity.class.getSimpleName();

    ActivityHelpBinding moBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeContent() {
        Common.hideKeyboard(this);
        setToolbar();

        moBinding.webViewHelp.getSettings().setJavaScriptEnabled(true);
        moBinding.webViewHelp.loadUrl("https://mobyads.in/moby/v2-apis/?fsAction=loadWebViewHtml&fsPage=faqs");
    }

    private void setToolbar() {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);

        ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(getString(R.string.help));
    }

}