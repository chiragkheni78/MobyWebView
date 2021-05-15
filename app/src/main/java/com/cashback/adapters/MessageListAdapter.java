package com.cashback.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.Ad;
import com.cashback.models.Message;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.DataObjectHolder> {

    private static String TAG = MessageListAdapter.class.getSimpleName();
    private ArrayList<Message> moMessageList;
    private Context moContext;


    public MessageListAdapter(Context foContext, List<Message> foMessageList) {
        moMessageList.addAll(foMessageList);
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvTitle, loTvDate, loTvContent;
        com.rey.material.widget.TextView loTvReadMore;
        LinearLayout loLlRoot;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvTitle = foView.findViewById(R.id.tvAdName);
            loTvDate = foView.findViewById(R.id.tvBrandName);
            loTvContent = foView.findViewById(R.id.tvOfferRewards);
            loTvReadMore = foView.findViewById(R.id.tvReadMoreMessage);

            loLlRoot = foView.findViewById(R.id.llRoot);
            foView.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            int liPosition = (int) foView.getTag();
            //if (foView.getId() == R.id.tvReadMoreMessage) {
                openMessage(moMessageList.get(liPosition));
            //}
        }

        private void openMessage(Message message){
//            if (!message.isSeen()) {
//                ((NotificationsActivity) mContext).hitAPIForSeenMessage(position, messageList.get(position).getMessage_id());
//            } else {
//                Intent intent = new Intent(moContext,
//                        NotificationMoreDetailScreen.class);
//                intent.putExtra("Title", messageList.get(position).getTitle());
//                intent.putExtra("Desc", messageList.get(position).getContent());
//                mContext.startActivity(intent);
//            }
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_list, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            Message loMessage = moMessageList.get(fiPosition);
            foHolder.loTvTitle.setText(loMessage.getTitle());
            foHolder.loTvDate.setText(loMessage.getDateTime());
            foHolder.loTvContent.setText(loMessage.getContent());
            foHolder.loLlRoot.setTag(fiPosition);
            foHolder.loTvReadMore.setTag(fiPosition);

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void setButton(Button loBtnAdDetails, Ad foAdOffer) {
        if (foAdOffer.getEngagedFlag() == true) {
            loBtnAdDetails.setText(Common.getDynamicText(moContext, "engaged"));
            loBtnAdDetails.setBackgroundColor(moContext.getResources().getColor(R.color.grey));
        } else {
            String lsPinColor = foAdOffer.getPinColor();
            int liDrawable;
            if (lsPinColor.equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                liDrawable = R.drawable.btn_green;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "online_coupon"));
            } else if (lsPinColor.equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
                liDrawable = R.drawable.btn_primary;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "instore_coupon"));
            } else {
                liDrawable = R.drawable.btn_yello;
                loBtnAdDetails.setText(Common.getDynamicText(moContext, "view_details"));
            }
            loBtnAdDetails.setBackground(ContextCompat.getDrawable(moContext, liDrawable));
        }
    }

    private void setOfferLabel(TextView foTvCashBackOffer, Ad foAdOffer) {

        int fiPrimaryColor = ActivityCompat.getColor(moContext, R.color.white);

        if (!foAdOffer.getDiscountUpTo().isEmpty()){
            foTvCashBackOffer.setText(Common.getColorText("Upto ", Color.WHITE));
            foTvCashBackOffer.append(Common.getColorText(foAdOffer.getDiscountUpTo(), fiPrimaryColor));
            foTvCashBackOffer.append(Common.getColorText(" Off", Color.WHITE));
        }

        if (!foAdOffer.getFlatCashBack().isEmpty()){

            if (foTvCashBackOffer.getText().length() > 0) {
                foTvCashBackOffer.append(Common.getColorText(" + ", Color.WHITE));
                foTvCashBackOffer.append(Common.getColorText("Extra ", Color.WHITE));
            } else {
                foTvCashBackOffer.setText(Common.getColorText("Extra ", Color.WHITE));
            }
            foTvCashBackOffer.append(Common.getColorText(foAdOffer.getFlatCashBack(), fiPrimaryColor));
            foTvCashBackOffer.append(Common.getColorText(" Cashback", Color.WHITE));
        }
        foTvCashBackOffer.setBackground(ActivityCompat.getDrawable(moContext, R.drawable.rect_black));
        foTvCashBackOffer.setGravity(Gravity.CENTER);
    }

    @Override
    public int getItemCount() {
        return moMessageList.size();
    }

    public void notifyList(List<Message> foMessageList) {
        moMessageList.addAll(foMessageList);
        notifyDataSetChanged();
    }
}
