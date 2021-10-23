package com.cashback.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.fragments.FragmentMyCoupons;
import com.cashback.models.Activity;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;


@SuppressWarnings("All")
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.DataObjectHolder> {

    private static String TAG = ActivityListAdapter.class.getSimpleName();
    private ArrayList<Activity> moActivityList;
    private Context moContext;
    private OnCouponItemClick onCouponItemClick;

    public interface OnCouponItemClick {
        void openCouponDetails(int position);

        void openBillUpload(int position);
    }


    public ActivityListAdapter(Context foContext, ArrayList<Activity> foActivityList, OnCouponItemClick onCouponItemClick) {
        moActivityList = foActivityList;
        moContext = foContext;
        this.onCouponItemClick = onCouponItemClick;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvAdName, loTvQuizReward, loTvDate, loTvCashBackAmount, tvCouponCode,
                tvRegisterBill, tvRequiredPayout, tvExpireDay, lblReward;
        LinearLayout loLlRoot, loLllRegisterBill;
        ImageView ivLogo;
        RelativeLayout cardItemActivity;

        public DataObjectHolder(View foView) {
            super(foView);
            lblReward = foView.findViewById(R.id.lblReward);
            tvRequiredPayout = foView.findViewById(R.id.tvRequiredPayout);
            cardItemActivity = foView.findViewById(R.id.cardItemActivityMain);
            loTvAdName = foView.findViewById(R.id.tvOfferName);
            loTvQuizReward = foView.findViewById(R.id.tvQuizReward);
            loTvDate = foView.findViewById(R.id.tvDate);
            loTvCashBackAmount = foView.findViewById(R.id.tvCashBackAmount);
            tvCouponCode = foView.findViewById(R.id.tvCouponCode);
            tvRegisterBill = foView.findViewById(R.id.tvRegisterBill);
            tvExpireDay = foView.findViewById(R.id.tvExpireDay);
            ivLogo = foView.findViewById(R.id.ivLogo);

            loLlRoot = foView.findViewById(R.id.llTimeline);
            loLllRegisterBill = foView.findViewById(R.id.llRegisterBill);
            foView.setOnClickListener(this);
            tvCouponCode.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            int liPosition = (int) foView.getTag();

            switch (foView.getId()) {
                case R.id.tvCouponCode:
                    if (onCouponItemClick != null) {
                        onCouponItemClick.openCouponDetails(liPosition);
                    }
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
                        if (onCouponItemClick != null) {
                            onCouponItemClick.openBillUpload(fiPosition);
                        }
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

            Common.loadImage(foHolder.ivLogo, loActivity.getFsAdLogo(),
                    moContext.getResources().getDrawable(R.drawable.ic_moby_small),
                    moContext.getResources().getDrawable(R.drawable.ic_moby_small));

            foHolder.tvCouponCode.setTag(fiPosition);
            foHolder.itemView.setTag(fiPosition);

            handleLogicalOperation(foHolder, loActivity);

            if (Common.msOfferId != null && !Common.msOfferId.isEmpty()) {
                if (Common.msOfferId.equalsIgnoreCase("" + loActivity.getAdID())) {
                    foHolder.loTvAdName.setTextColor(ActivityCompat.getColor(moContext, R.color.white));
                    foHolder.tvExpireDay.setTextColor(ActivityCompat.getColor(moContext, R.color.white));
                    foHolder.tvRequiredPayout.setTextColor(ActivityCompat.getColor(moContext, R.color.white));
                    foHolder.lblReward.setTextColor(ActivityCompat.getColor(moContext, R.color.white));

                    foHolder.tvCouponCode.setBackground(moContext.getResources().getDrawable(R.drawable.btn_blue));
                    foHolder.cardItemActivity.setBackgroundColor(ActivityCompat.getColor(moContext, R.color.colorPrimary));
                    onCouponItemClick.openCouponDetails(fiPosition);
                } else {
                    foHolder.loTvAdName.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
                    foHolder.tvExpireDay.setTextColor(ActivityCompat.getColor(moContext, R.color.red));
                    foHolder.tvRequiredPayout.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
                    foHolder.lblReward.setTextColor(ActivityCompat.getColor(moContext, R.color.black));

                    foHolder.cardItemActivity.setBackgroundColor(ActivityCompat.getColor(moContext, R.color.white));
                }
            } else {
                foHolder.loTvAdName.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
                foHolder.tvExpireDay.setTextColor(ActivityCompat.getColor(moContext, R.color.red));
                foHolder.tvRequiredPayout.setTextColor(ActivityCompat.getColor(moContext, R.color.black));
                foHolder.lblReward.setTextColor(ActivityCompat.getColor(moContext, R.color.black));

                foHolder.cardItemActivity.setBackgroundColor(ActivityCompat.getColor(moContext, R.color.white));
            }

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void handleLogicalOperation(DataObjectHolder foHolder, Activity foActivity) {
        if (foActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
            foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_grey));
        } else {
            foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_red_black));
        }

        if (foActivity.getCouponCode() == null || foActivity.getCouponCode().isEmpty()) {
            foHolder.tvCouponCode.setVisibility(View.GONE);
            foHolder.loLllRegisterBill.setVisibility(View.GONE);
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
            } else {
                Common.blinkAnimation(foHolder.tvCouponCode);

                foHolder.tvCouponCode.setPaintFlags(0);
                foHolder.tvCouponCode.setEnabled(true);
                foHolder.tvCouponCode.setClickable(true);
                foHolder.tvExpireDay.setVisibility(View.VISIBLE);
                foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.black));

                if (foActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())
                        && !foActivity.isBlinkShopOnline()) {
                    foHolder.tvCouponCode.clearAnimation();
                }
            }

            if (foActivity.isBillUploadEnable()) {
                if (foActivity.isCouponUsed()) {
                    foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_green_black));
                    if (foActivity.isBillUploaded()) {
                        foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.white));
                        foHolder.tvRegisterBill.setPaintFlags(foHolder.tvRegisterBill.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.white));
                        foHolder.tvRegisterBill.setPaintFlags(0);
                    }
                } else {
                    if (foActivity.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())
                            && !foActivity.isBlinkShopOnline()) {
                        foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.white));
                        foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_green_black));
                    } else {
                        foHolder.tvRegisterBill.setBackground(moContext.getResources().getDrawable(R.drawable.rect_grey));
                        foHolder.tvRegisterBill.setTextColor(moContext.getResources().getColor(R.color.black));
                    }
                }

                foHolder.loLllRegisterBill.setVisibility(View.VISIBLE);
            } else {
                foHolder.loLllRegisterBill.setVisibility(View.GONE);
            }

            if (foActivity.isCouponExpired()) {
                foHolder.tvExpireDay.setText(Common.getDynamicText(moContext, "coupon_expired"));
                foHolder.tvExpireDay.setTextColor(moContext.getResources().getColor(R.color.colorPrimary));
            }

        }
    }

    private void setQuizReward(TextView foTvQuizReward, Activity foActivity) {
        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.black);

        int rewardColor;
        if (Common.msOfferId != null && !Common.msOfferId.isEmpty()) {
            if (Common.msOfferId.equalsIgnoreCase("" + foActivity.getAdID())) {
                //foTvQuizReward.setText(Common.getColorText("Quiz Won ", Color.WHITE));
                rewardColor = ActivityCompat.getColor(moContext, R.color.white);
            } else {
                //foTvQuizReward.setText(Common.getColorText("Quiz Won ", Color.BLACK));
                rewardColor = (foActivity.isVirtualCash()) ? fiPrimaryColor : fiPrimaryColor;
            }
        } else {
            //foTvQuizReward.setText(Common.getColorText("Quiz Won ", Color.BLACK));
            rewardColor = (foActivity.isVirtualCash()) ? fiPrimaryColor : fiPrimaryColor;
        }

        //int rewardColor = (foActivity.isVirtualCash()) ? fiPrimaryColor : fiPrimaryColor;

        //foTvQuizReward.setText(Common.getColorText("Quiz Won ", Color.BLACK));
        foTvQuizReward.setText(Common.getColorText("Rs. " + foActivity.getQuizReward(), rewardColor));
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
                //foTvCashbackAmount.append(Common.getColorText("Extra ", Color.WHITE));
            } else {
                //foTvCashbackAmount.setText(Common.getColorText("Extra ", Color.WHITE));
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
