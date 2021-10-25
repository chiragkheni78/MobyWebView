package com.cashback.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.cashback.R;
import com.cashback.models.Advertisement;
import com.cashback.utils.Common;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class ShareBannerAdapter extends SliderViewAdapter<ShareBannerAdapter.DataObjectHolder> {

    private Context context;
    private String[]  moAdvertList;

    public ShareBannerAdapter(Context context, String[] foBannerList) {
        this.context = context;
        this.moAdvertList = foBannerList;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advert, null);
        return new DataObjectHolder(inflate);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder viewHolder, final int position) {

        String lsImageUrl = moAdvertList[position];

        Drawable loPlaceHolder = ActivityCompat.getDrawable(context, R.drawable.ic_share_banner);
        Drawable loError = ActivityCompat.getDrawable(context, R.drawable.ic_share_banner);

        Common.loadImage(viewHolder.ivBanner, lsImageUrl, loError, loPlaceHolder);

    }

    @Override
    public int getCount() {
        return moAdvertList.length;
    }

    class DataObjectHolder extends ShareBannerAdapter.ViewHolder {

        View itemView;
        ImageView ivBanner;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            this.itemView = itemView;
        }
    }

}