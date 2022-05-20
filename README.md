# Android On Demand Delivery Sample App
This is sample repo for on demand delivery feature

Table of Contents:
1. [Pre-requisites](#pre-requisites)
2. [Introduction](#introduction)
3. [Configure Instant Dynamic Feature Module](#configure-instant-dynamic-feature-module)
4. [Configure On Demand Dynamic Feature Module](#configure-on-demand-dynamic-feature-module)
5. [Download and Call Dynamic Feature from Base Module](#download-and-call-dynamic-feature-from-base-module)
6. [Setup Google Play Console for Instant App](#setup-google-play-console-for-instant-app)
7. [Screenshot](#screenshot)
8. [Question and Answer](#question-and-answer)
9. [Contributors](#contributors)

# Pre-requisites
- Android Studio 3.5 or higher
- Setup Google Play Core inside your `build.gradle`
```groovy
api "com.google.android.play:core:1.10.3"
```
- Access to Play Console in order to test the on-demand features

# Introduction
This demo app will explain to you how to configure instant and on demand feature app

There's 3 feature module that are presented inside the app:
1. instantmodule (Instant Dynamic Feature Module)
2. separatemoudle (On Demand Dynamic Feature Module)
3. bigvideo (On Demand Dynamic Feature Module - Large size 30MB)

# Configure Instant Dynamic Feature Module
With instant dynamic feature, user can try the app without needing to install APK(s) on their device.
They can experience the app through the **Try Now** button on the Google Play Store.

There is some criteria which you need to satisfy to use it:
1. The app maximum size (base app module and instant feature module) must be at most 10 MB
2. No background services

To create instant module from Android Studio, follow this step:
1. Select **File** > **New** > **Module** from Menu Bar.
2. In the **Create New Module** dialog, select **Instant Dynamic Feature Module** and click **Next**.

![](ReadmeImage/ConfigureInstant1.PNG?raw=true)

3. Specify module name, package name, language, minimum SDK, and module title. Then click **Finish**.

For Fusing, we will leave it as unchecked because we don't support pre-lollipop devices.

![](ReadmeImage/ConfigureInstant2.PNG?raw=true)

After that, android studio will automatically setup the instant module for your app
- `build.gradle` of base module to establish a relationship
```groovy
dynamicFeatures = [':feature:instantmodule']
```

- `AndroidManifest.xml` in base module
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:dist="http://schemas.android.com/apk/distribution"
    package="com.linecorp.id.ondemanddelivery">

    <dist:module dist:instant="true" />
```
- `AndroidManifest.xml` in Instant Module
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:dist="http://schemas.android.com/apk/distribution"
    package="com.linecorp.id.ondemanddelivery.feature.instantmodule">

    <dist:module
        dist:instant="true"
        dist:title="@string/title_instantmodule">
        <dist:delivery>
            <dist:install-time />
        </dist:delivery>
        <dist:fusing dist:include="false" />
    </dist:module>
```

For more detail information to configure Instant Delivery, you can read it at
https://developer.android.com/guide/app-bundle/instant-delivery

# Configure On Demand Dynamic Feature Module
With instant dynamic feature, user can download the base app at minimum size and later download and install those components on demand.

To create on demand module from Android Studio, follow this step:
1. Select **File** > **New** > **Module** from Menu Bar.
2. In the **Create New Module** dialog, select Instant Dynamic Feature Module and click **Next**.

![](ReadmeImage/ConfigureOnDemand1.PNG?raw=true)

3. Specify module name, package name, language, minimum SDK and click **Next**.

![](ReadmeImage/ConfigureOnDemand2.PNG?raw=true)

4. Specify module title and click **Finish**.

For Fusing, we will leave it as unchecked because we don't support pre-lollipop devices.

![](ReadmeImage/ConfigureOnDemand3.PNG?raw=true)

After that, Android Studio will automatically setup the on demand module for your app
- `build.gradle` of base module to establish a relationship
```groovy
dynamicFeatures = [':feature:separatemodule']
```

- `AndroidManifest.xml` in On Demand Module
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:dist="http://schemas.android.com/apk/distribution"
    package="com.linecorp.id.ondemanddelivery.feature.separatemodule">

    <dist:module
        dist:instant="false"
        dist:title="@string/title_separatemodule">
        <dist:delivery>
            <dist:on-demand />
        </dist:delivery>
        <dist:fusing dist:include="false" />
    </dist:module>
```

For more detail information to configure On Demand Delivery, you can read it at
https://developer.android.com/guide/app-bundle/on-demand-delivery

# Download and Call Dynamic Feature from Base Module
Before you start adding activiy, asset, etc. You need to configure the Application and setup some base activity class:
1. Create `OnDemandApplication` class and add it to `AndroidManifest`
```kotlin
class OnDemandApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}
```
```kotlin
<application
        android:name=".OnDemandApplication"
```
2. Create `BaseSplitActivity` (will use it later for base activity class)
```kotlin
abstract class BaseSplitActivity : AppCompatActivity() {

    override fun attachBaseContext(ctx: Context?) {
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
    }
}
```
3. Create `feature_names.xml` to list all the module name that is available
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="feature_name_separatemodule">separatemodule</string>
</resources>
```
4. Extend the `BaseSplitActivity` to `MainActivity`
```kotlin
class MainActivity : BaseSplitActivity() {

}
```
5. Create `PageSeparateActivity` inside your Dynamic Feature Module `separatemodule`.

**Note**: When creating the dynamic feature activity, you need to extend it from `BaseSplitActivity` class.
```kotlin
class PageSeparateActivity : BaseSplitActivity() {

}
```

After that the app is ready to download and call dynamic feature. Open your `MainActivity` and follow this step:
1. Create `SplitInstallManager`
```kotlin
private lateinit var manager: SplitInstallManager
```
2. Initialize `manager` in your `onCreate` method
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    manager = SplitInstallManagerFactory.create(this)
}
```
3. Create button `buttonOnDemandPageSeparate` in `activity_main.xml` (this button will download and call dynamic feature)
```xml
<Button
    android:id="@+id/buttonOnDemandPageSeparate"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:padding="16dp"
    android:gravity="center"
    android:text="On Demand Separate Page" />
```
4. Create `separateModuleName` and assign event to the button inside `onCreate` menthod
```kotlin
private val separateModuleName by lazy { getString(R.string.feature_name_separatemodule) }
```
```kotlin
binding.buttonOnDemandPageSeparate.setOnClickListener { loadAndLaunchModule(separateModuleName) }
```
5. Create `loadAndLaunchModule` method
```kotlin
private fun loadAndLaunchModule(name: String) {
    //Check if module has been installed
    if (manager.installedModules.contains(name)) {
        launchActivityWithModuleName(name)
        return
    }

    //Request install request
    val request = SplitInstallRequest.newBuilder()
        .addModule(name)
        .build()

    //Start download immediately
    manager.startInstall(request)
}
```
6. Create `SplitInstallStateUpdatedListener` listener
```kotlin
private val listener = SplitInstallStateUpdatedListener { state ->
    val multiInstall = state.moduleNames().size > 1
    val names = state.moduleNames().joinToString(" - ")

    when (state.status()) {
        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
            //In some cases, Google Play may require user confirmation before satisfying a download request. For example, if your app has not been installed by Google Play
            manager.startConfirmationDialogForResult(state, this, CONFIRMATION_REQUEST_CODE)
        }
        SplitInstallSessionStatus.INSTALLED -> {
            onSuccessfulLoad(names, launch = !multiInstall)
        }
    }
}
```
7. Register and unregister listener in `onResume` and `onPause` function
```kotlin
override fun onResume() {
    manager.registerListener(listener)
    super.onResume()
}

override fun onPause() {
    manager.unregisterListener(listener)
    super.onPause()
}
```
8. Launch activity upon success
```kotlin
private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
    if (launch) {
        if (manager.installedModules.contains(moduleName)) {
            launchActivityWithModuleName(moduleName)
        }
        else {
            Toast.makeText(this,
                "This feature is only available in Downloaded App version. Please install the app from PlayStore.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

private fun launchActivityWithModuleName(moduleName: String) {
    when (moduleName) {
        instantModuleName -> launchActivity(INSTANT_FEATURE_CLASSNAME)
        separateModuleName -> launchActivity(SEPARATE_FEATURE_CLASSNAME)
        bigVideoName -> launchActivity(BIG_VIDEO_CLASSNAME)
    }
}

private fun launchActivity(className: String) {
    val intent = Intent().setClassName(BuildConfig.APPLICATION_ID, className)
    startActivity(intent)
}
```

To see all the detail and validation, you can open [MainActivity](app/src/main/java/com/linecorp/id/ondemanddelivery/MainActivity.kt)

# Setup Google Play Console for Instant App
To be able to upload the instant app in Google Play Console, follow this step:
1. Select your app, then go to **Setup** > **Advanced settings** in `Release` Menu.
2. Select `Release Types` tabulation.
3. Click `Add release type` and Choose `Google Play Instant`.
4. Then `Google Play Instant` will be added to `Release Types` option.

![](ReadmeImage/SetupInstant.PNG?raw=true)

5. Now when you are about to upload abb in `Create New Release`, there will be option to choose.
6. Choose `Standard` to upload the Download App or choose `Instant apps only` for Instant App

![](ReadmeImage/SetupInstant2.PNG?raw=true)

# Screenshot
- **Try Now** button for Instant App

![](ReadmeImage/PlayStoreTryNow.jpg?raw=true)
![](ReadmeImage/PlayStoreInstantInstalling.jpg?raw=true)

- Instant App

![](ReadmeImage/InstantInstalling.jpg?raw=true)
![](ReadmeImage/InstantModuleSuccess.jpg?raw=true)

- Instant Module

![](ReadmeImage/Screenshot3.PNG?raw=true)

- Downloading Feature

![](ReadmeImage/Screenshot2.PNG?raw=true)

- On Demand Feature - Big Video

![](ReadmeImage/Screenshot4.PNG?raw=true)
![](ReadmeImage/Screenshot5.PNG?raw=true)

# Question and Answer
**Q: How to update Dynamic Feature module?**

**A: To update it, follow this step:**
1. Open `build.gradle` of the dynamic feature
2. Update `versionCode` and/or `versionName`
```groovy
defaultConfig {
    applicationId "com.linecorp.id.ondemanddelivery.feature.separatemodule"
    minSdkVersion 24
    targetSdkVersion 31
    versionCode 2
    versionName "1.0.1"

    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
}
```
3. You also need to update the app version to be able to upload it to PlayStore
```groovy
defaultConfig {
    applicationId "com.linecorp.id.ondemanddelivery"
    minSdkVersion 24
    targetSdkVersion 31
    versionCode 4
    versionName "1.1.1"

    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
}
```

- When app is updated, user will only update all the module they have installed (Base App size: 2.3MB)

![](ReadmeImage/PlayStoreTryNow.jpg?raw=true)
![](ReadmeImage/PlayStoreUpdate.jpg?raw=true)

- Updating app that has installed all dynamic feature (Downloading app size: 33.70MB)

![](ReadmeImage/PlayStoreUpdating.jpg?raw=true)

=========================================

**Q: How to implement `Dynamic Feature module` to app which has `build variant`?**

**A: The source code is now updated with `build variant` implementation to it.**
When app has `build variant`, all dynamic feature module need to have the same `build variant`.
You can read more detail information in https://android.jlelse.eu/dynamic-feature-module-with-product-flavors-and-app-bundle-c246640eb64d

When building the app, make sure you set all the `build variant` of app and dynamic feature to be the same

# Contributors

- [@ferryyuwono](https://github.com/ferryyuwono)
- [@aldoKelvianto](https://github.com/aldoKelvianto)
