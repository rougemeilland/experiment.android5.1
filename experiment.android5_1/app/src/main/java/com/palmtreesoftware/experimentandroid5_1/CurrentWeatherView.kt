package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.current_weather_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CurrentWeatherView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        View.inflate(context, R.layout.current_weather_view, this)
    }

    fun update(
        latitude: Double,
        longitude: Double,
        locale: java.util.Locale,
        onCompleted: (Boolean, DateTime) -> Unit
    ) {
        OpenWeatherMap.CurrentWeatherData.getInstance(
            context,
            scope,
            locale,
            latitude,
            longitude, { current, latestRequested ->
                updateWeatherView(current, latestRequested)
                onCompleted(current.isCached, latestRequested)
            }, { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
            }
        )
        AsyncUtility.getAddressFromLocation(
            context,
            scope,
            latitude,
            longitude,
            { address ->
                if (address != null) {
                    currentWeatherViewCity.text = address.locality
                }
            },
            { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
            }
        )
    }

    private fun updateWeatherView(
        current: OpenWeatherMap.CurrentWeatherData,
        latestRequested: DateTime
    ) {
        TODO("時刻の表示に、本日か昨日か明日かわかるようにしたい")

        val timeZone = TimeZone.getDefault()
        currentWeatherViewRequestedDateTime.text =
            latestRequested.atZone(timeZone).format("HH:mm:ss")
        currentWeatherViewFromCache.visibility = if (current.isCached) View.VISIBLE else View.GONE
        currentWeatherViewObservationDateTime.text =
            current.lastUpdated.atZone(timeZone).format("HH:mm:ss")
        AsyncUtility.getAddressFromLocation(
            context,
            scope,
            current.coord.latitude,
            current.coord.longitude,
            { address ->
                if (address != null) {
                    currentWeatherViewObservationAddress.text =
                        with(address) {
                            (0..maxAddressLineIndex).map { getAddressLine(it) }
                        }.joinToString("\n")
                }
            },
            { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
            }
        )
        current.sys.let {
            if (it.sunrise >= it.sunset) {
                currentWeatherViewWeatherSunriseOrSunsetLabel1.text = "日の入り"
                currentWeatherViewWeatherSunriseOrSunset1.text =
                    it.sunset.atZone(timeZone).format("HH:mm:ss")
                currentWeatherViewWeatherSunriseOrSunsetLabel2.text = "日の出"
                currentWeatherViewWeatherSunriseOrSunset2.text =
                    it.sunrise.atZone(timeZone).format("HH:mm:ss")
            } else {
                currentWeatherViewWeatherSunriseOrSunsetLabel1.text = "日の出"
                currentWeatherViewWeatherSunriseOrSunset1.text =
                    it.sunrise.atZone(timeZone).format("HH:mm:ss")
                currentWeatherViewWeatherSunriseOrSunsetLabel2.text = "日の入り"
                currentWeatherViewWeatherSunriseOrSunset2.text =
                    it.sunset.atZone(timeZone).format("HH:mm:ss")
            }
        }
        currentWeatherViewWeatherSymbol.dataSource =
            current.weathers.map { Pair(it.iconUrl, it.description) }.toTypedArray()
    }

    companion object {
        private val TAG = "CurrentWeatherView"
    }
}