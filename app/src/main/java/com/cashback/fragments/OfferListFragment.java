package com.cashback.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.AdvertisementActivity;
import com.cashback.activities.CouponDetailsActivity;
import com.cashback.activities.HomeActivity;
import com.cashback.activities.OfferDetailsActivity;
import com.cashback.adapters.AdvertAdapter;
import com.cashback.adapters.CategoryAdapter;
import com.cashback.adapters.DealsOfDayAdapter;
import com.cashback.adapters.OfferListAdapter;
import com.cashback.databinding.FragmentOfferListBinding;
import com.cashback.models.Ad;
import com.cashback.models.Advertisement;
import com.cashback.models.Category;
import com.cashback.models.OfferFilter;
import com.cashback.models.response.BypassQuizResponse;
import com.cashback.models.response.DealOfTheDayResponse;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.response.OfferFilterResponse;
import com.cashback.models.viewmodel.OfferListViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

import static com.cashback.AppGlobal.isDealBannerClosed;
import static com.cashback.AppGlobal.isSearchButtonBlink;
import static com.cashback.AppGlobal.moContext;
import static com.cashback.fragments.FragmentMyCoupons.REQUEST_COUPON_DETAILS;

@SuppressWarnings("All")
public class OfferListFragment extends BaseFragment implements View.OnClickListener, OfferListAdapter.OnAdItemClick {

    public OfferListFragment() {

    }

    FragmentOfferListBinding moBinding;
    OfferListViewModel moOfferListViewModel;

    OfferListAdapter moOfferListAdapter;
    CategoryAdapter moCategoryAdapter;

    ArrayList<Category> moCategories = new ArrayList<>();
    ArrayList<Ad> moOfferList = new ArrayList<>();
    public static int miCategoryId = -1;
    private long mlOfferID = 0, mlBannerID = 0;

    private int miCurrentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private LinearLayoutManager moLayoutManager;


    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = moLayoutManager.getChildCount();
            int totalItemCount = moLayoutManager.getItemCount();

