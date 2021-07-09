package com.cashback.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.cashback.R;
import com.cashback.activities.HomeActivity;
import com.cashback.models.Advertisement;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class AdvertAdapter extends
        SliderViewAdapter<AdvertAdapter.DataObjectHolder> {

    private Context context;
    private ArrayList<Advertisement> moAdvertList;
    private OnItemClick advertisementListener;

    public interface OnItemClick{
        void onItemClick(Advertisement advertisement);
    }

    public AdvertAdapter(Context context, ArrayList<Advertisement> foAdvertList, OnItemClick advertisementListener) {
        this.context = context;
        this.moAdvertList = foAdvertList;
        this.advertisementListener = advertisementListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advert, null);
        return new DataObjectHolder(inflate);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder viewHolder, final int position) {

        Advertisement loAdvertisement = moAdvertList.get(position);

        String lsImageUrl = loAdvertisement.getImageUrl();

        Drawable loPlaceHolder = ActivityCompat.getDrawable(context, R.drawable.iv_place_holder);
        Drawable loError = ActivityCompat.getDrawable(context, R.drawable.iv_place_holder);

        Common.loadImage(viewHolder.ivBanner, lsImageUrl, loError, loPlaceHolder);

        viewHolder.ivBanner.setOnClickListener(view -> {
            if (advertisementListener != null) {
                advertisementListener.onItemClick(loAdvertisement);
            }
        });
    }

    @Override
    public int getCount() {
        return moAdvertList.size();
    }

    class DataObjectHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView ivBanner;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            this.itemView = itemView;
        }
    }

}