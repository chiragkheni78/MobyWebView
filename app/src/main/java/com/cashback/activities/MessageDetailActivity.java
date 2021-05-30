package com.cashback.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.databinding.ActivityMessageDetailBinding;
import com.cashback.models.response.MessageDetailsResponse;
import com.cashback.models.viewmodel.MessagesViewModel;
import com.cashback.utils.Common;

public class MessageDetailActivity extends BaseActivity {
    private static final String TAG = MessageDetailActivity.class.getSimpleName();

    ActivityMessageDetailBinding moBinding;
    MessagesViewModel moMessagesDetailsViewModel;

    String sMessageId, sMessageDesc, sMessageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sMessageTitle = getIntent().getStringExtra("MessageTitle");
        sMessageDesc = getIntent().getStringExtra("MessageDesc");
        sMessageId = getIntent().getStringExtra("MessageId");

        moBinding = ActivityMessageDetailBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initializeContent() {
        moMessagesDetailsViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        moMessagesDetailsViewModel.seenMessageDetailStatus.observe(this, fetchMessageDetaisObserver);
        setToolbar();

        getMessageDetails();
        moBinding.tvTitle.setText(sMessageTitle);
        moBinding.tvDesc.setText(sMessageDesc);
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
        loTvToolbarTitle.setText(getString(R.string.message_details));
    }

    Observer<MessageDetailsResponse> fetchMessageDetaisObserver = new Observer<MessageDetailsResponse>() {
        @Override
        public void onChanged(MessageDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void getMessageDetails() {
        showProgressDialog();
        moMessagesDetailsViewModel.updateMessageSeen(getContext(), sMessageId);
    }
}
