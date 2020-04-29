package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.net.URL

abstract class AsyncUtility {
    companion object {
        fun <RESULT_T> runAasynchronously(
            scope: CoroutineScope,
            beforeWorking: () -> Any = { },
            worker: (Any, reporter: (Any) -> Unit) -> RESULT_T,
            progress: (Any) -> Unit = { },
            afterWorking: (RESULT_T) -> Unit = { },
            onFailed: (Throwable) -> Unit = { throwable ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, throwable.message, throwable)
                }
            }
        ) {
            scope.launch(CoroutineExceptionHandler { _, throwable -> onFailed(throwable) }) {
                val parameter = beforeWorking()
                val result = withContext(context = Dispatchers.IO) {
                    worker(parameter) { status ->
                        @Suppress("DeferredResultUnused")
                        async(context = Dispatchers.Main) {
                            progress(status)
                        }
                    }
                }
                afterWorking(result)
            }
        }

        fun getAddressFromLocation(
            context: Context,
            scope: CoroutineScope,
            locale: java.util.Locale,
            coordinates: Coordinates,
            onCompleted: (Address?) -> Unit,
            onFailed: (Throwable) -> Unit
        ) {
            runAasynchronously(
                scope,
                beforeWorking = {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.INTERNET
                        ) != PackageManager.PERMISSION_GRANTED
                    )
                        throw Exception("${AsyncUtility::class.java.canonicalName}.getAddressFromLocation(): Not granted Manifest.permission.INTERNET")
                    Geocoder(context, locale)
                },
                worker = { geocoder, _ ->
                    geocoder as Geocoder
                    geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
                },
                afterWorking = { addresses ->
                    onCompleted(if (addresses.isEmpty()) null else addresses[0])
                },
                onFailed = { ex ->
                    onFailed(ex)
                }
            )
        }

        fun downloadString(
            scope: CoroutineScope,
            uri: Uri,
            onCompleted: (String) -> Unit,
            onFailed: (Throwable) -> Unit
        ) {
            runAasynchronously(
                scope,
                beforeWorking = {
                    URL(uri.toString())
                },
                worker = { url, _ ->
                    url as URL
                    InputStreamReader(
                        url.openConnection().getInputStream(),
                        "UTF-8"
                    ).use {
                        it.readText()
                    }
                },
                afterWorking = { text ->
                    onCompleted(text)
                },
                onFailed = { throwable ->
                    onFailed(throwable)
                }
            )
        }

        fun downloadImage(
            scope: CoroutineScope,
            uri: Uri,
            onCompleted: (Bitmap) -> Unit,
            onFailed: (Throwable) -> Unit
        ) {
            runAasynchronously(
                scope,
                beforeWorking = {
                    URL(uri.toString())
                },
                worker = { url, _ ->
                    url as URL
                    url.openStream().use {
                        BitmapFactory.decodeStream(it)
                    }
                },
                afterWorking = { bitmap ->
                    onCompleted(bitmap)
                },
                onFailed = { throwable ->
                    onFailed(throwable)
                }
            )
        }

        private val TAG = AsyncUtility::class.java.name
    }
}