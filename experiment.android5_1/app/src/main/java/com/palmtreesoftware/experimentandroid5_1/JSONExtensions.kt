package com.palmtreesoftware.experimentandroid5_1

import org.json.JSONArray
import org.json.JSONObject

interface JSONObjectCompatible {
    fun toJSONObject(): JSONObject
}

interface JSONArrayCompatible {
    fun toJSONArray(): JSONArray
}

fun JSONArray.toIterableOfJSONObject(): Iterable<JSONObject> =
    (0 until length()).map { index -> getJSONObject(index) }

fun JSONArray.toIterableOfJSONArray(): Iterable<JSONArray> =
    (0 until length()).map { index -> getJSONArray(index) }

fun JSONArray.toIterableOfString(): Iterable<String> =
    (0 until length()).map { index -> getString(index) }

fun JSONArray.toIterableOfInt(): Iterable<Int> =
    (0 until length()).map { index -> getInt(index) }

fun JSONArray.toIterableOfLong(): Iterable<Long> =
    (0 until length()).map { index -> getLong(index) }

fun JSONArray.toIterableOfDouble(): Iterable<Double> =
    (0 until length()).map { index -> getDouble(index) }

fun JSONArray.toIterableOfLocale(): Iterable<java.util.Locale> =
    (0 until length()).map { index -> localeOf(getJSONObject(index)) }

fun Array<JSONObject>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value)
        }
    }

fun Array<JSONArray>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value)
        }
    }

fun Array<String>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value)
        }
    }

fun Array<Int>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value)
        }
    }

fun Array<Long>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value)
        }
    }

fun Array<java.util.Locale>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value.toJSONObject())
        }
    }

fun Array<android.location.Address>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value.toJSONObject())
        }
    }

fun Array<JSONObjectCompatible>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value.toJSONObject())
        }
    }

fun Array<JSONArrayCompatible>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            array.put(value.toJSONArray())
        }
    }

fun <T> Iterable<T>.toJSONArray(): JSONArray =
    JSONArray().also { array ->
        this.forEach { value ->
            when {
                value is JSONObject -> array.put(value)
                value is JSONArray -> array.put(value)
                value is String -> array.put(value)
                value is Int -> array.put(value)
                value is Long -> array.put(value)
                value is Double -> array.put(value)
                value is java.util.Locale -> array.put(value.toJSONObject())
                value is android.location.Address -> array.put(value.toJSONObject())
                value is JSONObjectCompatible -> array.put(value.toJSONObject())
                value is JSONArrayCompatible -> array.put(value.toJSONArray())
                else -> throw NotImplementedError()
            }
        }
    }

fun java.util.Locale.toJSONObject(): JSONObject =
    JSONObject().also { localeObject ->
        localeObject.put("language", language)
        country.also { country ->
            if (!country.isNullOrEmpty()) {
                localeObject.put("country", country)
                variant.also { variant ->
                    if (!variant.isNullOrEmpty()) {
                        localeObject.put("variant", variant)
                    }
                }
            }
        }
    }

fun localeOf(o: JSONObject): java.util.Locale {
    val language = o.getString("language")
    return o.optString("country").let { country ->
        if (country.isEmpty()) {
            java.util.Locale(language)
        } else {
            o.optString("variant").let { variant ->
                if (variant.isEmpty()) {
                    java.util.Locale(language, country)
                } else {
                    java.util.Locale(language, country, variant)
                }
            }
        }
    }
}

