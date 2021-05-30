package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.fragments.OfferListFragment;
import com.cashback.models.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {

    private static String TAG = DeviceListAdapter.class.getSimpleName();
    private List<Device> moDeviceList;
    private Context context;

    public int selectedItem;

    MyViewHolder moHolder;

    public DeviceListAdapter(Context context, ArrayList<Device> foDeviceList) {
        this.context = context;
        this.moDeviceList = foDeviceList;
        selectedItem = 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        moHolder = holder;
        holder.itemView.setTag(moDeviceList.get(position));
        holder.loTvDeviceName.setText(moDeviceList.get(position).getDeviceName());

        holder.loTvDeviceName.setActivated(false);

        if (selectedItem == position) {
            holder.loTvDeviceName.setActivated(true);
        }

        holder.loTvDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
//                moSearchFragment.getAdsByDevice(moDeviceList.get(position).getDeviceId());

                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moDeviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView loTvDeviceName;

        public MyViewHolder(View view) {
            super(view);
            loTvDeviceName = (TextView) view.findViewById(R.id.rbDevices);
        }
    }

    public int getSelectedPosition() {
        return selectedItem;
    }

}
