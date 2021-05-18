package com.cashback.activities;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.adapters.AdLocationAdapter;
import com.cashback.adapters.CouponAdapter;
import com.cashback.databinding.ActivityDetailsBinding;
import com.cashback.models.Activity;
import com.cashback.models.AdLocation;
import com.cashback.models.Coupon;
import com.cashback.models.response.ActivityDetailsResponse;
import com.cashback.models.viewmodel.ActivityDetailsViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

public class CouponDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = CouponDetailsActivity.class.getSimpleName();
    ActivityDetailsBinding moBinding;
    ActivityDetailsViewModel moActivityDetailsViewModel;

    private long miActivityId;
    Activity moActivity;


    TextWatcher moTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (moActivity != null) {
                if (moActivity.getCouponType().equalsIgnoreCase("default")) {
                    String number = s.toString().trim();

//                    if (FirebaseAuth.getInstance().getCurrentUser() != null &&
//                            FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null) {
//                        if (String.valueOf("+91" + number).equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
//                            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
//                            moBinding.tvMarkAsUsed.setClickable(true);
//                            moBinding.tvMarkAsUsed.setEnabled(true);
//                            Common.blinkAnimation(moBinding.tvMarkAsUsed);
//                        } else {
//                            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.twhite));
//                            moBinding.tvMarkAsUsed.setClickable(false);
//                            moBinding.tvMarkAsUsed.setEnabled(false);
//                        }
//                    }
                } else {
                    Log.d("TTT", "pass..." + moActivity.getCouponPassword());
                    if (s.toString().equalsIgnoreCase(moActivity.getCouponPassword().trim())) {
                        moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
                        moBinding.tvMarkAsUsed.setClickable(true);
                        moBinding.tvMarkAsUsed.setEnabled(true);
                        Common.blinkAnimation(moBinding.tvMarkAsUsed);
                    } else {
                        moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.twhite));
                        moBinding.tvMarkAsUsed.setClickable(false);
                        moBinding.tvMarkAsUsed.setEnabled(false);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moActivityDetailsViewModel = new ViewModelProvider(this).get(ActivityDetailsViewModel.class);
        moActivityDetailsViewModel.fetchActivityStatus.observe(this, fetchActivityObserver);
        moBinding.etMobileNumber.addTextChangedListener(moTextChangeListener);
        moBinding.tvShopOnline.setOnClickListener(this);
        moBinding.tvMarkAsUsed.setOnClickListener(this);
        moBinding.tvCancel.setOnClickListener(this);

        setToolbar();

        if (getIntent() != null) {
            miActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
            getActivityDetails();
        }
    }

    private void setToolbar() {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);
        loToolbar.setBackgroundColor(Color.TRANSPARENT);

        ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(getString(R.string.my_coupon));
    }

    private void getActivityDetails() {
        showProgressDialog();
        moActivityDetailsViewModel.getActivityDetails(getContext(), "", miActivityId);
    }

    Observer<ActivityDetailsResponse> fetchActivityObserver = new Observer<ActivityDetailsResponse>() {
        @Override
        public void onChanged(ActivityDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getActivity() != null) {
                    moActivity = loJsonObject.getActivity();
                    setUpCouponView();
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void setUpCouponView() {

        int liOrientation = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager loLayoutManager = new LinearLayoutManager(getContext(), liOrientation, false);
        moBinding.rvCoupon.setLayoutManager(loLayoutManager);
        CouponAdapter loOfferAdapter = new CouponAdapter(getContext(), moActivity.getCouponList());
        moBinding.rvCoupon.setAdapter(loOfferAdapter);

        int liOrientation2 = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager loLayoutManager2 = new LinearLayoutManager(getContext(), liOrientation2, false);
        moBinding.rvLocations.setLayoutManager(loLayoutManager2);
        AdLocationAdapter loStoreLocationAdapter = new AdLocationAdapter(getContext(), moActivity.getLocationList());
        moBinding.rvLocations.setAdapter(loStoreLocationAdapter);

        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            if (moActivity.getCouponList() != null && moActivity.getCouponList().size() > 0) {
                moBinding.llOffers.setVisibility(View.VISIBLE);
                moBinding.llInStore.setVisibility(View.GONE);
            }
            moBinding.rlMobile.setVisibility(View.GONE);
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            moBinding.llOffers.setVisibility(View.GONE);
            moBinding.llInStore.setVisibility(View.VISIBLE);
        }

        loOfferAdapter.setOnItemClickListener(new CouponAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Coupon loCoupon = moActivity.getCouponList().get(position);
                if (!loCoupon.getCouponName().isEmpty()) {
                    Common.setClipboard(getContext(), loCoupon.getCouponName());
                    dialogCopyToClipboard(loCoupon.getCouponName());
                } else {
                    openDeepLink(loCoupon.getCouponLink());
                }
            }
        });

        loStoreLocationAdapter.setOnItemClickListener(new AdLocationAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                AdLocation loLocation = moActivity.getLocationList().get(position);
                String lsUrl = "http://maps.google.com/maps?daddr=" + loLocation.getLatitude() + "," + loLocation.getLongitude();
                Common.openBrowser(getContext(), lsUrl);

                callAPIBlinkShopOnline();
                enablePhone();
            }

            private void enablePhone() {

                if (moActivity.isClickShopOnline()) {

                    if (!moBinding.etMobileNumber.isEnabled()) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                moBinding.etMobileNumber.setEnabled(true);
                                moBinding.etMobileNumber.setHintTextColor(ActivityCompat.getColor(getContext(), R.color.white));
                                byPassPhone();
                            }
                        }, 2000);
                    }
                }
            }
        });


        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName() + " (Online)");
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName() + " (InStore)");
            moBinding.tvShopOnline.setText(Common.getDynamicText(getContext(), "btn_shop_in_store"));
        }

        if (moActivity.getOfferDetails() != null && !moActivity.getOfferDetails().isEmpty())
            moBinding.tvCouponDesc.setText(Html.fromHtml(moActivity.getOfferDetails()));

        moBinding.tvBillUpload.setText(Common.getDynamicText(getContext(), "dont_forget_bill_upload")
                .replace("XXXXXX", moActivity.getWalletName()).replace("XX", String.valueOf(moActivity.getVirtualCashTransferDays())));


        final String fsLink = (!moActivity.getShopOnlineLink().isEmpty())
                ? moActivity.getShopOnlineLink()
                : Common.getLinkifiedMyText(moActivity.getCouponDescription());


        if (!fsLink.isEmpty() || moActivity.getLocationList().size() > 0 || moActivity.getCouponList().size() > 0) {
            moBinding.tvShopOnline.setTextColor(getResources().getColor(R.color.white));
            moBinding.tvShopOnline.setClickable(true);
            moBinding.tvShopOnline.setEnabled(true);

            if (moActivity.isClickShopOnline())
                Common.blinkAnimation(moBinding.tvShopOnline);

        } else {
            moBinding.tvShopOnline.setTextColor(getResources().getColor(R.color.twhite));
            moBinding.tvShopOnline.setClickable(false);
            moBinding.tvShopOnline.setEnabled(false);
        }

        if (moActivity.getCouponType().equalsIgnoreCase("default")) {
            moBinding.etMobileNumber.setHint(Common.getDynamicText(getContext(), "mobile_no_mark_used"));
            moBinding.tvNumberType.setVisibility(View.VISIBLE);
        } else {
            moBinding.etMobileNumber.setHint(Common.getDynamicText(getContext(), "ask_store_cashier"));
            moBinding.tvNumberType.setVisibility(View.GONE);
        }

    }

    private void openDeepLink(String fsUrl) {
        if (fsUrl != null && !fsUrl.isEmpty()) {
            Common.openBrowser(getContext(), fsUrl);
            moBinding.tvShopOnline.clearAnimation();

            callAPIBlinkShopOnline();

            if (moActivity.isClickShopOnline()) {
                if (!moBinding.etMobileNumber.isEnabled()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moBinding.etMobileNumber.setEnabled(true);
                            moBinding.etMobileNumber.setHintTextColor(ActivityCompat.getColor(getContext(), R.color.white));
                            Common.blinkAnimation(moBinding.tvMarkAsUsed);
                            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
                            moBinding.tvMarkAsUsed.setClickable(true);
                            moBinding.tvMarkAsUsed.setEnabled(true);
                            byPassPhone();
                        }
                    }, 2000);
                }
            }
        }
    }

    private void byPassPhone() {
        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            //moBinding.etMobileNumber.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+91", ""));
        }
    }

    private void callAPIBlinkShopOnline() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvShopOnline:

                final String fsLink = (!moActivity.getShopOnlineLink().isEmpty())
                        ? moActivity.getShopOnlineLink()
                        : Common.getLinkifiedMyText(moActivity.getCouponDescription());

                if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                    if (fsLink != null && !fsLink.isEmpty()) {
                        dialogCopyToClipboard(fsLink);
                    }
                } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                    moBinding.llInStore.setVisibility(View.VISIBLE);
                }
                moBinding.tvShopOnline.clearAnimation();
                break;
            case R.id.tvMarkAsUsed:

                break;
            case R.id.tvCancel:
                onBackPressed();
                break;
        }
    }

    private void dialogCopyToClipboard(String fsUrl) {
        try {
            Dialog loDialog = new Dialog(getContext());
            loDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loDialog.setContentView(R.layout.dialog_copy_to_clipboard);
            loDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            loDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loDialog.getWindow().setGravity(Gravity.CENTER);
            loDialog.setCancelable(true);
            loDialog.show();

            TextView loTvMessage = loDialog.findViewById(R.id.tvMessage);
            loTvMessage.setText(Common.getDynamicText(getContext(), "msg_copy_to_clipboard")
                    .replace("XX", String.valueOf(moActivity.getVirtualCashTransferDays())));
            Button loBtnUpdateCard = loDialog.findViewById(R.id.btnViewOffer);

            loBtnUpdateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loDialog.dismiss();
                    openDeepLink(fsUrl);
                }
            });
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }
}
