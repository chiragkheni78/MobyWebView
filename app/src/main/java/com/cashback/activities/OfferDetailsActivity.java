package com.cashback.activities;


import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.cashback.R;
import com.cashback.databinding.ActivityOfferDetailsBinding;
import com.cashback.models.Ad;
import com.cashback.models.OfferDetailsViewModel;
import com.cashback.models.response.OffersDetailsResponse;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.squareup.picasso.Picasso;

public class OfferDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = OfferDetailsActivity.class.getSimpleName();
    ActivityOfferDetailsBinding moBinding;
    OfferDetailsViewModel moOfferDetailsViewModel;

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
        moBinding.btnWinCoupon.setOnClickListener(this);

        getOfferDetails();
    }

    private void getOfferDetails() {
        showProgressDialog();
    }

    Observer<OffersDetailsResponse> fetchOfferDetailsObserver = new Observer<OffersDetailsResponse>() {
        @Override
        public void onChanged(OffersDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                setUpOfferDatailsView(loJsonObject.getOffer());
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }

        private void setUpOfferDatailsView(Ad offer) {
            if (offer != null) {
                if (!offer.getBannerUrl().isEmpty()) {
                    Picasso.get()
                            .load(offer.getBannerUrl())
                            .fit()
                            .into(moBinding.ivBanner);
                }
                if (!offer.getLogoUrl().isEmpty()) {
                    Picasso.get()
                            .load(offer.getLogoUrl())
                            .fit()
                            .into(moBinding.ivLogo);
                }
                moBinding.tvOfferName.setText(offer.getAdName());
                moBinding.tvOffer.setText("₹" + offer.getQuizReward() + " (C'Back)");

                moBinding.tvTitle.setText(offer.getDesc1());
                moBinding.tvDesc2.setText(offer.getDesc2());
                moBinding.tvDesc3.setText(offer.getDesc3());
                moBinding.tvDesc4.setText(offer.getDesc4());

                if (offer.getEngagedFlag() == 1) {
                    moBinding.btnWinCoupon.setText(Common.getDynamicText(getContext(), "action_already_engaged"));
                    moBinding.btnWinCoupon.setBackgroundColor(ActivityCompat.getColor(getContext(), R.color.grey));
                    moBinding.btnWinCoupon.setClickable(false);
                } else {
                    moBinding.btnWinCoupon.setText(Common.getDynamicText(getContext(), "win_coupon"));
                }

                if (offer.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                    moBinding.llRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
                } else if (offer.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
                    moBinding.llRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_red));
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWinCoupon:
                btnWinCouponPressed();
                break;
            default:
                break;
        }
    }

    private void btnWinCouponPressed() {

    }
}
