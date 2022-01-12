package com.cashback.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.cashback.R;
import com.cashback.models.Advertisement;
import com.cashback.models.response.DealOfTheDayResponse;
import com.cashback.utils.Common;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class DealsOfDayAdapter extends
        SliderViewAdapter<DealsOfDayAdapter.DataObjectHolder> {

    private Context context;
    private ArrayList<DealOfTheDayResponse> moAdvertList;
    private OnItemClick advertisementListener;

    public interface OnItemClick {
        void onItemClick(DealOfTheDayResponse advertisement, int position);
    }

    public DealsOfDayAdapter(Context context, ArrayList<DealOfTheDayResponse> foAdvertList, OnItemClick advertisementListener) {
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

        DealOfTheDayResponse loAdvertisement = moAdvertList.get(position);

        String lsImageUrl = loAdvertisement.getImage();

        // Drawable loPlaceHolder = ActivityCompat.getDrawable(context, R.drawable.iv_place_holder);
        //Drawable loError = ActivityCompat.getDrawable(context, R.drawable.iv_place_holder);
        //Log.d("TTT", "lsImageUrl..." + lsImageUrl);
        RequestCreator loRequest = Picasso.get().load(lsImageUrl.replace("https", "http"));
        loRequest.into(viewHolder.ivBanner);

        //Common.loadImage(viewHolder.ivBanner, lsImageUrl, loError, loPlaceHolder);

        viewHolder.ivBanner.setOnClickListener(view -> {
            if (advertisementListener != null) {
                advertisementListener.onItemClick(loAdvertisement, position);
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