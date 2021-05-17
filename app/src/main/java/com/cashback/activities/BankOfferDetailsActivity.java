package com.cashback.activities;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.R;
import com.cashback.databinding.ActivityBankOfferDetailsBinding;
import com.cashback.models.Ad;
import com.cashback.models.AdLocation;
import com.cashback.models.viewmodel.OfferDetailsViewModel;
import com.cashback.models.response.OfferDetailsResponse;
import com.cashback.utils.Common;
import com.squareup.picasso.Picasso;

public class BankOfferDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = BankOfferDetailsActivity.class.getSimpleName();
    ActivityBankOfferDetailsBinding moBinding;
    OfferDetailsViewModel moOfferDetailsViewModel;

    private Ad moOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityBankOfferDetailsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moOfferDetailsViewModel = new ViewModelProvider(this).get(OfferDetailsViewModel.class);
        moOfferDetailsViewModel.fetchOfferDetailsStatus.observe(this, fetchOfferDetailsObserver);
        moBinding.btnLocate.setOnClickListener(this);

        getOfferDetails();
    }

    private void getOfferDetails() {
        showProgressDialog();
    }

    Observer<OfferDetailsResponse> fetchOfferDetailsObserver = new Observer<OfferDetailsResponse>() {
        @Override
        public void onChanged(OfferDetailsResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                moOffer = loJsonObject.getOffer();
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

                moBinding.tvBankCard.setText(offer.getAdName() + "   " + offer.getCardType() + "   " + offer.getCardName());
                moBinding.tvOfferRewards.setText(offer.getDescription());

                moBinding.tvOfferDescription.setText(offer.getDescription());
                moBinding.tvDescription.setText(offer.getDescription());
                moBinding.tvAddress.setText(offer.getDesc3());
                moBinding.tvDeal.setText(offer.getTermsCondition());
                moBinding.tvDeal.setOnClickListener(BankOfferDetailsActivity.this);
                if (!offer.getDescription().isEmpty())
                    moBinding.tvOfferDescription.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLocate:
                btnLocatePressed();
                break;
            case R.id.tvDeal:
                Common.openBrowser(getContext(), moOffer.getTermsCondition());
                break;
            default:
                break;
        }
    }

    private void btnLocatePressed() {

        if (moOffer != null && moOffer.getLocationList().size() > 0){
            AdLocation loLocation = moOffer.getLocationList().get(0);
            String lsUrl = "http://maps.google.com/maps?daddr=" + loLocation.getLatitude() + "," + loLocation.getLongitude();
            Common.openBrowser(getContext(), lsUrl);
        }
    }
}
