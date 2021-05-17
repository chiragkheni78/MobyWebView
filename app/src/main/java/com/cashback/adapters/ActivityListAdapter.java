package com.cashback.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.activities.OfferDetailsActivity;
import com.cashback.models.Activity;
import com.cashback.models.Ad;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.DataObjectHolder> {

    private static String TAG = ActivityListAdapter.class.getSimpleName();
    private ArrayList<Activity> moActivityList;
    private Context moContext;


    public ActivityListAdapter(Context foContext, ArrayList<Activity> foActivityList) {
        moActivityList = foActivityList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvAdName, loTvQuizReward, loTvDate, loTvCashBackAmount, tvCouponCode, tvRegisterBill;

        ImageView loIvLogo;
        LinearLayout loLlRoot;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvAdName = foView.findViewById(R.id.tvAdName);
            loTvQuizReward = foView.findViewById(R.id.tvQuizReward);
            loTvDate = foView.findViewById(R.id.tvDate);
            loTvCashBackAmount = foView.findViewById(R.id.tvCashBackAmount);

            loLlRoot = foView.findViewById(R.id.llRoot);
            foView.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.btnAdDetails) {
//                int liPosition = (int) foView.getTag();
//                Intent loIntent = new Intent(moContext, OfferDetailsActivity.class);
//                loIntent.putExtra(Constants.IntentKey.OFFER_ID, moActivityList.get(liPosition).getAdID());
//                if (moActivityList.get(liPosition).getLocationList().size() > 0)
//                    loIntent.putExtra(Constants.IntentKey.LOCATION_ID, moActivityList.get(liPosition).getLocationList().get(0).getLocationID());
//                moContext.startActivity(loIntent);
            }
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            Activity loActivity = moActivityList.get(fiPosition);


        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void setQuizReward(TextView foTvQuizReward, Activity foActivity) {
        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.colorPrimary);
        int rewardColor = (foActivity.isVirtualCash())? fiPrimaryColor: fiPrimaryColor;

        foTvQuizReward.setText(Common.getColorText("Quiz Cash ", Color.BLACK));
        foTvQuizReward.append(Common.getColorText("Rs. "+ foActivity.getQuizReward(), rewardColor));
    }

    private void setOfferLabel(TextView foTvCashbackAmount, Activity foActivity) {
        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.colorPrimary);

        if (!foActivity.getDiscountUpTo().isEmpty()){
            foTvCashbackAmount.setText(Common.getColorText("Upto ", Color.WHITE));
            foTvCashbackAmount.append(Common.getColorText(foActivity.getDiscountUpTo(), fiPrimaryColor));
            foTvCashbackAmount.append(Common.getColorText(" Off", Color.WHITE));
        }

        if (!foActivity.getFlatCashBack().isEmpty()){

            if (foTvCashbackAmount.getText().length() > 0) {
                foTvCashbackAmount.append(Common.getColorText(" + ", Color.WHITE));
                foTvCashbackAmount.append(Common.getColorText("Extra ", Color.WHITE));
            } else {
                foTvCashbackAmount.setText(Common.getColorText("Extra ", Color.WHITE));
            }
            foTvCashbackAmount.append(Common.getColorText(foActivity.getFsCouponCode(), fiPrimaryColor));
            foTvCashbackAmount.append(Common.getColorText(" Cashback", Color.WHITE));
        }
        foTvCashbackAmount.setBackground(ActivityCompat.getDrawable(moContext, R.drawable.rect_black));
    }

    @Override
    public int getItemCount() {
        return moActivityList.size();
    }

    public void notifyList(ArrayList<Activity> foActivityList) {
        moActivityList = foActivityList;
        notifyDataSetChanged();
    }
}
