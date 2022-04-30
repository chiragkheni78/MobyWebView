package com.cashback.models.viewmodel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cashback.AppGlobal;
import com.cashback.dialog.MessageDialog;
import com.cashback.models.Ad;
import com.cashback.models.AdLocation;
import com.cashback.models.MapMarker;
import com.cashback.models.request.FetchOffersRequest;
import com.cashback.models.response.FetchOffersResponse;
import com.cashback.utils.APIClient;
import com.cashback.utils.Common;
import com.cashback.utils.Constants;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("All")
public class MapViewModel extends ViewModel {

    public static final String LOAD_MAP_VIEW = "loadMapView";
    public static final String FETCH_OFFERS = "fetchOffers";

    public MutableLiveData<FetchOffersResponse> fetchOffersStatus = new MutableLiveData<>();
    public MutableLiveData<String> functionCallStatus = new MutableLiveData<>();

    private ArrayList<Ad> moOfferList;
    ArrayList<MapMarker> moMapMarkerList;

    public void fetchOffers(Context foContext, String mobileNumber, double latitude, double longitude, boolean isMarketingAd, boolean isBlockUser, int pageViewType) {
        //latitude = 21.2308729;
        //longitude = 72.8611336;
        FetchOffersRequest loFetchOffersRequest = new FetchOffersRequest(mobileNumber, latitude, longitude, isMarketingAd, isBlockUser, pageViewType);
        loFetchOffersRequest.setAction(Constants.API.GET_OFFER_LIST.getValue());
        loFetchOffersRequest.setDeviceId(Common.getDeviceUniqueId(foContext));
        loFetchOffersRequest.setMobileNumber(AppGlobal.getPhoneNumber());

        String lsMessage = loFetchOffersRequest.validateData(foContext);
        if (lsMessage != null) {
            fetchOffersStatus.postValue(new FetchOffersResponse(true, lsMessage));
            return;
        }

        //API Call
        Call<FetchOffersResponse> loRequest = APIClient.getInterface().getOfferList(loFetchOffersRequest);
        Common.printReqRes(loRequest, "getOfferList", Common.LogType.REQUEST);

        loRequest.enqueue(new Callback<FetchOffersResponse>() {
            @Override
            public void onResponse(Call<FetchOffersResponse> call, Response<FetchOffersResponse> foResponse) {
                Common.printReqRes(foResponse.body(), "getOfferList", Common.LogType.RESPONSE);
                if (foResponse.isSuccessful()) {
                    FetchOffersResponse loJsonObject = foResponse.body();
                    if (loJsonObject.getOfferList() != null) {
                        if (loJsonObject.getOfferList().size() > 0) {
                            moOfferList = loJsonObject.getOfferList();
                        }
                    }
                    fetchOffersStatus.postValue(loJsonObject);
                } else {
                    String fsMessage = Common.getErrorMessage(foResponse);
                    fetchOffersStatus.postValue(new FetchOffersResponse(true, fsMessage));
                }
            }

            @Override
            public void onFailure(Call<FetchOffersResponse> call, Throwable t) {
                Common.printReqRes(t, "getOfferList", Common.LogType.ERROR);
                fetchOffersStatus.postValue(new FetchOffersResponse(true, t.getMessage()));
            }
        });
    }


    public ArrayList<MapMarker> getMapMarkerList() {
        ArrayList<MapMarker> loMapMarkerList = new ArrayList<>();
        if (moOfferList != null && moOfferList.size() > 0) {

            for (Ad loOffer : moOfferList) {
                for (AdLocation loLocation : loOffer.getLocationList()) {
                    MapMarker loMapMarker = new MapMarker();
                    loMapMarker.setAdID(loOffer.getAdID());
                    loMapMarker.setAdName(loOffer.getAdName());
                    loMapMarker.setAdType(loOffer.getAdType());
                    loMapMarker.setPinColor(loOffer.getPinColor());
                    loMapMarker.setProductName(loOffer.getProductName());
                    loMapMarker.setEngagedFlag(loOffer.getEngagedFlag());
                    loMapMarker.setQuizReward(loOffer.getQuizReward());
                    loMapMarker.setSecondReward(loOffer.getSecondReward());
                    loMapMarker.setNormalRewardAmount(loOffer.getNormalRewardAmount());
                    loMapMarker.setLocationID(loLocation.getLocationID());
                    loMapMarker.setLatitude(loLocation.getLatitude());
                    loMapMarker.setLongitude(loLocation.getLongitude());
                    loMapMarker.setLandmark(loLocation.getLandmark());
                    loMapMarker.setAdLogo(loOffer.getLogoUrl());
                    loMapMarker.setDiscountUpTo(loOffer.getDiscountUpTo());
                    loMapMarker.setFlatCashBack(loOffer.getFlatCashBack());
                    loMapMarkerList.add(loMapMarker);
                }
            }
        }
        moMapMarkerList = loMapMarkerList;
        return loMapMarkerList;
    }

