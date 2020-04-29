package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject

class OpenWeatherMap {
    abstract class WeatherData protected constructor(
        val sourceText: String,
        val isCached: Boolean
    ) {
        class Coord private constructor(val coordinates: Coordinates) {
            companion object {
                fun of(o: JSONObject): Coord =
                    Coord(
                        Coordinates(
                            o.getDouble("lat"),
                            o.getDouble("lon")
                        )
                    )
            }
        }

        class Main private constructor(
            val temperatureInCelsius: Double,
            val feelsLinkTemperatureInCelsius: Double,
            val minimumTemperatureInCelsius: Double,
            val maximumTemperatureInCelsius: Double,
            val humidityInPercent: Double,
            val pressureInHectopascal: Double,
            val pressureOnTheSeaLevelInHectopascal: Double?,
            val pressureOnTheGroundLevelInHectopascal: Double?
        ) {
            companion object {
                fun of(o: JSONObject): Main =
                    Main(
                        o.getDouble("temp"),
                        o.getDouble("feels_like"),
                        o.getDouble("temp_min"),
                        o.getDouble("temp_max"),
                        o.getDouble("humidity"),
                        o.getDouble("pressure"),
                        o.optDouble("sea_level").run { if (isNaN()) null else this },
                        o.optDouble("grnd_level").run { if (isNaN()) null else this }
                    )
            }
        }

        class Weather private constructor(
            val description: String,
            private val icon: String
        ) {
            val iconUrl: Uri
                get() =
                    Uri.Builder().run {
                        scheme("https")
                        authority("openweathermap.org")
                        path("/img/wn/$icon.png")
                        build()
                    }

            companion object {
                fun of(o: JSONObject): Weather =
                    Weather(
                        o.getString("description"),
                        o.getString("icon")
                    )
            }
        }

        /**
         * @param directionInDegrees 北から吹く風は 0° 。時計回りに増えていく。
         */
        class Wind private constructor(
            val speedInMeterPerSecond: Double,
            val directionInDegrees: Double?
        ) {
            val direction: SixteenDirections? =
                directionInDegrees?.let { SixteenDirections.ofDegrees(it) }

            companion object {
                fun of(o: JSONObject): Wind =
                    Wind(
                        o.getDouble("speed"),
                        o.optDouble("deg").run { if (isNaN()) null else this }
                    )
            }
        }

        /**
         * @param cloudsInPercent 曇天の度合い
         */
        class Clouds private constructor(
            val cloudsInPercent: Double
        ) {
            companion object {
                fun of(o: JSONObject): Clouds =
                    Clouds(
                        o.getDouble("all")
                    )
            }
        }

        class Precipitation private constructor(
            val amountOnLast1HourInMilliMeter: Double?,
            val amountOnLast3HourInMilliMeter: Double?
        ) {
            companion object {
                fun of(o: JSONObject): Precipitation =
                    Precipitation(
                        o.optDouble("1h").run { if (isNaN()) null else this },
                        o.optDouble("3h").run { if (isNaN()) null else this }
                    )
            }
        }

        class UltravioletIndex private constructor(val value: Double) {
            val rank: UltravioletIndexRank = UltravioletIndexRank.of(value)

            companion object {
                fun of(value: Double): UltravioletIndex =
                    UltravioletIndex(value)
            }
        }

        enum class UltravioletIndexRank(
            val minimumValue: Double,
            val mediaGraphicColorName: String,
            private val descriptionResourceId: Int
        ) {
            LOW(0.0, "#3EA72D", R.string.ultraviolet_index_rank_name_low),
            MODERATE(3.0, "#FFF300", R.string.ultraviolet_index_rank_name_moderate),
            HIGH(6.0, "#F18B00", R.string.ultraviolet_index_rank_name_high),
            VERY_HIGH(8.0, "#E53210", R.string.ultraviolet_index_rank_name_very_high),
            EXTREME(11.0, "#B567A4", R.string.ultraviolet_index_rank_name_extreme)
            ;

            fun getDescription(context: Context): String =
                context.getString(descriptionResourceId)

            companion object {
                fun of(value: Double): UltravioletIndexRank =
                    when {
                        value < 3.0 -> LOW
                        value < 6.0 -> MODERATE
                        value < 8.0 -> HIGH
                        value < 11.0 -> VERY_HIGH
                        else -> EXTREME
                    }
            }
        }

        companion object {
            protected class ApiCache(private val key: String) {
                fun save(context: Context, sourceText: String) {
                    context.getSharedPreferences(PREFS_NAME, 0).edit().also { prefs ->
                        prefs.putString(key, sourceText)
                        prefs.apply()
                    }
                }

                fun load(context: Context): String =
                    context.getSharedPreferences(PREFS_NAME, 0)
                        .getString(key, null).let { it ?: "" }
            }

            protected class LastRequestedDateTime(private val key: String) {
                fun setValue(context: Context, dateTime: DateTime) {
                    context.getSharedPreferences(PREFS_NAME, 0).edit().also { prefs ->
                        prefs.putLong(key, dateTime.epochMilliSeconds)
                        prefs.apply()
                    }
                }

                fun getValue(context: Context): DateTime =
                    DateTime.ofEpochMilliSeconds(
                        context.getSharedPreferences(PREFS_NAME, 0)
                            .getLong(key, 0)
                    )
            }

            @JvmStatic
            protected fun <T : WeatherData> getInstance(
                context: Context,
                scope: CoroutineScope,
                latestRequestedDateTime: LastRequestedDateTime,
                apiCache: ApiCache,
                url: Uri,
                onCompleted: (T, DateTime) -> Unit,
                onFailed: (Throwable) -> Unit,
                jsonStringParser: (String, Boolean) -> T
            ) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.INTERNET
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    throw Exception("${AsyncUtility::class.java.canonicalName}.getAddressFromLocation(): Not granted Manifest.permission.INTERNET")
                val now = DateTime.now()
                val latestRequested = latestRequestedDateTime.getValue(context)
                val minimumInterval = TimeDuration.ofSeconds(
                    context.resources.getInteger(
                        R.integer.open_weather_map_minimum_interval_seconds
                    ).toLong()
                )
                if (now - latestRequested < minimumInterval) {
                    val cache = try {
                        jsonStringParser(apiCache.load(context), true)
                    } catch (ex: Exception) {
                        if (Log.isLoggable(TAG, Log.ERROR)) {
                            Log.e(TAG, ex.message, ex)
                        }
                        onFailed(ex)
                        null
                    }
                    if (cache != null)
                        onCompleted(cache, latestRequested)
                } else {
                    latestRequestedDateTime.setValue(context, now)
                    AsyncUtility.downloadString(scope, url, { text ->
                        val data = try {
                            jsonStringParser(text, false)
                        } catch (ex: Exception) {
                            if (Log.isLoggable(TAG, Log.ERROR)) {
                                Log.e(TAG, ex.message, ex)
                            }
                            onFailed(ex)
                            null
                        }
                        if (data != null) {
                            apiCache.save(context, data.sourceText)
                            onCompleted(data, now)
                        }
                    }, { ex ->
                        if (Log.isLoggable(TAG, Log.ERROR)) {
                            Log.e(TAG, ex.message, ex)
                            onFailed(ex)
                        }
                    })
                }
            }
        }
    }

    class CurrentWeatherData private constructor(
        sourceText: String,
        isCached: Boolean,
        val coord: Coord,
        val sys: Sys,
        val main: Main,
        val weathers: Array<Weather>,
        val wind: Wind,
        val clouds: Clouds?,
        val rainFall: Precipitation?,
        val snowFall: Precipitation?,
        val lastUpdated: DateTime,
        val timeZone: TimeZone
    ) : WeatherData(sourceText, isCached) {
        companion object {
            private val latestRequestedDateTime: WeatherData.Companion.LastRequestedDateTime by lazy {
                WeatherData.Companion.LastRequestedDateTime(
                    PREF_KEY_LATEST_REQUEST_CURRENT_WEATHER_DATA
                )
            }

            private val apiCache: WeatherData.Companion.ApiCache by lazy {
                WeatherData.Companion.ApiCache(PREF_KEY_CACHE_CURRENT_WEATHER_DATA)
            }

            fun getInstance(
                context: Context,
                scope: CoroutineScope,
                locale: java.util.Locale,
                coordinates: Coordinates,
                onCompleted: (CurrentWeatherData, DateTime) -> Unit,
                onFailed: (Throwable) -> Unit
            ) {
                getInstance(
                    context,
                    scope,
                    latestRequestedDateTime,
                    apiCache,
                    buildRequest(context, locale, coordinates),
                    onCompleted,
                    onFailed,
                    { sourceText, isCached ->
                        of(sourceText, isCached)
                    })
            }

            private fun buildRequest(
                context: Context,
                locale: java.util.Locale,
                coordinates: Coordinates
            ): Uri =
                Uri.Builder().run {
                    scheme("https")
                    authority("api.openweathermap.org")
                    path("/data/2.5/weather")
                    appendQueryParameter(
                        "appid",
                        context.getString(R.string.openWeatherMapApiKey)
                    )
                    appendQueryParameter("units", "metric")
                    appendQueryParameter("lang", locale.language)
                    appendQueryParameter("lat", coordinates.latitude.toString())
                    appendQueryParameter("lon", coordinates.longitude.toString())
                    build()
                }

            private fun of(sourceText: String, isCached: Boolean): CurrentWeatherData =
                of(
                    sourceText,
                    try {
                        JSONObject(sourceText)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$sourceText")
                    },
                    isCached
                )

            private fun of(
                sourceText: String,
                o: JSONObject,
                isCached: Boolean
            ): CurrentWeatherData =
                CurrentWeatherData(
                    sourceText,
                    isCached,
                    Coord.of(o.getJSONObject("coord")),
                    Sys.of(o.getJSONObject("sys")),
                    Main.of(o.getJSONObject("main")),
                    o.getJSONArray("weather")
                        .toIterableOfJSONObject()
                        .map { Weather.of(it) }
                        .toTypedArray(),
                    Wind.of(o.getJSONObject("wind")),
                    o.optJSONObject("clouds")?.let { Clouds.of(it) },
                    o.optJSONObject("rain")?.let { Precipitation.of(it) },
                    o.optJSONObject("snow")?.let { Precipitation.of(it) },
                    DateTime.ofEpochSeconds(o.getLong("dt")),
                    TimeZone.ofTotalSeconds(o.getInt("timezone"))
                )
        }

        class Sys private constructor(val sunrise: DateTime, val sunset: DateTime) {
            companion object {
                fun of(o: JSONObject): Sys =
                    Sys(
                        DateTime.ofEpochSeconds(o.getLong("sunrise")),
                        DateTime.ofEpochSeconds(o.getLong("sunset"))
                    )
            }
        }
    }

    class OneCall private constructor(
        sourceText: String,
        isCached: Boolean,
        val coordinates: Coordinates,
        val timeZone: TimeZone,
        val current: CurrentOrHourly,
        val hourly: Array<CurrentOrHourly>,
        val daily: Array<Daily>
    ) : WeatherData(sourceText, isCached) {
        /**
         * @param directionInDegrees 北から吹く風は 0° 。時計回りに増えていく。
         */
        class WindOfOneShot(
            val speedMeterInPerSecond: Double,
            val directionInDegrees: Double?,
            val gustInMeterPerSecond: Double?
        ) {
            val direction: SixteenDirections? =
                directionInDegrees?.let { SixteenDirections.ofDegrees(it) }
        }

        /**
         * @param dewPointInCelsius 露点。ある大気の温度の一つで、その温度まで気温が下げられると、湿度が１００％（もしくはSaturated、飽和状態）になる気温。
         * @param ultravioletIndex UV指数
         */
        class CurrentOrHourly private constructor(
            val dateTime: DateTime,
            val weathers: Array<Weather>,
            val temperatureInCelsius: Double,
            val feelsLinkTemperatureInCelsius: Double,
            val humidityInPercent: Double,
            val dewPointInCelsius: Double?,
            val cloudsInPercent: Double,
            val wind: WindOfOneShot,
            val sunrise: DateTime,
            val sunset: DateTime,
            val ultravioletIndex: UltravioletIndex,
            val visibilityInMeters: Double?,
            val rainFall: Precipitation?,
            val snowFall: Precipitation?
        ) {
            companion object {
                fun of(o: JSONObject): CurrentOrHourly =
                    CurrentOrHourly(
                        DateTime.ofEpochSeconds(o.getLong("dt")),
                        o.getJSONArray("weather")
                            .toIterableOfJSONObject()
                            .map { Weather.of(it) }
                            .toTypedArray(),
                        o.getDouble("temp"),
                        o.getDouble("feels_like"),
                        o.getDouble("humidity"),
                        o.optDouble("dew_point").run { if (isNaN()) null else this },
                        o.getDouble("clouds"),
                        WindOfOneShot(
                            o.getDouble("wind_speed"),
                            o.optDouble("wind_deg").run { if (isNaN()) null else this },
                            o.optDouble("wind_gust").run { if (isNaN()) null else this }
                        ),
                        DateTime.ofEpochSeconds(o.getLong("sunrise")),
                        DateTime.ofEpochSeconds(o.getLong("sunset")),
                        UltravioletIndex.of((o.getDouble("uvi"))),
                        o.optDouble("visibility").run { if (isNaN()) null else this },
                        o.optJSONObject("rain")?.let { Precipitation.of(it) },
                        o.optJSONObject("snow")?.let { Precipitation.of(it) }
                    )
            }
        }

        /**
         * @param dewPointInCelsius 露点。ある大気の温度の一つで、その温度まで気温が下げられると、湿度が１００％（もしくはSaturated、飽和状態）になる気温。
         * @param ultravioletIndex UV指数
         */
        class Daily private constructor(
            val dateTime: DateTime,
            val weathers: Array<Weather>,
            val temperature: Temperature,
            val feelsLikeTemperature: FeelsLikeTemperature,
            val humidityPercent: Double,
            val dewPointCelsius: Double?,
            val cloudsPercent: Double,
            val wind: WindOfOneShot,
            val sunrise: DateTime,
            val sunset: DateTime,
            val ultravioletIndex: UltravioletIndex,
            val visibilityMeters: Double?,
            val rainFall: Precipitation?,
            val snowFall: Precipitation?
        ) {
            class Temperature private constructor(
                val morningCelsius: Double,
                val dayCelsius: Double,
                val eveningCelsius: Double,
                val nightCelsius: Double,
                val minimumCelsius: Double,
                val maximumCelsius: Double
            ) {
                companion object {
                    fun of(o: JSONObject): Temperature =
                        Temperature(
                            o.getDouble("morn"),
                            o.getDouble("day"),
                            o.getDouble("eve"),
                            o.getDouble("night"),
                            o.getDouble("min"),
                            o.getDouble("max")
                        )
                }
            }

            class FeelsLikeTemperature private constructor(
                val morning: Double,
                val day: Double,
                val evening: Double,
                val night: Double
            ) {
                companion object {
                    fun of(o: JSONObject): FeelsLikeTemperature =
                        FeelsLikeTemperature(
                            o.getDouble("morn"),
                            o.getDouble("day"),
                            o.getDouble("eve"),
                            o.getDouble("night")
                        )
                }
            }

            companion object {
                fun of(o: JSONObject): Daily =
                    Daily(
                        DateTime.ofEpochSeconds(o.getLong("dt")),
                        o.getJSONArray("weather")
                            .toIterableOfJSONObject()
                            .map { Weather.of(it) }
                            .toTypedArray(),
                        Temperature.of(o.getJSONObject("temp")),
                        FeelsLikeTemperature.of(o.getJSONObject("feels_like")),
                        o.getDouble("humidity"),
                        o.optDouble("dew_point").run { if (isNaN()) null else this },
                        o.getDouble("clouds"),
                        WindOfOneShot(
                            o.getDouble("wind_speed"),
                            o.optDouble("wind_deg").run { if (isNaN()) null else this },
                            o.optDouble("wind_gust").run { if (isNaN()) null else this }
                        ),
                        DateTime.ofEpochSeconds(o.getLong("sunrise")),
                        DateTime.ofEpochSeconds(o.getLong("sunset")),
                        UltravioletIndex.of((o.getDouble("uvi"))),
                        o.optDouble("visibility").run { if (isNaN()) null else this },
                        o.optJSONObject("rain")?.let { Precipitation.of(it) },
                        o.optJSONObject("snow")?.let { Precipitation.of(it) }
                    )
            }
        }

        companion object {
            private val latestRequestedDateTime: WeatherData.Companion.LastRequestedDateTime by lazy {
                WeatherData.Companion.LastRequestedDateTime(
                    PREF_KEY_LATEST_REQUEST_ONE_CALL
                )
            }

            private val apiCache: WeatherData.Companion.ApiCache by lazy {
                WeatherData.Companion.ApiCache(PREF_KEY_CACHE_ONE_CALL)
            }

            fun getInstance(
                context: Context,
                scope: CoroutineScope,
                locale: java.util.Locale,
                coordinates: Coordinates,
                onCompleted: (OneCall, DateTime) -> Unit,
                onFailed: (Throwable) -> Unit
            ) {
                getInstance(
                    context,
                    scope,
                    latestRequestedDateTime,
                    apiCache,
                    buildRequest(context, locale, coordinates),
                    onCompleted,
                    onFailed,
                    { sourceText, isCache -> fromJSONString(sourceText, isCache) })
            }

            private fun buildRequest(
                context: Context,
                locale: java.util.Locale,
                coordinates: Coordinates
            ): Uri =
                Uri.Builder().run {
                    scheme("https")
                    authority("api.openweathermap.org")
                    path("/data/2.5/onecall")
                    appendQueryParameter(
                        "appid",
                        context.getString(R.string.openWeatherMapApiKey)
                    )
                    appendQueryParameter("units", "metric")
                    appendQueryParameter("lang", locale.language)
                    appendQueryParameter("lat", coordinates.latitude.toString())
                    appendQueryParameter("lon", coordinates.longitude.toString())
                    build()
                }

            private fun fromJSONString(sourceText: String, isCached: Boolean): OneCall =
                of(
                    sourceText,
                    try {
                        JSONObject(sourceText)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$sourceText")
                    },
                    isCached
                )

            private fun of(
                sourceText: String,
                o: JSONObject,
                isCached: Boolean
            ): OneCall =
                OneCall(
                    sourceText,
                    isCached,
                    Coordinates(
                        o.getDouble("lat"),
                        o.getDouble("lon")
                    ),
                    TimeZone.of(o.getString("timezone")),
                    CurrentOrHourly.of(o.getJSONObject("current")),
                    o.getJSONArray("hourly")
                        .toIterableOfJSONObject()
                        .map { CurrentOrHourly.of(it) }
                        .toTypedArray(),
                    o.getJSONArray("daily")
                        .toIterableOfJSONObject()
                        .map { Daily.of(it) }
                        .toTypedArray())
        }
    }

    class FiveDayWeatherForecast private constructor(
        sourceText: String,
        isCached: Boolean,
        val city: City,
        val forecasts: Array<Forecast>
    ) : WeatherData(sourceText, isCached) {
        class City private constructor(
            val coord: Coord,
            val timeZone: TimeZone
        ) {
            companion object {
                fun of(o: JSONObject): City =
                    City(
                        Coord.of(o.getJSONObject("coord")),
                        TimeZone.ofTotalSeconds(o.getInt("timezone"))
                    )
            }
        }

        class Forecast private constructor(
            val main: Main,
            val weathers: Array<Weather>,
            val wind: Wind,
            val clouds: Clouds?,
            val rainFall: Precipitation?,
            val snowFall: Precipitation?,
            val forecasted: DateTime
        ) {
            companion object {
                fun of(o: JSONObject): Forecast =
                    Forecast(
                        Main.of(o.getJSONObject("main")),
                        o.getJSONArray("weather")
                            .toIterableOfJSONObject()
                            .map { Weather.of(it) }
                            .toTypedArray(),
                        Wind.of(o.getJSONObject("wind")),
                        o.optJSONObject("clouds")?.let { Clouds.of(it) },
                        o.optJSONObject("rain")?.let { Precipitation.of(it) },
                        o.optJSONObject("snow")?.let { Precipitation.of(it) },
                        DateTime.ofEpochSeconds(o.getLong("dt"))
                    )
            }
        }

        companion object {
            private val latestRequestedDateTime: WeatherData.Companion.LastRequestedDateTime by lazy {
                WeatherData.Companion.LastRequestedDateTime(
                    PREF_KEY_LATEST_REQUEST_FIVE_DAY_WEATHER_FORECAST
                )
            }

            private val apiCache: WeatherData.Companion.ApiCache by lazy {
                WeatherData.Companion.ApiCache(PREF_KEY_CACHE_FIVE_DAY_WEATHER_FORECAST)
            }

            fun getInstance(
                context: Context,
                scope: CoroutineScope,
                locale: java.util.Locale,
                coordinates: Coordinates,
                onCompleted: (FiveDayWeatherForecast, DateTime) -> Unit,
                onFailed: (Throwable) -> Unit
            ) {
                getInstance(
                    context,
                    scope,
                    latestRequestedDateTime,
                    apiCache,
                    buildRequest(context, locale, coordinates),
                    onCompleted,
                    onFailed,
                    { sourceText, isCache -> fromJSONString(sourceText, isCache) })
            }

            private fun buildRequest(
                context: Context,
                locale: java.util.Locale,
                coordinates: Coordinates
            ): Uri =
                Uri.Builder().run {
                    scheme("https")
                    authority("api.openweathermap.org")
                    path("/data/2.5/forecast")
                    appendQueryParameter(
                        "appid",
                        context.getString(R.string.openWeatherMapApiKey)
                    )
                    appendQueryParameter("units", "metric")
                    appendQueryParameter("lang", locale.language)
                    appendQueryParameter("lat", coordinates.latitude.toString())
                    appendQueryParameter("lon", coordinates.longitude.toString())
                    build()
                }

            private fun fromJSONString(
                sourceText: String,
                isCached: Boolean
            ): FiveDayWeatherForecast =
                of(
                    sourceText,
                    try {
                        JSONObject(sourceText)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$sourceText")
                    },
                    isCached
                )

            private fun of(
                sourceText: String,
                o: JSONObject,
                isCached: Boolean
            ): FiveDayWeatherForecast =
                FiveDayWeatherForecast(
                    sourceText,
                    isCached,
                    City.of(o.getJSONObject("city")),
                    o.getJSONArray("list")
                        .toIterableOfJSONObject()
                        .map { Forecast.of(it) }
                        .toTypedArray())
        }
    }

    companion object {
        private const val TAG = "OpenWeatherMap"
        private const val PREFS_NAME = "com.palmtreesoftware.experimentandroid5_1"
        private const val PREF_KEY_LATEST_REQUEST_CURRENT_WEATHER_DATA = "latest-request-weather"
        private const val PREF_KEY_LATEST_REQUEST_ONE_CALL = "latest-request-onecall"
        private const val PREF_KEY_LATEST_REQUEST_FIVE_DAY_WEATHER_FORECAST =
            "latest-request-forecast"
        private const val PREF_KEY_CACHE_CURRENT_WEATHER_DATA = "cache-weather"
        private const val PREF_KEY_CACHE_ONE_CALL = "cache-onecall"
        private const val PREF_KEY_CACHE_FIVE_DAY_WEATHER_FORECAST = "cache-forecast"
    }
}
