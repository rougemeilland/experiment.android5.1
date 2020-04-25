package com.palmtreesoftware.experimentandroid5_1

class Coordinates(val latitude: Double, val longitude: Double) {
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
}