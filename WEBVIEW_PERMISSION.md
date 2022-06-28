# Permission

#### Runtime permission

* Location Permission
* Camera Permission
* Open link in browser
* Open menu from native

## Getting Started


### Location Permission

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

If you want to access background location then you have to add below permission.
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

<application android:requestLegacyExternalStorage="true" 
 android:foregroundServiceType="location">

```
### Camera Permission

#### Manifest setting for access camera

You have to add permission in your `AndroidManifest.xml`
```xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-feature android:name="android.hardware.camera"
    android:required="true"/>
- But does not require it, you should `android:required="false"` If your camera feature work properly.

- When you store image in sd card or internal storage and find some problem then you should add below permission.
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

#### Open link in browser
 If you want to load your link in browser then add the function in your activity like-.
```java
   public void openLinkOnBrowser(String message) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
        mActivity.get().startActivity(browserIntent);
        }
```

#### Open menu from native
Open menu from native with below the code.
```java
    webview.loadUrl("your url")
```







    
