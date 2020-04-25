package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


abstract class LocationWatcher(private val context: Context) {

    abstract fun onLocationChanged(locationWatcherResult: LocationWatcherResult)

    private val locationClient by lazy {
        FusedLocationProviderClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let {
                it.lastLocation.let { location ->
                    onLocationChanged(
                        LocationWatcherResult(
                            Coordinates(
                                location.latitude,
                                location.longitude
                            ),
                            location.accuracy.toDouble()
                        )
                    )
                }
            }
            super.onLocationResult(locationResult)
        }
    }

    fun start(
        locationRequestSetting: LocationRequestSetting,
        onFailure: (Exception) -> Unit
    ) {
        locationClient.removeLocationUpdates(locationCallback)
        try {
            if (!checkPermissions(context))
                throw Exception(javaClass.canonicalName + ".checkSetting(): Access is denied")
            checkLocationRequestSetting(
                context, locationRequestSetting,
                {
                    locationClient.requestLocationUpdates(
                        locationRequestSetting.getLocationRequest(context),
                        locationCallback,
                        null
                    )
                },
                { ex -> onFailure(ex) }
            )
        } catch (ex: Exception) {
            onFailure(ex)
        }
    }

    fun stop() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    companion object {

        fun checkLocationRequestSetting(
            context: Context,
            locationRequestSetting: LocationRequestSetting,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            try {
                LocationServices
                    .getSettingsClient(context)
                    .checkLocationSettings(
                        LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequestSetting.getLocationRequest(context))
                            .setAlwaysShow(true)
                            .build()
                    )
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { ex ->
                        onFailure(ex)
                    }
            } catch (ex: Exception) {
                onFailure(ex)
            }
        }

        private fun checkPermissions(context: Context): Boolean {
            return Platform.sdK29Depended(
                @RequiresApi(Build.VERSION_CODES.Q) {
                    arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION/*,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION*/
                    )
                }, {
                    arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                })
                .all {
                    ActivityCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }
        }
    }
}
