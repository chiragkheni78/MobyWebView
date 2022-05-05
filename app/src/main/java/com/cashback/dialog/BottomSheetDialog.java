package com.cashback.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.DialogCategoryAdapter;
import com.cashback.adapters.MainStoreAdapter;
import com.cashback.databinding.BottomSheetLayoutBinding;
import com.cashback.models.Category;
import com.cashback.models.SubCategory;
import com.cashback.models.viewmodel.MapViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener, DialogCategoryAdapter.OnItemClickCategory {

    private boolean fbIsClickStore = false;
    private BottomSheetLayoutBinding moBinding;
    private OnApplyClickFilter onApplyClickFilter;
    private int moPosition;
    private int miLastCategoryId = -1, miLastMainStoreId = -1;
    private String msSearchText = "";
    private ArrayList<Category> moMainStoreList;
    private MainStoreAdapter mainStoreAdapter;
    private ArrayList<Category> moCategories;
    private DialogCategoryAdapter dialogCategoryAdapter;
    MapViewModel moMapViewModel;
    private Activity activity;
    // private FusedLocationProviderClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        moBinding = BottomSheetLayoutBinding.inflate(getLayoutInflater());
        initComponent();
        return getContentView(moBinding);
    }

    public BottomSheetDialog() {
    }

    public BottomSheetDialog(OnApplyClickFilter onApplyClickFilter, String searchText,
                             int categoryId, int mainStoreId, int adType, Activity activity) {
        this.onApplyClickFilter = onApplyClickFilter;
        this.msSearchText = searchText;
        this.miLastCategoryId = categoryId;
        this.miLastMainStoreId = mainStoreId;
        this.moPosition = adType;
        this.activity = activity;
    }

    public interface OnApplyClickFilter {
        void onFilterClick(int position, String searchText, int categoryId, int mainStoreIdn);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissionStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d("TTT", "dialog is pause...");
        // AppGlobal.fbIsBottomSheetIsOpen = false;
    }

    private void checkPermissionStatus() {
        if (!AppGlobal.fbIsGpsEnInApp && fbIsClickStore) {
            AppGlobal.fbIsBottomSheetIsOpen = true;
            if (!moMapViewModel.checkGPSEnabled(getActivity())) {
                moMapViewModel.enableGPS(getActivity());
            } else {
                moMapViewModel.getLastKnownLocation(getActivity());
            }
        }
    }

    private void initComponent() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        moBinding.rvMainStore.setLayoutManager(layoutManager);
        GridLayoutManager layoutManagerCategories = new GridLayoutManager(getActivity(), 2);
        moBinding.rvCategories.setLayoutManager(layoutManagerCategories);

        moMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        moMapViewModel.functionCallStatus.observe((LifecycleOwner) activity, functionCallObserver);
        //client = LocationServices.getFusedLocationProviderClient(getActivity());
        moBinding.btnApplyFilter.setOnClickListener(this);
        moBinding.tvOfflineStore.setOnClickListener(this);
        moBinding.tvOnlineStore.setOnClickListener(this);
        moBinding.tvAllStore.setOnClickListener(this);
        moBinding.ivMainStoreArrow.setOnClickListener(this);
        moBinding.ivCategories.setOnClickListener(this);
        moBinding.ivExpiringSoon.setOnClickListener(this);
        moBinding.ivBiggestSarvings.setOnClickListener(this);
        moBinding.btnSearch.setOnClickListener(this);

        if (!TextUtils.isEmpty(msSearchText)) {
            moBinding.etSearch.setText(msSearchText);
        }

        moBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    performFilterClick();
                    return true;
                }
                return false;
            }
        });

        clearBgForView();
        if (moPosition == 0) {
            moBinding.tvAllStore.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_color_primary_left));
            moBinding.tvAllStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        } else if (moPosition == 1) {
            moBinding.tvOnlineStore.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            moBinding.tvOnlineStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        } else {
            moBinding.tvOfflineStore.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_color_primary_right));
            moBinding.tvOfflineStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        setMainStoreAdapter();
        setCategoryAdapter();

        if (moPosition == 2) {
            /*moBinding.tvOnlineStore.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            moBinding.tvOnlineStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));*/
            moBinding.tvOnlineStore.performClick();
        }
        // setExpiringSoonAdapter();
        //  setBiggestServingAdapter();
    }

    Observer<String> functionCallObserver = fsFunctionName -> {
        switch (fsFunctionName) {
            case MapViewModel.FETCH_OFFERS:
                //Log.d("TTT", "call fetch offer bottom...");
                //moMapViewModel.getLastKnownLocation(getActivity());
                if (moMapViewModel.getCurrentLocation() != null) {
                    AppGlobal.setLocation(moMapViewModel.getCurrentLocation());
                }
                break;
        }
    };

    private void setMainStoreAdapter() {
        moMainStoreList = AppGlobal.getMoMainStore();
        if (moMainStoreList != null && moMainStoreList.size() > 0) {
           /* if (miLastMainStoreId == -1) {
                miLastMainStoreId = moCategories.get(0).getCategoryId();
            }*/
            mainStoreAdapter = new MainStoreAdapter(getActivity(), moMainStoreList,
                    this, miLastMainStoreId);
            moBinding.rvMainStore.setAdapter(mainStoreAdapter);
        }
    }


    private void setCategoryAdapter() {
        moCategories = AppGlobal.getCategories();
        if (moCategories != null && moCategories.size() > 0) {
            if (miLastCategoryId == -1) {
                miLastCategoryId = moCategories.get(0).getCategoryId();
            }
            dialogCategoryAdapter = new DialogCategoryAdapter(moCategories,
                    getActivity(), this, 1, miLastCategoryId);
            moBinding.rvCategories.setAdapter(dialogCategoryAdapter);
        }
    }
  /*  private void setBiggestServingAdapter() {
        DialogCategoryAdapter dialogCategoryAdapter = new DialogCategoryAdapter(biggestServing(),
                getActivity(), this, 3);
        moBinding.rvBiggestSarvings.setAdapter(dialogCategoryAdapter);
    }

    private void setExpiringSoonAdapter() {
        DialogCategoryAdapter dialogCategoryAdapter = new DialogCategoryAdapter(expiringSoon(),
                getActivity(), this, 2);
        moBinding.rvExpiringSoon.setAdapter(dialogCategoryAdapter);
    }*/

    private ArrayList<SubCategory> expiringSoon() {
        ArrayList<SubCategory> category = new ArrayList<>();
        category.add(new SubCategory("Brand Name", "12-Nov-2021"));
        category.add(new SubCategory("Brand Name", "22-Nov-2021"));
        category.add(new SubCategory("Brand Name", "27-Nov-2021"));
        return category;
    }

    private ArrayList<SubCategory> biggestServing() {
        ArrayList<SubCategory> category = new ArrayList<>();
        category.add(new SubCategory("Brand Name"));
        category.add(new SubCategory("Brand Name"));
        category.add(new SubCategory("Brand Name"));
        return category;
    }

    protected View getContentView(BottomSheetLayoutBinding binding) {
        this.moBinding = binding;
        return binding.getRoot();
    }

    private void clearBgForView() {
        moBinding.tvOnlineStore.setBackground(null);
        moBinding.tvOfflineStore.setBackground(null);
        moBinding.tvAllStore.setBackground(null);
        moBinding.tvOnlineStore.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        moBinding.tvOfflineStore.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        moBinding.tvAllStore.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnApplyFilter:
            case R.id.btnSearch:
               /* if(TextUtils.isEmpty(moBinding.etSearch.getText().toString().trim())){
                    Toast.makeText(activity,"Please enter search",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                performFilterClick();
                break;
            case R.id.tvAllStore:
                moPosition = 0;
                fbIsClickStore = true;
                checkPermissionStatus();
                enableDisableView(moBinding.rlCategories, true);
                clearBgForView();
                moBinding.tvAllStore.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_color_primary_left));
                moBinding.tvAllStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            case R.id.tvOfflineStore:
                moPosition = 2;
                fbIsClickStore = true;
                checkPermissionStatus();
                enableDisableView(moBinding.rlCategories, false);
                clearBgForView();
                moBinding.tvOfflineStore.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_color_primary_right));
                moBinding.tvOfflineStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            case R.id.tvOnlineStore:
                enableDisableView(moBinding.rlCategories, true);
                fbIsClickStore = false;
                moPosition = 1;
                clearBgForView();
                moBinding.tvOnlineStore.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                moBinding.tvOnlineStore.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            case R.id.ivMainStoreArrow:
                if (moBinding.rvMainStore.getVisibility() == View.VISIBLE) {
                    moBinding.ivMainStoreArrow.setRotation(90);
                    moBinding.rvMainStore.setVisibility(View.GONE);
                } else {
                    moBinding.ivMainStoreArrow.setRotation(270);
                    moBinding.rvMainStore.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ivCategories:
                if (moBinding.rvCategories.getVisibility() == View.VISIBLE) {
                    moBinding.ivCategories.setRotation(90);
                    moBinding.rvCategories.setVisibility(View.GONE);
                } else {
                    moBinding.ivCategories.setRotation(270);
                    moBinding.rvCategories.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ivBiggestSarvings:
                if (moBinding.rvBiggestSarvings.getVisibility() == View.VISIBLE) {
                    moBinding.ivBiggestSarvings.setRotation(90);
                    moBinding.rvBiggestSarvings.setVisibility(View.GONE);
                } else {
                    moBinding.ivBiggestSarvings.setRotation(270);
                    moBinding.rvBiggestSarvings.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ivExpiringSoon:
                if (moBinding.rvExpiringSoon.getVisibility() == View.VISIBLE) {
                    moBinding.ivExpiringSoon.setRotation(90);
                    moBinding.rvExpiringSoon.setVisibility(View.GONE);
                } else {
                    moBinding.ivExpiringSoon.setRotation(270);
                    moBinding.rvExpiringSoon.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void performFilterClick() {
        onApplyClickFilter.onFilterClick(
                moPosition,
                moBinding.etSearch.getText().toString().trim(),
                miLastCategoryId,
                miLastMainStoreId
        );
        AppGlobal.fbIsBottomSheetIsOpen = false;
        this.dismiss();
    }

    public void enableDisableView(View view, boolean enabled) {
        if (!enabled) {
            moBinding.llMainStore.setVisibility(View.GONE);
            // miLastMainStoreId = -1;
        } else {
            moBinding.llMainStore.setVisibility(View.VISIBLE);
        }
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
        // mainStoreAdapter.setBlurImage(enabled);
    }

    public void setMainStoreUnSelect() {
        moBinding.llMainStore.setVisibility(View.GONE);
        miLastMainStoreId = -1;
        for (int i = 0; i < moMainStoreList.size(); i++) {
            moMainStoreList.get(i).setSelected(false);
        }
        mainStoreAdapter.notifyDataSetChanged();
    }

    public void selectMainStore(int moPosition) {
        //Log.d("TTT", "main store..." + category.getCategoryId() + category.getCategoryName());
        for (int i = 0; i < moMainStoreList.size(); i++) {
            moMainStoreList.get(i).setSelected(false);
        }
        miLastMainStoreId = moMainStoreList.get(moPosition).getCategoryId();
        moMainStoreList.get(moPosition).setSelected(true);
        mainStoreAdapter.notifyDataSetChanged();
        miLastCategoryId = miLastMainStoreId;
        dialogCategoryAdapter.miLastCategoryId = miLastMainStoreId;
        dialogCategoryAdapter.notifyDataSetChanged();
       /* for (int i = 0; i < moCategories.size(); i++) {
            if (moCategories.get(i).getCategoryId() == miLastMainStoreId) {
                dialogCategoryAdapter.miLastCategoryId = miLastMainStoreId;
                miLastCategoryId = miLastMainStoreId;
                dialogCategoryAdapter.notifyDataSetChanged();
                break;
            }
        }*/

    }

    @Override
    public void onItemClick(Category category, int moCatPosition) {
        // Log.d("TTT", "category..." + category.getCategoryId() + category.getCategoryName());
        if (moCatPosition == 1) {
            miLastCategoryId = category.getCategoryId();
            Log.d("TTT", "category select..");

         /*   for (int i = 0; i < moMainStoreList.size(); i++) {
                if (moMainStoreList.get(i).getCategoryId() == miLastCategoryId) {
                    mainStoreAdapter.miMainStoreId = miLastCategoryId;
                    miLastMainStoreId = miLastCategoryId;
                    mainStoreAdapter.notifyDataSetChanged();
                    break;
                }
            }*/
            for (int i = 0; i < moMainStoreList.size(); i++) {
                moMainStoreList.get(i).setSelected(false);
            }
            for (int i = 0; i < moMainStoreList.size(); i++) {
                if (moMainStoreList.get(i).getCategoryId() == miLastCategoryId) {
                    mainStoreAdapter.miMainStoreId = miLastCategoryId;
                    miLastMainStoreId = miLastCategoryId;
                    moMainStoreList.get(i).setSelected(true);
                    mainStoreAdapter.notifyDataSetChanged();
                    break;
                }
            }
        } else if (moCatPosition == 2) {
            Log.d("TTT", "Expiring select..");
        } else {
            Log.d("TTT", "serving select..");
        }
    }
}