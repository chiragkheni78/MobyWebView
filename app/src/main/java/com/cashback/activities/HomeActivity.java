package com.cashback.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.cashback.R;
import com.cashback.databinding.ActivityHomeBinding;
import com.cashback.fragments.MapViewFragment;
import com.cashback.fragments.OfferListFragment;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    ActivityHomeBinding moBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(getContentView(moBinding));
        initializeContent();
    }

    private void initializeContent() {
        setNavigationBar();
        moBinding.toolbar.ivMobyIcon.setOnClickListener(this);
        moBinding.toolbar.ibShare.setOnClickListener(this);
        moBinding.toolbar.ibDashBoard.setOnClickListener(this);
        moBinding.toolbar.tvMyCoupon.setOnClickListener(this);
        moBinding.toolbar.rbList.setOnCheckedChangeListener(this);
        loadOfferListFragment();
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
                if(moBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    moBinding.drawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
                }else{
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
        if (getPreferenceManager().getReferralLink().isEmpty()) {
            String lsMessage = Common.getDynamicText(this, "share_message");
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
}
