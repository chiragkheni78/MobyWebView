package com.cashback.activities;


import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.adapters.MessageListAdapter;
import com.cashback.databinding.ActivityMessageBinding;
import com.cashback.models.Message;
import com.cashback.models.MessagesViewModel;
import com.cashback.models.response.MessageListResponse;
import com.cashback.utils.Common;

import java.util.ArrayList;

public class MessageActivity extends BaseActivity {

    private static final String TAG = MessageActivity.class.getSimpleName();
    ActivityMessageBinding moBinding;
    MessagesViewModel moMessagesViewModel;

    MessageListAdapter moMessageListAdapter;
    ArrayList<Message> moMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        moMessagesViewModel.fetchMessageStatus.observe(this, fetchMessageObserver);

        getMessageDetails();
    }

    private void getMessageDetails() {
        showProgressDialog();
        moMessagesViewModel.fetchMessageList(getContext());
    }

    Observer<MessageListResponse> fetchMessageObserver = new Observer<MessageListResponse>() {
        @Override
        public void onChanged(MessageListResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getMessageList() != null) {
                    moMessageList = loJsonObject.getMessageList();
                    moMessageListAdapter = new MessageListAdapter(getContext(), moMessageList);
                    moBinding.rvMessages.setAdapter(moMessageListAdapter);
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

}
