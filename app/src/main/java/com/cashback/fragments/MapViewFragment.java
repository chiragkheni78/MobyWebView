package com.cashback.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.PhoneLoginActivity;
import com.cashback.databinding.FragmentMapViewBinding;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.viewmodel.MapViewModel;
import com.cashback.utils.Common;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import static android.app.Activity.RESULT_OK;


public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    FragmentMapViewBinding moBinding;

    MapViewModel moMapViewModel;

    Observer<FetchOffersResponse> fetchOffersObserver = new Observer<FetchOffersResponse>() {

        @Override
        public void onChanged(FetchOffersResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getOfferList() != null) {
                    if (loJsonObject.getOfferList().size() > 0) {

                    }
                }
            } else {
                Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        moBinding = FragmentMapViewBinding.inflate(inflater, container, false);
        return getContentView(moBinding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initContent();

    }

    private void initContent() {
        initViewModel();

        moBinding.btnError.setOnClickListener(this);
        if (AppGlobal.getFirebaseUser() == null) {
            moBinding.llErrorMessage.setVisibility(View.VISIBLE);
            moBinding.tvErrorTitle.setText(Common.getDynamicText(getContext(), "text_verify_phone_no"));
            moBinding.tvErrorMessage.setText(Common.getDynamicText(getContext(), "msg_verify_phone_number"));
        } else if (!moMapViewModel.isLocationEnabled((getContext()))) {
            moBinding.llErrorMessage.setVisibility(View.VISIBLE);
            moBinding.tvErrorTitle.setText(Common.getDynamicText(getContext(), "disable_location"));
            moBinding.tvErrorMessage.setText(Common.getDynamicText(getContext(), "disable_location_msg"));
        } else {
            moBinding.llErrorMessage.setVisibility(View.GONE);
            setupMapView();
        }

    }

    private void initViewModel() {
        moMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        moMapViewModel.fetchOffersStatus.observe(getActivity(), fetchOffersObserver);
    }

    private void setupMapView() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnError:
                errorButtonPressed();
                break;
        }
    }

    private static final int REQUEST_PHONE_LOGIN = 837;
    public static boolean isReloadEnable = false;

    private void errorButtonPressed() {

        if (AppGlobal.getFirebaseUser() == null) {
            Intent loIntent = new Intent(getContext(), PhoneLoginActivity.class);
            startActivityForResult(loIntent, REQUEST_PHONE_LOGIN);
        } else if (!moMapViewModel.isLocationEnabled((getContext()))) {
            if (!moMapViewModel.checkGPSEnabled(getActivity())){
                isReloadEnable = true;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isReloadEnable){
            moMapViewModel.checkGPSEnabled(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHONE_LOGIN) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

}
