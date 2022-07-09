package com.mobycashback.webview;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobyWebviewPermission extends WebView {

    public interface Listener {
        void onPageStarted(String url, Bitmap favicon);

        void onPageFinished(String url);

        void onPageError(int errorCode, String description, String failingUrl);

        void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent);

        void onExternalPageRequest(String url);
    }

    private String geolocationOrigin;
    private GeolocationPermissions.Callback geolocationCallback;
    private static final int INPUT_FILE_REQUEST_CODE = 111;
    private static final int SCAN_QR_REQUEST_CODE = 112;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private long size = 0;

    boolean isGPSSettingCall;
    protected WebViewClient mCustomWebViewClient;
    protected WebChromeClient mCustomWebChromeClient;
    protected WeakReference<Activity> mActivity;
    public static final int REQUEST_LOCATION_PERMISSIONS = 11111;
    public static final int REQUEST_CAMERA_PERMISSIONS = 11113;
    public static final int REQUEST_MULTIPLE_ID_PERMISSIONS = 11112;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest mLocationRequest;
    protected Listener mListener;
    protected long mLastError;
    private boolean isStoragePermission;
    private boolean isCategories;

    public MobyWebviewPermission(Context context) {
        super(context);
        initView(context);
    }

    public MobyWebviewPermission(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MobyWebviewPermission(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void addHttpHeader(final String name, final String value) {
        mHttpHeaders.put(name, value);
    }

    private static final String TAG = "WebViewActivity";
    public ValueCallback<Uri[]> filePathCallBack;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView(Context context) {
        if (context instanceof Activity) {
            mActivity = new WeakReference<Activity>((Activity) context);
        }
        // i am not sure with these inflater lines
        // LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // you should not use a new instance of MyWebView here
        // MyWebview view = (MyWebView) inflater.inflate(R.layout.custom_webview, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setDatabaseEnabled(true);
        // this.getSettings().setGeolocationEnabled(true);
        this.getSettings().getAllowFileAccess();
        this.getSettings().getAllowFileAccessFromFileURLs();
        this.getSettings().getAllowUniversalAccessFromFileURLs();
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setUseWideViewPort(true);
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        this.addJavascriptInterface(new WebViewJavaScriptInterface(mActivity.get()), "app");

        super.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Log.d("TTT", "url..." + url);
                if (mCustomWebViewClient != null) {
                    // if the user-specified handler asks to override the request
                    if (mCustomWebViewClient.shouldOverrideUrlLoading(view, url)) {
                        // cancel the original request
                        //Log.d("TTT", "url..." + url);
                        return true;
                    }
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Log.d("TTT", "url..." + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                setLastError();

                if (mListener != null) {
                    mListener.onPageError(errorCode, description, failingUrl);
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                }
            }
        });
        // this.setWebViewClient(new MyWebViewClientN());
        //  this.setWebChromeClient(new MyWebChromeClient());

        super.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                Log.d("TTT", "onGeolocationPermissionsShowPrompt...");
                geolocationOrigin = origin;
                geolocationCallback = callback;

                if (checkAndRequestLocationPermissions()) {
                    checkLocationSettings();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReceivedTitle(view, title);
                } else {
                    super.onReceivedTitle(view, title);
                }
            }

            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                Log.d("TTT", "onShowFileChooser..");

                if (isStoragePermission) {
                    if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(null);
                    }
                    mUploadMessage = filePath;
                    //checkWritePermission();
                    if (checkAndRequestMultiplePermissions()) {
                        fileChoose();
                    }
                }
                return true;
            }
        });
    }

    public class WebViewJavaScriptInterface {

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openLinkOnBrowser(String message) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
            mActivity.get().startActivity(browserIntent);
        }

        @JavascriptInterface
        public void scanBarcode() {
            checkCameraPermission();
        }

        @JavascriptInterface
        public void openCampaignLink(String fsLink) {
            openBrowser(mActivity.get(), fsLink);
        }

        @JavascriptInterface
        public void triggerGPS() {
            checkLocationSettings();
        }

        @JavascriptInterface
        public void setClipboard(String text) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);

        }
    }

    public void loadJavaScript(String javascript) {
        this.evaluateJavascript(javascript, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }

    public void addJavascript() {

    }


    /* private void requestPermission() {
         if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
             Toast.makeText(mActivity.get(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
         } else {
             ActivityCompat.requestPermissions(mActivity.get(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_MULTIPLE_ID_PERMISSIONS);
         }
     }
 */
    private void fileChoose() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.get().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCameraPhotoPath =
                        getContext().getPackageName() + ".provider";
                Uri photoURI = FileProvider.getUriForFile(getContext(), mCameraPhotoPath, photoFile);

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[2];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        mActivity.get().startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), INPUT_FILE_REQUEST_CODE);

    }


    @SuppressLint("SetJavaScriptEnabled")
    public void setGeolocationEnabled(final boolean enabled) {
        //  isGPSSettingCall = enabled;
        getSettings().setGeolocationEnabled(enabled);
    }

    public void setAccessStorage(boolean isStoragePermission) {
        this.isStoragePermission = isStoragePermission;
    }

    private void scanBarcodeFun() {
        Intent intent = new Intent(mActivity.get(), QrScannerActivity.class);
        mActivity.get().startActivityForResult(intent, SCAN_QR_REQUEST_CODE);
    }

    private void openBrowser(Context foContext, String Url) {
        if (!Url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
            foContext.startActivity(intent);
        }
    }

    private void checkLocationSettings() {
        if (!isLocationEnabled()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isGPSSettingCall = true;
                    getLocation();
                }
            }, 400);
            return;
        }
        getLocation();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                Map<String, Integer> perm = new HashMap<>();
                // perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perm.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perm.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "location services permission granted");

                        checkLocationSettings();

                    } else {
                        Log.i(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity.get(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestLocationPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    if (geolocationCallback != null) {
                                                        geolocationCallback.invoke(geolocationOrigin, false, true);
                                                    }
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            if (geolocationCallback != null) {
                                geolocationCallback.invoke(geolocationOrigin, false, true);
                            }
                            Toast.makeText(mActivity.get(), "Go to app settings and enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            case REQUEST_MULTIPLE_ID_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "both permission granted");
                        fileChoose();
                    } else {
                        Log.i(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity.get(), Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(mActivity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    checkAndRequestMultiplePermissions();
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                           /* if (geolocationCallback != null) {
                                geolocationCallback.invoke(geolocationOrigin, false, true);
                            }*/
                            Toast.makeText(mActivity.get(), "Go to app settings and enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            case REQUEST_CAMERA_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanBarcodeFun();

                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity.get(), Manifest.permission.CAMERA)) {
                        showDialogOK("Camera Permission required for this app",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            checkCameraPermission();
                                            break;
                                    }
                                });
                    } else {
                        Toast.makeText(mActivity.get(), "Go to app settings and enable permissions", Toast.LENGTH_LONG).show();
                    }
                }
                break;
           /* case REQUEST_MULTIPLE_ID_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fileChoose();
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOK("Write External Permission required for this app",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            checkWritePermission();
                                            break;
                                    }
                                });
                    } else {
                        Toast.makeText(mActivity.get(), "Go to app settings and enable permissions", Toast.LENGTH_LONG).show();
                    }
                }
                break;*/
        }
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(mActivity.get(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scanBarcodeFun();

        } else {
            ActivityCompat.requestPermissions(mActivity.get(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS);
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity.get())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity.get());
        SettingsClient settingsClient = LocationServices.getSettingsClient(mActivity.get());

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(mActivity.get(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                if (ActivityCompat.checkSelfPermission(mActivity.get(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mActivity.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                callAPIs();
//                startLocationUpdates();
            }
        });

        task.addOnFailureListener(mActivity.get(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(mActivity.get(),
                                9999);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCAN_QR_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //here is where you get your result
                    String barcode = data.getStringExtra("SCAN_RESULT");
                    this.loadUrl("javascript:nativeQRScanReceiver('" + barcode + "')");
                }
                break;
            case INPUT_FILE_REQUEST_CODE:
                if (mUploadMessage == null) {
                    // mActivity.get().super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                try {
                    String file_path = mCameraPhotoPath.replace("file:", "");
                    File file = new File(file_path);
                    size = file.length();

                } catch (Exception e) {
                    Log.e("Error!", "Error while opening image file" + e.getLocalizedMessage());
                }

                if (data != null || mCameraPhotoPath != null) {
                    Integer count = 0; //fix fby https://github.com/nnian
                    ClipData images = null;
                    try {
                        images = data.getClipData();
                    } catch (Exception e) {
                        Log.e("Error!", e.getLocalizedMessage());
                    }

                    if (images == null && data != null && data.getDataString() != null) {
                        count = data.getDataString().length();
                    } else if (images != null) {
                        count = images.getItemCount();
                    }
                    Uri[] results = new Uri[count];
                    // Check that the response is a good one
                    if (resultCode == RESULT_OK) {
                        if (size != 0) {
                            // If there is not data, then we may have taken a photo
                            if (mCameraPhotoPath != null) {
                                results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                            }
                        } else if (data.getClipData() == null) {
                            results = new Uri[]{Uri.parse(data.getDataString())};
                        } else {

                            for (int i = 0; i < images.getItemCount(); i++) {
                                results[i] = images.getItemAt(i).getUri();
                            }
                        }
                    }

                    mUploadMessage.onReceiveValue(results);
                    mUploadMessage = null;
                }
        }
    }

    private void callAPIs() {
        if (geolocationCallback != null) {
            // call back to web chrome client
            geolocationCallback.invoke(geolocationOrigin, true, false);
        }
        isGPSSettingCall = false;
    }

    private boolean isLocationEnabled() {
        LocationManager locManager = (LocationManager) mActivity.get().getSystemService(Context.LOCATION_SERVICE);
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    @Override
    public void setWebViewClient(final WebViewClient client) {
        mCustomWebViewClient = client;
    }

    @Override
    public void setWebChromeClient(final WebChromeClient client) {
        mCustomWebChromeClient = client;
    }

    private boolean checkAndRequestMultiplePermissions() {
        int permissionExternalStorage = ContextCompat.checkSelfPermission(mActivity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(mActivity.get(), Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity.get(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_MULTIPLE_ID_PERMISSIONS);
            return false;
        }
        return true;
    }

    private boolean checkAndRequestLocationPermissions() {
        //int permissionExternalStorage = ContextCompat.checkSelfPermission(mActivity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(mActivity.get(), Manifest.permission.ACCESS_FINE_LOCATION);
        int backLocationPermission = ContextCompat.checkSelfPermission(mActivity.get(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (backLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
       /* if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity.get(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_LOCATION_PERMISSIONS);
            return false;
        }
        return true;
    }

    protected void setLastError() {
        mLastError = System.currentTimeMillis();
    }

    protected final Map<String, String> mHttpHeaders = new HashMap<String, String>();

    @Override
    public void loadUrl(final String url) {
        if (mHttpHeaders.size() > 0) {
            super.loadUrl(url, mHttpHeaders);
        } else {
            super.loadUrl(url);
        }
    }

    public void setListener(final Activity activity, final Listener listener) {
        mListener = listener;
        if (activity != null) {
            mActivity = new WeakReference<Activity>(activity);
        } else {
            mActivity = null;
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onResume() {
        if (isGPSSettingCall) {
            checkLocationSettings();
        }
        super.onResume();
    }
}
