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
import com.cashback.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private static String TAG = OfferListAdapter.class.getSimpleName();
    private List<Category> moCategoryList;
    private Context context;

    public int selectedItem;
    OfferListFragment moSearchFragment;

    MyViewHolder moHolder;

    public CategoryAdapter(Context context, ArrayList<Category> foCategoryList, OfferListFragment foFragment) {
        this.context = context;
        this.moCategoryList = foCategoryList;
        selectedItem = 0;
        moSearchFragment = foFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        moHolder = holder;
        holder.itemView.setTag(moCategoryList.get(position));
        holder.loTvCategoryName.setText(moCategoryList.get(position).getCategoryName());

        holder.loLlCategory.setBackground(ActivityCompat.getDrawable(context, R.drawable.rect_white_black));
        holder.loTvCategoryName.setTextColor(ActivityCompat.getColor(context, R.color.black));

        if (selectedItem == position) {
            holder.loLlCategory.setBackground(ActivityCompat.getDrawable(context, R.drawable.btn_blue));
            holder.loTvCategoryName.setTextColor(ActivityCompat.getColor(context, R.color.white));
        }

        holder.loLlCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousItem = selectedItem;
                selectedItem = position;
                moSearchFragment.getAdsByCategory(moCategoryList.get(position).getCategoryId());

                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView loTvCategoryName;
        public LinearLayout loLlCategory;

        public MyViewHolder(View view) {
            super(view);
            loTvCategoryName = (TextView) view.findViewById(R.id.tvCategory);
            loLlCategory = (LinearLayout) view.findViewById(R.id.llCategory);
        }
    }

    public void updateCategoryByPosition(int fiPosition) {

        if (moHolder != null) {
            selectedItem = fiPosition;
            notifyDataSetChanged();
        }
    }

    public void updateCategoryByID(int fiCategoryID) {

        if (moCategoryList != null) {
            for (int liPosition = 0; liPosition < moCategoryList.size(); liPosition++) {
                Category loCategory = moCategoryList.get(liPosition);
                if (loCategory.getCategoryId() == fiCategoryID) {
                    selectedItem = liPosition;

                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    public void notifyList(ArrayList<Category> foCategoryList) {
        moCategoryList = foCategoryList;
        notifyDataSetChanged();
    }

}
