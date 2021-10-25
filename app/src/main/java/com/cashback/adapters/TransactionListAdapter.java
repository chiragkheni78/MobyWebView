package com.cashback.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.activities.CouponDetailsActivity;
import com.cashback.activities.HomeActivity;
import com.cashback.models.Transaction;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

@SuppressWarnings("All")
public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.DataObjectHolder> {

    private static String TAG = TransactionListAdapter.class.getSimpleName();
    private ArrayList<Transaction> moTransactionList;
    private Context moContext;


    public TransactionListAdapter(Context foContext, ArrayList<Transaction> foTransactionList) {
        moTransactionList = foTransactionList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvTitle, loTvQuizReward, loTvActivityDate, loTvCashBackUpto, loTvStatus, loTvType, loTvApproxValidated;
        LinearLayout loLlCashback, lvTransactionStatus;
        TextView tvStatusTracked, tvStatusValidated, tvStatusCashPaid, tvItemTransactionCouponText, tvStatusRedirect;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvTitle = foView.findViewById(R.id.tvTitle);
            loTvQuizReward = foView.findViewById(R.id.tvQuizReward);
            loTvActivityDate = foView.findViewById(R.id.tvActivityDate);
            loTvCashBackUpto = foView.findViewById(R.id.tvCashbackUpto);
            loTvStatus = foView.findViewById(R.id.tvStatus);
            loTvType = foView.findViewById(R.id.tvType);
            loTvApproxValidated = foView.findViewById(R.id.tvApproxValidated);
            loLlCashback = foView.findViewById(R.id.llCashback);

            lvTransactionStatus = foView.findViewById(R.id.linearItemTransactionStatus);
            tvStatusTracked = foView.findViewById(R.id.tvItemTransactionTracked);
            tvStatusValidated = foView.findViewById(R.id.tvItemTransactionValidated);
            tvStatusCashPaid = foView.findViewById(R.id.tvItemTransactionCashPaid);

            tvItemTransactionCouponText = foView.findViewById(R.id.tvItemTransactionCouponText);
            tvStatusRedirect = foView.findViewById(R.id.tvStatusRedirect);

            //loTvCashBackUpto.setOnClickListener(this);
            foView.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            int liPosition = (int) foView.getTag();

            switch (foView.getId()) {
                case R.id.tvCashbackUpto:
                    Transaction loUserTransaction = moTransactionList.get(liPosition);
                    long llAdId = loUserTransaction.getAdID();
                    Common.msOfferId = "" + llAdId;

                    Intent intent = new Intent(moContext, HomeActivity.class);
                    intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.Action.WALLET_SCREEN);
                    moContext.startActivity(intent);
                    ((Activity) moContext).finishAffinity();
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
        //foHolder.loTvQuizReward.setText("Rs. " + foTransaction.getQuizReward());



//        if (foTransaction.isVirtualCash()) {
//            foHolder.loTvQuizReward.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
//        } else {
//            foHolder.loTvQuizReward.setTextColor(ActivityCompat.getColor(moContext, R.color.colorPrimary));
//        }

        /*if (foTransaction.getSecondReward() > 0) {*/
        foHolder.loLlCashback.setVisibility(View.VISIBLE);

        if (foTransaction.getActivityType().equalsIgnoreCase("MyCashReferral")){
            foHolder.loTvCashBackUpto.setText(foTransaction.getCashbackLabel());

            if (foTransaction.getTransactionStatus() == 0) {
                foHolder.loTvApproxValidated.setVisibility(View.VISIBLE);
            }

        } else {
            if (foTransaction.getTransactionStatus() > 0) {
                foHolder.loTvCashBackUpto.setText("Cashback Rs. " + String.valueOf(foTransaction.getCashbackReward()));
            } else {
                foHolder.loTvCashBackUpto.setText("Cashback Rs. " + String.valueOf(foTransaction.getQuizReward()));
            }
        }

        foHolder.tvItemTransactionCouponText.setText(foTransaction.getCashbackMsg());

//        foHolder.loTvCashBackUpto.setText("₹ " + foTransaction.getSecondReward());
        /*} else {
            foHolder.loLlCashback.setVisibility(View.GONE);
        }*/

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


        // status
        if (foTransaction.getTransactionStatus() == (-1)) {

            foHolder.loLlCashback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loIntent = new Intent(moContext, CouponDetailsActivity.class);
                    loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, foTransaction.getActivityID());
                    moContext.startActivity(loIntent);
                }
            });

            //foHolder.tvItemTransactionCouponText.setText(moContext.getResources().getString(R.string.cashback_upto));
            foHolder.tvStatusRedirect.setText("("+moContext.getResources().getString(R.string.virtual_cash)+")");

            foHolder.tvStatusTracked.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));
            foHolder.tvStatusValidated.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));
            foHolder.tvStatusCashPaid.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));

            foHolder.tvStatusTracked.setTextColor(ContextCompat.getColor(moContext, R.color.black));
            foHolder.tvStatusValidated.setTextColor(ContextCompat.getColor(moContext, R.color.black));
            foHolder.tvStatusCashPaid.setTextColor(ContextCompat.getColor(moContext, R.color.black));



        } else if (foTransaction.getTransactionStatus() == 0) {

            //foHolder.tvItemTransactionCouponText.setText(moContext.getResources().getString(R.string.cashback_upto));
            foHolder.tvStatusRedirect.setText("("+moContext.getResources().getString(R.string.virtual_cash)+")");

            foHolder.tvStatusTracked.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));
            foHolder.tvStatusValidated.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));
            foHolder.tvStatusCashPaid.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));

            foHolder.tvStatusTracked.setTextColor(ContextCompat.getColor(moContext, R.color.white));
            foHolder.tvStatusValidated.setTextColor(ContextCompat.getColor(moContext, R.color.black));
            foHolder.tvStatusCashPaid.setTextColor(ContextCompat.getColor(moContext, R.color.black));

        } else if (foTransaction.getTransactionStatus() == 1) {

            //foHolder.tvItemTransactionCouponText.setText(moContext.getResources().getString(R.string.cashback_rs));
            foHolder.tvStatusRedirect.setText("("+moContext.getResources().getString(R.string.pending_cash)+")");

//            foHolder.tvStatusTracked.setPaintFlags(foHolder.tvStatusTracked.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            foHolder.tvStatusTracked.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));
            foHolder.tvStatusValidated.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));
            foHolder.tvStatusCashPaid.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));

            foHolder.tvStatusTracked.setTextColor(ContextCompat.getColor(moContext, R.color.white));
            foHolder.tvStatusValidated.setTextColor(ContextCompat.getColor(moContext, R.color.white));

            foHolder.tvStatusCashPaid.setText(foTransaction.getEstimatedPayDate());

        } else if (foTransaction.getTransactionStatus() == 3) {

            //foHolder.tvItemTransactionCouponText.setText(moContext.getResources().getString(R.string.cashback_rs));
            foHolder.tvStatusRedirect.setText("("+moContext.getResources().getString(R.string.pending_cash)+")");

//            foHolder.tvStatusTracked.setPaintFlags(foHolder.tvStatusTracked.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            foHolder.tvStatusTracked.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));
            foHolder.tvStatusValidated.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_red));
            foHolder.tvStatusCashPaid.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.bg_white_blue));

            foHolder.tvStatusValidated.setText(moContext.getResources().getString(R.string.rejected));
            foHolder.tvStatusCashPaid.setText("");

            foHolder.tvStatusTracked.setTextColor(ContextCompat.getColor(moContext, R.color.white));
            foHolder.tvStatusValidated.setTextColor(ContextCompat.getColor(moContext, R.color.white));

        } else {

            //foHolder.tvItemTransactionCouponText.setText(moContext.getResources().getString(R.string.cashback_rs));
            foHolder.tvStatusRedirect.setText("("+moContext.getResources().getString(R.string.cash_paid)+")");

//            foHolder.tvStatusTracked.setPaintFlags(foHolder.tvStatusTracked.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            foHolder.tvStatusValidated.setPaintFlags(foHolder.tvStatusValidated.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            foHolder.tvStatusTracked.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));
            foHolder.tvStatusValidated.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));
            foHolder.tvStatusCashPaid.setBackgroundDrawable(ContextCompat.getDrawable(moContext, R.drawable.btn_blue));

            foHolder.tvStatusTracked.setTextColor(ContextCompat.getColor(moContext, R.color.white));
            foHolder.tvStatusValidated.setTextColor(ContextCompat.getColor(moContext, R.color.white));
            foHolder.tvStatusCashPaid.setTextColor(ContextCompat.getColor(moContext, R.color.white));

            foHolder.tvStatusCashPaid.setText(foTransaction.getEstimatedPayDate());
        }
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
