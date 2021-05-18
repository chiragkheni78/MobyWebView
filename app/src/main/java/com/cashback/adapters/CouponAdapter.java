package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        public DataObjectHolder(View foView) {
            super(foView);
            loTvMessage = (TextView) foView.findViewById(R.id.tvMessage);
            loTvOfferCode = (TextView) foView.findViewById(R.id.tvOfferCode);
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
    }

    @Override
    public int getItemCount() {
        return moOffersList.size();
    }

    public void notifyList(ArrayList<Coupon> foOffersList) {
        moOffersList = foOffersList;
        notifyDataSetChanged();
    }

}
