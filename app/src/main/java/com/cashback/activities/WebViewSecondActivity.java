package com.cashback.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.databinding.ActivityWebviewBinding;
import com.cashback.models.response.WebViewDataResponse;
import com.cashback.models.viewmodel.WebViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

@SuppressWarnings("All")
public class WebViewSecondActivity extends BaseActivity {

    private static final String TAG = WebViewSecondActivity.class.getSimpleName();
    ActivityWebviewBinding moBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }

    private void initializeContent() {
        Common.hideKeyboard(this);
        if (getIntent() != null) {
            String msPageName = getIntent().getStringExtra(Constants.IntentKey.WEBVIEW_PAGE_NAME);
            String lsTitle = getIntent().getStringExtra(Constants.IntentKey.SCREEN_TITLE);


            setToolbar(lsTitle);

            moBinding.webview.getSettings().setJavaScriptEnabled(true);
            moBinding.webview.loadUrl(msPageName);
        }
    }

    private void setToolbar(String fsTitle) {

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
        loTvToolbarTitle.setText(fsTitle);
    }
}
