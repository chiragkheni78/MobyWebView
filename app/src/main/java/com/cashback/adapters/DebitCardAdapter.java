package com.cashback.adapters;

/**
 * Created by Vivek on 08-11-2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.DebitCard;

import java.util.List;


public class DebitCardAdapter extends RecyclerView.Adapter<DebitCardAdapter.MyViewHolder> {

    private Context context;
    private List<DebitCard> list;
    private CancelClick mClick;

    public interface CancelClick {
        void onClick(int position, String bank, String card_type, String card_product, int size);
    }

    public DebitCardAdapter(Context context, List<DebitCard> list, CancelClick mClick) {
        this.context = context;
        this.list = list;
        this.mClick = mClick;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_debit_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            try {
                holder.loTvBankName.setText(list.get(position).getBankName());
//                holder.loTvProductName.setText(list.get(position).getCardProduct());
                holder.loTvCardType.setText(list.get(position).getCardType());
            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.loIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClick.onClick(position,list.get(position).getBankName(),list.get(position).getCardType(),list.get(position).getCardProduct(),list.size());

                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {

        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView loTvBankName, loTvProductName, loTvCardType;
        public ImageView loIvDelete;

        public MyViewHolder(View view) {
            super(view);
            loTvBankName = (TextView) view.findViewById(R.id.tvBankName);
            loTvProductName = (TextView) view.findViewById(R.id.tvProductName);
            loTvCardType = (TextView) view.findViewById(R.id.tvCardType);
            loIvDelete = (ImageView) view.findViewById(R.id.ivDelete);

        }
    }


}