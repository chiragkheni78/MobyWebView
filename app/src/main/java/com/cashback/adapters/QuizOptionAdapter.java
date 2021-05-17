package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.QuizOption;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

public class QuizOptionAdapter extends RecyclerView.Adapter<QuizOptionAdapter.DataObjectHolder> {

    private static String TAG = QuizOptionAdapter.class.getSimpleName();
    private ArrayList<QuizOption> moQuizOptionList;
    private Context moContext;

    private int selectedPosition = -1;

    public interface OnOptionSelectListener {
        void onOptionSelect(int imageData);
    }

    private OnOptionSelectListener onOptionSelectListener;

    public QuizOptionAdapter(Context foContext, ArrayList<QuizOption> foQuizOptionList, OnOptionSelectListener onOptionSelectListener) {
        moQuizOptionList = foQuizOptionList;
        moContext = foContext;
        this.onOptionSelectListener = onOptionSelectListener;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loTvOption;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvOption = foView.findViewById(R.id.tvOption);
            loTvOption.setOnClickListener(this);
            foView.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.tvOption) {
                int liPosition = (int) foView.getTag();

                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                selectedPosition = liPosition;
                notifyItemChanged(liPosition);

                if (moQuizOptionList != null && moQuizOptionList.size()>0){
                    onOptionSelectListener.onOptionSelect(moQuizOptionList.get(liPosition).getIndex());
                }
            }
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_option, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            QuizOption loQuizOption = moQuizOptionList.get(fiPosition);
            foHolder.loTvOption.setText(loQuizOption.getValue());
            foHolder.loTvOption.setTag(fiPosition);
            foHolder.itemView.setTag(fiPosition);

            if (selectedPosition == fiPosition) {
                foHolder.loTvOption.setSelected(true); //using selector drawable
                //foHolder.loTvOption.setTextColor(ContextCompat.getColor(holder.tvText.getContext(),R.color.white));
            } else {
                foHolder.loTvOption.setSelected(false);
                //foHolder.loTvOption.setTextColor(ContextCompat.getColor(holder.tvText.getContext(),R.color.black));
            }

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    @Override
    public int getItemCount() {
        return moQuizOptionList.size();
    }

}

