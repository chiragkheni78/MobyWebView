package com.cashback.activities;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.AdLocationAdapter;
import com.cashback.adapters.CouponAdapter;
import com.cashback.databinding.ActivityDetailsBinding;
import com.cashback.models.Activity;
import com.cashback.models.AdLocation;
import com.cashback.models.Coupon;
import com.cashback.models.response.ActivityDetailsResponse;
import com.cashback.models.response.ActivityMarkAsUsedResponse;
import com.cashback.models.response.UpdateShopOnlineBlinkResponse;
import com.cashback.models.viewmodel.ActivityDetailsViewModel;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;
import com.cashback.dialog.MessageDialog;
import com.google.firebase.auth.FirebaseAuth;

import static com.cashback.fragments.MapViewFragment.REQUEST_PHONE_LOGIN;
import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

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
                    if (getPreferenceManager().isPhoneVerified() &&
                            getPreferenceManager().getPhoneNumber() != null) {
                        if (String.valueOf("+91" + number).equalsIgnoreCase(AppGlobal.getPhoneNumber())) {
                            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
                            moBinding.tvMarkAsUsed.setClickable(true);
                            moBinding.tvMarkAsUsed.setEnabled(true);
                            moBinding.llMarkAsRead.setVisibility(View.VISIBLE);
                            Common.blinkAnimation(moBinding.tvMarkAsUsed);
                        } else {
                            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.twhite));
                            moBinding.tvMarkAsUsed.setClickable(false);
                            moBinding.tvMarkAsUsed.setEnabled(false);
                        }
                    }
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
        moActivityDetailsViewModel.updateMarkAsUsedStatus.observe(this, updateMarkAdUsedObserver);
        moActivityDetailsViewModel.updateShopOnlineBlinkStatus.observe(this, updateShopOnlineBlinkObserver);
        moBinding.etMobileNumber.addTextChangedListener(moTextChangeListener);
        moBinding.tvShopOnline.setOnClickListener(this);
        moBinding.tvShopOffline.setOnClickListener(this);
        moBinding.tvMarkAsUsed.setOnClickListener(this);
        moBinding.tvCancel.setOnClickListener(this);

        loadCouponView();
    }

    private void loadCouponView() {
        if (getIntent() != null) {
            miActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
            getActivityDetails();
        }
    }

    private void setToolbar() {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);
//        loToolbar.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout loIbNavigation = loToolbar.findViewById(R.id.llBack);
        loIbNavigation.setOnClickListener(v -> {
            onBackPressed();
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);

        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            loTvToolbarTitle.setText("ONLINE COUPON");
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            loTvToolbarTitle.setText("OFFLINE STORE COUPON");
        }

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
                    setToolbar();
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
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            moBinding.llOffers.setVisibility(View.GONE);
            moBinding.llInStore.setVisibility(View.GONE);
        }

        loOfferAdapter.setOnItemClickListener(new CouponAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Coupon loCoupon = moActivity.getCouponList().get(position);
                if (!loCoupon.getCouponLink().isEmpty()) {
                    Common.setClipboard(getContext(), loCoupon.getCouponName());
                    dialogCopyToClipboard(loCoupon.getCouponLink());
                } else {
                    //openDeepLink(loCoupon.getCouponLink());
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

                if (moActivity.isBlinkShopOnline()) {

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

        if (moActivity.getCouponType().equalsIgnoreCase("default")) {
            moBinding.etMobileNumber.setHint(Common.getDynamicText(getContext(), "mobile_no_mark_used"));
            moBinding.tvNumberType.setVisibility(View.VISIBLE);
        } else {
            moBinding.etMobileNumber.setHint(Common.getDynamicText(getContext(), "ask_store_cashier"));
            moBinding.tvNumberType.setVisibility(View.GONE);
        }

        moBinding.tvDiscountUpto.setText("UPTO " + moActivity.getDiscountUpTo() + " DISCOUNT");
        moBinding.tvExactCashback.setText("EXACT " + moActivity.getFlatCashBack() + " CASHBACK");


        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName() + " (Online)");

            moBinding.cvShopOnline.setVisibility(View.VISIBLE);
            moBinding.cvShopOffline.setVisibility(View.GONE);
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName() + " (InStore)");
            moBinding.tvShopOnline.setText(Common.getDynamicText(getContext(), "btn_shop_in_store"));

            moBinding.cvShopOnline.setVisibility(View.GONE);
            moBinding.cvShopOffline.setVisibility(View.VISIBLE);

            moBinding.tvDiscountUpto.append(" - AT STORE");
            moBinding.tvExactCashback.append(" - FROM US");
        }

        if (moActivity.getFlatCashBack() == null || moActivity.getFlatCashBack().isEmpty() || moActivity.getAdCouponType() == 1){
            moBinding.tvExactCashback.setVisibility(View.GONE);
        }

        if (moActivity.getDiscountUpTo() == null || moActivity.getDiscountUpTo().isEmpty()){
            moBinding.tvDiscountUpto.setVisibility(View.GONE);
        }

        moBinding.tvCoupon.setText(moActivity.getCouponCode());

        if (moActivity.getOfferDetails() != null && !moActivity.getOfferDetails().isEmpty()) {
            moBinding.tvWebView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            moBinding.tvWebView.getSettings().setJavaScriptEnabled(true);
            moBinding.tvWebView.loadDataWithBaseURL(null, moActivity.getOfferDetails(), "text/html", "utf-8", null);
        }
