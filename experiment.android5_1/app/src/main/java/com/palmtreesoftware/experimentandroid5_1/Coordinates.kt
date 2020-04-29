package com.palmtreesoftware.experimentandroid5_1

import android.net.Uri
import org.json.JSONObject

class Coordinates(val latitude: Double, val longitude: Double) : JSONObjectCompatible {
    init {
        if (latitude.isNaN())
            throw IllegalArgumentException("${javaClass.canonicalName}.init(): latitude must not be NaN")
        if (latitude.isInfinite())
            throw IllegalArgumentException("${javaClass.canonicalName}.init(): latitude must not be Infinite")
        if (longitude.isNaN())
            throw IllegalArgumentException("${javaClass.canonicalName}.init(): longitude must not be NaN")
        if (longitude.isInfinite())
            throw IllegalArgumentException("${javaClass.canonicalName}.init(): longitude must not be Infinite")
    }

    fun toGoogleMapUrl(): Uri =
        Uri.Builder().run {
            scheme("https")
            authority("maps.google.co.jp")
            path("/maps")
            appendQueryParameter("q", "${"%10f".format(latitude)},${"%10f".format(longitude)}")
            build()
        }

    override fun toJSONObject(): JSONObject =
        JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false
        other as Coordinates
        if (latitude != other.latitude)
            return false
        if (longitude != other.longitude)
            return false
        return true
    }

    override fun hashCode(): Int {
        return latitude.hashCode() * 31 + longitude.hashCode()
    }

    companion object {
        fun of(o: JSONObject): Coordinates =
            Coordinates(o.getDouble("latitude"), o.getDouble("longitude"))
    }
}