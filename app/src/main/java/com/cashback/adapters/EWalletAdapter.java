package com.cashback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.models.EWallet;

import java.util.List;

public class EWalletAdapter extends BaseAdapter {

    private Context moContext;
    private List<EWallet> moWalletList;

    public EWalletAdapter(Context foContext, List<EWallet> foWalletList) {
        moContext = foContext;
        moWalletList = foWalletList;
    }

    @Override
    public int getCount() {
        return moWalletList.size();
    }

    @Override
    public Object getItem(int fiPosition) {
        return moWalletList.get(fiPosition);
    }

    @Override
    public long getItemId(int fiPosition) {
        return fiPosition;
    }

    @Override
    public View getView(int fiPosition, View foView, ViewGroup viewGroup) {

        View loView = foView;
        LayoutInflater inflater = (LayoutInflater) moContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loView = inflater.inflate(R.layout.row_spn, null);

        if (moWalletList.size() > 0) {

            TextView moTvTitle = (TextView) loView.findViewById(R.id.tvRowTitle);
            moTvTitle.setText(moWalletList.get(fiPosition).getWalletName());
        }

        return loView;
    }

    public class ViewHolder {
        public TextView moTvTitle;

        public ViewHolder(View base) {
            moTvTitle = (TextView) base.findViewById(R.id.tvRowTitle);
        }
    }

    @Override
    public View getDropDownView(int fiPosition, View foView, ViewGroup parent) {
        View loView = foView;
        LayoutInflater inflater = (LayoutInflater) moContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loView = inflater.inflate(R.layout.row_spn_dropdown, null);

        if (moWalletList.size() > 0) {
            TextView moTvTitle = (TextView) loView.findViewById(R.id.tvRowTitle);
            moTvTitle.setText(moWalletList.get(fiPosition).getWalletName());
        }
        return loView;
    }
}
