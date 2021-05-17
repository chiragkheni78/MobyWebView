package com.cashback.activities;

import android.app.Dialog;
import android.content.Context;
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
import com.cashback.models.response.QuizDetailsResponse;
import com.cashback.models.viewmodel.HomeViewModel;
import com.cashback.models.viewmodel.QuizDetailsViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.SharedPreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

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
                showAdvertisements();
                break;
            case R.id.ibShare:
                shareApp();
                break;
            case R.id.tvMyCoupon:
                openMyCoupons();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            loadOfferListFragment();
        } else {
            loadMapViewFragment();
        }
    }

    private void showAdvertisements() {
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

    private void showDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
    }

    private void openMyCoupons() {
        Intent intent = new Intent(this, MyCouponsActivity.class);
        startActivity(intent);
    }

    public void loadOfferListFragment() {
        Bundle loBundle = new Bundle();
        Fragment loFragment = new OfferListFragment();
        loFragment.setArguments(loBundle);
        Common.replaceFragment(this, loFragment, Constants.FragmentTag.TAG_OFFER_LIST, false);
    }

    public void loadMapViewFragment() {
        Bundle loBundle = new Bundle();
        Fragment loFragment = new MapViewFragment();
        loFragment.setArguments(loBundle);
        Common.replaceFragment(this, loFragment, Constants.FragmentTag.TAG_MAP_VIEW, false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent loIntent = null;
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_my_coupons:
                openMyCoupons();
                break;
            case R.id.nav_ongoing_deals:
                openOngoingDeal();
                break;
            case R.id.nav_wallet:
                openWallet();
                break;
            case R.id.nav_messages:
                openMessages();
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

    private void logout() {

    }

    private void openTC() {

    }

    private void openProfile() {

    }

    private void openHelp() {

    }

    private void openMessages() {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

    private void openWallet() {

    }

    private void openOngoingDeal() {

    }


    Observer<GetSettingResponse> getSettingObserver = new Observer<GetSettingResponse>() {
        @Override
        public void onChanged(GetSettingResponse loJsonObject) {
            if (!loJsonObject.isError()) {

                if (loJsonObject.getAppUpdate().getStatus() == 1) {
                    //showAlertTwoBtn(fsMessage, message);
                } else if (loJsonObject.getAppUpdate().getStatus() == 2) {
                    //dialogUpdateApp(fsMessage, true, false);
                } else {
                    if (!getPreferenceManager().isMarketingAd()){
                        showFirstDialog(loJsonObject.getFirstTimeAlertTitle(), loJsonObject.getFirstTimeAlertMsg(), loJsonObject.getAdvertisementList());
                    }
                }

            } else {
                Common.showErrorDialog(getContext(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void showFirstDialog(String fsTitle, String fsMessage, ArrayList<Advertisement> foAdvertisementList){

        if (!AppGlobal.isDisplayRewardNote && !isFinishing()) {

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

            ArrayList<Advertisement> loAdvertisementList = foAdvertisementList;
            if (loAdvertisementList != null && loAdvertisementList.size() > 0) {
                String lsURL = moHomeViewModel.getAdvertImage(getContext(), loAdvertisementList);
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

            loBtnFindMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    AppGlobal.isDisplayRewardNote = true;
                    loadOfferListFragment();
                }
            });
        }
    }
}
