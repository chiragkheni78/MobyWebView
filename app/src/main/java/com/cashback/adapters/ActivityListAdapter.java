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
import com.cashback.activities.MessageActivity;
import com.cashback.activities.MyCouponsActivity;
import com.cashback.models.Activity;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

import static com.cashback.utils.Constants.IntentKey.ENGAGED_DATE;
import static com.cashback.utils.Constants.IntentKey.PIN_COLOR;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.DataObjectHolder> {

    private static String TAG = ActivityListAdapter.class.getSimpleName();
    private ArrayList<Activity> moActivityList;
    private Context moContext;


    public ActivityListAdapter(Context foContext, ArrayList<Activity> foActivityList) {
        moActivityList = foActivityList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvAdName, loTvQuizReward, loTvDate, loTvCashBackAmount, tvCouponCode, tvRegisterBill, tvExpireDay;
        LinearLayout loLlRoot;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvAdName = foView.findViewById(R.id.tvOfferName);
            loTvQuizReward = foView.findViewById(R.id.tvQuizReward);
            loTvDate = foView.findViewById(R.id.tvDate);
            loTvCashBackAmount = foView.findViewById(R.id.tvCashBackAmount);
            tvCouponCode = foView.findViewById(R.id.tvCouponCode);
            tvRegisterBill = foView.findViewById(R.id.tvRegisterBill);
            tvExpireDay = foView.findViewById(R.id.tvExpireDay);

            loLlRoot = foView.findViewById(R.id.llTimeline);
            foView.setOnClickListener(this);
            tvCouponCode.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            int liPosition = (int) foView.getTag();

            switch (foView.getId()) {
                case R.id.tvCouponCode:
                    ((MyCouponsActivity) moContext).openCouponDetails(liPosition);
                    break;
                default:
                    handleRegisterBill(liPosition);
                    break;
            }
        }

        private void handleRegisterBill(int fiPosition) {
            Activity loActivity = moActivityList.get(fiPosition);

            if (loActivity.isBillUploadEnable()) {
                if (loActivity.isBillUploaded()) {
                    Common.showErrorDialog(moContext, Common.getDynamicText(moContext, "bill_uploaded"), false);
                } else {
                    if ((!loActivity.isCouponUsed() && loActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue()))
                            || (loActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue()) && loActivity.isBlinkShopOnline())) {
                        Common.showErrorDialog(moContext, Common.getDynamicText(moContext, "upload_bill_restrict"), false);
                    } else {
                        //Open BillUpload Screen
//                        Intent loIntent = new Intent(moContext, BillUploadActivity.class);
//                        loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, loActivity.getActivityID());
//                        loIntent.putExtra(ENGAGED_DATE, loActivity.getQuizEngageDateTime());
//                        loIntent.putExtra(PIN_COLOR, loActivity.getPinColor());
//                        moContext.startActivity(loIntent);

                        ((MyCouponsActivity) moContext).openBillUpload(fiPosition);
                    }
                }
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
            setOfferLabel(foHolder.loTvCashBackAmount, loActivity);
            setQuizReward(foHolder.loTvQuizReward, loActivity);
            foHolder.loTvAdName.setText(loActivity.getAdName());
            foHolder.loTvDate.setText(loActivity.getQuizEngageDateTime());
            foHolder.tvExpireDay.setText(loActivity.getRemainDay());
            foHolder.tvCouponCode.setText(Common.getDynamicText(moContext, "view_coupon"));
            foHolder.tvRegisterBill.setText(Common.getDynamicText(moContext, "register_bill"));

            foHolder.tvCouponCode.setTag(fiPosition);
            foHolder.itemView.setTag(fiPosition);

            handleLogicalOperation(foHolder, loActivity);
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void handleLogicalOperation(DataObjectHolder foHolder, Activity foActivity) {
        if (foActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_green_black));
        } else {
            foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_red_black));
        }

        if (foActivity.getCouponCode() == null || foActivity.getCouponCode().isEmpty()) {
            foHolder.tvCouponCode.setVisibility(View.GONE);
            foHolder.tvRegisterBill.setVisibility(View.GONE);
            foHolder.tvRegisterBill.setPaintFlags(0);
            foHolder.tvExpireDay.setVisibility(View.GONE);
        } else {
            foHolder.tvCouponCode.setVisibility(View.VISIBLE);

            if (foActivity.isCouponUsed()) {
                foHolder.tvCouponCode.setPaintFlags(foHolder.tvCouponCode.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                foHolder.tvCouponCode.setEnabled(false);
                foHolder.tvCouponCode.setClickable(false);
                foHolder.tvExpireDay.setVisibility(View.GONE);
                foHolder.tvCouponCode.clearAnimation();
                if (!foActivity.isBillUploaded()) {
                    foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.white));
                    foHolder.tvRegisterBill.setPaintFlags(0);
                }
            } else {
                Common.blinkAnimation(foHolder.tvCouponCode);

                foHolder.tvCouponCode.setPaintFlags(0);
                foHolder.tvCouponCode.setEnabled(true);
                foHolder.tvCouponCode.setClickable(true);
                foHolder.tvExpireDay.setVisibility(View.VISIBLE);
                foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.twhite));

                if (foActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())
                        && !foActivity.isBlinkShopOnline()) {
                    foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.white));
                    foHolder.tvCouponCode.clearAnimation();
                }
            }

            if (foActivity.isBillUploadEnable()) {
                foHolder.tvRegisterBill.setVisibility(View.VISIBLE);
                foHolder.tvRegisterBill.setPaintFlags(0);
                if (foActivity.isBillUploaded()) {
                    foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.white));
                    foHolder.tvRegisterBill.setPaintFlags(foHolder.tvRegisterBill.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                foHolder.tvRegisterBill.setVisibility(View.GONE);
            }
            if (foActivity.isCouponExpired()) {
                foHolder.tvExpireDay.setText(Common.getDynamicText(moContext, "coupon_expired"));
                foHolder.tvExpireDay.setTextColor(moContext.getResources().getColor(R.color.colorPrimary));
            }

        }
    }

    private void setQuizReward(TextView foTvQuizReward, Activity foActivity) {
        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.colorPrimary);
        int rewardColor = (foActivity.isVirtualCash()) ? fiPrimaryColor : fiPrimaryColor;

        foTvQuizReward.setText(Common.getColorText("Quiz Won ", Color.BLACK));
        foTvQuizReward.append(Common.getColorText("Rs. " + foActivity.getQuizReward(), rewardColor));
    }

    private void setOfferLabel(TextView foTvCashbackAmount, Activity foActivity) {
        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.colorPrimary);

        if (!foActivity.getDiscountUpTo().isEmpty()) {
            foTvCashbackAmount.setText(Common.getColorText("Upto ", Color.WHITE));
            foTvCashbackAmount.append(Common.getColorText(foActivity.getDiscountUpTo(), fiPrimaryColor));
            foTvCashbackAmount.append(Common.getColorText(" Off", Color.WHITE));
        }

        if (!foActivity.getFlatCashBack().isEmpty()) {

            if (foTvCashbackAmount.getText().length() > 0) {
                foTvCashbackAmount.append(Common.getColorText(" + ", Color.WHITE));
                foTvCashbackAmount.append(Common.getColorText("Extra ", Color.WHITE));
            } else {
                foTvCashbackAmount.setText(Common.getColorText("Extra ", Color.WHITE));
            }
            foTvCashbackAmount.append(Common.getColorText(foActivity.getFlatCashBack(), fiPrimaryColor));
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