    public MarkerOptions getMapMarkerByPosition(int fiPosition) {

        MapMarker loMapMarker = moMapMarkerList.get(fiPosition);

        LatLng loPosition = new LatLng(loMapMarker.getLatitude(), loMapMarker.getLongitude());
        String lsTitle = loMapMarker.getDiscountUpTo();
        String lsSnippet;//loMapMarker.getProductName() + "\t\t₹" + loMapMarker.getQuizReward();
        BitmapDescriptor loPinIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);

        if (loMapMarker.getAdType().contains(Constants.AdType.BANK_OFFER.getValue())) {
            loPinIcon = BitmapDescriptorFactory.defaultMarker(62.0f);
            lsSnippet = loMapMarker.getProductName();
        } else {
            //lsSnippet = "Rs. " + loMapMarker.getQuizReward();
            lsSnippet = loMapMarker.getFlatCashBack();
            if (loMapMarker.isEngagedFlag()) {
                loPinIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE); // Gray
            } else {
                if (loMapMarker.getPinColor().equalsIgnoreCase(Constants.PinColor.RED.getValue())) {
                    loPinIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                } else if (loMapMarker.getPinColor().equalsIgnoreCase(Constants.PinColor.GREEN.getValue())) {
                    loPinIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                }
            }
        }

        return new MarkerOptions()
                .position(loPosition)
                .title(lsTitle)
                .snippet(lsSnippet)
                .icon(loPinIcon);
    }


    public boolean isLocationEnabled(Context foContext) {

        if (!Common.isGPSEnabled(foContext)
                || ContextCompat.checkSelfPermission(foContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void enableGPS(Activity foContext) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createLocationRequest(foContext);
            }
        }, 400);
    }

    public boolean checkGPSEnabled(Activity foContext) {
        if (!Common.isGPSEnabled(foContext)) {
            return false;
        } else {
            checkLocationPermission(foContext);
            return true;
        }

    }

    public final static int REQUEST_CHECK_SETTINGS = 200;
    public static final int MY_PERMISSIONS_LOCATION = 103;
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    Location moCurrentLocation;

    public LocationRequest getLocationRequest(Activity foContext) {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(10000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        return locationRequest;
    }

    public void createLocationRequest(Activity foContext) {

        getLocationRequest(foContext);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(foContext);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(foContext, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                getLastKnownLocation(foContext);
            }
        });

        task.addOnFailureListener(foContext, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                   /* MessageDialog loDialog = new MessageDialog(foContext, null, "GPS location is required to show in-store offers", "Enable GPS", false);
                    loDialog.setClickListener(v -> {
                        loDialog.dismiss();
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(foContext,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    });
                    loDialog.show();*/

                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(foContext,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }

    public void getLastKnownLocation(Activity foContext) {
        int permissionLocation = ContextCompat
                .checkSelfPermission(foContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(foContext);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(foContext, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                moCurrentLocation = location;
                                functionCallStatus.postValue(FETCH_OFFERS);
                            } else {
                                startLocationUpdates(foContext);
                            }
                        }
                    });
        } else
            checkLocationPermission(foContext);
    }


    private void checkLocationPermission(Activity foActivity) {
        if (ContextCompat.checkSelfPermission(foActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(foActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(foActivity, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_LOCATION);
            } else
                ActivityCompat.requestPermissions(foActivity, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_LOCATION);
        } else {
            functionCallStatus.postValue(LOAD_MAP_VIEW);
        }
    }

    public void startLocationUpdates(Activity foContext) {
        int permissionLocation = ContextCompat
                .checkSelfPermission(foContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(foContext);
            fusedLocationClient.requestLocationUpdates(getLocationRequest(foContext),
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    Log.d("TTT", "locationCallback location..." + location);
                    moCurrentLocation = location;
                    stopLocationUpdates();
                    functionCallStatus.postValue(FETCH_OFFERS);
                    break;
                }
            }
        }
    };

    public Location getCurrentLocation() {
        return moCurrentLocation;
    }

}
