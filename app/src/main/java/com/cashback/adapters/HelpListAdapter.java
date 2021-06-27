package com.cashback.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.models.response.HelpResponse;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

@SuppressWarnings("All")
public class HelpListAdapter extends RecyclerView.Adapter<HelpListAdapter.DataObjectHolder> {

    private static String TAG = HelpListAdapter.class.getSimpleName();
    private ArrayList<HelpResponse.HelpModel> moActivityList;
    private Context moContext;

˚
    public HelpListAdapter(Context foContext, ArrayList<HelpResponse.HelpModel> foActivityList) {
        moActivityList = foActivityList;
        moContext = foContext;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView loTvAdName, loTvData;

        public DataObjectHolder(View foView) {
            super(foView);
            loTvAdName = foView.findViewById(R.id.tvHelpName);
            loTvData = foView.findViewById(R.id.tvHelpText);
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help, parent, false);
        DataObjectHolder loDataObjectHolder = new DataObjectHolder(loView);
        return loDataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder foHolder, final int fiPosition) {
        try {
            HelpResponse.HelpModel loActivity = moActivityList.get(fiPosition);

            foHolder.loTvAdName.setText(loActivity.getFsTitle());
            foHolder.loTvData.setText(Html.fromHtml(loActivity.getFsHtmlContent()));

        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    @Override
    public int getItemCount() {
        return moActivityList.size();
    }

}
