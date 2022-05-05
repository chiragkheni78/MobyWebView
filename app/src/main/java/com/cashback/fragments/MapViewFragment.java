package com.cashback.fragments;

import static android.app.Activity.RESULT_OK;
import static com.cashback.models.viewmodel.MapViewModel.FETCH_OFFERS;
import static com.cashback.models.viewmodel.MapViewModel.LOAD_MAP_VIEW;
import static com.cashback.models.viewmodel.MapViewModel.MY_PERMISSIONS_LOCATION;
import static com.cashback.models.viewmodel.MapViewModel.REQUEST_CHECK_SETTINGS;
import static com.cashback.utils.Constants.IntentKey.SCREEN_TITLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashback.AppGlobal;
import com.cashback.R;
import com.cashback.activities.BankOfferDetailsActivity;
import com.cashback.activities.PhoneLoginActivity;
import com.cashback.activities.QuizDetailsActivity;
import com.cashback.databinding.FragmentMapViewBinding;
import com.cashback.dialog.MessageDialog;
import com.cashback.models.Ad;
import com.cashback.models.MapMarker;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.models.viewmodel.MapViewModel;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.cashback.utils.FirebaseEvents;
import com.cashback.utils.LogV2;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("All")
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    public MapViewFragment() {

    }

    private static final String TAG = MapViewFragment.class.getSimpleName();
    FragmentMapViewBinding moBinding;
    MapViewModel moMapViewModel;

    private ArrayList<Ad> moOfferList;
    Observer<FetchOffersResponse> fetchOffersObserver = new Observer<FetchOffersResponse>() {

        @Override
        public void onChanged(FetchOffersResponse loJsonObject) {
            if (!loJsonObject.isError()) {
                if (loJsonObject.getOfferList() != null) {
                    if (loJsonObject.getOfferList().size() > 0) {
                        moOfferList = loJsonObject.getOfferList();
                        showBankOfferMessage();
                        drawMarkers();
                    }
                }
            } else {
                Common.showErrorDialog(getActivity(), loJsonObject.getMessage(), false);
            }
            dismissProgressDialog();
        }
    };

    private void showBankOfferMessage() {
        if (!getPreferenceManager().isBankOfferMessageShown()) {
            String lsTitle = Common.getDynamicText(getContext(), "bank_offer_title");
            String lsMessage = Common.getDynamicText(getContext(), "bank_offer_message");
            String lsButtonName = Common.getDynamicText(getContext(), "btn_proceed");
            MessageDialog loDialog = new MessageDialog(getContext(), lsTitle, lsMessage, lsButtonName, false);
            loDialog.show();
            getPreferenceManager().setBankOfferMessageShown(true);
        }
    }

    Observer<String> functionCallObserver = new Observer<String>() {
        @Override
        public void onChanged(String foFunctionName) {
            switch (foFunctionName) {
                case LOAD_MAP_VIEW:
                    loadView();
                    break;
                case FETCH_OFFERS:
                    loadData();
                    break;
            }
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

        if (isReloadEnable) {
            if (moMapViewModel.checkGPSEnabled(getActivity())) {
                moMapViewModel.enableGPS(getActivity());
            }
        } else {
            if (moBinding.llErrorMessage.getVisibility() == View.VISIBLE)
                loadView();
        }
    }

    private void initContent() {
        initViewModel();
        moBinding.btnError.setOnClickListener(this);
    }

    private void loadView() {
        if (!getPreferenceManager().isPhoneVerified()) {
            errorButtonPressed();
            moBinding.llErrorMessage.setVisibility(View.VISIBLE);
            moBinding.tvErrorTitle.setText(Common.getDynamicText(getContext(), "text_verify_phone_no"));
            moBinding.tvErrorMessage.setText(Common.getDynamicText(getContext(), "msg_verify_phone_number_map"));
        } else if (!moMapViewModel.isLocationEnabled((getContext()))) {
             moBinding.llErrorMessage.setVisibility(View.VISIBLE);
             moBinding.tvErrorTitle.setText(Common.getDynamicText(getContext(), "disable_location"));
            moBinding.tvErrorMessage.setText(Common.getDynamicText(getContext(), "disable_location_msg"));
            errorButtonPressed();
            errorButtonPressed();
        } else {
            moBinding.llErrorMessage.setVisibility(View.GONE);
            setupMapView();
            moMapViewModel.getLastKnownLocation(getActivity());
        }
    }

    private void initViewModel() {
        moMapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        moMapViewModel.fetchOffersStatus.observe(getActivity(), fetchOffersObserver);
        moMapViewModel.functionCallStatus.observe(getActivity(), functionCallObserver);
    }

    private void setupMapView() {
        isReloadEnable = false;
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnError:
                errorButtonPressed();
                break;
        }
    }

    public void fetchOffers() {
        if (moCurrentLocation != null) {
            showProgressDialog();
            moMapViewModel.fetchOffers(getActivity(),
                    AppGlobal.getPhoneNumber(),
                    moCurrentLocation.getLatitude(),
                    moCurrentLocation.getLongitude(),
                    false,
                    Common.isLocationFromMockProvider(getContext(), moCurrentLocation),
                    Constants.OfferPage.MAP_VIEW.getValue());
        }
    }

    public static final int REQUEST_PHONE_LOGIN = 837;
    private static boolean isReloadEnable = false;

    private void errorButtonPressed() {

        if (!getPreferenceManager().isPhoneVerified()) {
            Intent loIntent = new Intent(getContext(), PhoneLoginActivity.class);
            loIntent.putExtra(SCREEN_TITLE, this.getResources().getString(R.string.msg_verify_phone_number_map));
            getActivity().startActivityForResult(loIntent, REQUEST_PHONE_LOGIN);
        } else if (!moMapViewModel.isLocationEnabled((getContext()))) {
            if (!moMapViewModel.checkGPSEnabled(getActivity())) {
                moMapViewModel.enableGPS(getActivity());
                /*isReloadEnable = true;
                loadView();*/
            }
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (isReloadEnable) {
            moMapViewModel.checkGPSEnabled(getActivity());
        } else {
            if (moBinding.llErrorMessage.getVisibility() == View.VISIBLE)
                loadView();
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHONE_LOGIN || requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                FirebaseEvents.trigger(getActivity(), null, FirebaseEvents.NEAR_BY_OTP_VERIFIED);
                loadView();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION:
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Granted:: ACCESS_FINE_LOCATION");
                    loadView();
                }
                break;
        }
    }

    private GoogleMap moGoogleMap;
    private List<Marker> moMarkerList;
    private HashMap<String, Integer> moMarkerMap;
    private Location moCurrentLocation;
    private Marker moCurrentLocationMarker;


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap foGoogleMap) {
        //foGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        foGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        foGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        foGoogleMap.setMyLocationEnabled(true);

        //foGoogleMap.setOnMarkerClickListener(markerClickListener);
        foGoogleMap.setOnCameraMoveListener(cameraMoveListener);
        //foGoogleMap.setInfoWindowAdapter(moInfoWindowAdapter);
        foGoogleMap.setInfoWindowAdapter(new MyInfoWindow());
        foGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull @NotNull Marker marker) {
                if (moMarkerMap != null && moMarkerMap.size() > 0
                        && marker.getId() != null && moMarkerMap.containsKey(marker.getId())) {

                    if (marker.getId() != null) {

                        int liPosition = moMarkerMap.get(marker.getId());
                        MapMarker loMapMarker = moMapViewModel.getMapMarkerList().get(liPosition);

                        Intent loIntent = new Intent(getContext(), QuizDetailsActivity.class);

                        if (loMapMarker.getAdType().contains(Constants.AdType.BANK_OFFER.getValue())) {
                            loIntent = new Intent(getContext(), BankOfferDetailsActivity.class);
                            loIntent.putExtra(Constants.IntentKey.OFFER_ID, loMapMarker.getAdID());
                            loIntent.putExtra(Constants.IntentKey.LOCATION_ID, loMapMarker.getLocationID());
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.IntentKey.OFFER_OBJECT, moOfferList.get(liPosition));
                            loIntent.putExtras(bundle);
                        }
                        getActivity().startActivity(loIntent);
                    }
                }
            }
        });
        MapsInitializer.initialize(getActivity());
        moGoogleMap = foGoogleMap;
        moMarkerList = new ArrayList<>();

    }

    private void loadData() {
        updateMyLocationOnMap();
        fetchOffers();
    }


    private void updateMyLocationOnMap() {
        Log.d("TTT", "updateMyLocationOnMap location..." + moCurrentLocation);
        moCurrentLocation = moMapViewModel.getCurrentLocation();
        if (moCurrentLocation != null) {
            if (moCurrentLocationMarker != null) {
                moCurrentLocationMarker.remove();
            }

            LatLng moCurrentLatLong = new LatLng(moCurrentLocation.getLatitude(), moCurrentLocation.getLongitude());

            try {
                Bitmap ob = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_person_pin_white_36dp);
                Bitmap obm = Bitmap.createBitmap(ob.getWidth(), ob.getHeight(), ob.getConfig());
                Canvas canvas = new Canvas(obm);
                Paint paint = new Paint();
                paint.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.secondary), PorterDuff.Mode.SRC_ATOP));
                canvas.drawBitmap(ob, 0f, 0f, paint);
                moCurrentLocationMarker = moGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(obm)).position(moCurrentLatLong).title("I'm here!"));

                moGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moCurrentLatLong, getPreferenceManager().getMapZoomLevel()));
            } catch (Exception e) {
                LogV2.logException(TAG, e);
            }
        }
    }

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (moMarkerMap != null && moMarkerMap.size() > 0
                    && marker.getId() != null && moMarkerMap.containsKey(marker.getId())) {

                if (marker.getId() != null) {

                    int liPosition = moMarkerMap.get(marker.getId());
                    MapMarker loMapMarker = moMapViewModel.getMapMarkerList().get(liPosition);

                    Intent loIntent = new Intent(getContext(), QuizDetailsActivity.class);//OfferDetailsActivity

                    if (loMapMarker.getAdType().contains(Constants.AdType.BANK_OFFER.getValue())) {
                        loIntent = new Intent(getContext(), BankOfferDetailsActivity.class);
                    }
                    loIntent.putExtra(Constants.IntentKey.OFFER_ID, loMapMarker.getAdID());
                    loIntent.putExtra(Constants.IntentKey.LOCATION_ID, loMapMarker.getLocationID());
                    getActivity().startActivity(loIntent);
                }
            }
            return false;
        }
    };

    GoogleMap.OnCameraMoveListener cameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 700);
        }
    };

    GoogleMap.InfoWindowAdapter moInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            LinearLayout info = new LinearLayout(getContext());
            info.setOrientation(LinearLayout.VERTICAL);

            TextView title = new TextView(getContext());
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);
            title.setText(marker.getTitle());

            TextView snippet = new TextView(getContext());
            snippet.setTextColor(Color.GRAY);
            snippet.setGravity(Gravity.CENTER);
            snippet.setText(marker.getSnippet());

            info.addView(title);
            info.addView(snippet);

            return info;
        }
    };

    private void drawMarkers() {
        if (moGoogleMap != null) {
            moGoogleMap.clear();
            if (moMarkerList != null && moMarkerList.size() > 0) {
                moMarkerList.clear();
            }
            moMarkerMap = new HashMap<>();

            ArrayList<MapMarker> loMarkerList = moMapViewModel.getMapMarkerList();

            if (loMarkerList.size() > 0) {
                for (int liPosition = 0; liPosition < loMarkerList.size(); liPosition++) {
                    Marker loMarker = moGoogleMap.addMarker(moMapViewModel.getMapMarkerByPosition(liPosition));
                    moMarkerMap.put(loMarker.getId(), liPosition);
                    moMarkerList.add(loMarker);
                }
            }
            updateMyLocationOnMap();
        }
    }

    class MyInfoWindow implements GoogleMap.InfoWindowAdapter {

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public View getInfoWindow(@NonNull @NotNull Marker marker) {
            View myContentsView = getLayoutInflater().inflate(R.layout.item_map_custom_view, null);
            TextView tvTitle = (TextView) myContentsView.findViewById(R.id.tvMarkerTitle);
            TextView tvPrice = (TextView) myContentsView.findViewById(R.id.tvMarkerPrice);
            TextView tvBrandName = (TextView) myContentsView.findViewById(R.id.tvBrandName);

            ImageView imageBack = (ImageView) myContentsView.findViewById(R.id.imageMarkerBackground);
            ImageView imagePin = (ImageView) myContentsView.findViewById(R.id.imageMarkerPin);


            if (moMarkerMap != null && moMarkerMap.size() > 0
                    && marker.getId() != null && moMarkerMap.containsKey(marker.getId())) {

                int liPosition = moMarkerMap.get(marker.getId());
                MapMarker loMapMarker = moMapViewModel.getMapMarkerList().get(liPosition);

                if (loMapMarker.getAdType().equalsIgnoreCase(Constants.AdType.BANK_OFFER.getValue())) {
                    tvTitle.setVisibility(View.VISIBLE);
                    tvPrice.setVisibility(View.VISIBLE);
                    imageBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_yellow_pin_bg));
                    tvTitle.setText(loMapMarker.getAdType());
                    tvPrice.setText(loMapMarker.getLandmark());
                } else {
                    if (marker != null && !TextUtils.isEmpty(marker.getTitle())) {
                        tvTitle.setText(marker.getTitle() + " Discount");
                    } else {
                        tvTitle.setVisibility(View.GONE);
                    }

                    imageBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_red_pin_bg));
                    if (!TextUtils.isEmpty(loMapMarker.getFlatCashBack()))
                        tvPrice.setText(loMapMarker.getFlatCashBack() + " Cashback");
                    else
                        tvPrice.setVisibility(View.GONE);
                }
               /* Common.loadImage(imagePin, loMapMarker.getAdLogo(),
                        getResources().getDrawable(R.drawable.ic_moby_small),
                        getResources().getDrawable(R.drawable.ic_moby_small));*/

                if (loMapMarker.getPinColor().equalsIgnoreCase(Constants.PinColor.YELLOW.getValue())) {
                    Picasso.get().
                            load(loMapMarker.getAdLogo()).
                            placeholder(R.drawable.ic_moby_small).
                            resize(50, 50).
                            into(imagePin, new MarkerCallback(marker));
                } else
                    Picasso.get().
                            load(Constants.IMAGE_BASE_URL + loMapMarker.getAdLogo()).
                            placeholder(R.drawable.ic_moby_small).
                            resize(50, 50).
                            into(imagePin, new MarkerCallback(marker));


                tvBrandName.setText(loMapMarker.getAdName());
            }

            return myContentsView;
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public View getInfoContents(@NonNull @NotNull Marker marker) {
            return null;
        }
    }

    public class MarkerCallback implements Callback {
        Marker marker = null;

        MarkerCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }

        @Override
        public void onError(Exception e) {

        }
    }
}

