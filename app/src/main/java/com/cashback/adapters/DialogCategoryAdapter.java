package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.Category;

import java.util.List;

public class DialogCategoryAdapter extends RecyclerView.Adapter<DialogCategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private OnItemClickCategory categoryListener;
    private int moCatPosition;
    //private OnItemClickExpiringSoon onItemClickExpiringSoon;
    //private OnItemClickBiggestServing onItemClickBiggestServing;
    public int lastSelectedPosition = -1;
    public int selectedItem;
    public int miLastCategoryId;

    public DialogCategoryAdapter(List<Category> subCategoryList, Context ctx,
                                 OnItemClickCategory categoryListener, int position,
                                 int miLastCategoryId) {
        this.categoryList = subCategoryList;
        context = ctx;
        selectedItem = 0;
        this.categoryListener = categoryListener;
        this.moCatPosition = position;
        this.miLastCategoryId = miLastCategoryId;
    }

    public interface OnItemClickCategory {
        void onItemClick(Category category, int moCatPosition);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DialogCategoryAdapter.ViewHolder holder, int position) {
        Category categoryItem = categoryList.get(position);
        holder.tvCategoryName.setText("" + categoryItem.getCategoryName());
        //  holder.rdCategorySelect.setChecked(lastSelectedPosition == position);

        if (miLastCategoryId == categoryItem.getCategoryId()) {
            holder.rdCategorySelect.setChecked(true);
        } else {
            holder.rdCategorySelect.setChecked(false);
        }

        holder.rdCategorySelect.setOnClickListener(view -> {
            if (categoryListener != null) {
                categoryListener.onItemClick(categoryItem, moCatPosition);
            }
            miLastCategoryId = categoryItem.getCategoryId();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
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
        }
    }
}