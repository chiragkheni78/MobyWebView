package com.cashback.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.databinding.ActivityOfferDetailsBinding;
import com.cashback.models.Ad;
import com.cashback.models.viewmodel.OfferDetailsViewModel;
import com.cashback.models.response.OfferDetailsResponse;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

public class OfferDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = OfferDetailsActivity.class.getSimpleName();
    ActivityOfferDetailsBinding moBinding;
    OfferDetailsViewModel moOfferDetailsViewModel;

    private Ad moOffer;
    private long miOfferID, miLocationID;

    private boolean isEngageLimitOver = false;
    private String msEngageLimitMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityOfferDetailsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moOfferDetailsViewModel = new ViewModelProvider(this).get(OfferDetailsViewModel.class);
        moOfferDetailsViewModel.fetchOfferDetailsStatus.observe(this, fetchOfferDetailsObserver);

        setToolbar();
        moBinding.llWinCoupon.setOnClickListener(this);

        if (getIntent() != null) {
            miOfferID = getIntent().getLongExtra(Constants.IntentKey.OFFER_ID, 0);
            miLocationID = getIntent().getLongExtra(Constants.IntentKey.LOCATION_ID, 0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getOfferDetails();
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
        loTvToolbarTitle.setVisibility(View.GONE);
    }

    private void getOfferDetails() {
        showProgressDialog();
        moOfferDetailsViewModel.fetchOfferDetails(getContext(), "", miOfferID, miLocationID);
    }

    Observer<OfferDetailsResponse> fetchOfferDetailsObserver = new Observer<OfferDetailsResponse>() {
        @Override
        public void onChanged(OfferDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                msEngageLimitMsg = loJsonObject.getEngageLimitOverMessage();
                isEngageLimitOver = loJsonObject.isAdEngageLimitOver();
                setUpOfferDetailsView(loJsonObject.getOffer());
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }

        private void setUpOfferDetailsView(Ad offer) {
            if (offer != null) {
                moOffer = offer;
                if (offer.getBannerUrl() != null) {
                    Common.loadImage(moBinding.ivBanner, offer.getBannerUrl(), null, ActivityCompat.getDrawable(getContext(), R.drawable.iv_place_holder));
                }
                if (offer.getLogoUrl() != null) {
                    Common.loadImage(moBinding.ivLogo, offer.getLogoUrl(), null, null);
//                    Picasso.get()
//                            .load(offer.getLogoUrl())
//                            .fit()
//                            .into(moBinding.ivLogo);
                }
                moBinding.tvOfferName.setText(offer.getAdName());
                setOfferRewards(offer);

                moBinding.tvTitle.setText(offer.getDesc1());
                moBinding.tvDesc2.setText(offer.getDesc2());
                moBinding.tvDesc3.setText(offer.getDesc3());
                moBinding.tvDesc4.setText(offer.getDesc4());

                if (offer.getEngagedFlag() == true) {
                    moBinding.btnWinCoupon.setText(Common.getDynamicText(getContext(), "action_already_engaged"));
                    moBinding.llWinCoupon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));
                    moBinding.btnWinCoupon.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    moBinding.btnWinCoupon.setClickable(false);
                } else {
                    moBinding.btnWinCoupon.setText(Common.getDynamicText(getContext(), "win_coupon"));
                    if (offer.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                        moBinding.llRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
                        moBinding.llWinCoupon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                    } else if (offer.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
                        moBinding.llRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_red));
                        moBinding.llWinCoupon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    }
                }
                moBinding.llRoot.setVisibility(View.VISIBLE);
            }
        }

        private void setOfferRewards(Ad offer) {

//            if (offer.getSecondReward() != 0) {
//                if (offer.getQuizReward() < offer.getNormalRewardAmount()) {
//                    moBinding.tvOfferRewards.setText("₹" + offer.getQuizReward() + " (C'Back)");
//                } else {
//                    moBinding.tvOfferRewards.setText("₹" + offer.getQuizReward() + " - ₹" + offer.getSecondReward() + " (C'Back)");
//                }
//            } else {
                moBinding.tvOfferRewards.setText("₹" + offer.getQuizReward() + " (Max Cashback)");
//            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llWinCoupon:
                btnWinCouponPressed();
                break;
            default:
                break;
        }
    }

    private void btnWinCouponPressed() {
        if (moOffer != null && !moOffer.getEngagedFlag()) {
            if (!isEngageLimitOver) {
                Intent loIntent = new Intent(moContext, QuizDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.IntentKey.OFFER_OBJECT, moOffer);
                loIntent.putExtras(bundle);
                moContext.startActivity(loIntent);
            } else {
                Common.showErrorDialog(getContext(), msEngageLimitMsg, true);
            }
        }
    }
}
