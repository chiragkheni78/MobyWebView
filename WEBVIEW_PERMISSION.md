# WebView Customization

### 1.Location Permission

#### Gradle Dependency
```java
dependencies{
        implementation'com.google.android.gms:play-services-location:20.0.0'
        }
```
#### Manifest setting
You have to add permission in your `AndroidManifest.xml`
```xml
  <!-- To request foreground location access permission. -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
    <!--If you want to access background location then you have to add below permission.-->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<application android:requestLegacyExternalStorage="true" android:foregroundServiceType="location">

```
-> Set the permission of location in `onRequestPermissionsResult()` Like - 
```java
 case REQUEST_LOCATION_PERMISSIONS:
        Map<String, Integer> perm = new HashMap<>();
        perm.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++)
            perm.put(permissions[i], grantResults[i]);
            // Check for both permissions
            if (perm.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i("Location", "location services permission granted");
    
            checkLocationSettings();
    
            } else{
                Log.i("Location", "Some permissions are not granted ask again ");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                    }});
                } else {
                    if (geolocationCallback != null) {
                        geolocationCallback.invoke(geolocationOrigin, false, true);
                    }
                    Toast.makeText(this, "Go to app settings and enable permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
```
-> Check location request, if permission is granted then return `true`.
```java
private boolean checkAndRequestLocationPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int backLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        /*
         *  Check the permission for background location.
         * */
        if (backLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_LOCATION_PERMISSIONS);
            return false;
        }
        return true;
    }
```
-> call this function whenever you want to set your permission.

-> Check the location setting are enabled or not with below code.
```java
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
```
-> Above function will call when `checkAndRequestLocationPermissions` is true Like - .
```java
 moBinding.ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestLocationPermissions()) {
                    checkLocationSettings();
                }
            }
        });
```
-> Set location service to enabled location For Example-
```java
    private boolean isLocationEnabled() {
        LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
```

-> Get the google play services location using `FusedLocation` provider in `getLocation()` Like -
```java
    private void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationActivity.this,
                        9999);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
```


### 2.Camera Permission

#### Manifest setting for access camera
You have to add permission in your `AndroidManifest.xml`
```xml

<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.autofocus" />
<uses-feature android:name="android.hardware.camera" 
    android:required="true" />
```
-> But does not require it, you should `android:required="false"` If your camera feature work
   properly.

-> Add provider under the application tag.
```xml
<application>
    <provider android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.provider"
        android:exported="false"
        android:grantUriPermissions="true">

        <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
</application>
```
-> add `file_paths.xml` file in `xml` directory of `resource`.
```xml
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <external-path name="external_files" path="." />
    </paths>
  ```

-> Set permission of camera and media access in `onRequestPermissionsResult()` in your activity Like-.
```java
 switch (requestCode) {
    case REQUEST_CAMERA_PERMISSIONS:
        Map<String, Integer> perms = new HashMap<>();
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);

                // check both permissions.
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "both permission are granted");
                    openCamera();
                } else {
                    Log.d(TAG, "Some permissions are not granted ask again ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOk("permission required for this app",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:

                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            checkCameraPermission();
                                            // proceed with logic by disabling the related features or quit the app.
                                            break;
                                    }
                                }
                            });
                    }
                }
            }
        }
 }

```
-> check the camera permission is granted or not in the `checkCameraPermission()` Like-
```java
    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(mActivity.get(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            // set the function to show image
        } else {
            ActivityCompat.requestPermissions(mActivity.get(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS);
        }
    }
```

-> Open Category to select the camera or gallery with below code.
```java
    private void fileChoose(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(this.getPackageManager())!=null){
            // set file path to store image 
        }
        Intent contentSelectionIntent=new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        contentSelectionIntent.setType("image/*");

        Intent[]intentArray;
        if(takePictureIntent!=null){
            intentArray=new Intent[]{takePictureIntent};
        }else{
            intentArray=new Intent[2];
        }

        Intent chooserIntent=new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT,contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE,"Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,intentArray);
        startActivityForResult(Intent.createChooser(chooserIntent,"Select images"),INPUT_FILE_REQUEST_CODE);
    }
```

-> create file where photo should go with below code. add this code under condition of `takePictureIntent`.
```java
    File photoFile=null;
        try{
            photoFile=createImageFile();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        /*
         *  Solve issue in the android version 12.
         * */
        
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            mCameraPhotoPath=
            this.getPackageName()+".provider";
            Uri photoURI=FileProvider.getUriForFile(this,mCameraPhotoPath,photoFile);

            // Continue only if the File was successfully created
            if(photoFile!=null){
                mCameraPhotoPath="file:"+photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        }else{
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
        }
```

-> create a path of file to store the image in `createImageFile()` function.
```java
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
```

#### 3.Open link in browser

If you want to load your link in browser then add the function in your activity like-.
```java
 /*
 * Call java interface.
 */
     webView.addJavascriptInterface(new WebViewJavaScriptInterface(mActivity.get()),"app");
```

-> `mActivity` is a object of `WeakReference<Activity>`.

-> You also need to create sub class for javascript interface in your activity Like-
```java
public class WebViewJavaScriptInterface {
    Context moContext;

    WebViewJavaScriptInterface(Context foContext) {
        moContext = foContext;
    }

    @JavascriptInterface
    public Object openLinkOnBrowser(String message) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
        startActivity(browserIntent);
        return message;
    }
}
```
-> Call this interface in your activity Like - 
```java
  moBinding.btnBrowser.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("JavascriptInterface")
            @Override
            public void onClick(View view) {
                webView.addJavascriptInterface(new WebViewJavaScriptInterface(mActivity.get()), "app");
                moBinding.webView.loadUrl("https://google.com");

            }
        });
```

#### 4.Open menu from native

Open menu from native with below the code.
```java
 moBinding.webview.loadJavaScript("openMenuFromNative('my-coupons')");

    public void loadJavaScript(String javascript) {
       this.evaluateJavascript(javascript, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            
            }
       });
    }
```







    
