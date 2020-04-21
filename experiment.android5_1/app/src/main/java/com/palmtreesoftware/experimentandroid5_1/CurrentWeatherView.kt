package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.current_weather_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CurrentWeatherView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val weatherSymbolContainer = object : WeatherSymbolContainer() {
        override fun onReset() {
            currentWeatherViewWeatherSymbolImage.setImageBitmap(null)
            currentWeatherViewWeatherSymbolImage.contentDescription = ""
            currentWeatherViewWeatherDescription.text = ""
            currentWeatherViewDataRowWeatherSummary.visibility = View.GONE
        }

        override fun onUpdate(image: Bitmap?, description: String) {
            if (image != null)
                currentWeatherViewWeatherSymbolImage.setImageBitmap(image)
            currentWeatherViewWeatherSymbolImage.contentDescription = description
            currentWeatherViewWeatherDescription.text = description
            currentWeatherViewDataRowWeatherSummary.visibility = View.VISIBLE
        }
    }

    init {
        View.inflate(context, R.layout.current_weather_view, this)
        resetView()
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
                resetView()
            }
        )
        AsyncUtility.getAddressFromLocation(
            context,
            locale,
            scope,
            latitude,
            longitude,
            { address ->
                if (address != null) {
                    currentWeatherViewCity.text = address.locality
                    currentWeatherViewDataRowCity.visibility = View.VISIBLE
                } else {
                    currentWeatherViewDataRowCity.visibility = View.GONE
                }
            },
            { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
                currentWeatherViewDataRowCity.visibility = View.GONE
            }
        )
    }

    private fun updateWeatherView(
        current: OpenWeatherMap.CurrentWeatherData,
        latestRequested: DateTime
    ) {
        val now = DateTime.now()
        val timeZone = TimeZone.getDefault()

        currentWeatherViewRequestedDateTime.text = formatTime(latestRequested, now, timeZone)
        currentWeatherViewFromCache.visibility = if (current.isCached) View.VISIBLE else View.GONE
        currentWeatherViewDataRowRequestedDateTime.visibility = View.VISIBLE

        currentWeatherViewObservationDateTime.text = formatTime(current.lastUpdated, now, timeZone)
        currentWeatherViewDataRowObservationDateTime.visibility = View.VISIBLE

        AsyncUtility.getAddressFromLocation(
            context,
            java.util.Locale.getDefault(),
            scope,
            current.coord.latitude,
            current.coord.longitude,
            { address ->
                if (address != null) {
                    // TODO("フル表記の住所が冗長なので、なるべく端末位置からの相対的な情報のみを含めるようにする方向で短縮を試みる。どうやらコードがロケール依存になるのを回避できない模様。")
                    currentWeatherViewObservationAddress.text =
                        with(address) {
                            (0..maxAddressLineIndex).map { getAddressLine(it) }
                        }.joinToString("\n")
                    currentWeatherViewDataRowObservationAddress.visibility = View.VISIBLE
                } else {
                    currentWeatherViewDataRowObservationAddress.visibility = View.GONE
                }
            },
            { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
                currentWeatherViewDataRowObservationAddress.visibility = View.GONE
            }
        )

        weatherSymbolContainer.dataSource =
            current.weathers.map { Pair(it.iconUrl, it.description) }.toTypedArray()
        current.main.run {
            currentWeatherViewTemparature.text = "%.1f℃ (%.1f℃/%.1f℃)".format(
                temperatureInCelsius,
                maximumTemperatureInCelsius,
                minimumTemperatureInCelsius
            )
            currentWeatherViewHumidity.text = "%.0f%%".format(humidityInPercent)
            currentWeatherViewFeelsLinkTemparature.text =
                "%.1f℃".format(feelsLinkTemperatureInCelsius)
            currentWeatherViewPressure.text =
                pressureOnTheGroundLevelInHectopascal.let { grandLevel ->
                    if (grandLevel != null) {
                        pressureOnTheSeaLevelInHectopascal.let { seaLevel ->
                            if (seaLevel != null) {
                                "%.0fhPa (現地気圧: %.0fhPa、海面気圧: %.0fhPa)".format(
                                    pressureInHectopascal,
                                    grandLevel,
                                    seaLevel
                                )
                            } else {
                                "%.0fhPa (現地気圧: %.0fhPa)".format(
                                    pressureInHectopascal,
                                    grandLevel
                                )
                            }
                        }
                    } else {
                        pressureOnTheSeaLevelInHectopascal.let { seaLevel ->
                            if (seaLevel != null) {
                                "%.0fhPa (海面気圧: %.0fhPa)".format(pressureInHectopascal, seaLevel)
                            } else {
                                "%.0fhPa".format(pressureInHectopascal)
                            }
                        }
                    }
                }
            currentWeatherViewDataRowPressure.visibility = View.VISIBLE
        }
        currentWeatherViewWind.text =
            current.wind.run {
                direction.let { direction ->
                    if (direction != null) {
                        "%sの風、風速%.1fm/s".format(
                            direction.getDescription(context),
                            speedInMeterPerSecond
                        )
                    } else {
                        "風速%.1fm/s".format(speedInMeterPerSecond)
                    }
                }
            }
        currentWeatherViewDataRowWeatherMain.visibility = View.VISIBLE

        current.clouds.let { clouds ->
            if (clouds == null)
                currentWeatherViewDataRowCloudiness.visibility = View.GONE
            else {
                currentWeatherViewCloudiness.text =
                    "%.0f%%".format(clouds.cloudsInPercent)
                currentWeatherViewDataRowCloudiness.visibility = View.VISIBLE
            }
        }

        fun setPrecipitation(
            data: OpenWeatherMap.WeatherData.Precipitation?,
            dataRow: View,
            textView: TextView
        ) {
            if (data == null)
                dataRow.visibility = View.GONE
            else {
                data.amountOnLast1HourInMilliMeter.let { last1Hour ->
                    if (last1Hour != null) {
                        data.amountOnLast3HourInMilliMeter.let { last3Hour ->
                            if (last3Hour != null) {
                                textView.text =
                                    "過去1時間: %.0fmm、過去3時間: %.0fmm".format(last1Hour, last3Hour)
                                dataRow.visibility = View.VISIBLE
                            } else {
                                textView.text = "過去1時間: %.0fmm".format(last1Hour)
                                dataRow.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        data.amountOnLast3HourInMilliMeter.let { last3Hour ->
                            if (last3Hour != null) {
                                textView.text = "過去3時間: %.0fmm".format(last3Hour)
                                dataRow.visibility = View.VISIBLE
                            } else {
                                dataRow.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
        setPrecipitation(
            current.rainFall,
            currentWeatherViewDataRowRainFall,
            currentWeatherViewRainFall
        )
        setPrecipitation(
            current.snowFall,
            currentWeatherViewDataRowSnowFall,
            currentWeatherViewSnowFall
        )

        current.sys.run {
            if (sunrise >= sunset) {
                currentWeatherViewWeatherSunriseOrSunsetLabel1.text = "日の入り"
                currentWeatherViewWeatherSunriseOrSunset1.text =
                    formatTime(sunset, now, timeZone)
                currentWeatherViewWeatherSunriseOrSunsetLabel2.text = "日の出"
                currentWeatherViewWeatherSunriseOrSunset2.text =
                    formatTime(sunrise, now, timeZone)
            } else {
                currentWeatherViewWeatherSunriseOrSunsetLabel1.text = "日の出"
                currentWeatherViewWeatherSunriseOrSunset1.text =
                    formatTime(sunrise, now, timeZone)
                currentWeatherViewWeatherSunriseOrSunsetLabel2.text = "日の入り"
                currentWeatherViewWeatherSunriseOrSunset2.text =
                    formatTime(sunset, now, timeZone)
            }
            currentWeatherViewDataRowSunriseSunset1.visibility = View.VISIBLE
            currentWeatherViewDataRowSunriseSunset2.visibility = View.VISIBLE
        }
    }

    private fun resetView() {
        arrayOf(
            currentWeatherViewDataRowCity,
            currentWeatherViewDataRowRequestedDateTime,
            currentWeatherViewDataRowObservationDateTime,
            currentWeatherViewDataRowObservationAddress,
            currentWeatherViewDataRowWeatherMain,
            currentWeatherViewDataRowWeatherSummary,
            currentWeatherViewDataRowPressure,
            currentWeatherViewDataRowCloudiness,
            currentWeatherViewDataRowRainFall,
            currentWeatherViewDataRowSnowFall,
            currentWeatherViewDataRowSunriseSunset1,
            currentWeatherViewDataRowSunriseSunset2
        ).forEach {
            it.visibility = View.GONE
        }
        weatherSymbolContainer.dataSource = arrayOf()
    }

    companion object {
        private val TAG = "CurrentWeatherView"

        @JvmStatic
        private fun formatTime(dateTime: DateTime, now: DateTime, timeZone: TimeZone): String {
            return (dateTime - now.atStartOfDay(timeZone)).days.let { days ->
                dateTime.atZone(timeZone).format(
                    (when {
                        days >= 2L -> "%d日後の%%s".format(days)
                        days == 1L -> "明日の%s"
                        days == 0L -> "%s" + (
                                (dateTime - now).let {
                                    when {
                                        it >= TimeDuration.ofHours(1) -> {
                                            " (%d時間後)".format(it.hours)
                                        }
                                        it >= TimeDuration.ofMinutes(1) -> {
                                            " (%d分後)".format(it.minutes)
                                        }
                                        it > TimeDuration.ofMinutes(-1) -> {
                                            ""
                                        }
                                        it >= TimeDuration.ofHours(-1) -> {
                                            " (%d分前)".format((-it).minutes)
                                        }
                                        else -> " (%d時間前)".format((-it).hours)
                                    }
                                })
                        days == -1L -> "昨日の%s"
                        else -> "%d日前の%%s".format(-days)
                    }).format("HH:mm:ss")
                )
            }
        }
    }
}