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
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <!--If you want to access background location then you have to add below permission.-->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<application android:requestLegacyExternalStorage="true" 
    android:foregroundServiceType="location">
```

Add settings for webview to access storage in `onCreate()` method in your activity.
```java
  WebView webView=findViewById(R.id.webView);
  WebSettings settings=webView.getSettings();
  settings.setJavaScriptEnabled(true);
  settings.setDomStorageEnabled(true);
  settings.setDatabaseEnabled(true);
        
        
/* Set global variable */
private String geolocationOrigin;
private GeolocationPermissions.Callback geolocationCallback;
public static final int REQUEST_LOCATION_PERMISSIONS = 101;
FusedLocationProviderClient fusedLocationClient;
LocationRequest mLocationRequest;
boolean isGPSSettingCall;
```

Set the permission of location in `onRequestPermissionsResult()` in the activity Like -
```java
switch (requestCode){
    case REQUEST_LOCATION_PERMISSIONS:
        Map<String, Integer> perm=new HashMap<>();
        perm.put(Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
        if(grantResults.length>0){
            for(int i=0;i<permissions.length;i++)
                perm.put(permissions[i],grantResults[i]);

            // Check for both permissions
            if(perm.get(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                Log.i("Location","location services permission granted");
                checkLocationSettings();
            }else{
                Log.i("Location","Some permissions are not granted ask again ");
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    showDialogOK("Location Services Permission required for this app",
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            switch(which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkAndRequestLocationPermissions();
                                break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    /*
                                     *  set geoLocationCallback.
                                     * */
                                    if(geolocationCallback!=null){
                                        geolocationCallback.invoke(geolocationOrigin,false,true);
                                    }
                                    // proceed with logic by disabling the related features or quit the app.
                                break;
                            }
                        }});
                }else{
                    if(geolocationCallback!=null){
                        geolocationCallback.invoke(geolocationOrigin,false,true);
                    }
                    Toast.makeText(this,"Go to app settings and enable permissions",Toast.LENGTH_LONG).show();
                }
            }
        }
    break;
}
```

To check the location setting are enabled or not in activity with below code.
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

Create `onGeolocationPermissionsShowPrompt()` under the `setWebChromeClient()` in onCreate().
Under this function you can call the `checkLocationSettings()` in your activity.
```java
    moBinding.webView.setWebChromeClient(new WebChromeClient(){
       @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            geolocationOrigin = origin;
            geolocationCallback = callback;
            if (checkAndRequestLocationPermissions()) {
                checkLocationSettings();
            }
        }
    });
```

Set location service to enabled location in activity For Example-
```java
    private boolean isLocationEnabled(){
        LocationManager locManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
```

Get the google play services location using `FusedLocation` provider in `getLocation()` in the activity Like -
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
            callAPIs();
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

Set geoLocationCallback in `callApis()` function in your activity.
```java
 private void callAPIs() {
    if (geolocationCallback != null) {
        // call back to web chrome client
        geolocationCallback.invoke(geolocationOrigin, true, false);
    }
    isGPSSettingCall = false;
}
```

Check location request, if permission is granted then return `true` in the activity Like-.
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
Add below code to set the force enable location permission in the activity.
```java
 public void onResume() {
        if (isGPSSettingCall) {
            checkLocationSettings();
        }
        super.onResume();
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
Does not require it, you should `android:required="false"` If your camera feature work properly.

Add provider under the application tag.
```xml

<application>
    <provider android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.provider" android:exported="false"
        android:grantUriPermissions="true">

        <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
</application>
```

add `file_paths.xml` file in `xml` directory of `resource`.
```xml

<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="." />
</paths>
  ```

Add settings of webview to access storage or file in `onCreate()` method in your activity.
```java
 WebSettings settings = webView.getSettings();
 settings.setJavaScriptEnabled(true);
 settings.setDomStorageEnabled(true);
 settings.setDatabaseEnabled(true);
 settings.getAllowFileAccess();
 settings.getAllowFileAccessFromFileURLs();
 
 /* Set Global variable */
public static final int REQUEST_CAMERA_PERMISSIONS = 102;
private static final int INPUT_FILE_REQUEST_CODE = 111;
public static final int REQUEST_MULTIPLE_ID_PERMISSIONS = 112;
String mCameraPhotoPath = null;
private ValueCallback<Uri[]> mUploadMessage;
private long size = 0;
```

Set permission to access camera and media in `onRequestPermissionsResult()` in your activity Like-.
```java
switch(requestCode){
    
    /*
     * Set multiple permission camera and media.
     * */
    case REQUEST_MULTIPLE_ID_PERMISSIONS:
        Map<String, Integer> perms = new HashMap<>();
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
    
            // check both permissions.
            if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "both permission are granted");
                fileChoose();
            } else {
                Log.d("TAG", "Some permissions are not granted ask again ");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialogOK("permission required for this app",
                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                            case DialogInterface.BUTTON_NEGATIVE:
                                checkAndRequestCameraPermissions();
                                // proceed with logic by disabling the related features or quit the app.
                            break;
                        }
                    }});
                }
            }
        }
    break;
            
    /*
     * set the camera permission
     * */
     case REQUEST_CAMERA_PERMISSIONS:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
 ```

Open Category for select the camera or gallery in activity with below code.
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

create file where photo should go with below code. add this code under condition of `takePictureIntent` in fileChoose() function.
```java
    File photoFile=null;
    try{
        photoFile=createImageFile();
    }catch(IOException e){
        e.printStackTrace();
    }
    /*
     *  Solve issue in the android version 12 with below code.
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

To set input file write a below code in `onActivityResult()` of your activity. 
```java
switch (requestCode){
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
```

check the camera permission is granted or not in the `checkCameraPermission()` in activity Like-
```java
private void checkCameraPermission() {
    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        // do something
    } else {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS);
    }
}
```

To create a path of file to store the image in `createImageFile()` function in activity.
```java
 private File createImageFile()throws IOException{
    // Create an image file name
    String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName="JPEG_"+timeStamp+"_";
    File storageDir=Environment.getExternalStoragePublicDirectory(
    Environment.DIRECTORY_PICTURES);
    File imageFile=File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );
    return imageFile;
 }
