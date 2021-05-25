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

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.activities.MessageActivity;
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
        Button btnAdDetails;
        CardView mainCard;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvTitle = foView.findViewById(R.id.tvTitle);
            loTvDate = foView.findViewById(R.id.tvDate);
            loTvContent = foView.findViewById(R.id.tvContent);
            loTvReadMore = foView.findViewById(R.id.tvReadMoreMessage);
            mainCard = foView.findViewById(R.id.cv_main);

            loLlRoot = foView.findViewById(R.id.llRoot);
            loTvReadMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            openMessage(moMessageList.get(getAdapterPosition()));
        }

        private void openMessage(Message message) {
            ((MessageActivity) moContext).openDetailsScreen(message.getTitle(), message.getContent(), "" + message.getMessageID());
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
            if (loMessage.isSeen()) {
                foHolder.mainCard.setCardBackgroundColor(moContext.getResources().getColor(R.color.white));
            } else {
                foHolder.mainCard.setCardBackgroundColor(moContext.getResources().getColor(R.color.colorPrimaryLight));
            }

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
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
