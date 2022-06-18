package com.mobycashback.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobycashback.model.Category;
import com.mobycashback.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private static String TAG = CategoryAdapter.class.getSimpleName();
    private List<Category> moCategoryList;
    private Context context;
    Intent intent;
    MyViewHolder moHolder;
    private static ClickListener clickListener;

    public CategoryAdapter(Context context, ArrayList<Category> foCategoryList) {
        this.context = context;
        this.moCategoryList = foCategoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        String url = APIClient.CATEGORY_URL + moCategoryList.get(position).getCategoryId();

        moHolder = holder;
        holder.itemView.setTag(moCategoryList.get(position));
        holder.loTvName.setText(moCategoryList.get(position).getCategoryName());
        Common.loadImageBank(holder.ivCategory, APIClient.IMAGE_URL + moCategoryList.get(position).getFsCategoryImage(), null, null);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(position,v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView loTvName;
        public CardView container;
        public ImageView ivCategory;

        public MyViewHolder(View itemView) {
            super(itemView);
            loTvName = itemView.findViewById(R.id.tvCategory);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            container = itemView.findViewById(R.id.categoryContainer);
        }
    }

    public void addAll(ArrayList<Category> foCategoryList) {
//        this.moCategoryList.clear();
        this.moCategoryList.addAll(foCategoryList);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}
