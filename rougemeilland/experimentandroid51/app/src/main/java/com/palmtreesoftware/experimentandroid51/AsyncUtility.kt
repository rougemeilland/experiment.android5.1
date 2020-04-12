package com.palmtreesoftware.experimentandroid51

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
            scope: CoroutineScope,
            latitude: Double,
            longitude: Double,
            callback: (Address) -> Unit
        ) {
            val geocorder = Geocoder(context, Locale.getDefault())
            scope.launch {
                try {
                    geocoderAsync(geocorder, latitude, longitude, callback)
                } catch (ex: Exception) {
                    scope.coroutineContext.cancelChildren()
                }
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun geocoderAsync(
            geocoder: Geocoder,
            latitude: Double,
            longitude: Double,
            callback: (Address) -> Unit
        ) {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    callback(addresses[0])
                }
            }
        }

        fun downloadString(
            scope: CoroutineScope,
            uri: Uri,
            callback: (String) -> Unit
        ) {
            val url = URL(uri.toString())
            scope.launch {
                try {
                    downloadStringAsync(url, callback)
                } catch (ex: Exception) {
                    scope.coroutineContext.cancelChildren()
                }
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun downloadStringAsync(
            url: URL,
            callback: (String) -> Unit
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
                    return
                }
            withContext(Dispatchers.Main) {
                callback(text)
            }
        }

        fun downloadImage(
            scope: CoroutineScope,
            uri: Uri,
            callback: (Bitmap) -> Unit
        ) {
            scope.launch {
                try {
                    downloadImageAsync(URL(uri.toString()), callback)
                } catch (ex: Exception) {
                    scope.coroutineContext.cancelChildren()
                }
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun downloadImageAsync(
            url: URL,
            callback: (Bitmap) -> Unit
        ) {
            try {

                url.openStream().use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    withContext(Dispatchers.Main) {
                        callback(bitmap)
                    }
                }
            } catch (ex: Exception) {
                throw ex
            }
        }
    }
}