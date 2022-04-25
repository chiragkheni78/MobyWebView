package com.cashback.adapters;

import android.content.Context;
import android.util.Log;
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

    public int selectedItem = -1;
    public int miMainStoreId = -1;
    BottomSheetDialog moSearchFragment;
    MyViewHolder moHolder;

    public MainStoreAdapter(Context context, ArrayList<Category> foCategoryList,
                            BottomSheetDialog foFragment,
                            int miMainStoreId) {
        this.context = context;
        this.moMainStoreList = foCategoryList;
        moSearchFragment = foFragment;
        this.miMainStoreId = miMainStoreId;
        /*if (miMainStoreId == -1) {
            selectedItem = 0;
        }*/
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_category, parent, false);
        return new MyViewHolder(itemView);
    }

    public void setBlurImage(boolean isBlur) {
        if (!isBlur) {
            for (int i = 0; i < moMainStoreList.size(); i++) {
                moMainStoreList.get(i).setBlurScale(0.6F);
            }
        } else {
            for (int i = 0; i < moMainStoreList.size(); i++) {
                moMainStoreList.get(i).setBlurScale(1);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        moHolder = holder;
        holder.itemView.setTag(moMainStoreList.get(position));

        holder.tvMainStore.setText(moMainStoreList.get(position).getCategoryName());
        Common.loadImage(holder.ivMainStore, moMainStoreList.get(position).getFsCategoryImage(),
                context.getResources().getDrawable(R.drawable.ic_moby_small),
                null);

        if (moMainStoreList.get(position).getBlurScale() != 0.0)
            holder.ivMainStore.setAlpha(moMainStoreList.get(position).getBlurScale());

        if (moMainStoreList.get(position).isSelected()) {
            holder.rlMainStore.setBackground(ActivityCompat.getDrawable(context, R.drawable.border_for_card));
        } else {
            holder.rlMainStore.setBackground(null);
        }

//        if (miMainStoreId == moMainStoreList.get(position).getCategoryId()) {
//            selectedItem = position;
//            holder.rlMainStore.setBackground(ActivityCompat.getDrawable(context, R.drawable.border_for_card));
//        } else if (selectedItem == position) {
//            holder.rlMainStore.setBackground(ActivityCompat.getDrawable(context, R.drawable.border_for_card));
//        } else
//            holder.rlMainStore.setBackground(null);

        holder.rlMainStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*miMainStoreId = -1;
                int previousItem = selectedItem;
                selectedItem = position;*/
                moSearchFragment.selectMainStore(position);
                // notifyItemChanged(previousItem);
                //notifyItemChanged(position);
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

