package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.vertical_weather_symbol_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

// TODO("画像の幅がこのコントロールの幅を無視してすごく大きく表示される。原因調査。")

class VerticalWeatherSymbolView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    private class WeatherInfo(val iconUrl: Uri, var iconImage: Bitmap?, val description: String)

    private val scope = CoroutineScope(Dispatchers.Default)
    private val imageUpdateHandler: Handler = Handler()
    private var imageUpdateRunnable: Runnable = Runnable {}
    private val updatingWeatherIconIntervalMilliSeconds: Long = 1000 * 1
    private val weatherInfoItems: MutableList<WeatherInfo> = mutableListOf()
    private var currentWeatherViewIndex = 0

    init {
        View.inflate(context, R.layout.vertical_weather_symbol_view, this)
    }

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
                    weather_symbol_view_image.setImageBitmap(null)
                    weather_symbol_view_image.contentDescription = ""
                    weather_symbol_view_description.text = ""
                }
                1 -> {
                    val weatherInfo = weatherInfoItems[0]
                    setView(weatherInfoItems[0])
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
                    setView(weatherInfoItems[currentWeatherViewIndex])
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

    private fun setView(weatherInfo: WeatherInfo) {
        if (weatherInfo.iconImage != null)
            weather_symbol_view_image.setImageBitmap(weatherInfo.iconImage)
        weather_symbol_view_image.contentDescription = weatherInfo.description
        weather_symbol_view_description.text = weatherInfo.description
    }

    companion object {
        private val TAG = "WeatherSymbolView"
    }
}