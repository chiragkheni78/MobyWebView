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

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.MessageListAdapter;
import com.cashback.databinding.ActivityMessageBinding;
import com.cashback.models.Message;
import com.cashback.models.response.MessageListResponse;
import com.cashback.models.viewmodel.MessagesViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.FirebaseEvents;

import java.util.ArrayList;

public class MessageActivity extends BaseActivity {

    private static final String TAG = MessageActivity.class.getSimpleName();

    ActivityMessageBinding moBinding;
    MessagesViewModel moMessagesViewModel;

    MessageListAdapter moMessageListAdapter;
    ArrayList<Message> moMessageList;

    Observer<MessageListResponse> fetchMessageObserver = new Observer<MessageListResponse>() {
        @Override
        public void onChanged(MessageListResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getMessageList() != null) {
                    moMessageList = loJsonObject.getMessageList();
                    LinearLayoutManager moLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    moBinding.rvMessages.setLayoutManager(moLayoutManager);

                    moMessageListAdapter = new MessageListAdapter(getContext(), moMessageList);
                    moBinding.rvMessages.setAdapter(moMessageListAdapter);
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                getMessageDetails();

            }
        }
    }

    private void initializeContent() {
        moMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        moMessagesViewModel.fetchMessageStatus.observe(this, fetchMessageObserver);
        setToolbar();
        getMessageDetails();
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
        loTvToolbarTitle.setText(getString(R.string.message));
    }

    private void getMessageDetails() {
        showProgressDialog();
        moMessagesViewModel.fetchMessageList(getContext());
    }

    public void openDetailsScreen(String title, String desc, String id) {
        Intent intent = new Intent(moContext, MessageDetailActivity.class);
        intent.putExtra("MessageTitle", title);
        intent.putExtra("MessageDesc", desc);
        intent.putExtra("MessageId", id);
        startActivityForResult(intent, 10);
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", AppGlobal.getPhoneNumber());
        FirebaseEvents.FirebaseEvent(MessageActivity.this, bundle, FirebaseEvents.MESSAGE_TRACKING);
        super.onBackPressed();
    }
}