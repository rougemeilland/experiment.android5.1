package com.palmtreesoftware.experimentandroid51

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class OpenWeatherMap {
    class Current(
        /*val id: String,*/
        /*val name: String,*/
        val coord: Coord,
        val sys: Sys,
        /*val timezone: TimeDuration,*/
        val main: Main,
        val weathers: Array<Weather>,
        val wind: Wind,
        val clouds: Clouds?,
        val rain: Rain?,
        val snow: Snow?,
        val lastUpdated: DateTime
    ) {
        fun toJSONString(): String = toJSONObject().toString()

        private fun toJSONObject(): JSONObject =
            JSONObject().also { o ->
                o.put("coord", coord.toJSONObject())
                o.put("sys", sys.toJSONObject())
                o.put("main", main.toJSONObject())
                o.put("weather", JSONArray(weathers.map { it.toJSONObject() }))
                o.put("wind", wind.toJSONObject())
                clouds?.let { o.put("clouds", it.toJSONObject()) }
                rain?.let { o.put("rain", it.toJSONObject()) }
                snow?.let { o.put("snow", it.toJSONObject()) }
                o.put("dt", lastUpdated.epochSeconds)
            }

        companion object {
            fun buildRequest(
                context: Context,
                locale: Locale,
                latitude: Double,
                longitude: Double
            ): Uri {
                return Uri.Builder().let {
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
            }

            fun fromJSONString(source: String): Current {
                return fromJSONObject(
                    try {
                        JSONObject(source)
                    } catch (ex: Exception) {
                        throw Exception("JSONObject(soource) is failed: source=$source")
                    }
                )
            }

            private fun fromJSONObject(o: JSONObject): Current {
                return Current(
                    /*getString(o, "id"),*/
                    /*getString(o, "name"),*/
                    Coord.fromJSONObject(getJSONObject(o, "coord")),
                    Sys.fromJSONObject(getJSONObject(o, "sys")),
                    /*TimeDuration.fromSeconds(getLong(o, "timezone").toDouble()),*/
                    Main.fromJSONObject(getJSONObject(o, "main")),
                    getArrayOfJSONObject(o, "weather").map { Weather.fromJSONObject(it) }
                        .toTypedArray(),
                    Wind.fromJSONObject(getJSONObject(o, "wind")),
                    Clouds.fromJSONObjectOrNull(optJSONObject(o, "clouds")),
                    Rain.fromJSONObjectOrNull(optJSONObject(o, "rain")),
                    Snow.fromJSONObjectOrNull(optJSONObject(o, "snow")),
                    DateTime.fromEpochSeconds(getLong(o, "dt"))
                )
            }

        }

        class Coord private constructor(val latitude: Double, val longitude: Double) {
            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    o.put("lat", latitude)
                    o.put("lon", longitude)
                }

            companion object {
                fun fromJSONObject(o: JSONObject): Coord {
                    return Coord(
                        getDouble(o, "lat"),
                        getDouble(o, "lon")
                    )
                }
            }
        }

        class Sys(
            /*val country: String,*/
            val sunrise: DateTime,
            val sunset: DateTime
        ) {
            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    o.put("sunrise", sunrise.epochSeconds)
                    o.put("sunset", sunset.epochSeconds)
                }

            companion object {
                fun fromJSONObject(o: JSONObject): Sys {
                    return Sys(
                        /*getString(o, "country"),*/
                        DateTime.fromEpochSeconds(getLong(o, "sunrise")),
                        DateTime.fromEpochSeconds(getLong(o, "sunset"))
                    )
                }
            }
        }

        class Main(
            /*val tempCelsius: Double,*/
            val temperatureCelsius: Double,
            val minTemperatureCelsius: Double,
            val maxTemperatureCelsius: Double,
            /*val pressureHectoPascal: Double,*/
            val humidityPercent: Double
            /*val seaLevelHectoPascal: Double?,*/
            /*val grndLevelHectoPascal: Double?*/
        ) {
            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    o.put("feels_like", temperatureCelsius)
                    o.put("temp_min", minTemperatureCelsius)
                    o.put("temp_max", maxTemperatureCelsius)
                    o.put("humidity", humidityPercent)
                }

            companion object {
                fun fromJSONObject(o: JSONObject): Main {
                    return Main(
                        /*getDouble(o, "temp"),*/
                        getDouble(o, "feels_like"),
                        getDouble(o, "temp_min"),
                        getDouble(o, "temp_max"),
                        /*getDouble(o, "pressure"),*/
                        getDouble(o, "humidity")
                        /*optDouble(o, "sea_level"),*/
                        /*optDouble(o, "grnd_level")*/
                    )
                }
            }
        }

        class Weather private constructor(
            /*val id: String,*/
            /*val main: String,*/
            val description: String,
            val icon: String
        ) {
            val iconUrl: Uri
                get() {
                    return Uri.Builder().let { uri ->
                        uri.scheme("https")
                        uri.authority("openweathermap.org")
                        uri.path("/img/wn/$icon.png")
                        uri.build()
                    }
                }

            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    o.put("description", description)
                    o.put("icon", description)
                }

            companion object {
                fun fromJSONObject(o: JSONObject): Weather {
                    return Weather(
                        /*getString(o, "id"),*/
                        /*getString(o, "main"),*/
                        getString(o, "description"),
                        getString(o, "icon")
                    )
                }
            }
        }

        class Wind private constructor(
            val speedMeterPerSecond: Double,
            /* 北から吹く風は 0° 。時計回りに増えていく。*/
            val degree: Double?
        ) {
            val direction: SixteenDirections?
                get() = degree?.let { getDirection(it) }

            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    o.put("speed", speedMeterPerSecond)
                    degree?.let { o.put("deg", it) }
                }

            companion object {
                fun fromJSONObject(o: JSONObject): Wind {
                    return Wind(
                        getDouble(o, "speed"),
                        optDouble(o, "deg")
                    )
                }
            }
        }

        class Clouds private constructor(
            // 曇天の度合い
            val allPercent: Double
        ) {
            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    o.put("all", allPercent)
                }

            companion object {
                fun fromJSONObjectOrNull(o: JSONObject?): Clouds? {
                    return if (o != null)
                        Clouds(
                            getDouble(o, "all")
                        )
                    else
                        null
                }
            }
        }

        class Rain private constructor(val `1hMilliMeter`: Double?, val `3hMilliMeter`: Double?) {
            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    `1hMilliMeter`?.let { o.put("1h", it) }
                    `3hMilliMeter`?.let { o.put("3h", it) }
                }

            companion object {
                fun fromJSONObjectOrNull(o: JSONObject?): Rain? {
                    return if (o != null)
                        Rain(
                            optDouble(o, "1h"),
                            optDouble(o, "3h")
                        )
                    else
                        null
                }
            }
        }

        class Snow private constructor(val `1hMilliMeter`: Double?, val `3hMilliMeter`: Double?) {
            fun toJSONObject(): JSONObject =
                JSONObject().also { o ->
                    `1hMilliMeter`?.let { o.put("1h", it) }
                    `3hMilliMeter`?.let { o.put("3h", it) }
                }

            companion object {
                fun fromJSONObjectOrNull(o: JSONObject?): Snow? {
                    return if (o != null)
                        Snow(
                            optDouble(o, "1h"),
                            optDouble(o, "3h")
                        )
                    else
                        null
                }
            }
        }
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

        @Suppress("SameParameterValue")
        private fun getArrayOfJSONObject(o: JSONObject, name: String): Array<JSONObject> {
            try {
                val array = o.getJSONArray(name)
                val result = mutableListOf<JSONObject>()
                for (index in 0 until array.length()) {
                    result.add(array.getJSONObject(index))
                }
                return result.toTypedArray()
            } catch (ex: Exception) {
                throw Exception("JSONObject.getArrayOfJSONObject('$name') is failed: source=$o")
            }
        }

        private fun optArrayOfJSONObject(o: JSONObject, name: String): Array<JSONObject>? =
            try {
                if (!o.has(name))
                    null
                else {
                    val array = o.getJSONArray(name)
                    val result = mutableListOf<JSONObject>()
                    for (index in 0 until array.length()) {
                        result.add(array.getJSONObject(index))
                    }
                    result.toTypedArray()
                }
            } catch (ex: Exception) {
                throw Exception("JSONObject.optJSONArray('$name') is failed: source=$o")
            }

        private fun getJSONObject(o: JSONObject, name: String): JSONObject {
            try {
                return o.getJSONObject(name)
            } catch (ex: Exception) {
                throw Exception("JSONObject.getJSONObject('$name') is failed: source=$o")
            }
        }

        private fun optJSONObject(o: JSONObject, name: String): JSONObject? {
            try {
                return if (o.has(name)) o.getJSONObject(name) else null
            } catch (ex: Exception) {
                throw Exception("JSONObject.optJSONObject('$name') is failed: source=$o")
            }
        }

        private fun getLong(o: JSONObject, name: String): Long {
            try {
                return o.getLong(name)
            } catch (ex: Exception) {
                throw Exception("JSONObject.getLong('$name') is failed: source=$o")
            }
        }

        private fun optLong(o: JSONObject, name: String): Long? {
            try {
                return if (o.has(name)) o.getLong(name) else null
            } catch (ex: Exception) {
                throw Exception("JSONObject.optLong('$name') is failed: source=$o")
            }
        }

        private fun getString(o: JSONObject, name: String): String {
            try {
                return o.getString(name)
            } catch (ex: Exception) {
                throw Exception("JSONObject.getString('$name') is failed: source=$o")
            }
        }

        private fun optString(o: JSONObject, name: String): String? {
            try {
                return if (o.has(name)) o.getString(name) else null
            } catch (ex: Exception) {
                throw Exception("JSONObject.optString('$name') is failed: source=$o")
            }
        }

        private fun getDouble(o: JSONObject, name: String): Double {
            try {
                return o.getDouble(name)
            } catch (ex: Exception) {
                throw Exception("JSONObject.getDouble('$name') is failed: source=$o")
            }
        }

        private fun optDouble(o: JSONObject, name: String): Double? {
            try {
                return if (o.has(name)) o.getDouble(name) else null
            } catch (ex: Exception) {
                throw Exception("JSONObject.optDouble('$name') is failed: source=$o")
            }
        }

        private fun getDirection(degree: Double): SixteenDirections? {
            return directions[((degree / 360 * 32).toInt() % 32).let {
                if (it >= 0) it else it + 32
            }]
        }
    }
}


