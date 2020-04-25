package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.URL
import java.util.*

abstract class AsyncUtility {
    companion object {
        fun getAddressFromLocation(
            context: Context,
            locale: Locale,
            scope: CoroutineScope,
            coordinates: Coordinates,
            onCompleted: (Address?) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.INTERNET
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    throw Exception("${AsyncUtility::class.java.canonicalName}.getAddressFromLocation(): Not granted Manifest.permission.INTERNET")
                val geocorder = Geocoder(context, locale)
                scope.launch {
                    geocoderAsync(geocorder, coordinates, onCompleted, onFailed)
                }
            } catch (ex: Exception) {
                onFailed(ex)
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun geocoderAsync(
            geocoder: Geocoder,
            coordinates: Coordinates,
            onCompleted: (Address?) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                val addresses =
                    geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
                withContext(Dispatchers.Main) {
                    onCompleted(if (addresses.isEmpty()) null else addresses[0])
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    onFailed(ex)
                }
            }
        }

        fun downloadString(
            scope: CoroutineScope,
            uri: Uri,
            onCompleted: (String) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                val url = URL(uri.toString())
                scope.launch {
                    downloadStringAsync(url, onCompleted, onFailed)
                }
            } catch (ex: Exception) {
                onFailed(ex)
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun downloadStringAsync(
            url: URL,
            onCompleted: (String) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                val text =
                    InputStreamReader(
                        url.openConnection().getInputStream(),
                        "UTF-8"
                    ).use {
                        it.readText()
                    }
                withContext(Dispatchers.Main) {
                    onCompleted(text)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    onFailed(ex)
                }
            }
        }

        fun downloadImage(
            scope: CoroutineScope,
            uri: Uri,
            onCompleted: (Bitmap) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                scope.launch {
                    downloadImageAsync(URL(uri.toString()), onCompleted, onFailed)
                }
            } catch (ex: Exception) {
                onFailed(ex)
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun downloadImageAsync(
            url: URL,
            onCompleted: (Bitmap) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                url.openStream().use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    withContext(Dispatchers.Main) {
                        onCompleted(bitmap)
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    onFailed(ex)
                }
            }
        }
    }
}