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
            foView.setOnClickListener(this);
        }

        @Override
        public void onClick(View foView) {
            if (foView.getId() == R.id.btnAdDetails) {
                int liPosition = (int) foView.getTag();

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
            foHolder.itemView.setTag(fiPosition);

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    @Override
    public int getItemCount() {
        return moQuizOptionList.size();
    }

}

