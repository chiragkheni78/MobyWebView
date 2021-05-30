package com.cashback.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.adapters.ImageSliderAdapter;
import com.cashback.databinding.ActivityImageSlidderBinding;
import com.cashback.databinding.ActivityWebviewBinding;
import com.cashback.models.Advertisement;
import com.cashback.models.response.AdvertisementResponse;
import com.cashback.models.response.WebViewDataResponse;
import com.cashback.models.viewmodel.WebViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.custom.CircularViewPagerHandler;

import java.util.ArrayList;

public class WebViewActivity extends BaseActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();
    ActivityWebviewBinding moBinding;
    WebViewModel moWebViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        initializeContent();
    }

    private void initializeContent() {
        Common.hideKeyboard(this);
        initViewModel();
        if (getIntent() != null) {
            String msPageName = getIntent().getStringExtra(Constants.IntentKey.WEBVIEW_PAGE_NAME);
            String lsTitle = getIntent().getStringExtra(Constants.IntentKey.SCREEN_TITLE);

            showProgressDialog();
            moWebViewModel.loadWebViewData(getContext(), msPageName);

            setToolbar(lsTitle);
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

    private void initViewModel() {
        moWebViewModel = new ViewModelProvider(this).get(WebViewModel.class);
        moWebViewModel.loadWebViewDataStatus.observe(this, advertImageObserver);
    }

    Observer<WebViewDataResponse> advertImageObserver = new Observer<WebViewDataResponse>() {
        @Override
        public void onChanged(WebViewDataResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                loadWebViewContent(loJsonObject.getMessage());
            } else {
                Common.showErrorDialog(WebViewActivity.this, loJsonObject.getMessage(), false);
                dismissProgressDialog();
            }
        }
    };

    private void loadWebViewContent(String fsContent) {
        moBinding.webview.getSettings().setJavaScriptEnabled(true);
        moBinding.webview.loadDataWithBaseURL(null, fsContent, "text/html", "utf-8", null);
        dismissProgressDialog();
    }

}