//            moBinding.tvCouponDesc.setText(Html.fromHtml(moActivity.getOfferDetails()));

        moBinding.tvBillUpload.setText(Common.getDynamicText(getContext(), "dont_forget_bill_upload")
                .replace("XXXXXX", moActivity.getWalletName()).replace("XX", String.valueOf(moActivity.getVirtualCashTransferDays())));


        if (moActivity.getAdCouponType() == 1){
            moBinding.tvBillUpload.setVisibility(View.GONE);
        } else {
            moBinding.tvBillUpload.setVisibility(View.VISIBLE);
        }

        if (!moActivity.getRemainDay().isEmpty()) {
            moBinding.tvExpireDay.setVisibility(View.VISIBLE);
            moBinding.tvExpireDay.setText(moActivity.getRemainDay());
        }

        final String fsLink = (!moActivity.getShopOnlineLink().isEmpty())
                ? moActivity.getShopOnlineLink()
                : Common.getLinkifiedMyText(moActivity.getCouponDescription());


        if (!fsLink.isEmpty() || moActivity.getLocationList().size() > 0 || moActivity.getCouponList().size() > 0) {
            moBinding.tvShopOnline.setTextColor(getResources().getColor(R.color.white));
            moBinding.tvShopOnline.setClickable(true);
            moBinding.tvShopOnline.setEnabled(true);

            if (moActivity.isBlinkShopOnline())
                Common.blinkAnimation(moBinding.tvShopOnline);
            else {
                moBinding.etMobileNumber.setEnabled(true);
                moBinding.etMobileNumber.setHintTextColor(ActivityCompat.getColor(getContext(), R.color.white));
                byPassPhone();
            }
        } else {
            moBinding.tvShopOnline.setTextColor(getResources().getColor(R.color.twhite));
            moBinding.tvShopOnline.setClickable(false);
            moBinding.tvShopOnline.setEnabled(false);
            //enable phone if no link exist
            moBinding.etMobileNumber.setEnabled(true);
            moBinding.etMobileNumber.setHintTextColor(ActivityCompat.getColor(getContext(), R.color.white));
        }
    }

    private void openDeepLink(String fsUrl) {
        if (fsUrl != null && !fsUrl.isEmpty()) {
            Common.openBrowser(getContext(), fsUrl);
            moBinding.tvShopOnline.clearAnimation();
            isShopOnlinePressed = true;
            callAPIBlinkShopOnline();

            if (moActivity.isBlinkShopOnline()) {
                if (!moBinding.etMobileNumber.isEnabled()) {
                    Handler handler = new Handler(Looper.getMainLooper());
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
                    }, 50);
                }
            }
        }
    }

    private void byPassPhone() {
        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            moBinding.etMobileNumber.setText(AppGlobal.getPhoneNumber().replace("+91", ""));

            //remove code when above code enable
//            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
//            moBinding.tvMarkAsUsed.setClickable(true);
//            moBinding.tvMarkAsUsed.setEnabled(true);
        }
    }

    private void callAPIBlinkShopOnline() {
        showProgressDialog();

        moActivityDetailsViewModel.updateShopOnlineBlink(getContext(), "", miActivityId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvShopOnline:
                shopOnlinePressed();
                break;
            case R.id.tvMarkAsUsed:
                markAsUsedPressed();
                break;
            case R.id.tvCancel:
                onBackPressed();
                break;
            case R.id.tvShopOffline:
                shopOfflinePressed();
        }
    }

    private void shopOfflinePressed() {
        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            if (!getPreferenceManager().isPhoneVerified()) {
                openPhoneLogin(null);
            } else {
                moBinding.llInStore.setVisibility(View.VISIBLE);
            }
        }
    }

    private void shopOnlinePressed() {

        String fsLink = (!moActivity.getShopOnlineLink().isEmpty())
                ? moActivity.getShopOnlineLink()
                : Common.getLinkifiedMyText(moActivity.getCouponDescription());

        if (fsLink.isEmpty() && moActivity.getCouponList().size() > 0) {
            fsLink = moActivity.getCouponList().get(0).getCouponLink();
        }

        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            if (fsLink != null && !fsLink.isEmpty()) {
                dialogCopyToClipboard(fsLink);
            }
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            moBinding.llInStore.setVisibility(View.VISIBLE);
        }
        moBinding.tvShopOnline.clearAnimation();
    }

    private void markAsUsedPressed() {
        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            openBillUploadActivity();
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            dialogUploadAlert();
        }
        moBinding.tvMarkAsUsed.clearAnimation();
    }

    private void dialogUploadAlert() {
        Dialog moDialog = new Dialog(getContext());
        moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moDialog.setContentView(R.layout.dialog_upload_alert);
        moDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        moDialog.getWindow().setGravity(Gravity.CENTER);
        moDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        moDialog.setCancelable(false);
        moDialog.show();

        final TextView loTvMessage = moDialog.findViewById(R.id.tvMessage);
        final Button loBtnNow = moDialog.findViewById(R.id.btnUpdateNow);
        final Button loBtnLater = moDialog.findViewById(R.id.btnLater);

        loTvMessage.setText(Common.getDynamicText(getContext(), "coupon_code_conformation_msg"));
        loBtnNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moDialog.dismiss();
                showProgressDialog();
                moActivityDetailsViewModel.updateMarkAsUsed(getContext(), "", miActivityId);
            }
        });

        loBtnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moDialog.dismiss();
            }
        });

    }

    Observer<ActivityMarkAsUsedResponse> updateMarkAdUsedObserver = new Observer<ActivityMarkAsUsedResponse>() {
        @Override
        public void onChanged(ActivityMarkAsUsedResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                openBillUploadActivity();
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void openBillUploadActivity() {
        if (moActivity != null) {
            Intent intent = new Intent();
            intent.setAction(Constants.IntentKey.Action.OPEN_BILL_UPLOAD);
            setResult(android.app.Activity.RESULT_OK, intent);
            finish();

//            Intent loIntent = new Intent(getContext(), BillUploadActivity.class);
//            loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, moActivity.getActivityID());
//            loIntent.putExtra(ENGAGED_DATE, moActivity.getQuizEngageDateTime());
//            loIntent.putExtra(PIN_COLOR, moActivity.getPinColor());
//            startActivity(loIntent);
//            finish();
        }
    }

    private void dialogCopyToClipboard(String fsUrl) {

        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin(fsUrl);
        } else {
            if (moActivity.getAdCouponType() == 1){
                openDeepLink(fsUrl);
                return;
            }

            try {
                String lsTitle = Common.getDynamicText(getContext(), "title_copy_to_clipboard");
                String lsMessage = Common.getDynamicText(getContext(), "msg_copy_to_clipboard")
                        .replace("XXXXX", moActivity.getWalletName())
                        .replace("YY", String.valueOf(moActivity.getVirtualCashTransferDays()));
                MessageDialog loDialog = new MessageDialog(CouponDetailsActivity.this, lsTitle, lsMessage, null, false);
                loDialog.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loDialog.dismiss();
                        openDeepLink(fsUrl);
                    }
                });
                loDialog.show();
            } catch (Exception e) {
                LogV2.logException(TAG, e);
            }
        }
    }

    private String msURL;

    private void openPhoneLogin(String fsUrl) {
        msURL = fsUrl;
        Intent loIntent = new Intent(getContext(), PhoneLoginActivity.class);
        loIntent.putExtra(SCREEN_TITLE, this.getResources().getString(R.string.msg_verify_phone_number_coupon));
        startActivityForResult(loIntent, REQUEST_PHONE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHONE_LOGIN) {
            if (resultCode == RESULT_OK) {
                if (msURL != null) {
                    dialogCopyToClipboard(msURL);
                    AdGydeEvents.shopOnlineClicked(getContext(), moActivity);
                } else {
                    shopOfflinePressed();
                }
            }
        }
    }

    Observer<UpdateShopOnlineBlinkResponse> updateShopOnlineBlinkObserver = new Observer<UpdateShopOnlineBlinkResponse>() {
        @Override
        public void onChanged(UpdateShopOnlineBlinkResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                moActivity.setBlinkShopOnline(false);
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    boolean isShopOnlinePressed = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isShopOnlinePressed) {
            Intent intent = new Intent();
            intent.setAction(Constants.IntentKey.Action.CLICK_SHOP_ONLINE);
            setResult(1, intent);
            setResult(android.app.Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Constants.IntentKey.Action.BY_PASS_QUIZ)) {
            Common.msOfferId = "" + moActivity.getAdID();
            Intent intent = new Intent(CouponDetailsActivity.this, HomeActivity.class);
            intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.FROM_COUPON);
            startActivity(intent);
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }
}
