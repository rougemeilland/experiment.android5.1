package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.net.URL
import java.util.*

class AsyncUtility {
    companion object {
        fun getAddressFromLocation(
            context: Context,
            locale: Locale,
            scope: CoroutineScope,
            latitude: Double,
            longitude: Double,
            onCompleted: (Address?) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            val geocorder = Geocoder(context, locale)
            scope.launch {
                try {
                    geocoderAsync(geocorder, latitude, longitude, onCompleted, onFailed)
                } catch (ex: Exception) {
                    onFailed(ex)
                    scope.coroutineContext.cancelChildren()
                }
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun geocoderAsync(
            geocoder: Geocoder,
            latitude: Double,
            longitude: Double,
            onCompleted: (Address?) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                withContext(Dispatchers.Main) {
                    onCompleted(if (addresses.isEmpty()) null else addresses[0])
                }
            } catch (ex: Exception) {
                onFailed(ex)
                return
            }
        }

        fun downloadString(
            scope: CoroutineScope,
            uri: Uri,
            onCompleted: (String) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            val url = URL(uri.toString())
            scope.launch {
                try {
                    downloadStringAsync(url, onCompleted, onFailed)
                } catch (ex: Exception) {
                    onFailed(ex)
                    scope.coroutineContext.cancelChildren()
                }
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun downloadStringAsync(
            url: URL,
            onCompleted: (String) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            val text =
                try {
                    InputStreamReader(
                        url.openConnection().getInputStream(),
                        "UTF-8"
                    ).use {
                        it.readText()
                    }
                } catch (ex: Exception) {
                    onFailed(ex)
                    return
                }
            withContext(Dispatchers.Main) {
                onCompleted(text)
            }
        }

        fun downloadImage(
            scope: CoroutineScope,
            uri: Uri,
            onCompleted: (Bitmap) -> Unit,
            onFailed: (Exception) -> Unit
        ) {
            scope.launch {
                try {
                    downloadImageAsync(URL(uri.toString()), onCompleted, onFailed)
                } catch (ex: Exception) {
                    onFailed(ex)
                    scope.coroutineContext.cancelChildren()
                }
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
                onFailed(ex)
                return
            }
        }
    }
}