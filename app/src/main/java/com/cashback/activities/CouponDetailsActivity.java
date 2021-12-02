package com.cashback.activities;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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

import static com.cashback.fragments.MapViewFragment.REQUEST_PHONE_LOGIN;
import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

public class CouponDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = CouponDetailsActivity.class.getSimpleName();
    ActivityDetailsBinding moBinding;
    ActivityDetailsViewModel moActivityDetailsViewModel;

    private long miActivityId;
    Activity moActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void setToolbar() {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);

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

    private void initializeContent() {
        moActivityDetailsViewModel = new ViewModelProvider(this).get(ActivityDetailsViewModel.class);
        moActivityDetailsViewModel.fetchActivityStatus.observe(this, fetchActivityObserver);
        moActivityDetailsViewModel.updateMarkAsUsedStatus.observe(this, updateMarkAdUsedObserver);
        moActivityDetailsViewModel.updateShopOnlineBlinkStatus.observe(this, updateShopOnlineBlinkObserver);

        moBinding.tvShopOnline.setOnClickListener(this);
        moBinding.tvShopOffline.setOnClickListener(this);
        moBinding.tvMarkAsUsed.setOnClickListener(this);

        loadCouponView();
    }

    private void loadCouponView() {
        if (getIntent() != null) {
            miActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
            getActivityDetails();
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

        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            setOnlineOfferListView(false);
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            setOfflineOfferListView();
        }

        displayEarnText();
        displayWebView();
        displayCashbackWalletMessage();

        //display remain day
        if (!moActivity.getRemainDay().isEmpty()) {
            moBinding.tvExpireDay.setVisibility(View.VISIBLE);
            moBinding.tvExpireDay.setText(moActivity.getRemainDay());
        }
    }

    private void displayCashbackWalletMessage() {
        //display cashback days message
        moBinding.tvBillUpload.setText(Common.getDynamicText(getContext(), "dont_forget_bill_upload")
                .replace("XXXXXX", moActivity.getWalletName()).replace("XX", String.valueOf(moActivity.getVirtualCashTransferDays())));

        if (moActivity.getAdCouponType() == 1) {
            moBinding.tvBillUpload.setVisibility(View.GONE);
        } else {
            moBinding.tvBillUpload.setVisibility(View.VISIBLE);
        }
    }

    private void displayWebView() {
        //display webview
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
        //  moBinding.tvCouponDesc.setText(Html.fromHtml(moActivity.getOfferDetails()));

    }

    private void setOfflineOfferListView() {
        //set offline location list
        int liOrientation2 = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager loLayoutManager2 = new LinearLayoutManager(getContext(), liOrientation2, false);
        moBinding.rvLocations.setLayoutManager(loLayoutManager2);
        AdLocationAdapter loStoreLocationAdapter = new AdLocationAdapter(getContext(), moActivity.getLocationList());
        moBinding.rvLocations.setAdapter(loStoreLocationAdapter);

        loStoreLocationAdapter.setOnItemClickListener(new AdLocationAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (!getPreferenceManager().isPhoneVerified()) {
                    openPhoneLogin(null);
                } else {
                    AdLocation loLocation = moActivity.getLocationList().get(position);
                    String lsUrl = "http://maps.google.com/maps?daddr=" + loLocation.getLatitude() + "," + loLocation.getLongitude();
                    Common.openBrowser(getContext(), lsUrl);

                    callAPIBlinkShopOnline();
                    enableMarkAsUsed();
                    moBinding.tvShopOffline.clearAnimation();
                }
            }
        });

        //online component invisible
        moBinding.llOnlineOffers.setVisibility(View.GONE);
        moBinding.tvShopOnline.setVisibility(View.GONE);

        //offline component visible
        moBinding.llOffline.setVisibility(View.VISIBLE);
        moBinding.tvMarkAsUsed.setVisibility(View.VISIBLE);
