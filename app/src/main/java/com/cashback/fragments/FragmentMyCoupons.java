package com.cashback.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cashback.R;
import com.cashback.activities.BillUploadActivity;
import com.cashback.activities.CouponDetailsActivity;
import com.cashback.activities.HomeActivity;
import com.cashback.adapters.ActivityListAdapter;
import com.cashback.databinding.ActivityMyCouponsBinding;
import com.cashback.models.Activity;
import com.cashback.models.response.ActivityListResponse;
import com.cashback.models.viewmodel.ActivityListViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.LogV2;

import java.util.ArrayList;

import static com.cashback.activities.MyCouponsActivity.REQUEST_ACTIVITY_BILL_UPLOAD;
import static com.cashback.activities.MyCouponsActivity.REQUEST_COUPON_DETAILS;
import static com.cashback.utils.Constants.IntentKey.ENGAGED_DATE;
import static com.cashback.utils.Constants.IntentKey.PIN_COLOR;

@SuppressWarnings("All")
public class FragmentMyCoupons extends BaseFragment implements View.OnClickListener, ActivityListAdapter.OnCouponItemClick {

    public FragmentMyCoupons() {

    }

    private static final String TAG = FragmentMyCoupons.class.getSimpleName();
    ActivityMyCouponsBinding moBinding;
    ActivityListViewModel moActivityListViewModel;

    ActivityListAdapter moActivityListAdapter;
    ArrayList<Activity> moActivityList = new ArrayList<>();
    private LinearLayoutManager moLayoutManager;

    private String[] maSortBy = new String[]{"", "coupon-expired-desc", "price-desc", "ad_name-asc"};
    private String[] maFilter = new String[]{"", "bill_uploaded", "bill_verified", "cashback_recieved"};

    private String msSortBy = "", msFilter = "";
    private boolean isPendingBillUpload;
    private int miTotalVerifiedBill;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = ActivityMyCouponsBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeContent();
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityMyCouponsBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }*/

    private void initializeContent() {
        moActivityListViewModel = new ViewModelProvider(this).get(ActivityListViewModel.class);
        moActivityListViewModel.fetchActivityStatus.observe(getActivity(), fetchActivityObserver);
        setToolbar();
        moLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        moBinding.rvActivityList.setLayoutManager(moLayoutManager);
        moActivityListAdapter = new ActivityListAdapter(getContext(), moActivityList, this);
        moBinding.rvActivityList.setAdapter(moActivityListAdapter);
        setFilterView();
        getActivityList();

        if (getArguments() != null) {
            long llActivityId = getArguments().getLong(Constants.IntentKey.ACTIVITY_ID, 0);
            if (llActivityId > 0) {
                Intent loIntent = new Intent(getContext(), CouponDetailsActivity.class);
                loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, llActivityId);
                getActivity().startActivity(loIntent);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Common.stCouponId = "";
    }

    private void setToolbar() {

        /*Toolbar loToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(loToolbar);*/

        /*ImageButton loIbNavigation = loToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView loTvToolbarTitle = loToolbar.findViewById(R.id.tvToolbarTitle);
        loTvToolbarTitle.setText(getString(R.string.my_coupon));*/
    }

    private void setFilterView() {
        String[] laSortBy = getResources().getStringArray(R.array.sort_by);
        ArrayAdapter<String> loSortAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_spinner, laSortBy);
        loSortAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        moBinding.spinSortBy.setAdapter(loSortAdapter);

        String[] laFilter = getResources().getStringArray(R.array.filter_by);
        ArrayAdapter<String> loFilterAdapter = new ArrayAdapter<String>(getActivity(),
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

                    if (loJsonObject.getActivityList().size() == 0) {
                        moBinding.tvNoData.setVisibility(View.VISIBLE);
                        moBinding.rvActivityList.setVisibility(View.GONE);
                    } else {
                        moBinding.tvNoData.setVisibility(View.GONE);
                        moBinding.rvActivityList.setVisibility(View.VISIBLE);
                    }
                    isPendingBillUpload = loJsonObject.isPendingBillUpload();
                    miTotalVerifiedBill = loJsonObject.getTotalVerifiedBill();
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

    /*public void onBackPressed() {
        //super.onBackPressed();
        if (isPendingBillUpload) {
            showBackToCouponDialog();
        } else {
            backToHome(null);
        }
    }*/

    private void showBackToCouponDialog() {
        try {
            //if (!isFinishing()) {
                final Dialog moDialog = new Dialog(getActivity());
                moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                moDialog.setContentView(R.layout.dialog_my_coupon_back);

                TextView loTvTitle = moDialog.findViewById(R.id.tvTitle);
                TextView loTvMessage = moDialog.findViewById(R.id.tvMessage);
                TextView loTvError = moDialog.findViewById(R.id.tvErrorMessage);
                Button loBtnOfflineOffers = moDialog.findViewById(R.id.btnOfflineOffers);
                Button loBtnRedeemCoupon = moDialog.findViewById(R.id.btnRedeemCoupon);
                Button loBtnOnlineOffers = moDialog.findViewById(R.id.btnOnlineOffers);

                String lsTitle = Common.getDynamicText(getContext(), "back_from_timeline_title");
                String lsMessage = Common.getDynamicText(getContext(), "back_from_timeline_message");

                loTvTitle.setText(Html.fromHtml(lsTitle));
                loTvMessage.setText(Html.fromHtml(lsMessage));

                loBtnOfflineOffers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (miTotalVerifiedBill == 0){
                            loTvError.setVisibility(View.VISIBLE);
                            return;
                        }
                        moDialog.dismiss();
                        ((HomeActivity) getActivity()).openNavigationBarFragments(3);
                        /*backToHome(MAP_SCREEN);*/
                    }
                });

                loBtnRedeemCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moDialog.dismiss();
                    }
                });

                loBtnOnlineOffers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moDialog.dismiss();
