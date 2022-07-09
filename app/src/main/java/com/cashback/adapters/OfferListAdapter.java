package com.cashback.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
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

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.models.Ad;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.DataObjectHolder> {

    private static String TAG = OfferListAdapter.class.getSimpleName();
    private ArrayList<Ad> moOfferList;
    private Context moContext;

    private long miOfferID;
    private OnAdItemClick onAdItemClick;

    public OfferListAdapter(Context foContext, ArrayList<Ad> foOfferList, OnAdItemClick onAdItemClick) {
        moOfferList = foOfferList;
        moContext = foContext;
        this.onAdItemClick = onAdItemClick;
    }

    public interface OnAdItemClick {
        void handleOfferDetails(int position);
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvAdName, loTvBrandName, loTvOffer, loTvLeft;
        Button loBtnAdDetails;
        ImageView loIvLogo;
        LinearLayout loLlRoot, loLlFade;
        View view1;
        //LoaderChip loLoaderChip;
        //RelativeLayout rlTop;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvAdName = foView.findViewById(R.id.tvAdName);
            loTvBrandName = foView.findViewById(R.id.tvBrandName);
            loTvOffer = foView.findViewById(R.id.tvOfferRewards);
            loTvLeft = foView.findViewById(R.id.tvLeft);
            loBtnAdDetails = foView.findViewById(R.id.btnAdDetails);
            loIvLogo = foView.findViewById(R.id.ivLogo);
            loLlRoot = foView.findViewById(R.id.llRoot);
            loLlFade = foView.findViewById(R.id.llFade);
            view1 = foView.findViewById(R.id.view1);
            // rlTop = foView.findViewById(R.id.rlTop);
            // loLoaderChip = foView.findViewById(R.id.loaderChip);
            foView.setOnClickListener(this);
            loBtnAdDetails.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {

            int liPosition = (int) foView.getTag();
            if (onAdItemClick != null) {
                onAdItemClick.handleOfferDetails(liPosition);
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
            String lsOfferLeft = loAdOffer.getOfferLeft() + " Left";
            foHolder.loTvLeft.setText(lsOfferLeft);
            setOfferLabel(foHolder.loTvOffer, loAdOffer);
            setLogo(foHolder.loIvLogo, loAdOffer);
            setBackground(foHolder, loAdOffer);
            setButton(foHolder.loBtnAdDetails, loAdOffer);
            foHolder.itemView.setTag(fiPosition);
            foHolder.loBtnAdDetails.setTag(fiPosition);

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void setBackground(DataObjectHolder foHolder, Ad loAdOffer) {
        if (loAdOffer.getAdID() == miOfferID) {
//            foHolder.loLlRoot.setBackgroundColor(ActivityCompat.getColor(moContext, R.color.green));
//            foHolder.loBtnAdDetails.setBackground(ContextCompat.getDrawable(moContext, R.drawable.btn_white_coupon));
//            foHolder.loBtnAdDetails.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
//            foHolder.loTvAdName.setTextColor(ActivityCompat.getColor(moContext, R.color.white));
//            foHolder.loTvBrandName.setTextColor(ActivityCompat.getColor(moContext, R.color.white));
//
//            foHolder.loBtnAdDetails.setCompoundDrawablesWithIntrinsicBounds(null, null, Common.getColorDrawable(moContext, R.drawable.ic_next_12, R.color.black), null);
            setAnimations(foHolder.loLlFade);
        } else {

        }
        foHolder.loLlRoot.setBackgroundColor(ActivityCompat.getColor(moContext, R.color.white));
        foHolder.loBtnAdDetails.setBackground(ContextCompat.getDrawable(moContext, R.drawable.btn_green));
        foHolder.loBtnAdDetails.setTextColor(ActivityCompat.getColor(moContext, R.color.white));
        foHolder.loTvAdName.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
        foHolder.loTvBrandName.setTextColor(ActivityCompat.getColor(moContext, R.color.black));

        foHolder.loBtnAdDetails.setCompoundDrawablesWithIntrinsicBounds(null, null, Common.getColorDrawable(moContext, R.drawable.ic_next_12, R.color.white), null);
    }

    private void setAnimations(LinearLayout foLlFade) {
        int colorFrom = moContext.getResources().getColor(R.color.white);
        int colorTo = moContext.getResources().getColor(R.color.red);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1500); // milliseconds
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                foLlFade.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    private void setButton(Button loBtnAdDetails, Ad foAdOffer) {
        if (foAdOffer.getEngagedFlag()) {
            loBtnAdDetails.setText(Common.getDynamicText(moContext, "engaged"));
            loBtnAdDetails.setBackgroundColor(moContext.getResources().getColor(R.color.grey));
        } else {
            String lsPinColor = foAdOffer.getPinColor();
            int liDrawable;
            if (lsPinColor.equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                liDrawable = R.drawable.btn_green;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "online_coupon"));
                //loBtnAdDetails.setText(Common.getDynamicText(moContext, "select"));
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
        if (foAdOffer.getPinColor().equalsIgnoreCase(Constants.PinColor.YELLOW.getValue())) {
            Common.loadImageBank(loIvLogo, foAdOffer.getLogoUrl(), null, null);
        } else {
            Common.loadImage(loIvLogo, foAdOffer.getLogoUrl(), null, null);
        }

    }

    private void setOfferLabel(TextView foTvCashBackOffer, Ad foAdOffer) {

        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.colorPrimary);

        if (!foAdOffer.getDiscountUpTo().isEmpty()) {
            foTvCashBackOffer.setText(Common.getColorText("Upto ", Color.WHITE));
            // foTvCashBackOffer.append(Common.getColorSizeText(foAdOffer.getDiscountUpTo(), fiPrimaryColor));
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
            //  foTvCashBackOffer.append(Common.getColorSizeText(foAdOffer.getFlatCashBack(), fiPrimaryColor));
            foTvCashBackOffer.append(Common.getColorText(foAdOffer.getFlatCashBack(), fiPrimaryColor));

            if (foAdOffer.getProviderCashLabel().equals("yes")) {
                foTvCashBackOffer.append(Common.getColorText(" "+AppGlobal.msProviderCashLabel, Color.WHITE));
            } else {
                foTvCashBackOffer.append(Common.getColorText(" "+Constants.CASHBACK, Color.WHITE));
            }

            // foTvCashBackOffer.append(Common.getColorSpaceText(" Cashback", Color.WHITE));
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

    public void notifyFirstItem(long flOfferID) {
        miOfferID = flOfferID;
        notifyDataSetChanged();
    }
}
