package com.cashback.fragments;

import static com.cashback.AppGlobal.isSearchButtonBlink;
import static com.cashback.models.viewmodel.MapViewModel.FETCH_OFFERS;
import static com.cashback.models.viewmodel.MapViewModel.LOAD_MAP_VIEW;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

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
import com.cashback.activities.HomeActivity;
import com.cashback.activities.OfferDetailsActivity;
import com.cashback.activities.QuizDetailsActivity;
import com.cashback.adapters.CategoryAdapter;
import com.cashback.adapters.DealsOfDayAdapter;
import com.cashback.adapters.OfferListAdapter;
import com.cashback.databinding.FragmentOfferListBinding;
import com.cashback.dialog.BottomSheetDialog;
import com.cashback.dialog.MessageDialog;
import com.cashback.models.Ad;
import com.cashback.models.AdLocation;
import com.cashback.models.Category;
import com.cashback.models.OfferFilter;
import com.cashback.models.response.BypassQuizResponse;
import com.cashback.models.response.DealOfTheDayResponse;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.response.OfferFilterResponse;
import com.cashback.models.viewmodel.MapViewModel;
import com.cashback.models.viewmodel.OfferListViewModel;
import com.cashback.utils.AdGydeEvents;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;

@SuppressWarnings("All")
public class OfferListFragment extends BaseFragment implements View.OnClickListener, OfferListAdapter.OnAdItemClick, BottomSheetDialog.OnApplyClickFilter {

    public OfferListFragment() {

    }

    private static final String TAG = OfferListFragment.class.getSimpleName();
    FragmentOfferListBinding moBinding;
    OfferListViewModel moOfferListViewModel;
    MapViewModel moMapViewModel;

    OfferListAdapter moOfferListAdapter;
    CategoryAdapter moCategoryAdapter;

    ArrayList<Category> moCategories = new ArrayList<>();
    ArrayList<Ad> moOfferList = new ArrayList<>();
    public static int miCategoryId = -1;
    public static int miLastCategoryId = -1;
    private long mlOfferID = 0, mlBannerID = 0;

    private boolean isFilterApply = false;
    private int miCurrentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private LinearLayoutManager moLayoutManager;
    private String msSearchText = "";
    private int miAdType = 1;
    private int miLastMainStoreId = -1;

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

