package com.cashback.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.Coupon;
import com.cashback.utils.Common;

import java.util.ArrayList;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.DataObjectHolder> {

    private static String TAG = CouponAdapter.class.getSimpleName();
    private ArrayList<Coupon> moOffersList;
    private Context moContext;

    private static ClickListener clickListener;

    public CouponAdapter(Context foContext, ArrayList<Coupon> foOffersList) {
        moOffersList = foOffersList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvMessage, loTvOfferCode;
        LinearLayout llFade;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvMessage = (TextView) foView.findViewById(R.id.tvMessage);
            loTvOfferCode = (TextView) foView.findViewById(R.id.tvOfferCode);
            llFade = foView.findViewById(R.id.llFade);
            loTvOfferCode.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.tvOfferCode) {
                clickListener.onItemClick(getAdapterPosition(), foView);
            }
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_offer, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        Coupon loOffers = moOffersList.get(fiPosition);
        foHolder.loTvMessage.setText(loOffers.getDetails());
        foHolder.loTvOfferCode.setText(loOffers.getCouponName());
        foHolder.loTvMessage.setTag(fiPosition);
        if (loOffers.getCouponName().isEmpty())
            foHolder.loTvOfferCode.setText(Common.getDynamicText(moContext, "view_offer"));

        foHolder.llFade.setAnimation(null);
        foHolder.llFade.setBackgroundColor(ActivityCompat.getColor(moContext, R.color.white));
        if (selectionPosition == fiPosition) {
            // Log.d("TTT", "set anim..." + fiPosition);
            setAnimations(foHolder.llFade);
        }
    }

    ValueAnimator colorAnimation;

    private void setAnimations(LinearLayout foLlFade) {
        if (colorAnimation != null) {
            colorAnimation.cancel();
        }
        int colorFrom = moContext.getResources().getColor(R.color.white);
        int colorTo = moContext.getResources().getColor(R.color.red);
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
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

    @Override
    public int getItemCount() {
        return moOffersList.size();
    }

    public void notifyList(ArrayList<Coupon> foOffersList) {
        moOffersList = foOffersList;
        notifyDataSetChanged();
    }

    int selectionPosition = -1;

    public void notifyFirstItem(int selectionPosition) {
        //Log.d("TTT", "selectionPosition..." + selectionPosition);
        this.selectionPosition = selectionPosition;
        notifyDataSetChanged();
    }
}
