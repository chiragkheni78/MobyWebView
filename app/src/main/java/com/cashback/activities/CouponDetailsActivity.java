package com.cashback.activities;

import static com.cashback.fragments.MapViewFragment.REQUEST_PHONE_LOGIN;
import static com.cashback.models.viewmodel.ActivityDetailsViewModel.REQUEST_CAMERA;
import static com.cashback.utils.Constants.IntentKey.FROM_QUIZ;
import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.AdLocationAdapter;
import com.cashback.adapters.CouponAdapter;
import com.cashback.adapters.DealsOfDayAdapter;
import com.cashback.databinding.ActivityDetailsBinding;
import com.cashback.dialog.MessageDialog;
import com.cashback.models.Activity;
import com.cashback.models.AdLocation;
import com.cashback.models.Coupon;
import com.cashback.models.response.ActivityDetailsResponse;
import com.cashback.models.response.ActivityMarkAsUsedResponse;
import com.cashback.models.response.DealOfTheDayResponse;
import com.cashback.models.response.GetUpdateUserSessionResponse;
import com.cashback.models.response.UpdateShopOnlineBlinkResponse;
import com.cashback.models.viewmodel.ActivityDetailsViewModel;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.cashback.utils.LogV2;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;
import java.util.Locale;

public class CouponDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = CouponDetailsActivity.class.getSimpleName();
    ActivityDetailsBinding moBinding;
    ActivityDetailsViewModel moActivityDetailsViewModel;
    private boolean mbShopOnlinePressed = false;
    private long miActivityId;
    private String isFrom;
    Activity moActivity;
    CouponAdapter loOfferAdapter;

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
            loTvToolbarTitle.setText("Online Coupon");
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            loTvToolbarTitle.setText("Offline Store Coupon");
        }
    }

    private void initializeContent() {
        moActivityDetailsViewModel = new ViewModelProvider(this).get(ActivityDetailsViewModel.class);
        moActivityDetailsViewModel.fetchActivityStatus.observe(this, fetchActivityObserver);
        moActivityDetailsViewModel.updateMarkAsUsedStatus.observe(this, updateMarkAdUsedObserver);
        moActivityDetailsViewModel.updateShopOnlineBlinkStatus.observe(this, updateShopOnlineBlinkObserver);
        moActivityDetailsViewModel.getUpdateUserSessionResponse.observe(this, getUpdateUserSessionResponse);

        moBinding.tvShopOnline.setOnClickListener(this);
        moBinding.tvShopOffline.setOnClickListener(this);
        moBinding.tvMarkAsUsed.setOnClickListener(this);
        moBinding.ivArrow.setOnClickListener(this);

        loadCouponView();
    }

    private void loadCouponView() {
        if (getIntent() != null) {
            miActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
            isFrom = getIntent().getStringExtra(Constants.IntentKey.IS_FROM);
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
        moBinding.llTop.setVisibility(View.VISIBLE);
        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            setOnlineOfferListView(false);
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            setOfflineOfferListView();
        }

        displayEarnText();
        if (moActivity.getAdCouponType() == 1) {
            moBinding.cdWebview.setVisibility(View.GONE);
            if (moActivity.getCouponList() != null && moActivity.getCouponList().size() > 0) {
                moBinding.rlBanner.setVisibility(View.VISIBLE);

                moBinding.imageSlider.setIndicatorEnabled(true);
                moBinding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                moBinding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                moBinding.imageSlider.startAutoCycle();

                ArrayList<DealOfTheDayResponse> couponList = new ArrayList<>();

                for (int i = 0; i < moActivity.getCouponList().size(); i++) {
                    DealOfTheDayResponse dealOfTheDayResponse = new DealOfTheDayResponse();
                    dealOfTheDayResponse.setImage(moActivity.getCouponList().get(i).getCouponBanner());
                    couponList.add(dealOfTheDayResponse);
                }

                moBinding.imageSlider.setSliderAdapter(new DealsOfDayAdapter(getContext(), couponList, new DealsOfDayAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(DealOfTheDayResponse foDealList, int position) {
                        if (moActivity.getCouponList() != null &&
                                moActivity.getCouponList().size() > 0
                                && loOfferAdapter != null) {
                            loOfferAdapter.notifyFirstItem(position);


                            float sliderHeight = moBinding.imageSlider.getHeight();
                            float headerHeight = moBinding.llHeader.getHeight();
                            View view = moBinding.rvCoupon.getLayoutManager().findViewByPosition(position);
                            if (view != null) {
                                float X = view.getX();
                                float Y = view.getY();
                                moBinding.nsvTop.smoothScrollTo(0, (int) (Y + sliderHeight + headerHeight));
//                                moBinding.rvCoupon.scrollToPosition(position);
                            }

                        }
                    }
                }));
            }
        } else {
            displayWebView();
        }

        displayCashbackWalletMessage();

        //display remain day
        if (!moActivity.getRemainDay().isEmpty()) {
            moBinding.tvExpireDay.setVisibility(View.VISIBLE);
            moBinding.tvExpireDay.setText(moActivity.getRemainDay());
        }
    }

    private void displayCashbackWalletMessage() {
        //display cashback days message
        if (moActivity.getProviderCashLabel().equals("yes")) {
            moBinding.tvBillUpload.setText("( " + AppGlobal.msProviderCashLabel + " Credit After Tracking Message" + " )");
        } else {
            moBinding.tvBillUpload.setText(Common.getDynamicText(getContext(), "dont_forget_bill_upload")
                    .replace("XXXXXX", moActivity.getWalletName()).replace("XX", String.valueOf(moActivity.getVirtualCashTransferDays())));
        }

        if (moActivity.getAdCouponType() == 1 || moActivity.getFlatCashBack().equals("0")) {
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
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            moBinding.tvWebView.getSettings().setJavaScriptEnabled(true);

            // Log.d("TTT", "moActivity.getOfferDetails()...." + moActivity.getOfferDetails());
            moBinding.tvWebView.loadDataWithBaseURL(null, moActivity.getOfferDetails(), "text/html", "utf-8", null);
        }
    }

    private void storeLocationAdapterClick() {
        AdLocation loLocation = moActivity.getLocationList().get(moPosition);
        String lsUrl = "http://maps.google.com/maps?daddr=" + loLocation.getLatitude() + "," + loLocation.getLongitude();
        Common.openBrowser(getContext(), lsUrl);

        callAPIBlinkShopOnline();
        enableMarkAsUsed();
        moBinding.tvShopOffline.clearAnimation();
    }

    boolean isNearStoreClick = false;
    int moPosition;

    private void setOfflineOfferListView() {
        //set offline location list
        int liOrientation2 = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager loLayoutManager2 = new LinearLayoutManager(getContext(), liOrientation2, false);
        moBinding.rvLocations.setLayoutManager(loLayoutManager2);
        AdLocationAdapter loStoreLocationAdapter = new AdLocationAdapter(getContext(), moActivity.getLocationList());
        moBinding.rvLocations.setAdapter(loStoreLocationAdapter);

        moBinding.tvShopOffline.setVisibility(View.VISIBLE);
        loStoreLocationAdapter.setOnItemClickListener(new AdLocationAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                isNearStoreClick = true;
                moPosition = position;
                if (!getPreferenceManager().isPhoneVerified()) {
                    openPhoneLogin(null);
                } else {
                    storeLocationAdapterClick();
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

        if (moActivity.getMarkAsUsedType() == 1 || moActivity.getMarkAsUsedType() == 4) {
            moBinding.tvMarkAsUsed.setText(getString(R.string.btn_use_coupon_store));
        } else if (moActivity.getMarkAsUsedType() == 2 || moActivity.getMarkAsUsedType() == 3) {
            moBinding.tvMarkAsUsed.setText(getString(R.string.btn_scan_moby_qr_code));
        }
    }

    private void setOnlineOfferListView(boolean isOfflineCall) {
        //set online offer list
        int liOrientation = Common.getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager loLayoutManager = new LinearLayoutManager(getContext(), liOrientation, false);
        moBinding.rvCoupon.setLayoutManager(loLayoutManager);
        //CustomLayoutManager customLayoutManager = new CustomLayoutManager(this);
        // moBinding.rvCoupon.setLayoutManager(customLayoutManager);

        loOfferAdapter = new CouponAdapter(getContext(), moActivity.getCouponList());
        moBinding.rvCoupon.setAdapter(loOfferAdapter);

        loOfferAdapter.setOnItemClickListener(new CouponAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //trigger event
                moActivityDetailsViewModel.buttonClicked(moActivity.getAdID(), Constants.IntentKey.OFFER_ONLINE);
                if (moActivity.isBlinkShopOnline()) {
                    AdGydeEvents.getOfferClicked(getContext(), moActivity);
                }

                FirebaseEvents.trigger(CouponDetailsActivity.this, null, FirebaseEvents.GET_OFFER_CLICKED);

                Coupon loCoupon = moActivity.getCouponList().get(position);
                if (!isOfflineCall) {
                    if (!loCoupon.getCouponLink().isEmpty()) {
                        Common.setClipboard(getContext(), loCoupon.getCouponCode());
                        dialogCopyToClipboard(loCoupon.getCouponLink());

                        if (loCoupon.getCouponName().equals("Copy Code")) {
                            Toast.makeText(moContext, "Code Copied", Toast.LENGTH_SHORT).show();
                        }

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

        moBinding.tvHeader.setText("SOME OTHER OFFERS IN THE STORE");
        if (moActivity.getAdCouponType() != 1) {

            if (moActivity.getProviderCashLabel().equals("yes")) {
                String lsSubline = "\n" + AppGlobal.msProviderCashLabel + " IS THE SAME XX EXTRA";
                lsSubline = lsSubline.replace("XX", moActivity.getFlatCashBack());
                moBinding.tvHeader.append(Common.getColorSizeText(lsSubline, Color.BLACK, 0.80f));
            } else {
                String lsSubline = "\nCASHBACK IS THE SAME XX EXTRA";
                lsSubline = lsSubline.replace("XX", moActivity.getFlatCashBack());
                moBinding.tvHeader.append(Common.getColorSizeText(lsSubline, Color.BLACK, 0.80f));
            }
        }

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
        moBinding.tvDiscountUpto.setText(Common.getColorText("Upto\n", Color.BLACK));
        moBinding.tvDiscountUpto.append(Common.getColorSizeText(moActivity.getDiscountUpTo(), Color.BLACK, 1.30f));
        moBinding.tvDiscountUpto.append(Common.getColorSizeText("\nDiscount", Color.BLACK, 1.15f));

        if (moActivity.getAdCouponType() == 1) {
            moBinding.tvExactCashback.setText(Common.getColorText("Upto\n", Color.WHITE));
            moBinding.tvExactCashback.append(Common.getColorSizeText(moActivity.getFlatCbAmazon(), Color.WHITE, 1.30f));
            moBinding.tvExactCashback.append(Common.getColorSizeText("\nInstant\nDiscount", Color.WHITE, 1.10f));
            moBinding.tvCardMsg.setVisibility(View.VISIBLE);
        } else {
            moBinding.tvExactCashback.setText(Common.getColorText("Exact\n", Color.WHITE));
            moBinding.tvExactCashback.append(Common.getColorSizeText(moActivity.getFlatCashBack(), Color.WHITE, 1.30f));

            if (moActivity.getProviderCashLabel().equals("yes")) {
                moBinding.tvExactCashback.append(Common.getColorSizeText("\n" + AppGlobal.msProviderCashLabel, Color.WHITE, 1.10f));
            } else {
                moBinding.tvExactCashback.append(Common.getColorSizeText("\n" + Constants.CASHBACK, Color.WHITE, 1.30f));
            }
        }

        moBinding.lblAdditional.setText(moActivity.getAdditionLabel());

        if (moActivity.getProviderCashLabel().equals("yes")) {
            moBinding.tvMaxCashback.setText("Max " + AppGlobal.msProviderCashLabel + " Rs." + moActivity.getQuizReward() + "");
        } else {
            moBinding.tvMaxCashback.setText("Max Cashback Rs. " + moActivity.getQuizReward() + "");
        }

        if (moActivity.getQuizReward() == 0 || moActivity.getAdCouponType() == 1) {
            moBinding.tvMaxCashback.setVisibility(View.GONE);
        }

        Common.loadImage(moBinding.ivOfferImage, moActivity.getAdLogo(),
                getResources().getDrawable(R.drawable.ic_moby_small),
                null);
        if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName());
        } else if (moActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
            moBinding.tvBrand.setText(moActivity.getAdName());
            moBinding.tvShopOnline.setText(Common.getDynamicText(getContext(), "btn_shop_in_store"));
        }

//        if (moActivity.getFlatCashBack() == null || moActivity.getFlatCashBack().isEmpty() || moActivity.getAdCouponType() == 1) {
//            moBinding.tvExactCashback.setVisibility(View.GONE);
//        }

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

    Observer<GetUpdateUserSessionResponse> getUpdateUserSessionResponse = new Observer<GetUpdateUserSessionResponse>() {
        @Override
        public void onChanged(GetUpdateUserSessionResponse loJsonObject) {
            dismissProgressDialog();
        }
    };
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
                mbShopOnlinePressed = true;
                shopOnlinePressed();
                break;
            case R.id.tvMarkAsUsed:
                dialogMarkAsUsed();
                break;
            case R.id.tvShopOffline:
                //shopOfflinePressed(false);
                moBinding.llInStore.setVisibility(View.VISIBLE);
            case R.id.ivArrow:
                if (moBinding.ivArrow.getTag() != null) {
                    if (moBinding.ivArrow.getTag().equals("UP")) {
                        moBinding.ivArrow.setTag("DOWN");
                        moBinding.ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    } else {
                        moBinding.ivArrow.setTag("UP");
                        moBinding.ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }
                } else {
                    moBinding.ivArrow.setTag("UP");
                    moBinding.ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_keyboard_arrow_up_24));
                }
                int[] location = new int[2];
                moBinding.ivArrow.getLocationOnScreen(location);
                int x = location[0];
                int height = location[1];
                moBinding.nsvTop.smoothScrollTo(x, height + 50, 2000);
                break;

        }
    }

    private void shopOfflinePressed(boolean isPerformClick) {
        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin(null);
        } else {
            moBinding.llInStore.setVisibility(View.VISIBLE);
        }
        moBinding.tvShopOffline.clearAnimation();

        if (isPerformClick) {
            if (isNearStoreClick) {
                storeLocationAdapterClick();
            } else {
                moBinding.tvMarkAsUsed.performClick();
            }
        }
    }

    private void shopOnlinePressed() {
        FirebaseEvents.trigger(CouponDetailsActivity.this, null, FirebaseEvents.SHOP_ONLINE_CLICKED);

        String lsLink = getShopOnlineLink();

        if (lsLink != null && !lsLink.isEmpty()) {
            dialogCopyToClipboard(lsLink);
        }

        //trigger event
        if (moActivity.isBlinkShopOnline()) {
            AdGydeEvents.shopOnlineClicked(getContext(), moActivity);
        }
        moBinding.tvShopOnline.clearAnimation();
        moActivityDetailsViewModel.buttonClicked(moActivity.getAdID(), Constants.IntentKey.SHOP_ONLINE);
    }

    private void dialogMarkAsUsed() {

        moBinding.tvMarkAsUsed.clearAnimation();
        if (!getPreferenceManager().isPhoneVerified()) {
            openPhoneLogin(null);
            return;
        }
       // Log.d("TTT", "mark as used.." + moActivity.getMarkAsUsedType());
        if (moActivity != null) {
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
    }

    private static final int REQUEST_QR = 1000;

    private void openQRCode() {
        if (moActivityDetailsViewModel.isCameraPermissionGranted(this)) {
            Intent loIntent = new Intent(getContext(), QrScannerActivity.class);
            startActivityForResult(loIntent, REQUEST_QR);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted...");
                    moBinding.tvMarkAsUsed.performClick();
                } //else {
                // Common.showErrorDialog(getContext(), "", false);
                // }
                break;
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
        final EditText loEtStoreCode = moDialog.findViewById(R.id.etStoreCode);

        if (moActivity.getMarkAsUsedType() == 3) {
            loEtStoreCode.setVisibility(View.GONE);
        }
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
                } else if (moActivity.getMarkAsUsedType() == 4 && TextUtils.isEmpty(loEtStoreCode.getText().toString().trim())) {
                    Common.showErrorDialog(getContext(),
                            "Please enter store code",
                            false);
                    return;
                } else if (moActivity.getMarkAsUsedType() == 4 && !TextUtils.isEmpty(loEtStoreCode.getText().toString().trim())) {
                    if (!loEtStoreCode.getText().toString().trim().equals(moActivity.getQrCodeText())) {
                        Common.showErrorDialog(getContext(),
                                "Invalid store code, Please contact store owner",
                                false);
                        return;
                    }
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
                String lsMessage;
                if (moActivity.isBillUploadEnable()) {
                    lsMessage = Common.getDynamicText(getContext(), "msg_cc_bill_upload_cb");
                } else {
                    lsMessage = Common.getDynamicText(getContext(), "msg_copy_to_clipboard");
                }

                if (moActivity.getProviderCashLabel().equals("yes")) {
                    lsMessage = getString(R.string.cash_label_dialog_msg);
                } else {
                    lsMessage = Common.getDynamicText(getContext(), "msg_copy_to_clipboard");
                }

                lsMessage = lsMessage.replace("XXXXX", moActivity.getWalletName())
                        .replace("YY", String.valueOf(moActivity.getVirtualCashTransferDays()));

               /* if (lsTitle.contains("CASHBACK TRACKING ACTIVATED")) {
                    lsTitle.replaceAll("TRACKING", "");
                }*/

                if (moActivity.getProviderCashLabel().equals("yes")) {
                    lsTitle = AppGlobal.msProviderCashLabel.toUpperCase(Locale.ROOT) + " ACTIVATED";
                }

                MessageDialog loDialog = new MessageDialog(CouponDetailsActivity.this, lsTitle, lsMessage, "SHOP AS USUAL", false);
                loDialog.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loDialog.dismiss();
                        openDeepLink(fsUrl);

                        FirebaseEvents.trigger(getContext(), null, FirebaseEvents.CASHBACK_ACTIVATE_OK_CLICKED);
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
            AdGydeEvents.redirectToURL(getContext(), fsUrl);
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
                if (mbShopOnlinePressed) {
                    mbShopOnlinePressed = false;

                    FirebaseEvents.trigger(CouponDetailsActivity.this, null, FirebaseEvents.SHOP_ONLINE_VERIFIED);
                }
                if (msURL != null) {
                    dialogCopyToClipboard(msURL);
                } else {
                    shopOfflinePressed(true);
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
            FirebaseEvents.trigger(CouponDetailsActivity.this, null, FirebaseEvents.SHOP_ONLINE_BACK_PRESS);

            if (!TextUtils.isEmpty(isFrom) && isFrom.equalsIgnoreCase(FROM_QUIZ)) {
                Intent intent = new Intent(CouponDetailsActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            } else if (getIntent() != null && getIntent().getAction() != null && moActivity != null &&
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
