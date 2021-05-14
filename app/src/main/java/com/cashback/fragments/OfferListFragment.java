package com.cashback.fragments;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.R;
import com.cashback.adapters.CategoryAdapter;
import com.cashback.adapters.OfferListAdapter;
import com.cashback.databinding.FragmentOfferListBinding;
import com.cashback.models.Ad;
import com.cashback.models.Category;
import com.cashback.models.OfferFilter;
import com.cashback.models.OfferListViewModel;
import com.cashback.models.response.OfferFilterResponse;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;

import java.util.ArrayList;

import static com.cashback.AppGlobal.isSearchButtonBlink;

public class OfferListFragment extends BaseFragment implements View.OnClickListener {

    FragmentOfferListBinding moBinding;
    OfferListViewModel moOfferListViewModel;

    OfferListAdapter moOfferListAdapter;
    CategoryAdapter moCategoryAdapter;

    ArrayList<Category> moCategories = new ArrayList<>();
    ArrayList<Ad> moOfferList = new ArrayList<>();
    public static int miCategoryId = -1;

    private int miCurrentPage = 1;
    public static int visibleLastIndex = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = FragmentOfferListBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeContent();
    }

    private void initializeContent() {
        moOfferListViewModel = new ViewModelProvider(this).get(OfferListViewModel.class);
        moOfferListViewModel.fetchCategoryStatus.observe(getActivity(), fetchCategoryObserver);
        moOfferListViewModel.fetchOffersStatus.observe(getActivity(), fetchOffersObserver);

        moBinding.btnSearch.setOnClickListener(this);
        moBinding.floatingActionSearch.setOnClickListener(this);
        moBinding.rvOfferList.addOnScrollListener(moOfferScrollListener);


        final LinearLayoutManager loLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        moBinding.rvCategory.setLayoutManager(loLayoutManager1);
        moCategoryAdapter = new CategoryAdapter(getActivity(), moCategories, OfferListFragment.this);
        moBinding.rvCategory.setAdapter(moCategoryAdapter);


        final LinearLayoutManager loLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        moBinding.rvOfferList.setLayoutManager(loLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(moBinding.rvOfferList.getContext(), loLayoutManager.getOrientation());
        moBinding.rvOfferList.addItemDecoration(dividerItemDecoration);
        moOfferListAdapter = new OfferListAdapter(getActivity(), moOfferList);
        moBinding.rvOfferList.setAdapter(moOfferListAdapter);

        fetchCategory();
    }

    private void fetchCategory() {
        showProgressDialog();
        moOfferListViewModel.fetchCategory(getActivity());
    }

    private void fetchOffers() {
        showProgressDialog();

        String lsSearchText = (moBinding.etSearch.getText().length() > 0) ? moBinding.etSearch.getText().toString().trim() : "";
        OfferFilter loOfferFilter = new OfferFilter(lsSearchText, miCurrentPage, miCategoryId);

        moOfferListViewModel.fetchOffers(getActivity(),
                "",
                0.0,
                0.0,
                false,
                false,
                Constants.OfferPage.OFFER_LIST.getValue(),
                loOfferFilter);
    }

    Observer<FetchOffersResponse> fetchOffersObserver = new Observer<FetchOffersResponse>() {
        @Override
        public void onChanged(FetchOffersResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getOfferList() != null) {
                    moOfferList.addAll(loJsonObject.getOfferList());
                    moOfferListAdapter.notifyList(moOfferList);
                    if (isSearchButtonBlink)
                        Common.blinkAnimation(moBinding.floatingActionSearch);
                }
            } else {
                Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    Observer<OfferFilterResponse> fetchCategoryObserver = new Observer<OfferFilterResponse>() {
        @Override
        public void onChanged(OfferFilterResponse loJsonObject) {
            dismissProgressDialog();
            if (!loJsonObject.isError()) {
                if (loJsonObject.getCategoryList() != null && loJsonObject.getCategoryList().size() > 0) {
                    moCategories = loJsonObject.getCategoryList();
                    moCategoryAdapter.notifyList(moCategories);

                    moBinding.llSearchCategory.setVisibility(View.VISIBLE);

                    miCategoryId = moCategories.get(0).getCategoryId();
                    moBinding.rvCategory.setVisibility(View.VISIBLE);

                    fetchOffers();
                }
            } else {
                Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                searchButtonPressed();
                break;
            case R.id.floating_action_search:
                floatingSearchPress();
                break;
        }
    }

    RecyclerView.OnScrollListener moOfferScrollListener = new RecyclerView.OnScrollListener() {

        private boolean isScrolledDown = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_SETTLING && isScrolledDown) {
                miCurrentPage = miCurrentPage + 1;
                fetchOffers();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            isScrolledDown = dy < 0;
        }
    };

    private void floatingSearchPress() {
        isSearchButtonBlink = false;
        moBinding.floatingActionSearch.clearAnimation();
        if (moBinding.rlSearch.getVisibility() == View.GONE) {
            moBinding.rlSearch.setVisibility(View.VISIBLE);
            moBinding.floatingActionSearch.setImageDrawable(ActivityCompat.getDrawable(getActivity(), R.drawable.ic_close_24));
            ImageViewCompat.setImageTintList(
                    moBinding.floatingActionSearch,
                    ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary))
            );
            moBinding.floatingActionSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            moBinding.rlSearch.setVisibility(View.GONE);
            moBinding.floatingActionSearch.setImageDrawable(ActivityCompat.getDrawable(getActivity(), R.drawable.ic_search_white_24dp));

            ImageViewCompat.setImageTintList(
                    moBinding.floatingActionSearch,
                    ColorStateList.valueOf(getResources().getColor(R.color.white))
            );
            moBinding.floatingActionSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            if (!moBinding.etSearch.getText().toString().trim().isEmpty()) {
                moBinding.etSearch.setText("");
                refreshData();
            }
        }
    }

    private void searchButtonPressed() {
        if (moCategories != null) miCategoryId = moCategories.get(0).getCategoryId();
        Common.hideKeyboard(getActivity());
        refreshData();
        moCategoryAdapter.updateCategoryByPosition(0);
    }

    public void getAdsByCategory(int fiCategoryId) {
        if (miCategoryId != fiCategoryId) {
            miCategoryId = fiCategoryId;
            moBinding.etSearch.setText("");
        }
        refreshData();
    }

    private void refreshData() {
        if (moOfferList != null) moOfferList.clear();
        miCurrentPage = 1;
        visibleLastIndex = 0;
        moBinding.btnSearch.clearAnimation();
        fetchOffers();
    }
}
