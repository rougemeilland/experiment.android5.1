package com.palmtreesoftware.experimentandroid5_1

interface TimeZoneForm {
    val isDefault: Boolean
    fun getSymbol(): String?
    fun getTimeDifference(): Triple<Int, Int, Int>?
    fun reset()
    fun set(timeZoneId: String)
    fun set(hours: Int, minutes: Int)
}
