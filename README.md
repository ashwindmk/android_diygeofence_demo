# DIY Geofence Demo App

Android custom geofence library developed to overcome the drawbacks of Android's native geofencing framework.


## Download the Library

Add the following libraries in your `app/build.gradle` file.

```gradle
dependencies {
    ...

    // Location
    implementation 'com.google.android.gms:play-services-location:16.0.0'

    // DIY-Geofence
    implementation 'com.ashwin.android:diy-geofence:0.0.9'
}
```


## Create Geofence Listener

```kotlin
class MyGeofenceListener : GeofenceListener {
    override fun onEnter(context: Context, id: String) {
        ...
    }

    override fun onExit(context: Context, id: String) {
        ...
    }
}
```


## Initialize diy-geofence library in your application class

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super()
        DiyGeofenceManager.init(this, BuildConfig.DEBUG, MyGeofenceListener(), true)
        ...
    }
}
```


## Location Permission

It is necessary for the app to have location permission granted by the user.

Diy-geofence library will not function until the app gets permission to access location.


## Add Geofence

```kotlin
val id = "home"
val lat = 19.185
val lng = 72.085
val rad = 500.0
DiyGeofenceManager.addGeofence(this, id, lat, lng, rad)
```


## Remove Geofence

```kotlin
val id = "home"
DiyGeofenceManager.removeGeofence(this, id)
```
