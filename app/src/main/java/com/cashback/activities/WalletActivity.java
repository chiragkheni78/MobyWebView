package com.cashback.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.adapters.ExpiryHistoryAdapter;
import com.cashback.adapters.TransactionListAdapter;
import com.cashback.databinding.ActivityWalletBinding;
import com.cashback.dialog.MessageDialog;
import com.cashback.models.Transaction;
import com.cashback.models.response.MyCashHistory;
import com.cashback.models.response.TransactionListResponse;
import com.cashback.models.viewmodel.WalletViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.rey.material.widget.Button;

import java.util.ArrayList;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = WalletActivity.class.getSimpleName();
    ActivityWalletBinding moBinding;
    WalletViewModel moWalletViewModel;

    TransactionListAdapter moTransactionListAdapter;
    ExpiryHistoryAdapter moExpiryHistoryAdapter;
    ArrayList<Transaction> moTransactionList = new ArrayList<>();
    ArrayList<MyCashHistory> moMyCashHistory = new ArrayList<>();
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
        moBinding.tvTimelineCoupon.setOnClickListener(this);
        moBinding.imageInfo.setOnClickListener(this);
        moLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        moBinding.rvTransactionList.setLayoutManager(moLayoutManager);
        moTransactionListAdapter = new TransactionListAdapter(getContext(), moTransactionList);
        moBinding.rvTransactionList.setAdapter(moTransactionListAdapter);

        moExpiryHistoryAdapter = new ExpiryHistoryAdapter(getContext(), moMyCashHistory);
        moBinding.rvRowExpiryHistory.setAdapter(moExpiryHistoryAdapter);
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


    private void launchWalletDialog() {
        Dialog b = new Dialog(this, R.style.Theme_Dialog);
        LayoutInflater inflater = this.getLayoutInflater();
        b.requestWindowFeature(Window.FEATURE_NO_TITLE);
        b.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.getWindow().setGravity(Gravity.CENTER);
        final View dialogView = inflater.inflate(R.layout.dialog_wallet_empty, null);
        b.setContentView(dialogView);
        b.setCancelable(false);

        final Button btnShareApp = dialogView.findViewById(R.id.btnShareApp);
        final Button btnShopNow = dialogView.findViewById(R.id.btnShopNow);

        btnShopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(moContext, HomeActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentHome.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.LOAD_OFFER_PAGE);
                moContext.startActivity(intentHome);
            }
        });
        btnShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.LOAD_SHARE_PAGE);
                startActivity(intent);
                finish();
            }
        });
        b.show();
    }


    private void setViewData(TransactionListResponse foJsonObject) {
        moTransactionList = foJsonObject.getTransactionList();
        moTransactionListAdapter.notifyList(moTransactionList);

        if (foJsonObject.getTransactionList().size() == 0) {
            launchWalletDialog();
            moBinding.rlWallet.setVisibility(View.GONE);
        } else {
            if (!getPreferenceManager().getVirtualCashClicked())
                Common.blinkAnimation(moBinding.imageInfo);
            moBinding.rlWallet.setVisibility(View.VISIBLE);
        }

        moBinding.tvWallet.setText("(Credited In Your " + foJsonObject.getWalletName() + ")");
        //if (foJsonObject.isActivityCouponExist()) {
        //int liStart = getResources().getColor(R.color.white);
        //  int liEnd = getResources().getColor(R.color.colorAccent);

            /*ObjectAnimator textColorAnim = ObjectAnimator.ofInt(moBinding.tvTimelineCoupon, "textColor", liEnd, liStart);
            textColorAnim.setDuration(1500);
            textColorAnim.setEvaluator(new ArgbEvaluator());
            textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
            textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
            textColorAnim.start();*/
        //}
        showTotalRewards(foJsonObject);
    }

    String moRedeemVal;

    private void showTotalRewards(TransactionListResponse foJsonObject) {
        moRedeemVal = foJsonObject.getMyCash().getAutoRedeemPer();
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
        moBinding.tvTotalCredit.setText("Rs. " + foJsonObject.getFiTotalCredit());
        moBinding.tvMyCash.setText("Rs. " + foJsonObject.getMyCash().getUsedMyCash());
        String text = String.format(getResources().getString(R.string.my_case_redeem),
                String.valueOf(foJsonObject.getMyCash().getAutoRedeemPer()) + "%");
        moBinding.tvTimelineCoupon.setText(text);
        moBinding.lblMyCash.setText(" MyCash (Rs. " + foJsonObject.getMyCash().getTotalMyCash() + ")");

        moMyCashHistory.addAll(foJsonObject.getMyCash().getMyCashHistory());
        moExpiryHistoryAdapter.notifyDataSetChanged();
        if (foJsonObject.getMyCash().getTotalMyCash() > 0) {
            // moBinding.tvAmount.setText("Rs. " + foJsonObject.getMyCash().getMyCashHistory().get(0).getRemainAmount());
            // moBinding.tvExpiry.setText(foJsonObject.getMyCash().getMyCashHistory().get(0).getExpiryDate());
            //  moBinding.tvTitleExp.setText(foJsonObject.getMyCash().getMyCashHistory().get(0).getTitle());

            moBinding.tvMyCashTotal.setText("Rs. " + foJsonObject.getMyCash().getTotalMyCash());
            String textMyOffer = String.format(getResources().getString(R.string.my_case_offer),
                    "Rs. " + String.valueOf(foJsonObject.getMyCash().getTotalMyCash()));
            moBinding.tvTimelineCoupon.setText("(" + text + ")");

            Spannable spannable = new SpannableString(textMyOffer);
            String str = spannable.toString();
            int iStart = str.indexOf("Expiry/History");
            int iEnd = iStart + 14;

            SpannableString ssText = new SpannableString(spannable);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (moBinding.tbExpiryHistory.getVisibility() == View.VISIBLE) {
                        moBinding.tbExpiryHistory.setVisibility(View.GONE);
                        moBinding.rvRowExpiryHistory.setVisibility(View.GONE);
                    } else {
                        moBinding.tbExpiryHistory.setVisibility(View.VISIBLE);
                        moBinding.rvRowExpiryHistory.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(getResources().getColor(R.color.colorPrimary));
                }
            };

            int iStartBold = str.indexOf("Rs.");
            ssText.setSpan(clickableSpan, iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssText.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), iStartBold, iStartBold + String.valueOf(foJsonObject.getMyCash().getTotalMyCash()).length() + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            moBinding.tvMyCashText.setText(ssText);
            moBinding.tvMyCashText.setMovementMethod(LinkMovementMethod.getInstance());
            moBinding.tvMyCashText.setHighlightColor(Color.TRANSPARENT);
            moBinding.tvMyCashText.setEnabled(true);
            moBinding.llHiddenMyCash.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTimelineCoupon:
                openCouponList();
                break;
            case R.id.imageInfo:
                moBinding.imageInfo.clearAnimation();
                getPreferenceManager().setVirtualCashClicked();

                try {
                    String text = String.format(getResources().getString(R.string.info_message),
                            String.valueOf(moRedeemVal) + "%", String.valueOf(moRedeemVal) + "%");
                    text = text.replaceAll("100", "100%");
                    MessageDialog loDialog = new MessageDialog(this, null, text, getString(R.string.btn_shop_now), false);
                    loDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openCouponList();
                        }
                    });
                    loDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
