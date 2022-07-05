# MobyCashbackWebView


#### Beautiful and customizable Android method for webview

* Builder pattern
* Dynamic url
* Location Enability
* Storage Permission
* Custom toolbar text color
* Custom background color for toolbar
* Category Permission

## Getting started

#### Gradle Dependency (jcenter)

Add it in your root settings.gradle at the end of repositories:

```java
	dependencyResolutionManagement {
        repositories {
			...
            jcenter()
			maven { url 'https://jitpack.io' }
		}
	}
 ```
 
#### Gradle Dependency (jcenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file.

```java
dependencies {
    implementation 'com.github.chiragkheni78:MobyWebView:V1.0.8'
}
```


#### Manifest Settings

You have to add permission in your `AndroidManifest.xml`

```xml
<application
  android:requestLegacyExternalStorage="true">
```

#### Basic WebView

```java
MobyWebViewBuilder builder = new MobyWebViewBuilder().setUrl(url);
builder.loadWebView();
```


## Customization


#### GPS permission
```java
Enable GPS permission with below code.
setAccessGPS(true)
```

#### Storage permission
```java
Enable storage permission with below code.
setAccessStorage(true)
```

#### Toolbar Color
```java
Toolbar color will be set as `setPrimaryColor`.
setPrimaryColor("color")
```


#### Toolbar Menu Color
```java
Toolbar menu color will be set as `setSecondaryColor`.
setSecondaryColor("color")
```

#### Toolbar Text Color
```java
Toolbar color will be set as `setPrimaryTextColor`.
setPrimaryTextColor("color")
```

#### Toolbar Secondary Text Color
```java
Toolbar color will be set as `setSecondaryTextColor`.
setSecondaryTextColor("color")
```

#### 
```java
Toolbar color will be set as `setSecondaryTextColor`.
setSecondaryTextColor("color")
```

#### Category permission
```java
Show Category function is used for enable 'category screen' before webview. 
If you set true then the category screen visible else its not appear and 'direct display webview'.
You can use below function for set category screen.
setCategories(true)
```
# For Custom WebView Permission
You Can See this [Documentation](https://github.com/chiragkheni78/MobyWebView/blob/main/WEBVIEW_PERMISSION.md)

