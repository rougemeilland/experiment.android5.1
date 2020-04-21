package com.palmtreesoftware.experimentandroid5_1


import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class WeatherSymbolContainer {
    private class WeatherInfo(val iconUrl: Uri, var iconImage: Bitmap?, val description: String)

    private val scope = CoroutineScope(Dispatchers.Default)
    private val imageUpdateHandler: Handler = Handler()
    private var imageUpdateRunnable: Runnable = Runnable {}
    private val updatingWeatherIconIntervalMilliSeconds: Long = 1000 * 1
    private val weatherInfoItems: MutableList<WeatherInfo> = mutableListOf()
    private var currentWeatherViewIndex = 0

    abstract fun onReset()
    abstract fun onUpdate(image: Bitmap?, description: String)

    var dataSource: Array<Pair<Uri, String>>
        get() =
            weatherInfoItems
                .map { Pair(it.iconUrl, it.description) }
                .toTypedArray()
        set(value) {
            weatherInfoItems.clear()
            value.map { WeatherInfo(it.first, null, it.second) }
                .forEach {
                    weatherInfoItems.add(it)
                }
            currentWeatherViewIndex = 0
            weatherInfoItems.forEach {
                AsyncUtility.downloadImage(
                    scope,
                    it.iconUrl,
                    { bitmap ->
                        it.iconImage = bitmap
                    }, { ex ->
                        if (Log.isLoggable(TAG, Log.ERROR)) {
                            Log.e(TAG, ex.message, ex)
                        }
                    }
                )
            }
            requestToUpdateIconImage()
        }

    private fun requestToUpdateIconImage() {
        imageUpdateHandler.removeCallbacks(imageUpdateRunnable)
        imageUpdateRunnable = Runnable {
            imageUpdateHandler.removeCallbacks(imageUpdateRunnable)
            when (weatherInfoItems.size) {
                0 -> {
                    onReset()
                }
                1 -> {
                    val weatherInfo = weatherInfoItems[0]
                    weatherInfoItems[0].let {
                        onUpdate(it.iconImage, it.description)
                    }
                    if (weatherInfo.iconImage == null) {
                        imageUpdateHandler.postDelayed(
                            imageUpdateRunnable,
                            updatingWeatherIconIntervalMilliSeconds
                        )
                    }
                }
                else -> {
                    if (currentWeatherViewIndex >= weatherInfoItems.count())
                        currentWeatherViewIndex = 0
                    weatherInfoItems[currentWeatherViewIndex].let {
                        onUpdate(it.iconImage, it.description)
                    }
                    ++currentWeatherViewIndex
                    imageUpdateHandler.postDelayed(
                        imageUpdateRunnable,
                        updatingWeatherIconIntervalMilliSeconds
                    )
                }
            }
        }
        imageUpdateHandler.post(imageUpdateRunnable)
    }

    companion object {
        private val TAG = "WeatherSymbolView"
    }
}