fun android.location.Address.toJSONObject(): JSONObject =
    JSONObject().also { addressObject ->
        addressObject.put("locale", locale.toJSONObject())
        if (hasLatitude())
            addressObject.put("latitude", latitude)
        if (hasLongitude())
            addressObject.put("longitude", longitude)
        maxAddressLineIndex.also { maxAddressLineIndex ->
            if (maxAddressLineIndex >= 0) {
                addressObject.putOpt(
                    "addressLines",
                    JSONArray().also { arrayObject ->
                        (0..maxAddressLineIndex).forEach { index ->
                            arrayObject.put(getAddressLine(index))
                        }
                    }
                )
            }
        }
        addressObject.putOpt("adminArea", adminArea)
        addressObject.putOpt("countryCode", countryCode)
        addressObject.putOpt("countryName", countryName)
        addressObject.putOpt("featureName", featureName)
        addressObject.putOpt("locality", locality)
        addressObject.putOpt("phone", phone)
        addressObject.putOpt("postalCode", postalCode)
        addressObject.putOpt("premises", premises)
        addressObject.putOpt("subAdminArea", subAdminArea)
        addressObject.putOpt("subLocality", subLocality)
        addressObject.putOpt("subThoroughfare", subThoroughfare)
        addressObject.putOpt("thoroughfare", thoroughfare)
        addressObject.putOpt("url", url)
    }

fun addressOf(o: JSONObject): android.location.Address =
    android.location.Address(localeOf(o.getJSONObject("locale")))
        .apply {
            clearLatitude()
            clearLongitude()
            o.optDouble("latitude").also {
                if (!it.isNaN())
                    latitude = it
            }
            o.optDouble("longitude").also {
                if (!it.isNaN())
                    longitude = it
            }
            o.optJSONArray("addressLines")?.also { array ->
                (0 until array.length()).forEach { index ->
                    setAddressLine(index, array.getString(index))
                }
            }
            o.optString("adminArea").also {
                if (it.isNotEmpty())
                    adminArea = it
            }
            o.optString("countryCode").also {
                if (it.isNotEmpty())
                    countryCode = it
            }
            o.optString("countryName").also {
                if (it.isNotEmpty())
                    countryName = it
            }
            o.optString("featureName").also {
                if (it.isNotEmpty())
                    featureName = it
            }
            o.optString("locality").also {
                if (it.isNotEmpty())
                    locality = it
            }
            o.optString("locality").also {
                if (it.isNotEmpty())
                    locality = it
            }
            o.optString("phone").also {
                if (it.isNotEmpty())
                    phone = it
            }
            o.optString("postalCode").also {
                if (it.isNotEmpty())
                    postalCode = it
            }
            o.optString("premises").also {
                if (it.isNotEmpty())
                    premises = it
            }
            o.optString("subAdminArea").also {
                if (it.isNotEmpty())
                    subAdminArea = it
            }
            o.optString("subAdminArea").also {
                if (it.isNotEmpty())
                    subAdminArea = it
            }
            o.optString("subLocality").also {
                if (it.isNotEmpty())
                    subLocality = it
            }
            o.optString("subThoroughfare").also {
                if (it.isNotEmpty())
                    subThoroughfare = it
            }
            o.optString("thoroughfare").also {
                if (it.isNotEmpty())
                    thoroughfare = it
            }
            o.optString("url").also {
                if (it.isNotEmpty())
                    url = it
            }
        }

// 【重要】 文字列リソースに json 文字列を含めることは困難であるため、 json ファイルを raw リソースとして扱うのが最適解の模様。
// 理由： 文字列リソースには、特定の文字列(例えば、ダブルクォート、シングルクォート)を含めることができない。
//       XMLエンコードしても CDATA として扱っても、結果は同じ。(更に言えば、 getString でも getText でも同じ)
//       ダブルクォートは getSting の際に剥ぎ取られてしまう。シングルクォートに至ってはコンパイル時にエラーが発生する。
//       この現象は、文字列リソースを記述する際に &quot; にエンコードしても、 <![CDATA[ "a" 'b' ]]> とかでくくっても結果は変わらなかった。
//       ダブルクォート文字に対して、getString が認識しないであろう独自のエンコードを行えばいけそうではあるが、
//       手間も考慮し、 json 文字列を文字列リソースとして扱うことは断念。
//       代替手段として json ファイルを raw リソースとして扱うことにした。
fun android.content.res.Resources.getJSONObject(resourceId: Int): JSONObject =
    JSONObject(this.openRawResource(resourceId).bufferedReader().use { it.readText() })

fun android.content.res.Resources.getJSONArray(resourceId: Int): JSONArray =
    JSONArray(this.openRawResource(resourceId).bufferedReader().use { it.readText() })
