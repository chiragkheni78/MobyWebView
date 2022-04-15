package com.cashback.activities;

import static com.cashback.models.viewmodel.MapViewModel.FETCH_OFFERS;
import static com.cashback.models.viewmodel.MapViewModel.LOAD_MAP_VIEW;
import static com.cashback.models.viewmodel.MapViewModel.MY_PERMISSIONS_LOCATION;
import static com.cashback.utils.Constants.IntentKey.Action.ACTIVITY_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.MAP_SCREEN;
import static com.cashback.utils.Constants.IntentKey.Action.MESSAGE_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.OFFER_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.WALLET_SCREEN;
import static com.cashback.utils.Constants.IntentKey.IS_FROM;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.databinding.ActivityHomeBinding;
import com.cashback.fragments.FragmentHelp;
import com.cashback.fragments.FragmentMyCoupons;
import com.cashback.fragments.MapViewFragment;
import com.cashback.fragments.OfferListFragment;
import com.cashback.fragments.ShareFragment;
import com.cashback.models.Advertisement;
import com.cashback.models.response.GetUpdateUserSessionResponse;
import com.cashback.models.response.GetSettingResponse;
import com.cashback.models.viewmodel.HomeViewModel;
import com.cashback.models.viewmodel.MapViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


@SuppressWarnings("All")
public class HomeActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    ActivityHomeBinding moBinding;
    HomeViewModel moHomeViewModel;
    MapViewModel moMapViewModel;
    private boolean isLoadOfferPage;
    private String stFrom = "";
    private AppUpdateManager appUpdateManager;
    Animation moAnimBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));

        if (getIntent() != null && getIntent().hasExtra(IS_FROM)) {
            stFrom = getIntent().getStringExtra(IS_FROM);
        }
        initializeContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Common.stOfferShow = true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        /*if (fragment instanceof FragmentMyCoupons) {
            ((FragmentMyCoupons) fragment).onBackPressed();
        } else {*/
        super.onBackPressed();
        /*}*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        if (fragment instanceof MapViewFragment) {
            ((MapViewFragment) fragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            switch (requestCode) {
                case MY_PERMISSIONS_LOCATION:
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "Permission Granted:: ACCESS_FINE_LOCATION");
                        moMapViewModel.functionCallStatus.postValue(LOAD_MAP_VIEW);
                    }
                    break;
            }
        }
        Log.e("AppName", "Here is Location");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        if (fragment != null) {
            if (fragment instanceof MapViewFragment) {
                ((MapViewFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof FragmentMyCoupons) {
                ((FragmentMyCoupons) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof OfferListFragment) {
                ((OfferListFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment instanceof FragmentHelp) {
                ((FragmentHelp) fragment).onActivityResult(requestCode, resultCode, data);
            } else {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 100) {
                if (resultCode == RESULT_OK) {
                    popupSnackbarForCompleteUpdate();
                } else {
                    Toast.makeText(moContext, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 101) {
                if (resultCode == RESULT_OK) {
                    popupSnackbarForCompleteUpdate();
                } else {
                    Toast.makeText(moContext, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(findViewById(R.id.drawer_layout),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    private void initializeContent() {

        moHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        moHomeViewModel.getSettingStatus.observe(this, getSettingObserver);
        moHomeViewModel.getUpdateUserSession.observe(this, getUpdateUserSessionObserver);

        moMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        moMapViewModel.functionCallStatus.observe(this, functionCallObserver);

        setNavigationBar();
        moBinding.toolbar.ivMobyIcon.setOnClickListener(this);
        moBinding.toolbar.ibShare.setOnClickListener(this);
        moBinding.toolbar.ibDashBoard.setOnClickListener(this);
        moBinding.toolbar.tvMyCoupon.setOnClickListener(this);
        moBinding.toolbar.rbList.setOnCheckedChangeListener(this);
        moBinding.drawerLayout.setDrawerListener(this);

        moBinding.floatingMenuOption.setOnClickListener(view -> {
            moBinding.drawerLayout.openDrawer(Gravity.START); //OPEN Nav Drawer!
        });

        moBinding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                //item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.itemCoupons:
                        openNavigationBarFragments(R.id.itemCoupons);
                        break;
                    case R.id.itemOffer:
                        openNavigationBarFragments(R.id.itemOffer);
                        break;

                    case R.id.itemNearBy:
                        openNavigationBarFragments(R.id.itemNearBy);
                        break;
                    case R.id.itemShare:
                        openNavigationBarFragments(R.id.itemShare);
                        //openHelp();
                        break;
                }
                return false;
            }
        });

        if (stFrom.equalsIgnoreCase(Constants.IntentKey.FROM_COUPON) || stFrom.equalsIgnoreCase(WALLET_SCREEN)) {
            moBinding.navigation.getMenu().getItem(3).setChecked(true);
            openMyCoupons(0);
        } else if (stFrom.equalsIgnoreCase(Constants.IntentKey.LOAD_OFFER_PAGE)) {
            moBinding.navigation.getMenu().getItem(1).setChecked(true);
            loadOfferListFragment(0, 0, 0, 0);
        } else if (stFrom.equalsIgnoreCase(Constants.IntentKey.LOAD_SHARE_PAGE)) {
            moBinding.navigation.getMenu().getItem(4).setChecked(true);
            openShareFragement();
        } else {
            getSettings();
//            moBinding.navigation.getMenu().getItem(0).setChecked(true);
        }
    }

    int moPosition;

    public void openNavigationBarFragments(int position) {
        moPosition = position;
        if (position != R.id.itemNearBy) {
            moBinding.navigation.getMenu().findItem(position).setChecked(true);
        }

        if (position == R.id.itemCoupons) {
            openMyCoupons(0);
        } else if (position == R.id.itemOffer) {
            loadOfferListFragment(0, 0, 0, 0);
        } else if (position == R.id.itemNearBy) {
            if (AppGlobal.getTotalBillVerified() > 0) {
                moBinding.navigation.getMenu().findItem(position).setChecked(true);
                loadMapViewFragment();
            } else {
                if (!AppGlobal.fbIsGpsEnInApp) {
                    mbIsLocationRequired = true;
                    checkPermissionStatus();
                } else {
                    dialogOpenMapPopup(HomeActivity.this);
                }
            }
        } else if (position == R.id.itemShare) {
            openShareFragement();
        }
    }

    private void openShareFragement() {
        FirebaseEvents.trigger(HomeActivity.this, null, FirebaseEvents.SHARE_OPTION);

        ShareFragment shareFragment = new ShareFragment();
        Common.replaceFragment(HomeActivity.this, shareFragment, Constants.FragmentTag.TAG_SHARE, false);
    }

    public void updateBottomMenu(int fiPosition) {
        moBinding.navigation.getMenu().getItem(fiPosition).setChecked(true);
    }

    private void getSettings() {
        showProgressDialog();
        moHomeViewModel.getGlobalSetting(getContext());
    }

    private void setNavigationBar() {
        Toolbar loToolbar = findViewById(R.id.toolbar);
        setToolbar(loToolbar);

        ActionBarDrawerToggle loToggle = new ActionBarDrawerToggle(
                this, moBinding.drawerLayout, loToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        moBinding.drawerLayout.addDrawerListener(loToggle);
        loToggle.setDrawerIndicatorEnabled(false);
        loToggle.syncState();

        NavigationView loNavigationView = (NavigationView) findViewById(R.id.nav_view);
        loNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setToolbar(Toolbar foToolbar) {

        setSupportActionBar(foToolbar);

        ImageButton loIbNavigation = (ImageButton) foToolbar.findViewById(R.id.ibNavigation);
        loIbNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    moBinding.drawerLayout.closeDrawer(Gravity.START); //CLOSE Nav Drawer!
                } else {
                    moBinding.drawerLayout.openDrawer(Gravity.START); //OPEN Nav Drawer!
                }
            }
        });

        loIbNavigation.setVisibility(View.VISIBLE);
        loIbNavigation.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.ic_round_menu_24));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMobyIcon:
                openOngoingDeal();
                break;
            case R.id.ibShare:
                openNavigationBarFragments(R.id.itemShare);
                break;
            case R.id.tvMyCoupon:
                openMyCoupons(0);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            loadOfferListFragment(0, 0, 0, 0);
        } else {
            loadMapViewFragment();
        }
    }

    private void shareApp() {
        if (!getPreferenceManager().getReferralLink().isEmpty()) {
            String lsMessage = Common.getDynamicText(this, "share_message");
            if (getPreferenceManager().getReferralCode() != null)
                lsMessage = lsMessage.replace("CCCCC", getPreferenceManager().getReferralCode());
            lsMessage = lsMessage.replace("XXXXXX", getPreferenceManager().getReferralLink());
            Common.shareApp(this, lsMessage);
        }
    }

    private void blinkText(View view) {
        moAnimBlink = new AlphaAnimation(0.0f, 1.0f);
        moAnimBlink.setDuration(700); //You can manage the blinking time with this parameter
        moAnimBlink.setStartOffset(20);
        moAnimBlink.setRepeatMode(Animation.REVERSE);
        moAnimBlink.setRepeatCount(Animation.INFINITE);
        view.startAnimation(moAnimBlink);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent loIntent = null;
        switch (item.getItemId()) {
            case R.id.nav_Share:
                openNavigationBarFragments(R.id.itemShare);
                break;
            case R.id.nav_home:
                openNavigationBarFragments(R.id.itemOffer);
                break;
            case R.id.nav_my_coupons:
                //openMyCoupons(0);
                openNavigationBarFragments(R.id.itemCoupons);

                break;
////            case R.id.nav_ongoing_deals:
////                openOngoingDeal();
//                break;
            case R.id.nav_wallet:
                openWallet();
                break;
            case R.id.nav_messages:
                openMessages(0);
                break;
            case R.id.nav_help:
                openHelp();
//                openNavigationBarFragments(R.id.itemHelp);
                break;
            case R.id.nav_profile:
                openProfile();
                break;
            case R.id.nav_tc:
                openTC();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        moBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openMyCoupons(long flActivityID) {
        FragmentMyCoupons fragmentMyCoupons = new FragmentMyCoupons();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.IntentKey.ACTIVITY_ID, flActivityID);
        fragmentMyCoupons.setArguments(bundle);
        Common.replaceFragment(HomeActivity.this, fragmentMyCoupons, Constants.FragmentTag.TAG_MY_COUPON_LIST, false);
    }

    public void loadOfferListFragment(int fiCategoryId, long flOfferId, long flLocationId, long flBannerId) {
        Bundle loBundle = new Bundle();
        Fragment loFragment = new OfferListFragment();
        loBundle.putInt(Constants.IntentKey.CATEGORY_ID, fiCategoryId);
        loBundle.putLong(Constants.IntentKey.OFFER_ID, flOfferId);
        loBundle.putLong(Constants.IntentKey.LOCATION_ID, flLocationId);
        loBundle.putLong(Constants.IntentKey.BANNER_ID, flBannerId);
        loFragment.setArguments(loBundle);
        Common.replaceFragment(this, loFragment, Constants.FragmentTag.TAG_OFFER_LIST, false);
        moBinding.navigation.getMenu().getItem(0).setChecked(true);
    }

    public void loadMapViewFragment() {
        Bundle loBundle = new Bundle();
        Fragment loFragment = new MapViewFragment();
        loFragment.setArguments(loBundle);
        Common.replaceFragment(this, loFragment, Constants.FragmentTag.TAG_MAP_VIEW, false);
    }

    private void logout() {
        AppGlobal.logOutUser();
        Intent intent = new Intent(HomeActivity.this, ShortProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void openTC() {
        //openWebView(getString(R.string.terms_condition), Constants.WebViewPage.TERMS_CONDITION.getValue());
        Intent loIntent = new Intent(HomeActivity.this, WebViewSecondActivity.class);
        loIntent.putExtra(Constants.IntentKey.SCREEN_TITLE, getString(R.string.terms_condition));
        loIntent.putExtra(Constants.IntentKey.WEBVIEW_PAGE_NAME, "http://mobyads.in/moby/v2-apis/?fsAction=loadWebViewHtml&fsPage=tandc");
        startActivity(loIntent);
    }

    private void openProfile() {
        Intent loIntent = new Intent(this, UserProfileActivity.class);
        startActivity(loIntent);
    }

    private void openHelp() {
        Intent loIntent = new Intent(this, HelpActivity.class);
        startActivity(loIntent);

//        FragmentHelp fragmentHelp = new FragmentHelp();
//        Bundle bundleHelp = new Bundle();
//        bundleHelp.putString(Constants.IntentKey.SCREEN_TITLE, getString(R.string.help));
//        bundleHelp.putString(Constants.IntentKey.ADVERT_SCREEN_TYPE, Constants.AdvertScreenType.HELP_SCREEN.getValue());
//        fragmentHelp.setArguments(bundleHelp);
//        Common.replaceFragment(HomeActivity.this, fragmentHelp, Constants.FragmentTag.TAG_MY_OFFER_LIST, false);
    }

    private void openMessages(long llMessageId) {
        Intent loIntent = new Intent(this, MessageActivity.class);
        loIntent.putExtra(Constants.IntentKey.MESSAGE_ID, llMessageId);
        startActivity(loIntent);
    }

    private void openWallet() {
        Intent loIntent = new Intent(this, WalletActivity.class);
        startActivity(loIntent);
    }

    private void openOngoingDeal() {
        openAdvertisement(getString(R.string.ongoing_deals), Constants.AdvertScreenType.ONGOING_DEALS.getValue());
        moBinding.toolbar.ivMobyIcon.clearAnimation();
        getPreferenceManager().setBlinkMobyIcon(false);
    }

    private void openAdvertisement(String fsScreenTitle, String fsScreenType) {
        Intent loIntent = new Intent(this, AdvertisementActivity.class);
        loIntent.putExtra(Constants.IntentKey.SCREEN_TITLE, fsScreenTitle);
        loIntent.putExtra(Constants.IntentKey.ADVERT_SCREEN_TYPE, fsScreenType);
        startActivity(loIntent);
    }

    private void openWebView(String fsScreenTitle, String fsPageName) {
        Intent loIntent = new Intent(this, WebViewActivity.class);
        loIntent.putExtra(Constants.IntentKey.SCREEN_TITLE, fsScreenTitle);
        loIntent.putExtra(Constants.IntentKey.WEBVIEW_PAGE_NAME, fsPageName);
        startActivity(loIntent);
    }

    Observer<GetSettingResponse> getSettingObserver = new Observer<GetSettingResponse>() {
        @Override
        public void onChanged(GetSettingResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (!loJsonObject.isDeviceExist() && !loJsonObject.isUserExist()) {
                    logout();
                    return;
                }
                getPreferenceManager().setMapZoomLevel(loJsonObject.getZoomLevel());
                getPreferenceManager().setQuizTimePeriod(loJsonObject.getQuizTimeInterval());
                getPreferenceManager().setOfferListPageSize(loJsonObject.getOfferListPageSize());
                getPreferenceManager().setShareBannerUrl(loJsonObject.getShareBanner());
                handleView(loJsonObject);
            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };
    Observer<GetUpdateUserSessionResponse> getUpdateUserSessionObserver = new Observer<GetUpdateUserSessionResponse>() {
        @Override
        public void onChanged(GetUpdateUserSessionResponse loJsonObject) {
            dismissProgressDialog();
        }
    };

    private void handleView(GetSettingResponse foJsonObject) {

        if (getPreferenceManager().isBlinkMobyIcon()) {
            Common.blinkAnimation(moBinding.toolbar.ivMobyIcon);
        }

        if (getIntent() != null && getIntent().getAction() != null) {
            String lsAction = getIntent().getAction();
            if (lsAction.equalsIgnoreCase(MAP_SCREEN)) {
                moBinding.toolbar.rbMap.setChecked(true);
            } else {
                openNotifications();
            }
        } else {
            appUpdateManager = AppUpdateManagerFactory.create(this);
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

            if (foJsonObject.getAppUpdate().getStatus() == 1) {
                showUpdateApp(foJsonObject);
            } else if (foJsonObject.getAppUpdate().getStatus() == 2) {
                showUpdateApp(foJsonObject);
            } else {
                if (!getPreferenceManager().isMarketingAd()) {
                    if (stFrom.equalsIgnoreCase(Constants.IntentKey.FROM_COUPON)) {
                        openNavigationBarFragments(R.id.itemCoupons);
                    } else {
                        moJsonObject = foJsonObject;
                        if (!AppGlobal.fbIsGpsEnInApp) {
                            loadOffers();
                            return;
                        }
                        mbIsLocationRequired = true;
                        checkPermissionStatus();
                    }
                }
            }
        }
    }

    private boolean mbIsLocationRequired = false;
    GetSettingResponse moJsonObject;

    private void loadOffers() {

        if (!AppGlobal.isDisplayRewardNote) {
            showFirstDialog(moJsonObject.getFirstTimeAlertTitle(), moJsonObject.getFirstTimeAlertMsg(), moJsonObject.getAdvertisementList());
        } else {
            int liCategoryID = getIntent().getIntExtra(Constants.IntentKey.CATEGORY_ID, 0);
            long llOfferID = getIntent().getLongExtra(Constants.IntentKey.OFFER_ID, 0);
            long llBannerID = getIntent().getLongExtra(Constants.IntentKey.BANNER_ID, 0);
            loadOfferListFragment(liCategoryID, llOfferID, 0, llBannerID);
        }
    }

    private void showFirstDialog(String fsTitle, String fsMessage, ArrayList<Advertisement> foAdvertisementList) {

        if (!isFinishing()) {

            final Dialog alertDialog = new Dialog(getContext());
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.dialog_reward_note);
            alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            alertDialog.setCancelable(false);
            alertDialog.show();

            if (!isFinishing())
                alertDialog.show();

            Button loBtnShopNow = alertDialog.findViewById(R.id.b_cis_ok);
            Button loBtnFindMore = alertDialog.findViewById(R.id.b_cis_cancel);
            TextView loTvTitle = alertDialog.findViewById(R.id.tv_cis_title);
            TextView loTvNote = alertDialog.findViewById(R.id.tv_cis_message);
            ImageView loIvBanner = alertDialog.findViewById(R.id.ivBanner);
            ImageView imageMainClose = alertDialog.findViewById(R.id.imageMainClose);

            if (fsTitle.contains("MOBY")) {
                fsTitle = fsTitle.replaceAll("MOBY", "").trim();
            }
            loTvTitle.setText(fsTitle);
            loBtnShopNow.setText(Common.getDynamicText(getContext(), "btn_redeem_coupon"));
            loBtnFindMore.setText(Common.getDynamicText(getContext(), "btn_get_free_coupon"));

            if (fsMessage != null && !fsMessage.isEmpty()) {
                loTvNote.setText(fsMessage);
            } else {
                loTvNote.setText(Common.getDynamicText(getContext(), "default_reward_note"));
            }

            Advertisement loAdvertisement = null;

            ArrayList<Advertisement> loAdvertisementList = foAdvertisementList;
            if (loAdvertisementList != null && loAdvertisementList.size() > 0) {
                int liPosition = moHomeViewModel.getAdvertPosition(getContext(), loAdvertisementList);
                loAdvertisement = loAdvertisementList.get(liPosition);
                String lsURL = loAdvertisement.getImageUrl();
                if (lsURL != null) {
                    //Common.loadImage(loIvBanner, "https://res.cloudinary.com/demo/image/upload/sample.jpg", null, null);

                    Picasso.get().load(lsURL.replace("https", "http")).into(loIvBanner);

                    loIvBanner.setVisibility(View.VISIBLE);
                }
            } else loIvBanner.setVisibility(View.GONE);

            loBtnShopNow.setOnClickListener(view -> {
                alertDialog.dismiss();
                AppGlobal.isDisplayRewardNote = true;
                /*Intent intent = new Intent(getContext(), MyCouponsActivity.class);
                startActivity(intent);*/
                openNavigationBarFragments(R.id.itemCoupons);
            });

            imageMainClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loBtnFindMore.performClick();
                }
            });

            Advertisement finalLoAdvertisement = loAdvertisement;
            loBtnFindMore.setOnClickListener(view -> {
                alertDialog.dismiss();
                AppGlobal.isDisplayRewardNote = true;

                int liCategoryId = (finalLoAdvertisement == null) ? 0 : finalLoAdvertisement.getCategoryID();
                long llOfferID = (finalLoAdvertisement == null) ? 0 : finalLoAdvertisement.getAdID();
                long llBannerID = (finalLoAdvertisement == null) ? 0 : finalLoAdvertisement.getBannerID();

                loadOfferListFragment(liCategoryId, llOfferID, 0, llBannerID);
                moBinding.navigation.getMenu().getItem(0).setChecked(true);
            });
        }
    }

    private void openNotifications() {
        if (getIntent() != null) {
            String lsAction = getIntent().getAction();
            if (lsAction != null) {
                switch (lsAction) {
                    case ACTIVITY_LIST:
                        long llActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
                        openMyCoupons(llActivityId);
                        break;

                    case WALLET_SCREEN:
                        //long llActivityId = getIntent().getLongExtra(Constants.IntentKey.ACTIVITY_ID, 0);
                        openWallet();
                        break;
                    case MESSAGE_LIST:
                        long llMessageId = getIntent().getLongExtra(Constants.IntentKey.MESSAGE_ID, 0);
                        openMessages(llMessageId);
                        break;
                    default:
                    case OFFER_LIST:
                        int liCategoryId = getIntent().getIntExtra(Constants.IntentKey.CATEGORY_ID, 0);
                        long llOfferId = getIntent().getLongExtra(Constants.IntentKey.OFFER_ID, 0);
                        long llLocationId = getIntent().getLongExtra(Constants.IntentKey.LOCATION_ID, 0);
                        loadOfferListFragment(liCategoryId, llOfferId, llLocationId, 0);
                        break;
                }
            }
        }
    }

    private void showUpdateApp(GetSettingResponse foJsonObject) {
        final Dialog mDialougeBox = new Dialog(this);
        mDialougeBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialougeBox.setContentView(R.layout.dialog_update_app);
        mDialougeBox.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mDialougeBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialougeBox.getWindow().setGravity(Gravity.CENTER);
        mDialougeBox.setCancelable(false);
        mDialougeBox.show();

        TextView tvUpdateMessage = mDialougeBox.findViewById(R.id.tvUpdateMessage);
        tvUpdateMessage.setText(foJsonObject.getAppUpdate().getMessage());

        TextView tvSkip = mDialougeBox.findViewById(R.id.tvSkip);
        TextView updateApp = mDialougeBox.findViewById(R.id.updateApp);

        if (foJsonObject.getAppUpdate().getStatus() == 2) {
            tvSkip.setVisibility(View.GONE);
        }

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialougeBox.hide();
                // load data
                showFirstDialog(foJsonObject.getFirstTimeAlertTitle(), foJsonObject.getFirstTimeAlertMsg(), foJsonObject.getAdvertisementList());
            }
        });

        updateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialougeBox.hide();
                Common.redirectPlayStore(HomeActivity.this);
            }
        });
    }

    private void checkPermissionStatus() {
        Log.d("TTT", "mbIsLocationRequired..." + mbIsLocationRequired);
        if (mbIsLocationRequired) {
            if (!moMapViewModel.checkGPSEnabled(this)) {
                moMapViewModel.enableGPS(this);
            }
        }
    }

    Observer<String> functionCallObserver = new Observer<String>() {
        @Override
        public void onChanged(String foFunctionName) {
            switch (foFunctionName) {
                case LOAD_MAP_VIEW:
                    Log.d("TTT", "map view load...");
                    //all permission granted
                    mbIsLocationRequired = false;
                    showProgressDialog();
                    moMapViewModel.getLastKnownLocation(HomeActivity.this);
                    break;
                case FETCH_OFFERS:
                    Log.d("TTT", "call fetch offer...");
                    dismissProgressDialog();
                    if (moPosition == R.id.itemNearBy) {
                        dialogOpenMapPopup(HomeActivity.this);
                    } else {
                        if (AppGlobal.fbIsBottomSheetIsOpen) {
                            AppGlobal.fbIsBottomSheetIsOpen = false;
                            return;
                        }
                        AppGlobal.setLocation(moMapViewModel.getCurrentLocation());
                        if (AppGlobal.getLocation() != null) {
                            loadOffers();
                        } else {
                            moMapViewModel.getLastKnownLocation(HomeActivity.this);
                        }

                    }
                    break;
            }
        }
    };

    public void dialogOpenMapPopup(Activity foContext) {
        Dialog moDialog = new Dialog(foContext);
        moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moDialog.setContentView(R.layout.dialog_map_alert);
        moDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        moDialog.getWindow().setGravity(Gravity.CENTER);
        moDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        moDialog.setCancelable(false);
        moDialog.show();

        final Button loBtnLater = moDialog.findViewById(R.id.btnContinue);
        TextView tvMapDialogText = moDialog.findViewById(R.id.tvMapPopupText1);

        Common.blinkAnimation(tvMapDialogText);

        loBtnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moDialog.dismiss();
            }
        });

//        String TEST_PAGE_URL = "https://app.mobyads.in/publisher/A01234567/?fsMobile=918140663133&fsEmail=johndeo@gmail.com&fsFirstName=Chirag&fsLastName=Kheni&fiDeviceType=0";
//
//        FinestWebViewBuilder builder = new FinestWebViewBuilder().
//                setAccessStorage(true).
//                setAccessGPS(true).
//                setUrl(TEST_PAGE_URL).
//                build();
//        builder.loadWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (mbIsLocationRequired) {
        //if (AppGlobal.fbIsGpsEnInApp) {
        checkPermissionStatus();
        // }
        //}
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        Menu menu = moBinding.navView.getMenu();
        View navWallet = findViewById(R.id.nav_wallet);
        blinkText(navWallet);

//        MenuItem photo = menu.findItem(R.id.nav_wallet);
//        TextView textView = new TextView(this);
//        textView.setText("Moby Wallet");
//        textView.setGravity(Gravity.CENTER);
//        blinkText(textView);
//        photo.setActionView(textView);
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        try {
            if (moAnimBlink != null)
                moAnimBlink.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
