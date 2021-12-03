package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.AdLocation;


import java.util.ArrayList;

public class AdLocationAdapter extends RecyclerView.Adapter<AdLocationAdapter.DataObjectHolder> {

    private static String TAG = AdLocationAdapter.class.getSimpleName();
    private ArrayList<AdLocation> moStoreLocationList;
    private Context moContext;

    private static ClickListener clickListener;

    public AdLocationAdapter(Context foContext, ArrayList<AdLocation> foStoreLocationList) {
        moStoreLocationList = foStoreLocationList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvLocation;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvLocation = (TextView) foView.findViewById(R.id.tvLocation);
            loTvLocation.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.tvLocation) {
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
        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_location, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        AdLocation loStoreLocation = moStoreLocationList.get(fiPosition);
        foHolder.loTvLocation.setText("Locate Nearby store " + (fiPosition + 1));
    }

    @Override
    public int getItemCount() {
        return moStoreLocationList.size();
    }

    public void notifyList(ArrayList<AdLocation> foStoreLocationList) {
        moStoreLocationList = foStoreLocationList;
        notifyDataSetChanged();
    }

}
