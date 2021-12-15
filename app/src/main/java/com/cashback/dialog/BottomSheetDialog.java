package com.cashback.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.adapters.DialogCategoryAdapter;
import com.cashback.adapters.MainStoreAdapter;
import com.cashback.databinding.BottomSheetLayoutBinding;
import com.cashback.models.Category;
import com.cashback.models.SubCategory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener, DialogCategoryAdapter.OnItemClickCategory {

    private BottomSheetLayoutBinding moBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        moBinding = BottomSheetLayoutBinding.inflate(getLayoutInflater());
        initComponent();
        return getContentView(moBinding);
    }

    private void initComponent() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        moBinding.rvMainStore.setLayoutManager(layoutManager);

        moBinding.ivMainStoreArrow.setOnClickListener(this);
        moBinding.ivCategories.setOnClickListener(this);
        moBinding.ivExpiringSoon.setOnClickListener(this);
        moBinding.ivBiggestSarvings.setOnClickListener(this);

        setMainStoreAdapter();
        setCategoryAdapter();
        setExpiringSoonAdapter();
        setBiggestServingAdapter();
    }

    private void setMainStoreAdapter() {
        ArrayList<Category> moCategories = AppGlobal.getCategories();
        MainStoreAdapter mainStoreAdapter = new MainStoreAdapter(getActivity(), moCategories, this);
        moBinding.rvMainStore.setAdapter(mainStoreAdapter);
    }

    private void setBiggestServingAdapter() {
        DialogCategoryAdapter dialogCategoryAdapter = new DialogCategoryAdapter(biggestServing(),
                getActivity(), this, 3);
        moBinding.rvBiggestSarvings.setAdapter(dialogCategoryAdapter);
    }

    private void setExpiringSoonAdapter() {
        DialogCategoryAdapter dialogCategoryAdapter = new DialogCategoryAdapter(expiringSoon(),
                getActivity(), this, 2);
        moBinding.rvExpiringSoon.setAdapter(dialogCategoryAdapter);
    }

    private void setCategoryAdapter() {
        DialogCategoryAdapter dialogCategoryAdapter = new DialogCategoryAdapter(category(),
                getActivity(), this, 1);
        moBinding.rvCategories.setAdapter(dialogCategoryAdapter);
    }

    private ArrayList<SubCategory> category() {
        ArrayList<SubCategory> category = new ArrayList<>();
        category.add(new SubCategory("All categories", "2000"));
        category.add(new SubCategory("Fashion", "566"));
        category.add(new SubCategory("Shoes", "769"));
        category.add(new SubCategory("Appliance", "567"));
        category.add(new SubCategory("Electronic", "345"));
        category.add(new SubCategory("Gadgets", "800"));
        return category;
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    public void selectMainStore(Category category) {
        Log.d("TTT", "category..." + category.getCategoryName());
    }

    @Override
    public void onItemClick(SubCategory category, int moCatPosition) {
        Log.d("TTT", "category..." + category.getCategory());
        if (moCatPosition == 1) {
            Log.d("TTT", "category select..");
        } else if (moCatPosition == 2) {
            Log.d("TTT", "Expiring select..");
        } else {
            Log.d("TTT", "serving select..");
        }
    }
}