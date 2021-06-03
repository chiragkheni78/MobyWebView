package com.cashback.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.activities.BillUploadActivity;
import com.cashback.activities.CouponDetailsActivity;
import com.cashback.activities.MyCouponsActivity;
import com.cashback.models.Activity;
import com.cashback.models.Transaction;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

import static com.cashback.utils.Constants.IntentKey.ENGAGED_DATE;
import static com.cashback.utils.Constants.IntentKey.PIN_COLOR;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.DataObjectHolder> {

    private static String TAG = TransactionListAdapter.class.getSimpleName();
    private ArrayList<Transaction> moTransactionList;
    private Context moContext;


    public TransactionListAdapter(Context foContext, ArrayList<Transaction> foTransactionList) {
        moTransactionList = foTransactionList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvTitle, loTvQuizReward, loTvActivityDate, loTvCashBackUpto, loTvStatus, loTvType;
        LinearLayout loLlCashback;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvTitle = foView.findViewById(R.id.tvTitle);
            loTvQuizReward = foView.findViewById(R.id.tvQuizReward);
            loTvActivityDate = foView.findViewById(R.id.tvActivityDate);
            loTvCashBackUpto = foView.findViewById(R.id.tvCashbackUpto);
            loTvStatus = foView.findViewById(R.id.tvStatus);
            loTvType = foView.findViewById(R.id.tvType);
            loLlCashback = foView.findViewById(R.id.llCashback);


            loTvCashBackUpto.setOnClickListener(this);
            foView.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            int liPosition = (int) foView.getTag();

            switch (foView.getId()) {
                case R.id.tvCashbackUpto:
                    Transaction loUserTransaction = moTransactionList.get(liPosition);
                    long llAdId = loUserTransaction.getAdID();
                    //boolean lbIsGiftCard = (!loUserTransaction.getMobyCoupon().isEmpty()) ? true : false;

                    Intent intent = new Intent(moContext, MyCouponsActivity.class);
                    intent.putExtra(Constants.IntentKey.OFFER_ID, llAdId);
                    //intent.putExtra("foGiftCard", lbIsGiftCard);
                    moContext.startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_list, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            Transaction loTransaction = moTransactionList.get(fiPosition);

            foHolder.loTvCashBackUpto.setTag(fiPosition);
            foHolder.itemView.setTag(fiPosition);

            handleLogicalOperation(foHolder, loTransaction);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void handleLogicalOperation(DataObjectHolder foHolder, Transaction foTransaction) {

        foHolder.loTvTitle.setText(foTransaction.getAdName());
        foHolder.loTvActivityDate.setText(foTransaction.getQuizEngageDateTime());
        foHolder.loTvQuizReward.setText("Rs. " + foTransaction.getQuizReward());

        if (foTransaction.isVirtualCash()) {
            foHolder.loTvQuizReward.setTextColor(ActivityCompat.getColor(moContext, R.color.secondary));
        } else {
            foHolder.loTvQuizReward.setTextColor(ActivityCompat.getColor(moContext, R.color.colorPrimary));
        }

        if (foTransaction.getSecondReward() > 0) {
            foHolder.loLlCashback.setVisibility(View.VISIBLE);
            foHolder.loTvCashBackUpto.setText("₹ " + foTransaction.getSecondReward());
        } else {
            foHolder.loLlCashback.setVisibility(View.GONE);
        }

        String lsType = moContext.getString(R.string.instant_cash);
        String lsStatus = "";
        if (foTransaction.isVirtualCash()) {
            lsType = moContext.getString(R.string.virtual_cash);
            if (!foTransaction.isVerifiedCash()) {
                lsStatus = moContext.getString(R.string.status_pending);
                foHolder.loTvStatus.setTextColor(ActivityCompat.getColor(moContext, R.color.red));
            } else {
                lsStatus = moContext.getString(R.string.status_credited);
                foHolder.loTvStatus.setTextColor(ActivityCompat.getColor(moContext, R.color.green));
            }
            if (foTransaction.isCashback()){
                lsType = moContext.getString(R.string.cash_back);
            }
        } else {
            lsStatus = moContext.getString(R.string.status_credited);
            foHolder.loTvStatus.setTextColor(ActivityCompat.getColor(moContext, R.color.green));
        }
        foHolder.loTvStatus.setText(lsStatus);
        foHolder.loTvType.setText(lsType);
    }

    @Override
    public int getItemCount() {
        return moTransactionList.size();
    }

    public void notifyList(ArrayList<Transaction> foTransactionList) {
        moTransactionList = foTransactionList;
        notifyDataSetChanged();
    }
}