package com.cashback.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.databinding.ActivityHomeBinding;
import com.cashback.fragments.MapViewFragment;
import com.cashback.fragments.OfferListFragment;
import com.cashback.models.Advertisement;
import com.cashback.models.response.GetSettingResponse;
import com.cashback.models.viewmodel.HomeViewModel;
import com.cashback.models.viewmodel.WebViewModel;
import com.cashback.services.MyFirebaseMessagingService;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.cashback.utils.Constants.IntentKey.Action.ACTIVITY_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.MAP_SCREEN;
import static com.cashback.utils.Constants.IntentKey.Action.MESSAGE_LIST;
import static com.cashback.utils.Constants.IntentKey.Action.OFFER_LIST;

public class HomeActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    ActivityHomeBinding moBinding;

    HomeViewModel moHomeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {

        moHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        moHomeViewModel.getSettingStatus.observe(this, getSettingObserver);

        setNavigationBar();
        moBinding.toolbar.ivMobyIcon.setOnClickListener(this);
        moBinding.toolbar.ibShare.setOnClickListener(this);
        moBinding.toolbar.ibDashBoard.setOnClickListener(this);
        moBinding.toolbar.tvMyCoupon.setOnClickListener(this);
        moBinding.toolbar.rbList.setOnCheckedChangeListener(this);

        getSettings();
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
                    moBinding.drawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
                } else {
                    moBinding.drawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!
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
                shareApp();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent loIntent = null;
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_my_coupons:
                openMyCoupons(0);
                break;
            case R.id.nav_ongoing_deals:
                openOngoingDeal();
                break;
            case R.id.nav_wallet:
                openWallet();
                break;
            case R.id.nav_messages:
                openMessages(0);
                break;
            case R.id.nav_help:
                openHelp();
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
        Intent loIntent = new Intent(this, MyCouponsActivity.class);
        loIntent.putExtra(Constants.IntentKey.ACTIVITY_ID, flActivityID);
        startActivity(loIntent);
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
        openWebView(getString(R.string.terms_condition), Constants.WebViewPage.TERMS_CONDITION.getValue());
    }

    private void openProfile() {
        Intent loIntent = new Intent(this, UserProfileActivity.class);
        startActivity(loIntent);
    }

    private void openHelp() {
        openAdvertisement(getString(R.string.help), Constants.AdvertScreenType.HELP_SCREEN.getValue());
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
                if (!loJsonObject.isDeviceExist()){
                    logout();
                    return;
                }
                getPreferenceManager().setMapZoomLevel(loJsonObject.getZoomLevel());
                getPreferenceManager().setQuizTimePeriod(loJsonObject.getQuizTimeInterval());
                getPreferenceManager().setOfferListPageSize(loJsonObject.getOfferListPageSize());
                handleView(loJsonObject);

            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void handleView(GetSettingResponse foJsonObject) {

        if (getPreferenceManager().isBlinkMobyIcon()){
            Common.blinkAnimation(moBinding.toolbar.ivMobyIcon);
        }

        if (getIntent() != null && getIntent().getAction() != null){
            String lsAction = getIntent().getAction();
            if (lsAction.equalsIgnoreCase(MAP_SCREEN)){
                moBinding.toolbar.rbMap.setChecked(true);
            } else {
                openNotifications();
            }
        } else {
            if (foJsonObject.getAppUpdate().getStatus() == 1) {
                //showAlertTwoBtn(fsMessage, message);
            } else if (foJsonObject.getAppUpdate().getStatus() == 2) {
                //dialogUpdateApp(fsMessage, true, false);
            } else {
                if (!getPreferenceManager().isMarketingAd()) {
                    if (!AppGlobal.isDisplayRewardNote) {
                        showFirstDialog(foJsonObject.getFirstTimeAlertTitle(), foJsonObject.getFirstTimeAlertMsg(), foJsonObject.getAdvertisementList());
                    }else {

                        int liCategoryID = getIntent().getIntExtra(Constants.IntentKey.CATEGORY_ID, 0);
                        long llOfferID = getIntent().getLongExtra(Constants.IntentKey.OFFER_ID, 0);
                        long llBannerID = getIntent().getLongExtra(Constants.IntentKey.BANNER_ID, 0);

                        loadOfferListFragment(liCategoryID,llOfferID,0, llBannerID);
                    }
                }
            }
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

            loBtnShopNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    AppGlobal.isDisplayRewardNote = true;
                    Intent intent = new Intent(getContext(), MyCouponsActivity.class);
                    startActivity(intent);
                }
            });

            Advertisement finalLoAdvertisement = loAdvertisement;
            loBtnFindMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    AppGlobal.isDisplayRewardNote = true;

                    int liCategoryId = (finalLoAdvertisement == null)? 0 : finalLoAdvertisement.getCategoryID();
                    long llOfferID = (finalLoAdvertisement == null)? 0 : finalLoAdvertisement.getAdID();
                    long llBannerID = (finalLoAdvertisement == null)? 0 : finalLoAdvertisement.getBannerID();

                    loadOfferListFragment(liCategoryId, llOfferID, 0, llBannerID);
                }
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
}
