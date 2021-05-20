package com.cashback.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.adapters.ActivityListAdapter;
import com.cashback.databinding.ActivityMyCouponsBinding;
import com.cashback.models.Activity;
import com.cashback.models.response.ActivityListResponse;
import com.cashback.models.viewmodel.ActivityListViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

public class MyCouponsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MyCouponsActivity.class.getSimpleName();
    ActivityMyCouponsBinding moBinding;
    ActivityListViewModel moActivityListViewModel;

    ActivityListAdapter moActivityListAdapter;
    ArrayList<Activity> moActivityList = new ArrayList<>();
    private LinearLayoutManager moLayoutManager;

    private String[] maSortBy = new String[]{"", "coupon-expired-desc", "price-desc", "ad_name-asc"};
    private String[] maFilter = new String[]{"", "bill_uploaded", "bill_verified", "cashback_recieved"};

    private String msSortBy = "", msFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMyCouponsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        moActivityListViewModel = new ViewModelProvider(this).get(ActivityListViewModel.class);
        moActivityListViewModel.fetchActivityStatus.observe(MyCouponsActivity.this, fetchActivityObserver);
        setToolbar();
        moLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        moBinding.rvActivityList.setLayoutManager(moLayoutManager);
        moActivityListAdapter = new ActivityListAdapter(getContext(), moActivityList);
        moBinding.rvActivityList.setAdapter(moActivityListAdapter);
        setFilterView();
        getActivityList();

        if (getIntent() != null){
            long llActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
            if (llActivityId > 0){
                Intent loIntent = new Intent(getContext(), CouponDetailsActivity.class);
                loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, llActivityId);
                moContext.startActivity(loIntent);
            }
        }
    }

    private void setToolbar() {

        Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);

        ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(getString(R.string.my_coupon));
    }

    private void setFilterView() {
        String[] laSortBy = getResources().getStringArray(R.array.sort_by);
        ArrayAdapter<String> loSortAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, laSortBy);
        loSortAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        moBinding.spinSortBy.setAdapter(loSortAdapter);

        String[] laFilter = getResources().getStringArray(R.array.filter_by);
        ArrayAdapter<String> loFilterAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, laFilter);
        loFilterAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        moBinding.spinFilter.setAdapter(loFilterAdapter);

        moBinding.spinSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (!msSortBy.equalsIgnoreCase(maSortBy[pos])) {
                    msSortBy = maSortBy[pos];
                   // mbIsFilterCall = true;
                    getActivityList();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        });

        moBinding.spinFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item is selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (!msFilter.equalsIgnoreCase(maFilter[pos])) {
                    msFilter = maFilter[pos];
                    //mbIsFilterCall = true;
                    getActivityList();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        });
    }

    private void getActivityList() {
        showProgressDialog();
        moActivityListViewModel.fetchActivityList(getContext(), "", msSortBy, msFilter);
    }

    Observer<ActivityListResponse> fetchActivityObserver = new Observer<ActivityListResponse>() {
        @Override
        public void onChanged(ActivityListResponse loJsonObject) {
            dismissProgressDialog();
            if (!loJsonObject.isError()) {
                if (loJsonObject.getActivityList() != null) {
                    moActivityList = loJsonObject.getActivityList();
                    moActivityListAdapter.notifyList(moActivityList);
                    moBinding.llFilterSorting.setVisibility(View.VISIBLE);

                    if (loJsonObject.getActivityList().size() == 0){
                        moBinding.tvNoData.setVisibility(View.VISIBLE);
                        moBinding.rvActivityList.setVisibility(View.GONE);
                    } else {
                        moBinding.tvNoData.setVisibility(View.GONE);
                        moBinding.rvActivityList.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }
}
