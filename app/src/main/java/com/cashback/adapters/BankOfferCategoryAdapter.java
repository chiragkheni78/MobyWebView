package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.BankOfferCategory;

import java.util.ArrayList;

public class BankOfferCategoryAdapter extends RecyclerView.Adapter<BankOfferCategoryAdapter.DataObjectHolder> {

    private static String TAG = BankOfferCategoryAdapter.class.getSimpleName();
    private ArrayList<BankOfferCategory> moBankOfferCategoryList;
    private Context moContext;

    public BankOfferCategoryAdapter(Context foContext, ArrayList<BankOfferCategory> foBankcardList) {
        moBankOfferCategoryList = foBankcardList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvBankOfferCategory;


        public DataObjectHolder(View foView) {
            super(foView);
            loTvBankOfferCategory = (TextView) foView.findViewById(R.id.rbCategory);
            loTvBankOfferCategory.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.rbCategory) {
                int liPosition = (int) foView.getTag();
            }
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_offer_category, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        BankOfferCategory loBankOfferCategory = moBankOfferCategoryList.get(fiPosition);
        foHolder.loTvBankOfferCategory.setText(loBankOfferCategory.getCategoryName());
        foHolder.loTvBankOfferCategory.setTag(fiPosition);

        if (loBankOfferCategory.isSelected()) {
            foHolder.loTvBankOfferCategory.setActivated(true);
        } else foHolder.loTvBankOfferCategory.setActivated(false);

        foHolder.loTvBankOfferCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loBankOfferCategory.setSelected(!loBankOfferCategory.isSelected());
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return moBankOfferCategoryList.size();
    }


    public String getSelectedCategory() {

        StringBuilder loSbBankCard = new StringBuilder();
        for (BankOfferCategory loCategory : moBankOfferCategoryList) {

            if (loCategory.isSelected()) {
                if (loSbBankCard.length() > 0) {
                    loSbBankCard.append(",");
                }
                loSbBankCard.append(loCategory.getCategoryId());
            }
        }
        return loSbBankCard.toString();
    }

}
