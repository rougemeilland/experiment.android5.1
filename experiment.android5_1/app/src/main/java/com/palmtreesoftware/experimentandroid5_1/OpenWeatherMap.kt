package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject
import kotlin.math.floor

class OpenWeatherMap {
    abstract class WeatherData protected constructor(
        val sourceText: String,
        val isCached: Boolean
    ) {
        class Coord private constructor(val latitude: Double, val longitude: Double) {
            companion object {
                fun fromJSONObject(o: JSONObject): Coord =
                    Coord(
                        getDouble(o, "lat"),
                        getDouble(o, "lon")
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
            val pressureOnTheSeaLevelInHectopascal: Double,
            val pressureOnTheGroundLevelInHectopascal: Double
        ) {
            companion object {
                fun fromJSONObject(o: JSONObject): Main =
                    Main(
                        getDouble(o, "temp"),
                        getDouble(o, "feels_like"),
                        getDouble(o, "temp_min"),
                        getDouble(o, "temp_max"),
                        getDouble(o, "humidity"),
                        getDouble(o, "pressure"),
                        getDouble(o, "sea_level"),
                        getDouble(o, "grnd_level")
                    )
            }
        }

        class Weather private constructor(
            val description: String,
            val icon: String
        ) {
            val iconUrl: Uri
                get() =
                    Uri.Builder().let { uri ->
                        uri.scheme("https")
                        uri.authority("openweathermap.org")
                        uri.path("/img/wn/$icon.png")
                        uri.build()
                    }

            companion object {
                fun fromJSONObject(o: JSONObject): Weather =
                    Weather(
                        getString(o, "description"),
                        getString(o, "icon")
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
            val direction: SixteenDirections?
                get() = directionInDegrees?.let { getDirection(it) }

            companion object {
                fun fromJSONObject(o: JSONObject): Wind =
                    Wind(
                        getDouble(o, "speed"),
                        optDouble(o, "deg")
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
                fun fromJSONObjectOrNull(o: JSONObject?): Clouds? =
                    if (o != null)
                        Clouds(
                            getDouble(o, "all")
                        )
                    else
                        null
            }
        }

        class Precipitation private constructor(
            val amountPer1HourInInMilliMeter: Double?,
            val amountPer3HourInInMilliMeter: Double?
        ) {
            companion object {
                fun fromJSONObjectOrNull(o: JSONObject?): Precipitation? =
                    if (o != null)
                        Precipitation(
                            optDouble(o, "1h"),
                            optDouble(o, "3h")
                        )
                    else
                        null
            }
        }

        class UltravioletIndex private constructor(
            val value: Double,
            val rank: UltravioletIndexRank
        ) {

            companion object {
                fun fromValue(value: Double): UltravioletIndex =
                    when {
                        value < 3.0 -> UltravioletIndex(value, UltravioletIndexRank.LOW)
                        value < 6.0 -> UltravioletIndex(value, UltravioletIndexRank.MODERATE)
                        value < 8.0 -> UltravioletIndex(value, UltravioletIndexRank.HIGH)
                        value < 11.0 -> UltravioletIndex(value, UltravioletIndexRank.VERY_HIGH)
                        else -> UltravioletIndex(value, UltravioletIndexRank.EXTREME)
                    }
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
        }

        protected abstract fun saveToCache(context: Context)

        protected fun saveToCache(context: Context, prefKey: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(prefKey, sourceText)
            prefs.apply()
        }

        companion object {
            private val directions: Array<SixteenDirections> =
                arrayOf(
                    SixteenDirections.N,
                    SixteenDirections.NNE,
                    SixteenDirections.NNE,
                    SixteenDirections.NE,
                    SixteenDirections.NE,
                    SixteenDirections.ENE,
                    SixteenDirections.ENE,
                    SixteenDirections.E,
                    SixteenDirections.E,
                    SixteenDirections.ESE,
                    SixteenDirections.ESE,
                    SixteenDirections.SE,
                    SixteenDirections.SE,
                    SixteenDirections.SSE,
                    SixteenDirections.SSE,
                    SixteenDirections.S,
                    SixteenDirections.S,
                    SixteenDirections.SSW,
                    SixteenDirections.SSW,
                    SixteenDirections.SW,
                    SixteenDirections.SW,
                    SixteenDirections.WSW,
                    SixteenDirections.WSW,
                    SixteenDirections.W,
                    SixteenDirections.W,
                    SixteenDirections.WNW,
                    SixteenDirections.WNW,
                    SixteenDirections.NW,
                    SixteenDirections.NW,
                    SixteenDirections.NNW,
                    SixteenDirections.NNW,
                    SixteenDirections.N
                )

            @JvmStatic
            protected fun <T : WeatherData> getInstance(
                context: Context,
                scope: CoroutineScope,
                timeStampKey: String,
                url: Uri,
                callback: (T) -> Unit,
                downloadedJSONStringParser: (String) -> T,
                cacheLoader: () -> T?
            ) {
                val now = DateTime.now()
                if (now - getLatestRequestDateTime(context, timeStampKey) < minimumInterval) {
                    val cache = cacheLoader()
                    if (cache != null) {
                        callback(cache)
                    } else {
                        getCurrentFromServer(
                            scope,
                            context,
                            timeStampKey,
                            url,
                            now,
                            callback,
                            downloadedJSONStringParser
                        )
                    }
                } else {
                    getCurrentFromServer(
                        scope,
                        context,
                        timeStampKey,
                        url,
                        now,
                        callback,
                        downloadedJSONStringParser
                    )
                }
            }

            private fun <T : WeatherData> getCurrentFromServer(
                scope: CoroutineScope,
                context: Context,
                timeStampKey: String,
                url: Uri,
                now: DateTime,
                callback: (T) -> Unit,
                downloadedJSONStringParser: (String) -> T
            ) {
                setLatestRequestDateTime(context, timeStampKey, now)
                AsyncUtility.downloadString(scope, url) { text ->
                    val data = try {
                        downloadedJSONStringParser(text)
                    } catch (ex: Exception) {
                        null
                    }
                    if (data != null) {
                        data.saveToCache(context)
                        callback(data)
                    }
                }
            }

            private fun getLatestRequestDateTime(
                context: Context,
                timeStampKey: String
            ): DateTime =
                context.getSharedPreferences(PREFS_NAME, 0).let { prefs ->
                    DateTime.fromEpochSeconds(prefs.getLong(timeStampKey, 0))
                }

            private fun setLatestRequestDateTime(
                context: Context,
                timeStampKey: String,
                dateTime: DateTime
            ) {
                val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
                prefs.putLong(timeStampKey, dateTime.epochSeconds)
                prefs.apply()
            }

            @JvmStatic
            protected fun loadFromCache(context: Context, cacheKey: String): String? =
                context.getSharedPreferences(PREFS_NAME, 0)
                    .getString(cacheKey, null)

            @Suppress("SameParameterValue")
            @JvmStatic
            protected fun getArrayOfJSONObject(o: JSONObject, name: String): Array<JSONObject> =
                try {
                    val array = o.getJSONArray(name)
                    val result = mutableListOf<JSONObject>()
                    for (index in 0 until array.length()) {
                        result.add(array.getJSONObject(index))
                    }
                    result.toTypedArray()
                } catch (ex: Exception) {
                    throw Exception("JSONObject.getArrayOfJSONObject('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun getJSONObject(o: JSONObject, name: String): JSONObject =
                try {
                    o.getJSONObject(name)
                } catch (ex: Exception) {
                    throw Exception("JSONObject.getJSONObject('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun optJSONObject(o: JSONObject, name: String): JSONObject? =
                try {
                    if (o.has(name))
                        o.getJSONObject(name)
                    else
                        null
                } catch (ex: Exception) {
                    throw Exception("JSONObject.optJSONObject('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun getInt(o: JSONObject, name: String): Int =
                try {
                    o.getInt(name)
                } catch (ex: Exception) {
                    throw Exception("JSONObject.getLong('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun getLong(o: JSONObject, name: String): Long =
                try {
                    o.getLong(name)
                } catch (ex: Exception) {
                    throw Exception("JSONObject.getLong('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun getString(o: JSONObject, name: String): String =
                try {
                    o.getString(name)
                } catch (ex: Exception) {
                    throw Exception("JSONObject.getString('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun getDouble(o: JSONObject, name: String): Double =
                try {
                    o.getDouble(name)
                } catch (ex: Exception) {
                    throw Exception("JSONObject.getDouble('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun optDouble(o: JSONObject, name: String): Double? =
                try {
                    if (o.has(name))
                        o.getDouble(name)
                    else
                        null
                } catch (ex: Exception) {
                    throw Exception("JSONObject.optDouble('$name') is failed: source=$o")
                }

            @JvmStatic
            protected fun getDirection(degree: Double): SixteenDirections? =
                directions[
                        (floor(degree / 360 * 32).toInt() % 32)
                            .let {
                                if (it >= 0)
                                    it
                                else
                                    it + 32
                            }]
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
        override fun saveToCache(context: Context) {
            saveToCache(context, PREF_KEY_CACHE_CURRENT_WEATHER_DATA)
        }

        companion object {
            fun getInstance(
                context: Context,
                scope: CoroutineScope,
                locale: java.util.Locale,
                latitude: Double,
                longitude: Double,
                callback: (CurrentWeatherData) -> Unit
            ) {
                getInstance(
                    context,
                    scope,
                    PREF_KEY_LATEST_REQUEST_CURRENT_WEATHER_DATA,
                    buildRequest(context, locale, latitude, longitude),
                    callback,
                    { sourceText -> fromJSONString(sourceText, false) },
                    { loadFromCache(context) })
            }

            private fun buildRequest(
                context: Context,
                locale: java.util.Locale,
                latitude: Double,
                longitude: Double
            ): Uri =
                Uri.Builder().let {
                    it.scheme("https")
                    it.authority("api.openweathermap.org")
                    it.path("/data/2.5/weather")
                    it.appendQueryParameter(
                        "appid",
                        context.getString(R.string.openWeatherMapApiKey)
                    )
                    it.appendQueryParameter("units", "metric")
                    it.appendQueryParameter("lang", locale.language)
                    it.appendQueryParameter("lat", latitude.toString())
                    it.appendQueryParameter("lon", longitude.toString())
                    it.build()
                }

            private fun fromJSONString(sourceText: String, isCached: Boolean): CurrentWeatherData =
                fromJSONObject(
                    sourceText,
                    try {
                        JSONObject(sourceText)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$sourceText")
                    },
                    isCached
                )

            private fun fromJSONObject(
                sourceText: String,
                o: JSONObject,
                isCached: Boolean
            ): CurrentWeatherData =
                CurrentWeatherData(
                    sourceText,
                    isCached,
                    Coord.fromJSONObject(getJSONObject(o, "coord")),
                    Sys.fromJSONObject(getJSONObject(o, "sys")),
                    Main.fromJSONObject(getJSONObject(o, "main")),
                    getArrayOfJSONObject(o, "weather").map { Weather.fromJSONObject(it) }
                        .toTypedArray(),
                    Wind.fromJSONObject(getJSONObject(o, "wind")),
                    Clouds.fromJSONObjectOrNull(optJSONObject(o, "clouds")),
                    Precipitation.fromJSONObjectOrNull(optJSONObject(o, "rain")),
                    Precipitation.fromJSONObjectOrNull(optJSONObject(o, "snow")),
                    DateTime.fromEpochSeconds(getLong(o, "dt")),
                    TimeZone.ofTotalSeconds(getInt(o, "timezone"))
                )

            private fun loadFromCache(context: Context): CurrentWeatherData? =
                loadFromCache(context, PREF_KEY_CACHE_CURRENT_WEATHER_DATA)?.let { sourceText ->
                    try {
                        fromJSONString(sourceText, true)
                    } catch (ex: Exception) {
                        null
                    }
                }
        }

        class Sys private constructor(val sunrise: DateTime, val sunset: DateTime) {
            companion object {
                fun fromJSONObject(o: JSONObject): Sys =
                    Sys(
                        DateTime.fromEpochSeconds(getLong(o, "sunrise")),
                        DateTime.fromEpochSeconds(getLong(o, "sunset"))
                    )
            }
        }
    }

    class OneCall private constructor(
        sourceText: String,
        isCached: Boolean,
        val latitude: Double,
        val longitude: Double,
        val timeZone: TimeZone,
        val current: CurrentOrHourly,
        val hourly: Array<CurrentOrHourly>,
        val daily: Array<Daily>
    ) : WeatherData(sourceText, isCached) {
        override fun saveToCache(context: Context) {
            saveToCache(context, PREF_KEY_CACHE_ONE_CALL)
        }

        /**
         * @param directionInDegrees 北から吹く風は 0° 。時計回りに増えていく。
         */
        class WindOfOneShot(
            val speedMeterInPerSecond: Double,
            val directionInDegrees: Double?,
            val gustInMeterPerSecond: Double?
        ) {
            val direction: SixteenDirections?
                get() = directionInDegrees?.let { getDirection(it) }
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
                fun fromJSONObject(o: JSONObject): CurrentOrHourly =
                    CurrentOrHourly(
                        DateTime.fromEpochSeconds(getLong(o, "dt")),
                        getArrayOfJSONObject(o, "weather").map { Weather.fromJSONObject(it) }
                            .toTypedArray(),
                        getDouble(o, "temp"),
                        getDouble(o, "feels_like"),
                        getDouble(o, "humidity"),
                        optDouble(o, "dew_point"),
                        getDouble(o, "clouds"),
                        WindOfOneShot(
                            getDouble(o, "wind_speed"),
                            optDouble(o, "wind_deg"),
                            optDouble(o, "wind_gust")
                        ),
                        DateTime.fromEpochSeconds(getLong(o, "sunrise")),
                        DateTime.fromEpochSeconds(getLong(o, "sunset")),
                        UltravioletIndex.fromValue((getDouble(o, "uvi"))),
                        optDouble(o, "visibility"),
                        Precipitation.fromJSONObjectOrNull(optJSONObject(o, "rain")),
                        Precipitation.fromJSONObjectOrNull(optJSONObject(o, "snow"))
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
                    fun fromJSONObject(o: JSONObject): Temperature =
                        Temperature(
                            getDouble(o, "morn"),
                            getDouble(o, "day"),
                            getDouble(o, "eve"),
                            getDouble(o, "night"),
                            getDouble(o, "min"),
                            getDouble(o, "max")
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
                    fun fromJSONObject(o: JSONObject): FeelsLikeTemperature =
                        FeelsLikeTemperature(
                            getDouble(o, "morn"),
                            getDouble(o, "day"),
                            getDouble(o, "eve"),
                            getDouble(o, "night")
                        )
                }
            }

            companion object {
                fun fromJSONObject(o: JSONObject): Daily =
                    Daily(
                        DateTime.fromEpochSeconds(getLong(o, "dt")),
                        getArrayOfJSONObject(o, "weather").map { Weather.fromJSONObject(it) }
                            .toTypedArray(),
                        Temperature.fromJSONObject(getJSONObject(o, "temp")),
                        FeelsLikeTemperature.fromJSONObject(getJSONObject(o, "feels_like")),
                        getDouble(o, "humidity"),
                        optDouble(o, "dew_point"),
                        getDouble(o, "clouds"),
                        WindOfOneShot(
                            getDouble(o, "wind_speed"),
                            optDouble(o, "wind_deg"),
                            optDouble(o, "wind_gust")
                        ),
                        DateTime.fromEpochSeconds(getLong(o, "sunrise")),
                        DateTime.fromEpochSeconds(getLong(o, "sunset")),
                        UltravioletIndex.fromValue((getDouble(o, "uvi"))),
                        optDouble(o, "visibility"),
                        Precipitation.fromJSONObjectOrNull(optJSONObject(o, "rain")),
                        Precipitation.fromJSONObjectOrNull(optJSONObject(o, "snow"))
                    )
            }
        }

        companion object {
            fun getInstance(
                context: Context,
                scope: CoroutineScope,
                locale: java.util.Locale,
                latitude: Double,
                longitude: Double,
                callback: (OneCall) -> Unit
            ) {
                getInstance(
                    context,
                    scope,
                    PREF_KEY_LATEST_REQUEST_ONE_CALL,
                    buildRequest(context, locale, latitude, longitude),
                    callback,
                    { sourceText -> fromJSONString(sourceText, false) },
                    { loadFromCache(context) })
            }

            private fun buildRequest(
                context: Context,
                locale: java.util.Locale,
                latitude: Double,
                longitude: Double
            ): Uri =
                Uri.Builder().let {
                    it.scheme("https")
                    it.authority("api.openweathermap.org")
                    it.path("/data/2.5/onecall")
                    it.appendQueryParameter(
                        "appid",
                        context.getString(R.string.openWeatherMapApiKey)
                    )
                    it.appendQueryParameter("units", "metric")
                    it.appendQueryParameter("lang", locale.language)
                    it.appendQueryParameter("lat", latitude.toString())
                    it.appendQueryParameter("lon", longitude.toString())
                    it.build()
                }

            private fun fromJSONString(sourceText: String, isCached: Boolean): OneCall =
                fromJSONObject(
                    sourceText,
                    try {
                        JSONObject(sourceText)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$sourceText")
                    },
                    isCached
                )

            private fun fromJSONObject(
                sourceText: String,
                o: JSONObject,
                isCached: Boolean
            ): OneCall =
                OneCall(
                    sourceText,
                    isCached,
                    getDouble(o, "lat"),
                    getDouble(o, "lon"),
                    TimeZone.of(getString(o, "timezone")),
                    CurrentOrHourly.fromJSONObject(getJSONObject(o, "current")),
                    getArrayOfJSONObject(o, "hourly").map { CurrentOrHourly.fromJSONObject(it) }
                        .toTypedArray(),
                    getArrayOfJSONObject(o, "daily").map { Daily.fromJSONObject(it) }
                        .toTypedArray())

            private fun loadFromCache(context: Context): OneCall? =
                loadFromCache(context, PREF_KEY_CACHE_ONE_CALL)?.let { sourceText ->
                    try {
                        fromJSONString(sourceText, true)
                    } catch (ex: Exception) {
                        null
                    }
                }
        }
    }

    class FiveDayWeatherForecast private constructor(
        sourceText: String,
        isCached: Boolean,
        val city: City,
        val forecasts: Array<Forecast>
    ) : WeatherData(sourceText, isCached) {
        override fun saveToCache(context: Context) {
            saveToCache(context, PREF_KEY_CACHE_FIVE_DAY_WEATHER_FORECAST)
        }

        class City private constructor(
            val coord: Coord,
            val timeZone: TimeZone
        ) {
            companion object {
                fun fromJSONObject(o: JSONObject): City =
                    City(
                        Coord.fromJSONObject(getJSONObject(o, "coord")),
                        TimeZone.ofTotalSeconds(getInt(o, "timezone"))
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
                fun fromJSONObject(o: JSONObject): Forecast =
                    Forecast(
                        Main.fromJSONObject(getJSONObject(o, "main")),
                        getArrayOfJSONObject(o, "weather").map { Weather.fromJSONObject(it) }
                            .toTypedArray(),
                        Wind.fromJSONObject(getJSONObject(o, "wind")),
                        Clouds.fromJSONObjectOrNull(optJSONObject(o, "clouds")),
                        Precipitation.fromJSONObjectOrNull(optJSONObject(o, "rain")),
                        Precipitation.fromJSONObjectOrNull(optJSONObject(o, "snow")),
                        DateTime.fromEpochSeconds(getLong(o, "dt"))
                    )
            }
        }

        companion object {
            fun getInstance(
                context: Context,
                scope: CoroutineScope,
                locale: java.util.Locale,
                latitude: Double,
                longitude: Double,
                callback: (FiveDayWeatherForecast) -> Unit
            ) {
                getInstance(
                    context,
                    scope,
                    PREF_KEY_LATEST_REQUEST_FIVE_DAY_WEATHER_FORECAST,
                    buildRequest(context, locale, latitude, longitude),
                    callback,
                    { sourceText -> fromJSONString(sourceText, false) },
                    { loadFromCache(context) })
            }

            private fun buildRequest(
                context: Context,
                locale: java.util.Locale,
                latitude: Double,
                longitude: Double
            ): Uri =
                Uri.Builder().let {
                    it.scheme("https")
                    it.authority("api.openweathermap.org")
                    it.path("/data/2.5/forecast")
                    it.appendQueryParameter(
                        "appid",
                        context.getString(R.string.openWeatherMapApiKey)
                    )
                    it.appendQueryParameter("units", "metric")
                    it.appendQueryParameter("lang", locale.language)
                    it.appendQueryParameter("lat", latitude.toString())
                    it.appendQueryParameter("lon", longitude.toString())
                    it.build()
                }

            private fun fromJSONString(
                sourceText: String,
                isCached: Boolean
            ): FiveDayWeatherForecast =
                fromJSONObject(
                    sourceText,
                    try {
                        JSONObject(sourceText)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$sourceText")
                    },
                    isCached
                )

            private fun fromJSONObject(
                sourceText: String,
                o: JSONObject,
                isCached: Boolean
            ): FiveDayWeatherForecast =
                FiveDayWeatherForecast(
                    sourceText,
                    isCached,
                    City.fromJSONObject(getJSONObject(o, "city")),
                    getArrayOfJSONObject(o, "list").map { Forecast.fromJSONObject(it) }
                        .toTypedArray())

            private fun loadFromCache(context: Context): FiveDayWeatherForecast? =
                loadFromCache(
                    context,
                    PREF_KEY_CACHE_FIVE_DAY_WEATHER_FORECAST
                )?.let { sourceText ->
                    try {
                        fromJSONString(sourceText, true)
                    } catch (ex: Exception) {
                        null
                    }
                }
        }
    }

    companion object {
        private const val PREFS_NAME = "com.palmtreesoftware.experimentandroid5_1"
        private const val PREF_KEY_LATEST_REQUEST_CURRENT_WEATHER_DATA = "latest-request-weather"
        private const val PREF_KEY_LATEST_REQUEST_ONE_CALL = "latest-request-onecall"
        private const val PREF_KEY_LATEST_REQUEST_FIVE_DAY_WEATHER_FORECAST =
            "latest-request-forecast"
        private const val PREF_KEY_CACHE_CURRENT_WEATHER_DATA = "cache-weather"
        private const val PREF_KEY_CACHE_ONE_CALL = "cache-onecall"
        private const val PREF_KEY_CACHE_FIVE_DAY_WEATHER_FORECAST = "cache-forecast"
        private val minimumInterval: TimeDuration = TimeDuration.fromMinutes(10.0)

    }
}