```

Check camera and gallery request, if permission is granted then return `true` in activity.
```java
private boolean checkAndRequestCameraPermissions(){
    int loPermissionStorage=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int loCameraPerm=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

    List<String> loPermissionList=new ArrayList<>();
    if(loCameraPerm!=PackageManager.PERMISSION_GRANTED){
        loPermissionList.add(Manifest.permission.CAMERA);
    }
    if(loPermissionStorage!=PackageManager.PERMISSION_GRANTED){
        loPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    if(!loPermissionList.isEmpty()){
        ActivityCompat.requestPermissions(this,loPermissionList.toArray(new String[loPermissionList.size()]),
        REQUEST_CAMERA_PERMISSIONS);
        return false;
    }
    return true;
}
```

Add `onShowFileChooser()` under the `setWebChromeClient()` in onCreate() method of your activity.
Under this function you can call the `checkAndRequestCameraPermissions()` in your activity Like-.
```java
moBinding.webView.setWebChromeClient(new WebChromeClient(){
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
        super.onShowFileChooser(webView, filePath, fileChooserParams);
        mUploadMessage = filePath;
        if (checkAndRequestCameraPermissions()) {
            fileChoose();
        }
        return true;
    }
});
```


#### 3.Open link in browser

#### Manifest setting to open link in browser 

You have to add permission in your `AndroidManifest.xml`

```xml

<uses-permission android:name="android.permission.INTERNET" />
```

If you want to load your link in browser then add the function in your activity like-.
```java
 /*
 * Call java interface.
 */
   webView.addJavascriptInterface(new WebViewJavaScriptInterface(mActivity.get()),"app");
   moBinding.webView.loadUrl(url);
```

You can also use `MainActivity.this` instead of `mActivity.get()` if not work properly `mActivity.get()`.
mActivity is a object of `WeakReference<Activity>` which is declare as a global variable.

Create sub class for javascript interface in your activity Like-
```java
public class WebViewJavaScriptInterface {
    Context moContext;

    WebViewJavaScriptInterface(Context foContext) {
        moContext = foContext;
    }

    /*
     * Using below function open link in browser.
     * */
    @JavascriptInterface
    public Object openLinkOnBrowser(String message) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
        startActivity(browserIntent);
        return message;
    }
}
```
Also add the `setWebChromeClient()` in onCreate() of your activity Like-
```java
moBinding.webView.setWebChromeClient(new WebChromeClient(){
    /*
    * This function will be show alert dialog.
    */
    @Override
    public boolean onJsAlert(WebView view,String url,String message,JsResult result){
        return super.onJsAlert(view,url,message,result);
    }
}
```

#### 4.Open menu from native

#### Manifest setting for access open menu from native

You have to add permission in your `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
If you want open menu from native then just add below code whenever you want to call either click on button or onCreate() in your activity like below.
```java
 webView.evaluateJavascript("openMenuFromNative('my-offers')", null);
```

#### Set Common Alert Dialog
To show the custom alert dialog in the activity with below code.
```java
private void showDialogOK(String message,DialogInterface.OnClickListener okListener){
    new AlertDialog.Builder(mActivity.get())
    .setMessage(message)
    .setPositiveButton("OK",okListener)
    .setNegativeButton("Cancel",okListener)
    .create()
    .show();
}
```
