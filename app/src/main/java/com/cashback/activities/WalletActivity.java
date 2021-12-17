package com.cashback.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.cashback.adapters.TransactionListAdapter;
import com.cashback.databinding.ActivityWalletBinding;
import com.cashback.dialog.MessageDialog;
import com.cashback.models.Transaction;
import com.cashback.models.response.TransactionListResponse;
import com.cashback.models.viewmodel.WalletViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = WalletActivity.class.getSimpleName();
    ActivityWalletBinding moBinding;
    WalletViewModel moWalletViewModel;

    TransactionListAdapter moTransactionListAdapter;
    ArrayList<Transaction> moTransactionList = new ArrayList<>();
    private LinearLayoutManager moLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        initViewModel();
        setToolbar();
        moBinding.btnShareApp.setOnClickListener(this);
        moBinding.btnShopNow.setOnClickListener(this);
        moBinding.tvTimelineCoupon.setOnClickListener(this);
        moBinding.imageInfo.setOnClickListener(this);
        moLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        moBinding.rvTransactionList.setLayoutManager(moLayoutManager);
        moTransactionListAdapter = new TransactionListAdapter(getContext(), moTransactionList);
        moBinding.rvTransactionList.setAdapter(moTransactionListAdapter);

        getTransactionList();
    }

    private void initViewModel() {
        moWalletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        moWalletViewModel.fetchTransactionStatus.observe(WalletActivity.this, fetchTransactionObserver);
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
        loTvToolbarTitle.setText(getString(R.string.moby_wallet));
    }


    private void getTransactionList() {
        showProgressDialog();
        moWalletViewModel.fetchTransactionList(getContext());
    }

    Observer<TransactionListResponse> fetchTransactionObserver = new Observer<TransactionListResponse>() {
        @Override
        public void onChanged(TransactionListResponse loJsonObject) {
            dismissProgressDialog();
            if (!loJsonObject.isError()) {
                if (loJsonObject.getTransactionList() != null) {
                    setViewData(loJsonObject);
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
        }
    };

    private void setViewData(TransactionListResponse foJsonObject) {
        moTransactionList = foJsonObject.getTransactionList();
        moTransactionListAdapter.notifyList(moTransactionList);

        if (foJsonObject.getTransactionList().size() == 0) {
            moBinding.rlEmptyData.setVisibility(View.VISIBLE);
            moBinding.rlWallet.setVisibility(View.GONE);
        } else {
            moBinding.rlEmptyData.setVisibility(View.GONE);
            moBinding.rlWallet.setVisibility(View.VISIBLE);
        }

        moBinding.tvWallet.setText("Credited In Your " + foJsonObject.getWalletName());
        if (foJsonObject.isActivityCouponExist()) {
            int liStart = getResources().getColor(R.color.white);
            int liEnd = getResources().getColor(R.color.colorAccent);

            ObjectAnimator textColorAnim = ObjectAnimator.ofInt(moBinding.tvTimelineCoupon, "textColor", liEnd, liStart);
            textColorAnim.setDuration(1500);
            textColorAnim.setEvaluator(new ArgbEvaluator());
            textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
            textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
            textColorAnim.start();
        }
        showTotalRewards(foJsonObject);
    }

    private void showTotalRewards(TransactionListResponse foJsonObject) {
        long mlTotalVirtualCash = 0, mlTotalInstantCash = 0;
        if (moTransactionList.size() > 0) {
            for (Transaction loUTrans : moTransactionList) {
                if (loUTrans.isVirtualCash()) {
                    if (loUTrans.getTransactionStatus() == 0 || loUTrans.getTransactionStatus() == -1) {
                        mlTotalVirtualCash = mlTotalVirtualCash + loUTrans.getQuizReward();
                    } else {
                        mlTotalInstantCash = mlTotalInstantCash + loUTrans.getCashbackReward();
                    }
                } else {
                    mlTotalInstantCash = mlTotalInstantCash + loUTrans.getQuizReward();
                }
            }
        }
        moBinding.tvInstantCash.setText("Rs. " + foJsonObject.getTotalCashAmount());
        moBinding.tvVirtualCash.setText("Rs. " + foJsonObject.getTotalVirtualCash());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTimelineCoupon:
                openCouponList();
                break;
            case R.id.btnShareApp:
                Intent intent = new Intent(WalletActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.LOAD_SHARE_PAGE);
                startActivity(intent);
                finish();
                break;
            case R.id.btnShopNow:
                Intent intentHome = new Intent(moContext, HomeActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentHome.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.LOAD_OFFER_PAGE);
                moContext.startActivity(intentHome);
                //finishAffinity();
                break;
            case R.id.imageInfo:
                MessageDialog loDialog = new MessageDialog(this, null, getString(R.string.info_message), getString(R.string.btn_shop_now), false);
                loDialog.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCouponList();
                    }
                });
                loDialog.show();
                break;
            default:
                break;
        }
    }

    private void openCouponList() {
        Intent intent = new Intent(moContext, HomeActivity.class);
        intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.Action.WALLET_SCREEN);
        moContext.startActivity(intent);
        ((Activity) moContext).finishAffinity();
    }
}
