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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class ShareBannerAdapter extends SliderViewAdapter<ShareBannerAdapter.DataObjectHolder> {

    private Context context;
    //private String[] maAdvertList;
    private AdvertAdapter.OnItemClick advertisementListener;

    public ShareBannerAdapter(Context context, String[] foBannerList, AdvertAdapter.OnItemClick advertisementListener) {
        this.context = context;
        //this.maAdvertList = foBannerList;
        this.advertisementListener = advertisementListener;
    }

    private ArrayList<Advertisement> moAdvertList;
    public ShareBannerAdapter(Context context, ArrayList<Advertisement> foAdvertList, AdvertAdapter.OnItemClick advertisementListener) {
        this.context = context;
        this.moAdvertList = foAdvertList;
        this.advertisementListener = advertisementListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dod, null);
        return new DataObjectHolder(inflate);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder viewHolder, final int position) {

        Advertisement loAdvertisement = moAdvertList.get(position);
        String lsImageUrl = loAdvertisement.getImageUrl();

        Drawable loPlaceHolder = ActivityCompat.getDrawable(context, R.drawable.ic_share_banner);
        Drawable loError = ActivityCompat.getDrawable(context, R.drawable.ic_share_banner);

        RequestCreator loRequest = Picasso.get().load(lsImageUrl.replace("https", "http"));
        loRequest.into(viewHolder.ivBanner);

       // Common.loadImage(viewHolder.ivBanner, lsImageUrl, loError, loPlaceHolder);
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