//                        backToHome(null);

                        ((HomeActivity) getActivity()).openNavigationBarFragments(1);
                    }
                });

                if (moDialog.getWindow() != null) {
                    moDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    moDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    moDialog.getWindow().setGravity(Gravity.CENTER);
                    moDialog.setCancelable(false);
                    moDialog.show();
                }
            //}
        } catch (Exception e) {
            LogV2.logException(TAG, e);
        }
    }

    private void backToHome(String fsAction) {
        /*Intent loIntent = new Intent(getContext(), HomeActivity.class);
        loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (fsAction != null){
            loIntent.setAction(fsAction);
        }
        startActivity(loIntent);*/
        //getActivity().getSupportFragmentManager().popBackStack();
    }

    private int miPosition = -1;

    @Override
    public void openBillUpload(int fiPosition) {
        miPosition = fiPosition;
        Intent loIntent = new Intent(getActivity(), BillUploadActivity.class);
        loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, moActivityList.get(fiPosition).getActivityID());
        loIntent.putExtra(ENGAGED_DATE, moActivityList.get(fiPosition).getQuizEngageDateTime());
        loIntent.putExtra(PIN_COLOR, moActivityList.get(fiPosition).getPinColor());
        getActivity().startActivityForResult(loIntent, REQUEST_ACTIVITY_BILL_UPLOAD);
    }

    @Override
    public void openCouponDetails(int fiPosition) {
        miPosition = fiPosition;
        Intent loIntent = new Intent(getActivity(), CouponDetailsActivity.class);
        loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, moActivityList.get(fiPosition).getActivityID());
        getActivity().startActivityForResult(loIntent, REQUEST_COUPON_DETAILS);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ACTIVITY_BILL_UPLOAD) {
            if (resultCode == getActivity().RESULT_OK) { // Activity.RESULT_OK
                if (miPosition > -1) {
                    moActivityList.get(miPosition).setBillUploaded(true);
                    moActivityList.get(miPosition).setCouponUsed(true);
                    moActivityListAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_COUPON_DETAILS) {
            if (resultCode == getActivity().RESULT_OK) { // Activity.RESULT_OK
                if (miPosition > -1) {
                    String lsAction = data.getAction();
                    if (lsAction.equalsIgnoreCase(Constants.IntentKey.Action.OPEN_BILL_UPLOAD)) {
                        openBillUpload(miPosition);
                    } else if (lsAction.equalsIgnoreCase(Constants.IntentKey.Action.CLICK_SHOP_ONLINE)) {
                        moActivityList.get(miPosition).setBlinkShopOnline(false); //disable blink
                        moActivityListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}
