package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.SubCategory;

import java.util.List;

public class DialogCategoryAdapter extends RecyclerView.Adapter<DialogCategoryAdapter.ViewHolder> {

    private List<SubCategory> subCategoryList;
    private Context context;
    private OnItemClickCategory categoryListener;
    private int moCatPosition;
    //private OnItemClickExpiringSoon onItemClickExpiringSoon;
    //private OnItemClickBiggestServing onItemClickBiggestServing;
    private int lastSelectedPosition = -1;
    public int selectedItem;

    public DialogCategoryAdapter(List<SubCategory> subCategoryList, Context ctx, OnItemClickCategory categoryListener, int position) {
        this.subCategoryList = subCategoryList;
        context = ctx;
        selectedItem = 0;
        this.categoryListener = categoryListener;
        this.moCatPosition = position;
    }

    /*  public DialogCategoryAdapter(List<SubCategory> subCategoryList, Context ctx, OnItemClickExpiringSoon categoryListener) {
          this.subCategoryList = subCategoryList;
          context = ctx;
          this.onItemClickExpiringSoon = categoryListener;
      }

      public DialogCategoryAdapter(List<SubCategory> subCategoryList, Context ctx, OnItemClickBiggestServing categoryListener) {
          this.subCategoryList = subCategoryList;
          context = ctx;
          this.onItemClickBiggestServing = categoryListener;
      }
  */
    public interface OnItemClickCategory {
        void onItemClick(SubCategory category, int moCatPosition);
    }

    public interface OnItemClickExpiringSoon {
        void onItemClickExpiringSoon(SubCategory category);
    }

    public interface OnItemClickBiggestServing {
        void onItemClickBiggestServing(SubCategory category);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DialogCategoryAdapter.ViewHolder holder, int position) {
        SubCategory categoryItem = subCategoryList.get(position);
        holder.tvCategoryPrice.setText(categoryItem.getPrice());
        holder.tvCategoryName.setText("" + categoryItem.getCategory());
        holder.rdCategorySelect.setChecked(lastSelectedPosition == position);

        holder.rdCategorySelect.setOnClickListener(view -> {
            if (categoryListener != null) {
                categoryListener.onItemClick(categoryItem, moCatPosition);
            }
            lastSelectedPosition = position;
            notifyDataSetChanged();
           /* int previousItem = selectedItem;
            selectedItem = position;
           notifyItemChanged(previousItem);
            notifyItemChanged(position);*/
        });
    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCategoryPrice;
        public TextView tvCategoryName;
        public RadioButton rdCategorySelect;

        public ViewHolder(View view) {
            super(view);
            tvCategoryPrice = (TextView) view.findViewById(R.id.tvCategoryPrice);
            tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
            rdCategorySelect = (RadioButton) view.findViewById(R.id.rdCategorySelect);
            /*rdCategorySelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   *//* lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();*//*

                }
            });*/
        }
    }
}