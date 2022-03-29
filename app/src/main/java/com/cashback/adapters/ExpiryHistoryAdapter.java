package com.cashback.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.Transaction;
import com.cashback.models.response.MyCashHistory;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

@SuppressWarnings("All")
public class ExpiryHistoryAdapter extends RecyclerView.Adapter<ExpiryHistoryAdapter.DataObjectHolder> {

    private static String TAG = ExpiryHistoryAdapter.class.getSimpleName();
    private ArrayList<MyCashHistory> moMyCashHistoryList;
    private Context moContext;


    public ExpiryHistoryAdapter(Context foContext, ArrayList<MyCashHistory> moMyCashHistoryList) {
        this.moMyCashHistoryList = moMyCashHistoryList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView loTvTitleExp, loTvAmount, loTvExpiry;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvTitleExp = foView.findViewById(R.id.tvTitleExp);
            loTvAmount = foView.findViewById(R.id.tvAmount);
            loTvExpiry = foView.findViewById(R.id.tvExpiry);
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expiry_history, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            MyCashHistory loTransaction = moMyCashHistoryList.get(fiPosition);

            foHolder.itemView.setTag(fiPosition);

            handleLogicalOperation(foHolder, loTransaction);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void handleLogicalOperation(DataObjectHolder foHolder, MyCashHistory foTransaction) {
        foHolder.loTvAmount.setText("Rs. " + foTransaction.getRemainAmount());
        foHolder.loTvExpiry.setText(foTransaction.getExpiryDate());
        foHolder.loTvTitleExp.setText(foTransaction.getTitle());

    }

    @Override
    public int getItemCount() {
        return moMyCashHistoryList.size();
    }

    public void notifyList(ArrayList<MyCashHistory> moMyCashHistoryList) {
        this.moMyCashHistoryList = moMyCashHistoryList;
        Log.d("TTT", "moMyCashHistoryList..." + moMyCashHistoryList.size());
        notifyDataSetChanged();
    }
}
