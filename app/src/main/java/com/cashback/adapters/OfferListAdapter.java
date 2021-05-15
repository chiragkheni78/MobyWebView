package com.cashback.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.activities.OfferDetailsActivity;
import com.cashback.models.Ad;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.DataObjectHolder> {

    private static String TAG = OfferListAdapter.class.getSimpleName();
    private ArrayList<Ad> moOfferList;
    private Context moContext;


    public OfferListAdapter(Context foContext, ArrayList<Ad> foOfferList) {
        moOfferList = foOfferList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvAdName, loTvBrandName, loTvOffer;
        Button loBtnAdDetails;
        ImageView loIvLogo;
        LinearLayout loLlRoot;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvAdName = foView.findViewById(R.id.tvAdName);
            loTvBrandName = foView.findViewById(R.id.tvBrandName);
            loTvOffer = foView.findViewById(R.id.tvOfferRewards);
            loBtnAdDetails = foView.findViewById(R.id.btnAdDetails);
            loIvLogo = foView.findViewById(R.id.ivLogo);
            loLlRoot = foView.findViewById(R.id.llRoot);
            foView.setOnClickListener(this);
            loBtnAdDetails.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.btnAdDetails) {
                int liPosition = (int) foView.getTag();
                Intent loIntent = new Intent(moContext, OfferDetailsActivity.class);
                loIntent.putExtra(Constants.IntentKey.OFFER_ID, moOfferList.get(liPosition).getAdID());
                if (moOfferList.get(liPosition).getLocationList().size() > 0)
                    loIntent.putExtra(Constants.IntentKey.LOCATION_ID, moOfferList.get(liPosition).getLocationList().get(0).getLocationID());
                moContext.startActivity(loIntent);
            }
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_list, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            Ad loAdOffer = moOfferList.get(fiPosition);
            foHolder.loTvAdName.setText(loAdOffer.getAdName());
            foHolder.loTvBrandName.setText(loAdOffer.getProductName());
            setOfferLabel(foHolder.loTvOffer, loAdOffer);
            setLogo(foHolder.loIvLogo, loAdOffer);
            setButton(foHolder.loBtnAdDetails, loAdOffer);
            foHolder.loLlRoot.setTag(fiPosition);
            foHolder.loBtnAdDetails.setTag(fiPosition);

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void setButton(Button loBtnAdDetails, Ad foAdOffer) {
        if (foAdOffer.getEngagedFlag() == true) {
            loBtnAdDetails.setText(Common.getDynamicText(moContext, "engaged"));
            loBtnAdDetails.setBackgroundColor(moContext.getResources().getColor(R.color.grey));
        } else {
            String lsPinColor = foAdOffer.getPinColor();
            int liDrawable;
            if (lsPinColor.equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                liDrawable = R.drawable.btn_green;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "online_coupon"));
            } else if (lsPinColor.equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
                liDrawable = R.drawable.btn_primary;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "instore_coupon"));
            } else {
                liDrawable = R.drawable.btn_yello;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "view_details"));
            }
            loBtnAdDetails.setBackground(ContextCompat.getDrawable(moContext, liDrawable));
        }
    }

    private void setLogo(ImageView loIvLogo, Ad foAdOffer) {
        Common.loadImage(loIvLogo, foAdOffer.getLogoUrl(),
                moContext.getResources().getDrawable(R.drawable.ic_moby_small),
                moContext.getResources().getDrawable(R.drawable.ic_moby_small));
    }

    private void setOfferLabel(TextView foTvCashBackOffer, Ad foAdOffer) {

        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.white);

        if (!foAdOffer.getDiscountUpTo().isEmpty()) {
            foTvCashBackOffer.setText(Common.getColorText("Upto ", Color.WHITE));
            foTvCashBackOffer.append(Common.getColorText(foAdOffer.getDiscountUpTo(), fiPrimaryColor));
            foTvCashBackOffer.append(Common.getColorText(" Off", Color.WHITE));
        }

        if (!foAdOffer.getFlatCashBack().isEmpty()) {

            if (foTvCashBackOffer.getText().length() > 0) {
                foTvCashBackOffer.append(Common.getColorText(" + ", Color.WHITE));
                foTvCashBackOffer.append(Common.getColorText("Extra ", Color.WHITE));
            } else {
                foTvCashBackOffer.setText(Common.getColorText("Extra ", Color.WHITE));
            }
            foTvCashBackOffer.append(Common.getColorText(foAdOffer.getFlatCashBack(), fiPrimaryColor));
            foTvCashBackOffer.append(Common.getColorText(" Cashback", Color.WHITE));
        }
        foTvCashBackOffer.setBackground(ActivityCompat.getDrawable(moContext, R.drawable.rect_black));
        foTvCashBackOffer.setGravity(Gravity.CENTER);
    }

    @Override
    public int getItemCount() {
        return moOfferList.size();
    }

    public void notifyList(ArrayList<Ad> foOfferList) {
        moOfferList = foOfferList;
        notifyDataSetChanged();
    }
}
