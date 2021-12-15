package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.dialog.BottomSheetDialog;
import com.cashback.models.Category;
import com.cashback.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class MainStoreAdapter extends RecyclerView.Adapter<MainStoreAdapter.MyViewHolder> {

    private static String TAG = OfferListAdapter.class.getSimpleName();
    private List<Category> moMainStoreList;
    private Context context;

    public int selectedItem;
    BottomSheetDialog moSearchFragment;
    MyViewHolder moHolder;

    public MainStoreAdapter(Context context, ArrayList<Category> foCategoryList, BottomSheetDialog foFragment) {
        this.context = context;
        this.moMainStoreList = foCategoryList;
        selectedItem = 0;
        moSearchFragment = foFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        moHolder = holder;
        holder.itemView.setTag(moMainStoreList.get(position));

        holder.tvMainStore.setText(moMainStoreList.get(position).getCategoryName());
        Common.loadImage(holder.ivMainStore, moMainStoreList.get(position).getFsCategoryImage(),
                context.getResources().getDrawable(R.drawable.ic_moby_small),
                null);

        if (selectedItem == position) {
            holder.rlMainStore.setBackground(ActivityCompat.getDrawable(context, R.drawable.border_for_card));
        } else {
            holder.rlMainStore.setBackground(null);
        }

        holder.rlMainStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                moSearchFragment.selectMainStore(moMainStoreList.get(position));
                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moMainStoreList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivMainStore;
        public TextView tvMainStore;
        public RelativeLayout rlMainStore;

        public MyViewHolder(View view) {
            super(view);
            ivMainStore = view.findViewById(R.id.ivMainStore);
            tvMainStore = view.findViewById(R.id.tvMainStore);
            rlMainStore = view.findViewById(R.id.rlMainStore);
        }
    }

    public void notifyList(ArrayList<Category> foCategoryList) {
        moMainStoreList = foCategoryList;
        notifyDataSetChanged();
    }
}