            int firstVisibleItemPosition = moLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 10)
                        && firstVisibleItemPosition >= 0) {
                    miCurrentPage = miCurrentPage + 1;
                    fetchOffers();
                }
            }
        }
    };

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

    @Override
    public void onResume() {
        super.onResume();
        showDealOfTheDayImage();
    }

    private void showDealOfTheDayImage() {
        ArrayList<DealOfTheDayResponse> loDealList = AppGlobal.getDealOfTheDayResponse();
        if (loDealList != null && loDealList.size() > 0) {
            if (Common.stOfferShow && !AppGlobal.isDealBannerClosed) {
                moBinding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                moBinding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                moBinding.imageSlider.startAutoCycle();
                moBinding.imageSlider.setSliderAdapter(new DealsOfDayAdapter(getContext(), loDealList, new DealsOfDayAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(DealOfTheDayResponse foDealList) {
                        //showDealOfTheDayImage();

                        if (miCategoryId != foDealList.getCategory()) {
                            miCategoryId = foDealList.getCategory();
                            moBinding.etSearch.setText("");
                        }

                        miCategoryId = foDealList.getCategory();
                        mlBannerID = foDealList.getBannerId();
                        mlOfferID = foDealList.getAdId();
                        miCurrentPage = 1;
                        isLastPage = false;
                        if (moOfferList != null) moOfferList.clear();

                        Common.msOfferId = mlOfferID+"";
                        moOfferListAdapter.notifyFirstItem(mlOfferID);

                        moBinding.btnSearch.clearAnimation();

                        //update category selection
                        moCategoryAdapter.updateCategoryByID(miCategoryId);
                        int liPosition = moCategoryAdapter.getSelectedPosition();
                        moBinding.rvCategory.scrollToPosition(liPosition);

                        fetchOffers();
                    }
                }));

//                RequestCreator loRequest = Picasso.get().load(loDealList.getImage().replace("https", "http"));
//                loRequest.into(moBinding.imageDealOfTheDay);

                moBinding.rlBanner.setVisibility(View.VISIBLE);

                //Common.stOfferShow = false;
            }

            moBinding.imageMainClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppGlobal.isDealBannerClosed = true;
                    moBinding.rlBanner.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initializeContent() {
        initViewModel();

        moBinding.btnSearch.setOnClickListener(this);
        moBinding.floatingActionSearch.setOnClickListener(this);
        //moBinding.rvOfferList.addOnScrollListener(recyclerViewOnScrollListener);

        setCategoryView();

        moLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        moBinding.rvOfferList.setLayoutManager(moLayoutManager);
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(moBinding.rvOfferList.getContext(), moLayoutManager.getOrientation());
        moBinding.rvOfferList.addItemDecoration(dividerItemDecoration);*/
        moOfferListAdapter = new OfferListAdapter(getActivity(), moOfferList, this);
        moBinding.rvOfferList.setAdapter(moOfferListAdapter);

        moBinding.etSearch.setOnFocusChangeListener(Common.getFocusChangeListener(getActivity()));

        moBinding.nestedOfferList.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!isLoading && !isLastPage) {
                        /*if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 10)
                                && firstVisibleItemPosition >= 0) {*/
                        miCurrentPage = miCurrentPage + 1;
                        fetchOffers();
                        /*}*/
                    }
                }
            }
        });

        if (getArguments() != null) {
            miCategoryId = getArguments().getInt(Constants.IntentKey.CATEGORY_ID);
            mlOfferID = getArguments().getLong(Constants.IntentKey.OFFER_ID);
            long llLocationId = getArguments().getLong(Constants.IntentKey.LOCATION_ID);
            mlBannerID = getArguments().getLong(Constants.IntentKey.BANNER_ID);

            if (miCategoryId > 0 && moCategoryAdapter != null) {
                // setSelection
                moCategoryAdapter.updateCategoryByID(miCategoryId);
                int liPosition = moCategoryAdapter.getSelectedPosition();
                moBinding.rvCategory.scrollToPosition(liPosition);
            }
            fetchOffers();


            if (mlOfferID > 0 && llLocationId > 0) {
                Intent loIntent = new Intent(getActivity(), OfferDetailsActivity.class);
                loIntent.putExtra(Constants.IntentKey.OFFER_ID, mlOfferID);
                loIntent.putExtra(Constants.IntentKey.LOCATION_ID, llLocationId);
                startActivity(loIntent);
            }
        } else fetchOffers();
    }

    private void setCategoryView() {
        moCategories = AppGlobal.getCategories();
        if (moCategories != null && moCategories.size() > 0) {
            final LinearLayoutManager loLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            moBinding.rvCategory.setLayoutManager(loLayoutManager1);
            moCategoryAdapter = new CategoryAdapter(getActivity(), moCategories, OfferListFragment.this);
            moBinding.rvCategory.setAdapter(moCategoryAdapter);

            moBinding.llSearchCategory.setVisibility(View.VISIBLE);

            miCategoryId = moCategories.get(0).getCategoryId();
            moBinding.rvCategory.setVisibility(View.VISIBLE);
        }
    }

    private void initViewModel() {
        moOfferListViewModel = new ViewModelProvider(this).get(OfferListViewModel.class);
//        moOfferListViewModel.fetchCategoryStatus.observe(getActivity(), fetchCategoryObserver);
        moOfferListViewModel.fetchOffersStatus.observe(getActivity(), fetchOffersObserver);
        moOfferListViewModel.bypassQuizStatus.observe(getActivity(), bypassQuizObserver);
    }

    private void fetchOffers() {
        if (miCurrentPage == 1)
            showProgressDialog();
        isLoading = true;

        String lsSearchText = (moBinding.etSearch.getText().length() > 0) ? moBinding.etSearch.getText().toString().trim() : "";
        OfferFilter loOfferFilter = new OfferFilter(lsSearchText, miCurrentPage, miCategoryId, mlBannerID);

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
                    if (loJsonObject.getOfferList().size() > 0) {
                        moBinding.rvOfferList.setVisibility(View.VISIBLE);
                        int scrollToPosition = moLayoutManager.getItemCount();
                        moOfferList.addAll(loJsonObject.getOfferList());
                        moOfferListAdapter.notifyList(moOfferList);

                        if (mlOfferID > 0 && miCurrentPage == 1) {
                            moOfferListAdapter.notifyFirstItem(mlOfferID);
                        }

//                        if (miCurrentPage > 1){
//                            moBinding.rvOfferList.smoothScrollToPosition(scrollToPosition);
//                        } else {
//                            moBinding.rvOfferList.smoothScrollToPosition(0);
//                        }

                        if (isSearchButtonBlink)
                            Common.blinkAnimation(moBinding.floatingActionSearch);

                    } else
                        isLastPage = true;
                }
            } else {
                moBinding.rvOfferList.setVisibility(View.GONE);
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            isLoading = false;
            dismissProgressDialog();
        }
    };

    private void fetchCategory() {
        showProgressDialog();
        moOfferListViewModel.fetchCategory(getActivity());
    }

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
            Common.hideKeyboard(getActivity());
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
        if (moCategories != null)
            miCategoryId = moCategories.get(0).getCategoryId();
        Common.hideKeyboard(getActivity());
        refreshData();
        moCategoryAdapter.updateCategoryByPosition(0);
    }

    public void getAdsByCategory(int fiCategoryId) {

        //showDealOfTheDayImage();

        if (miCategoryId != fiCategoryId) {
            miCategoryId = fiCategoryId;
            moBinding.etSearch.setText("");
        }
        refreshData();
    }

    private void refreshData() {
        if (moOfferList != null) moOfferList.clear();
        miCurrentPage = 1;
        mlBannerID = 0;
        mlOfferID = -1;
        moOfferListAdapter.notifyFirstItem(mlOfferID);
        isLastPage = false;
        moBinding.btnSearch.clearAnimation();
        fetchOffers();
    }

    long llTempAdId = -1;
    @Override
    public void submitQuiz(int position) {
        showProgressDialog();
        llTempAdId = moOfferList.get(position).getAdID();
        moOfferListViewModel.bypassQuiz(getActivity(), llTempAdId);
    }

    Observer<BypassQuizResponse> bypassQuizObserver = new Observer<BypassQuizResponse>() {
        @Override
        public void onChanged(BypassQuizResponse loJsonObject) {
            dismissProgressDialog();
            if (!loJsonObject.isError()) {

//                Common.msOfferId = "" + llTempAdId;
//                Intent intent = new Intent(getActivity(), HomeActivity.class);
//                intent.putExtra(Constants.IntentKey.IS_FROM, Constants.IntentKey.FROM_COUPON);
//                startActivity(intent);
//                getActivity().finishAffinity();
                openMyCoupons(loJsonObject.getActivityID());
            } else {
                Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
            }
        }
    };

    private void openMyCoupons(long flActivityID) {
        FragmentMyCoupons fragmentMyCoupons = new FragmentMyCoupons();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.IntentKey.FUNCTION, Constants.IntentKey.Action.BY_PASS_QUIZ);
        bundle.putLong(Constants.IntentKey.ACTIVITY_ID, flActivityID);
        fragmentMyCoupons.setArguments(bundle);
        Common.replaceFragment(getActivity(), fragmentMyCoupons, Constants.FragmentTag.TAG_MY_COUPON_LIST, false);
        ((HomeActivity)getActivity()).updateBottomMenu(3);
    }
}