    TextView.OnEditorActionListener moSerchListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchButtonPressed();
                return true;
            }
            return false;
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
                // moBinding.imageSlider.setIndicatorEnabled(false);
                // moBinding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                moBinding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                moBinding.imageSlider.startAutoCycle();
                moBinding.imageSlider.setSliderAdapter(new DealsOfDayAdapter(getContext(), loDealList, new DealsOfDayAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(DealOfTheDayResponse foDealList, int position) {

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

                        Common.msOfferId = mlOfferID + "";
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
            } else {
                moBinding.rlBanner.setVisibility(View.GONE);
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

    // private FirebaseAnalytics mFirebaseAnalytics;

    private void initializeContent() {
        initViewModel();
       /* mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent("Myevent", bundle);*/

        moBinding.btnSearch.setOnClickListener(this);
        moBinding.floatingActionSearch.setOnClickListener(this);
        moBinding.etSearch.setOnEditorActionListener(moSerchListener);
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

        moMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        moMapViewModel.functionCallStatus.observe(this, functionCallObserver);
    }

    private void fetchOffers() {
        if (!isFilterApply) {
            msSearchText = "";
        }
        if (miCurrentPage == 1)
            showProgressDialog();
        isLoading = true;

        //  String lsSearchText = (moBinding.etSearch.getText().length() > 0) ? moBinding.etSearch.getText().toString().trim() : "";
        OfferFilter loOfferFilter = new OfferFilter(msSearchText, miCurrentPage, miCategoryId, mlBannerID, miAdType);

        if (AppGlobal.getLocation() == null) {
            AppGlobal.setLocation(moMapViewModel.getCurrentLocation());
        }
        moOfferListViewModel.fetchOffers(getActivity(),
                "",
                AppGlobal.getLocation() == null ? 0.0 : AppGlobal.getLocation().getLatitude(),
                AppGlobal.getLocation() == null ? 0.0 : AppGlobal.getLocation().getLongitude(),
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
                        // moOfferList.get(0).setEngagedFlag(true);
                        moOfferListAdapter.notifyList(moOfferList);

                        if (mlOfferID > 0 && miCurrentPage == 1) {
                            moOfferListAdapter.notifyFirstItem(mlOfferID);
                            //  showDealOfTheDayImage();
                        }

                        if (miCurrentPage == 1 && AppGlobal.isNewUser) {
                            Bundle bundle = new Bundle();
                            bundle.putString("mobile", AppGlobal.getPhoneNumber());
                            FirebaseEvents.trigger(getActivity(), bundle, FirebaseEvents.OFFER_LOADED_PAGE);

                            AdGydeEvents.offerLoaded(getActivity());
                        }
                        //else {
//                            moBinding.rvOfferList.smoothScrollToPosition(0);
//                        }

                        if (isSearchButtonBlink)
                            Common.blinkAnimation(moBinding.floatingActionSearch);

                    } else {
                        isLastPage = true;
                    }
                }
            } else {
                moBinding.rvOfferList.setVisibility(View.GONE);
                if (isFilterApply) {
                    isFilterApply = false;
                    MessageDialog loDialog = new MessageDialog(getContext(), null,
                            getString(R.string.ads_list_is_empty), null, false);
                    loDialog.setClickListener(v -> {
                        loDialog.dismiss();
                    });
                    loDialog.show();
                } else {
                    Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
                }
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
                //floatingSearchPress();
                BottomSheetDialog bottomSheet = new BottomSheetDialog(this,
                        msSearchText,
                        miLastCategoryId,
                        miLastMainStoreId,
                        miAdType);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
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

    private int miPosition = -1;

    @Override
    public void handleOfferDetails(int position) {
        miPosition = position;
        Ad loOffer = moOfferList.get(position);
        if (!loOffer.getEngagedFlag()) {
            if (loOffer.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("mobile", AppGlobal.getPhoneNumber());
                FirebaseEvents.trigger(getActivity(), bundle, FirebaseEvents.SELECT_DEAL);
            }
            if (loOffer.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
                verifyLocation(loOffer);
            } else {
                if (loOffer.isQuizFlow()) {
                    openQuizDetails(loOffer);
                } else callAPIByPassQuiz(loOffer);
            }
        }
    }

    private void openQuizDetails(Ad foOffer) {
        Intent loIntent = new Intent(moContext, QuizDetailsActivity.class);
        loIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IntentKey.OFFER_OBJECT, foOffer);
        loIntent.putExtras(bundle);
        moContext.startActivity(loIntent);
    }

    private void callAPIByPassQuiz(Ad foOffer) {
        showProgressDialog();
        moOfferListViewModel.bypassQuiz(getActivity(), foOffer.getAdID());
    }

    private void verifyLocation(Ad loOffer) {
        if (AppGlobal.getLocation() != null) {
            AdLocation adLocation = loOffer.getLocationList().get(0);
            Location loAdLocation = new Location("shop");
            loAdLocation.setLatitude(adLocation.getLatitude());
            loAdLocation.setLongitude(adLocation.getLongitude());
            if (Common.isUserInRadius(AppGlobal.getLocation(), loAdLocation, loOffer.getCoverageRadius())) {
                if (loOffer.isQuizFlow()) {
                    openQuizDetails(loOffer);
                } else callAPIByPassQuiz(loOffer);
            } else {
                String lsMessage = Common.getDynamicText(getActivity(), "alert_msg_out_range")
                        .replace("%s", String.valueOf(loOffer.getCoverageRadius()));
                MessageDialog loDialog = new MessageDialog(getActivity(),
                        null,
                        lsMessage,
                        null,
                        false);
                loDialog.show();
            }
        } else {
            showProgressDialog();
            checkPermissionStatus();
        }
    }

    Observer<BypassQuizResponse> bypassQuizObserver = new Observer<BypassQuizResponse>() {
        @Override
        public void onChanged(BypassQuizResponse loJsonObject) {
            dismissProgressDialog();
            if (!loJsonObject.isError()) {
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
        Common.replaceFragment(getMoContext(), fragmentMyCoupons, Constants.FragmentTag.TAG_MY_COUPON_LIST, false);
        ((HomeActivity) getActivity()).updateBottomMenu(3);
    }

    private void checkPermissionStatus() {
        if (!moMapViewModel.checkGPSEnabled(getActivity())) {
            moMapViewModel.enableGPS(getActivity());
        }
    }

    Observer<String> functionCallObserver = new Observer<String>() {
        @Override
        public void onChanged(String fsFunctionName) {
            switch (fsFunctionName) {
                case LOAD_MAP_VIEW:
                    moMapViewModel.getLastKnownLocation(getActivity());
                    break;
                case FETCH_OFFERS:
                    dismissProgressDialog();
                    if (moMapViewModel.getCurrentLocation() != null) {
                        AppGlobal.setLocation(moMapViewModel.getCurrentLocation());
                        handleOfferDetails(miPosition);
                    }
                    break;
            }
        }
    };

    @Override
    public void onFilterClick(int position, String searchText, int categoryId, int mainStoreId) {
        isFilterApply = true;
        //Log.d("TTT", "before id..." + categoryId + "\t " + mainStoreId);
        miLastMainStoreId = mainStoreId;
        miLastCategoryId = categoryId;
        miAdType = position;
        miCategoryId = categoryId;
//        if (categoryId >= 0)
//            miCategoryId = categoryId;
//        else
//            miCategoryId = mainStoreId;
        // Log.d("TTT", "after id..." + miCategoryId);
        msSearchText = (searchText.length() > 0) ? searchText.toString().trim() : "";
        refreshData();
    }
}
