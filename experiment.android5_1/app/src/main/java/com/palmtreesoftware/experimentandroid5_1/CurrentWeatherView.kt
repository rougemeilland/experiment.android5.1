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

class CurrentWeatherView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val scope: CoroutineScope =
        try {
            context as CoroutineScope
        } catch (ex: Throwable) {
            throw Exception("Activity must implement CoroutineScope", ex)
        }

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
                                            context.getString(
                                                R.string.current_weather_view_format_relative_location,
                                                observationAddressText,
                                                when {
                                                    distance >= 1000000.0 ->
                                                        context.getString(
                                                            R.string.current_weather_view_format_distance_1,
                                                            distance / 1000
                                                        )
                                                    distance >= 100000.0 ->
                                                        context.getString(
                                                            R.string.current_weather_view_format_distance_2,
                                                            distance / 1000
                                                        )
                                                    distance >= 10000.0 ->
                                                        context.getString(
                                                            R.string.current_weather_view_format_distance_3,
                                                            distance / 1000
                                                        )
                                                    else ->
                                                        context.getString(
                                                            R.string.current_weather_view_format_distance_4,
                                                            distance
                                                        )
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
                        observationAddress?.addressLines?.joinToString("\n")?.toHankaku() ?: ""
                }
            }
        }
    }

    private val weatherSymbolContainer = object : WeatherSymbolContainer(scope) {
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
            scope,
            locale,
            coordinates,
            { address ->
                if (address != null) {
                    currentAddress = address
                    updateAddresses(locale)
                    currentWeatherViewCityAccuracy.text = context.getString(
                        R.string.current_weather_view_format_location_accuracy,
                        accuracy
                    )
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

        currentWeatherViewRequestedDateTime.text =
            latestRequested.formatRelativeTime(context, now, timeZone)
        currentWeatherViewFromCache.visibility = if (current.isCached) View.VISIBLE else View.GONE
        currentWeatherViewDataRowRequestedDateTime.visibility = View.VISIBLE

        currentWeatherViewObservationDateTime.text =
            current.lastUpdated.formatRelativeTime(context, now, timeZone)
        currentWeatherViewDataRowObservationDateTime.visibility = View.VISIBLE

        AsyncUtility.getAddressFromLocation(
            context,
            scope,
            locale,
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
            currentWeatherViewTemparature.text = context.getString(
                R.string.current_weather_view_format_temperature,
                temperatureInCelsius,
                maximumTemperatureInCelsius,
                minimumTemperatureInCelsius
            )
            currentWeatherViewHumidity.text =
                context.getString(R.string.current_weather_view_format_humidity, humidityInPercent)
            currentWeatherViewFeelsLinkTemparature.text =
                context.getString(
                    R.string.current_weather_view_format_feels_like_temperature,
                    feelsLinkTemperatureInCelsius
                )
            currentWeatherViewPressure.text =
                pressureOnTheGroundLevelInHectopascal.let { grandLevel ->
                    if (grandLevel != null) {
                        pressureOnTheSeaLevelInHectopascal.let { seaLevel ->
                            if (seaLevel != null) {
                                context.getString(
                                    R.string.current_weather_view_format_pressure_1,
                                    pressureInHectopascal,
                                    grandLevel,
                                    seaLevel
                                )
                            } else {
                                context.getString(
                                    R.string.current_weather_view_format_pressure_2,
                                    pressureInHectopascal,
                                    grandLevel
                                )
                            }
                        }
                    } else {
                        pressureOnTheSeaLevelInHectopascal.let { seaLevel ->
                            if (seaLevel != null) {
                                context.getString(
                                    R.string.current_weather_view_format_pressure_3,
                                    pressureInHectopascal,
                                    seaLevel
                                )
                            } else {
                                context.getString(
                                    R.string.current_weather_view_format_pressure_4,
                                    pressureInHectopascal
                                )
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
                        context.getString(
                            R.string.current_weather_view_format_wind_1,
                            direction.getDescription(context),
                            speedInMeterPerSecond
                        )
                    } else {
                        context.getString(
                            R.string.current_weather_view_format_wind_2,
                            speedInMeterPerSecond
                        )
                    }
                }
            }
        currentWeatherViewDataRowWeatherMain.visibility = View.VISIBLE

        current.clouds.let { clouds ->
            if (clouds == null)
                currentWeatherViewDataRowCloudiness.visibility = View.GONE
            else {
                currentWeatherViewCloudiness.text =
                    context.getString(
                        R.string.current_weather_view_format_clouds,
                        clouds.cloudsInPercent
                    )
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
                                    context.getString(
                                        R.string.current_weather_view_format_precipitation_1,
                                        last1Hour,
                                        last3Hour
                                    )
                                dataRow.visibility = View.VISIBLE
                            } else {
                                textView.text = context.getString(
                                    R.string.current_weather_view_format_precipitation_2,
                                    last1Hour
                                )
                                dataRow.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        data.amountOnLast3HourInMilliMeter.let { last3Hour ->
                            if (last3Hour != null) {
                                textView.text = context.getString(
                                    R.string.current_weather_view_format_precipitation_3,
                                    last3Hour
                                )
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
                currentWeatherViewWeatherSunriseOrSunsetLabel1.text =
                    context.getString(R.string.current_weather_view_sunset_label)
                currentWeatherViewWeatherSunriseOrSunset1.text =
                    sunset.formatRelativeTime(context, now, timeZone)
                currentWeatherViewWeatherSunriseOrSunsetLabel2.text =
                    context.getString(R.string.current_weather_view_format_sunrise_label)
                currentWeatherViewWeatherSunriseOrSunset2.text =
                    sunrise.formatRelativeTime(context, now, timeZone)
            } else {
                currentWeatherViewWeatherSunriseOrSunsetLabel1.text =
                    context.getString(R.string.current_weather_view_format_sunrise_label)
                currentWeatherViewWeatherSunriseOrSunset1.text =
                    sunrise.formatRelativeTime(context, now, timeZone)
                currentWeatherViewWeatherSunriseOrSunsetLabel2.text =
                    context.getString(R.string.current_weather_view_sunset_label)
                currentWeatherViewWeatherSunriseOrSunset2.text =
                    sunset.formatRelativeTime(context, now, timeZone)
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
        private const val TAG = "CurrentWeatherView"
    }
}