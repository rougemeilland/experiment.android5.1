package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
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
    private var currentAddress: Address? = null
    private var observationAddress: Address? = null

    private fun updateAddresses(locale: java.util.Locale) {
        currentAddress.let { currentAddress ->
            if (currentAddress != null) {
                val addressUtility = AddressFormatter.of(locale)
                currentWeatherViewCity.text =
                    addressUtility.getLocality(currentAddress).toHankaku()
                observationAddress.let { observationAddress ->
                    currentWeatherViewObservationAddress.text =
                        if (observationAddress != null) {
                            addressUtility
                                .omitAddress(currentAddress, observationAddress)
                                .toHankaku().let { observationAddressText ->
                                    currentAddress.distanceTo(observationAddress).let { distance ->
                                        if (distance == null) {
                                            observationAddressText
                                        } else {
                                            "%s\n(現在地から約 %s)".format(
                                                observationAddressText,
                                                when {
                                                    distance >= 1000000.0 ->
                                                        "%,.0fkm".format(distance / 1000)
                                                    distance >= 100000.0 ->
                                                        "%,.1fkm".format(distance / 1000)
                                                    distance >= 10000.0 ->
                                                        "%,.2fkm".format(distance / 1000)
                                                    else ->
                                                        "%,.0fm".format(distance)
                                                }
                                            )
                                        }
                                    }
                                }
                        } else {
                            ""
                        }
                }
            } else {
                currentWeatherViewCity.text = ""
                observationAddress.let { observationAddress ->
                    currentWeatherViewObservationAddress.text =
                        if (observationAddress != null) {
                            (0 until observationAddress.maxAddressLineIndex)
                                .joinToString("\n") { index ->
                                    observationAddress.getAddressLine(index)
                                }.toHankaku()
                        } else {
                            ""
                        }
                }
            }
        }
    }

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

    fun reset() {
        resetView()
    }

    fun update(
        coordinates: Coordinates,
        accuracy: Double,
        locale: java.util.Locale,
        onCompleted: (Boolean, DateTime) -> Unit
    ) {
        OpenWeatherMap.CurrentWeatherData.getInstance(
            context,
            scope,
            locale,
            coordinates,
            { current, latestRequested ->
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
            coordinates,
            { address ->
                if (address != null) {
                    currentAddress = address
                    updateAddresses(locale)
                    currentWeatherViewCityAccuracy.text = "(精度: %,.0fm)".format(accuracy)
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
        val locale = java.util.Locale.getDefault()

        currentWeatherViewRequestedDateTime.text = formatTime(latestRequested, now, timeZone)
        currentWeatherViewFromCache.visibility = if (current.isCached) View.VISIBLE else View.GONE
        currentWeatherViewDataRowRequestedDateTime.visibility = View.VISIBLE

        currentWeatherViewObservationDateTime.text = formatTime(current.lastUpdated, now, timeZone)
        currentWeatherViewDataRowObservationDateTime.visibility = View.VISIBLE

        AsyncUtility.getAddressFromLocation(
            context,
            locale,
            scope,
            current.coord.coordinates,
            { address ->
                if (address != null) {
                    observationAddress = address
                    updateAddresses(locale)
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
            currentWeatherViewTemparature.text = "%,.1f℃ (%,.1f℃/%,.1f℃)".format(
                temperatureInCelsius,
                maximumTemperatureInCelsius,
                minimumTemperatureInCelsius
            )
            currentWeatherViewHumidity.text = "%,.0f%%".format(humidityInPercent)
            currentWeatherViewFeelsLinkTemparature.text =
                "%,.1f℃".format(feelsLinkTemperatureInCelsius)
            currentWeatherViewPressure.text =
                pressureOnTheGroundLevelInHectopascal.let { grandLevel ->
                    if (grandLevel != null) {
                        pressureOnTheSeaLevelInHectopascal.let { seaLevel ->
                            if (seaLevel != null) {
                                "%,.0fhPa (現地気圧: %,.0fhPa、海面気圧: %,.0fhPa)".format(
                                    pressureInHectopascal,
                                    grandLevel,
                                    seaLevel
                                )
                            } else {
                                "%,.0fhPa (現地気圧: %,.0fhPa)".format(
                                    pressureInHectopascal,
                                    grandLevel
                                )
                            }
                        }
                    } else {
                        pressureOnTheSeaLevelInHectopascal.let { seaLevel ->
                            if (seaLevel != null) {
                                "%,.0fhPa (海面気圧: %,.0fhPa)".format(pressureInHectopascal, seaLevel)
                            } else {
                                "%,.0fhPa".format(pressureInHectopascal)
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
                        "%sの風、風速 %,.2fm/s".format(
                            direction.getDescription(context),
                            speedInMeterPerSecond
                        )
                    } else {
                        "風速 %,.2fm/s".format(speedInMeterPerSecond)
                    }
                }
            }
        currentWeatherViewDataRowWeatherMain.visibility = View.VISIBLE

        current.clouds.let { clouds ->
            if (clouds == null)
                currentWeatherViewDataRowCloudiness.visibility = View.GONE
            else {
                currentWeatherViewCloudiness.text =
                    "%,.0f%%".format(clouds.cloudsInPercent)
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
                                    "過去1時間: %,.0fmm、過去3時間: %,.0fmm".format(last1Hour, last3Hour)
                                dataRow.visibility = View.VISIBLE
                            } else {
                                textView.text = "過去1時間: %,.0fmm".format(last1Hour)
                                dataRow.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        data.amountOnLast3HourInMilliMeter.let { last3Hour ->
                            if (last3Hour != null) {
                                textView.text = "過去3時間: %,.0fmm".format(last3Hour)
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
        private fun formatTime(dateTime: DateTime, now: DateTime, timeZone: TimeZone): String =
            (dateTime - now.atStartOfDay(timeZone)).days.let { days ->
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