//        if (!moActivity.isBlinkShopOnline()) {
//
//        } else {
//            Common.blinkAnimation(moBinding.tvShopOffline);
//        }
        setMarkAsUsedButton();
        setOnlineOfferListView(true);
    }

    private void setMarkAsUsedButton() {

//        1 : Mark with user mobile no
//        2 : Mark with QR code scan at store
//        3 : Mark with coupon code on excel upload(With QR)
//        4 : Mark with coupon code on excel upload(Without QR)

        if (moActivity.getMarkAsUsedType() == 1) {
            moBinding.tvMarkAsUsed.setText(getString(R.string.btn_use_coupon_store));
        } else if (moActivity.getMarkAsUsedType() == 2 || moActivity.getMarkAsUsedType() == 3) {
            moBinding.tvMarkAsUsed.setText(getString(R.string.btn_scan_moby_qr_code));
        } else if (moActivity.getMarkAsUsedType() == 4) {
            moBinding.tvMarkAsUsed.setText(getString(R.string.btn_view_coupon_code));
        }
    }

    private void setOnlineOfferListView(boolean isOfflineCall) {
        //set online offer list
        int liOrientation = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager loLayoutManager = new LinearLayoutManager(getContext(), liOrientation, false);
        moBinding.rvCoupon.setLayoutManager(loLayoutManager);
        CouponAdapter loOfferAdapter = new CouponAdapter(getContext(), moActivity.getCouponList());
        moBinding.rvCoupon.setAdapter(loOfferAdapter);

        loOfferAdapter.setOnItemClickListener(new CouponAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Coupon loCoupon = moActivity.getCouponList().get(position);
                if (!isOfflineCall) {
                    if (!loCoupon.getCouponLink().isEmpty()) {
                        Common.setClipboard(getContext(), loCoupon.getCouponCode());
                        dialogCopyToClipboard(loCoupon.getCouponLink());
                        moBinding.tvShopOnline.clearAnimation();
                    } else {
                        //openDeepLink(loCoupon.getCouponLink());
                    }
                } else {
                    //move to nearby store button
                    moBinding.tvMarkAsUsed.performClick();
                }
            }
        });

        //handle visibility in offline mode & online mode
        if (!isOfflineCall) {
            setShopOnlineLink();
            //online component visible
            moBinding.tvShopOnline.setVisibility(View.VISIBLE);

            //offline component invisible
            moBinding.llOffline.setVisibility(View.GONE);
        }

        if (moActivity.getCouponList() != null && moActivity.getCouponList().size() > 0) {
            moBinding.llOnlineOffers.setVisibility(View.VISIBLE);
        }
    }

    private void setShopOnlineLink() {
        //display shop online link
        String lsLink = getShopOnlineLink();

        if (!lsLink.isEmpty() || moActivity.getLocationList().size() > 0 || moActivity.getCouponList().size() > 0) {
            moBinding.tvShopOnline.setTextColor(getResources().getColor(R.color.white));
            moBinding.tvShopOnline.setClickable(true);
            moBinding.tvShopOnline.setEnabled(true);

            if (moActivity.isBlinkShopOnline()) {
                Common.blinkAnimation(moBinding.tvShopOnline);
            }
        } else {
            moBinding.tvShopOnline.setTextColor(getResources().getColor(R.color.twhite));
            moBinding.tvShopOnline.setClickable(false);
            moBinding.tvShopOnline.setEnabled(false);
        }
    }

    private String getShopOnlineLink() {
        String fsLink = (!moActivity.getShopOnlineLink().isEmpty())
                ? moActivity.getShopOnlineLink()
                : Common.getLinkifiedMyText(moActivity.getCouponDescription());

        if (fsLink.isEmpty() && moActivity.getCouponList().size() > 0) {
            fsLink = moActivity.getCouponList().get(0).getCouponLink();
        }
        return fsLink == null ? "" : fsLink;
    }

    private void displayEarnText() {
        //display cashback text
        moBinding.tvDiscountUpto.setText("UPTO " + moActivity.getDiscountUpTo() + " DISCOUNT");
        moBinding.tvExactCashback.setText("EXACT " + moActivity.getFlatCashBack() + " CASHBACK");

        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName() + " (Online)");
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName() + " (Offline)");
            moBinding.tvShopOnline.setText(Common.getDynamicText(getContext(), "btn_shop_in_store"));

            moBinding.tvDiscountUpto.append(" - AT STORE");
            moBinding.tvExactCashback.append(" - FROM US");
        }

        if (moActivity.getFlatCashBack() == null || moActivity.getFlatCashBack().isEmpty() || moActivity.getAdCouponType() == 1) {
            moBinding.tvExactCashback.setVisibility(View.GONE);
        }

        if (moActivity.getDiscountUpTo() == null || moActivity.getDiscountUpTo().isEmpty()) {
            moBinding.tvDiscountUpto.setVisibility(View.GONE);
        }
    }

    private void enableMarkAsUsed() {
        if (moActivity.isBlinkShopOnline()) {
            Common.blinkAnimation(moBinding.tvMarkAsUsed);
            moBinding.tvMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
            moBinding.tvMarkAsUsed.setClickable(true);
            moBinding.tvMarkAsUsed.setEnabled(true);
            moBinding.tvMarkAsUsed.setVisibility(View.VISIBLE);

            setOnlineOfferListView(true);
        }
    }

    private void callAPIBlinkShopOnline() {
        if (moActivity.isBlinkShopOnline()) {
            showProgressDialog();
            moActivityDetailsViewModel.updateShopOnlineBlink(getContext(), "", miActivityId);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvShopOnline:
                shopOnlinePressed();
                break;
            case R.id.tvMarkAsUsed:
                dialogMarkAsUsed();
                break;
            case R.id.tvShopOffline:
                shopOfflinePressed();
        }
    }

    private void shopOfflinePressed() {
        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin(null);
        } else {
            moBinding.llInStore.setVisibility(View.VISIBLE);
        }
        moBinding.tvShopOffline.clearAnimation();
    }

    private void shopOnlinePressed() {
        String lsLink = getShopOnlineLink();

        if (lsLink != null && !lsLink.isEmpty()) {
            dialogCopyToClipboard(lsLink);
        }
        moBinding.tvShopOnline.clearAnimation();
    }

    private void dialogMarkAsUsed() {

        moBinding.tvMarkAsUsed.clearAnimation();
        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin(null);
            return;
        }

        if (moActivity.getMarkAsUsedType() == 1) {
            dialogMarkWithPhone();
        } else if (moActivity.getMarkAsUsedType() == 2) {
            openQRCode();
        } else if (moActivity.getMarkAsUsedType() == 3) {
            openQRCode();
        } else if (moActivity.getMarkAsUsedType() == 4) {
            dialogMarkWithCouponCode();
        }
    }

    private static final int REQUEST_QR = 1000;

    private void openQRCode() {
        if (moActivityDetailsViewModel.isCameraPermissionGranted(this)) {
            Intent loIntent = new Intent(getContext(), QrScannerActivity.class);
            startActivityForResult(loIntent, REQUEST_QR);
        }
    }

    private void dialogMarkWithCouponCode() {
        Dialog moDialog = new Dialog(getContext());
        moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moDialog.setContentView(R.layout.dialog_qr_store_coupon);
        moDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        moDialog.getWindow().setGravity(Gravity.CENTER);
        moDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        moDialog.setCancelable(true);
        moDialog.show();

        final Button loBtnMarkAsUsed = moDialog.findViewById(R.id.btnMarkAsUsed);
        final TextView loTvCouponCode = moDialog.findViewById(R.id.tvCouponCode);
        final TextView loTvCbMessage = moDialog.findViewById(R.id.tvCbMessage);
        final EditText loEtBillAmount = moDialog.findViewById(R.id.etBillAmount);

        loTvCouponCode.setText(moActivity.getCouponCode());
        loTvCbMessage.setText(Common.getDynamicText(getContext(), "your_cashback_credit")
                .replace("XXX", moActivity.getFlatCashBack()).replace("YY", String.valueOf(moActivity.getVirtualCashTransferDays())));

        loBtnMarkAsUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int liBillAmount = loEtBillAmount.getText().length() == 0 ? 0 : (int) Double.parseDouble(loEtBillAmount.getText().toString());

                if (liBillAmount <= 0) {
                    Common.showErrorDialog(getContext(),
                            Common.getDynamicText(getContext(), "validate_transaction_amount"),
                            false);
                    return;
                }

                moDialog.dismiss();
                callAPIUpdateMarkAsUsed(liBillAmount);
            }
        });
    }

    private void dialogMarkWithPhone() {
        Dialog moDialog = new Dialog(getContext());
        moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moDialog.setContentView(R.layout.dialog_store_coupon);
        moDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        moDialog.getWindow().setGravity(Gravity.CENTER);
        moDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        moDialog.setCancelable(true);
        moDialog.show();

        final Button loBtnMarkAsUsed = moDialog.findViewById(R.id.btnMarkAsUsed);
        final EditText loEtBillAmount = moDialog.findViewById(R.id.etBillAmount);
        final EditText loEtPhone = moDialog.findViewById(R.id.etPhoneNo);

        loEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString().trim();
                if (getPreferenceManager().isPhoneVerified() && getPreferenceManager().getPhoneNumber() != null) {
                    if (String.valueOf("+91" + number).equalsIgnoreCase(AppGlobal.getPhoneNumber())) {
                        loBtnMarkAsUsed.setTextColor(getResources().getColor(R.color.white));
                        loBtnMarkAsUsed.setClickable(true);
                        loBtnMarkAsUsed.setEnabled(true);
                    } else {
                        loBtnMarkAsUsed.setTextColor(getResources().getColor(R.color.twhite));
                        loBtnMarkAsUsed.setClickable(false);
                        loBtnMarkAsUsed.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        loBtnMarkAsUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int liBillAmount = loEtBillAmount.getText().length() == 0 ? 0 : (int) Double.parseDouble(loEtBillAmount.getText().toString());
                String lsPhone = "+91" + loEtPhone.getText().toString().trim();

                if (liBillAmount <= 0) {
                    Common.showErrorDialog(getContext(),
                            Common.getDynamicText(getContext(), "validate_transaction_amount"),
                            false);
                    return;
                } else if (lsPhone.length() <= 10) {
                    Common.showErrorDialog(getContext(),
                            Common.getDynamicText(getContext(), "validate_phone"),
                            false);
                    return;
                }

                moDialog.dismiss();
                callAPIUpdateMarkAsUsed(liBillAmount);
            }
        });
    }

    private void callAPIUpdateMarkAsUsed(int fiBillAmount) {
        showProgressDialog();
        moActivityDetailsViewModel.updateMarkAsUsed(getContext(), "", miActivityId, fiBillAmount);
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
        }
    }

    private void dialogCopyToClipboard(String fsUrl) {

        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin(fsUrl);
        } else {
            if (moActivity.getAdCouponType() == 1) { // only coupon then direct open link
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

    private void openDeepLink(String fsUrl) {
        if (fsUrl != null && !fsUrl.isEmpty()) {
            Common.openBrowser(getContext(), fsUrl);

            isShopOnlinePressed = true;
            callAPIBlinkShopOnline();
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
        if (requestCode == REQUEST_QR) {
            if (resultCode == RESULT_OK) {
                String lsScannedText = data.getStringExtra(Constants.IntentKey.QR_DATA);
                if (!lsScannedText.isEmpty()) {
                    if (moActivity.getQrCodeText().isEmpty() || (!moActivity.getQrCodeText().equalsIgnoreCase(lsScannedText))) {
                        Common.showErrorDialog(getContext(), Common.getDynamicText(getContext(), "scanned_qr_is_not_correct"), false);
                    } else {
                        if (moActivity.getMarkAsUsedType() == 2) {
                            dialogMarkWithPhone();
                        } else if (moActivity.getMarkAsUsedType() == 3) {
                            dialogMarkWithCouponCode();
                        }
                    }
                }
            }
        }
    }

    boolean isShopOnlinePressed = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isShopOnlinePressed) {
            Intent intent = new Intent();
            intent.setAction(Constants.IntentKey.Action.CLICK_SHOP_ONLINE);
            setResult(android.app.Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (getIntent() != null && getIntent().getAction() != null && moActivity != null &&
                    getIntent().getAction().equalsIgnoreCase(Constants.IntentKey.Action.BY_PASS_QUIZ)) {
                Common.msOfferId = "" + moActivity.getAdID(); //not possible to check long as null so add try catch for null pointer
                Intent intent = new Intent(CouponDetailsActivity.this, HomeActivity.class);
                intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.FROM_COUPON);
                startActivity(intent);
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }
}
