package com.cashback.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.adapters.MessageListAdapter;
import com.cashback.databinding.ActivityMessageBinding;
import com.cashback.databinding.ActivityReferEarnBinding;
import com.cashback.models.Message;
import com.cashback.models.response.MessageListResponse;
import com.cashback.models.viewmodel.MessagesViewModel;
import com.cashback.utils.Common;

import java.util.ArrayList;

public class ShareActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ShareActivity.class.getSimpleName();

    ActivityReferEarnBinding moBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityReferEarnBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }


    private void initializeContent() {
        setToolbar();
        moBinding.tvWhatsapp.setOnClickListener(this);
        moBinding.tvMessenger.setOnClickListener(this);
        moBinding.tvSMS.setOnClickListener(this);
        moBinding.tvEmail.setOnClickListener(this);
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
        loTvToolbarTitle.setText(getString(R.string.refer_earn));
    }

    private String getMessage() {
        if (!getPreferenceManager().getReferralLink().isEmpty()) {
            String lsMessage = Common.getDynamicText(this, "share_message");
            if (getPreferenceManager().getReferralCode() != null)
                lsMessage = lsMessage.replace("CCCCC", getPreferenceManager().getReferralCode());
            lsMessage = lsMessage.replace("XXXXXX", getPreferenceManager().getReferralLink());
            return lsMessage;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvWhatsapp:
                Common.openWhatsapp(getContext(), getMessage());
                break;
            case R.id.tvMessenger:
                Common.openMessenger(getContext(), getMessage());
                break;
            case R.id.tvSMS:
                Common.openSMS(getContext(), getMessage());
                break;
            case R.id.tvEmail:
                Common.openEmail(getContext(), getMessage());
                break;
        }
    }
}