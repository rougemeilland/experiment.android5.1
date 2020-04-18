package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

abstract class TimeZone protected constructor(val id: String) {
    protected abstract class SymbolicTimeZone private constructor(
        private val timeZoneId: String,
        private val shortNameResourceId: Int
    ) : TimeZone(timeZoneId) {
        @RequiresApi(Build.VERSION_CODES.O)
        private class TimeZoneSDK26 private constructor(
            private val o: java.time.ZoneId,
            timeZoneId: String,
            shortNameResourceId: Int
        ) : SymbolicTimeZone(timeZoneId, shortNameResourceId) {
            override val rawObject: Any
                get() = o

            companion object {
                fun getDefaultTimeZoneId(): String =
                    java.time.ZoneId.systemDefault().id

                fun getDefault(): TimeZone =
                    java.time.ZoneId.systemDefault().let { defaultTimeZone ->
                        TimeZoneSDK26(
                            defaultTimeZone,
                            defaultTimeZone.id,
                            mapTimeZoneIdToResourceId(defaultTimeZone.id)
                        )
                    }

                fun createInstance(timeZoneId: String): TimeZone =
                    TimeZoneSDK26(
                        java.time.ZoneId.of(timeZoneId),
                        timeZoneId,
                        mapTimeZoneIdToResourceId(timeZoneId)
                    )
            }
        }

        private class TimeZoneSDK22 private constructor(
            private val o: java.util.TimeZone,
            timeZoneId: String,
            shortNameResourceId: Int
        ) : SymbolicTimeZone(timeZoneId, shortNameResourceId) {
            override val rawObject: Any
                get() = o

            companion object {
                fun getDefaultTimeZoneId(): String =
                    java.util.TimeZone.getDefault().id

                fun getDefault(): TimeZone =
                    java.util.TimeZone.getDefault().let { defaultTimeZone ->
                        TimeZoneSDK22(
                            defaultTimeZone,
                            defaultTimeZone.id,
                            mapTimeZoneIdToResourceId(defaultTimeZone.id)
                        )
                    }

                fun createInstance(timeZoneId: String): TimeZone =
                    try {
                        // 与えられたタイムゾーンIDが GMT ではなく、かつ、 与えられたタイムゾーンiDから作ってみた TimeZone オブジェクトの id プロパティが GMT だった場合は不当なタイムゾーンIDであると判断する
                        // ※ TimeZone.getTimeZone() が未知のタイムゾーンを全部 GMT とみなしてしまうため
                        val o = java.util.TimeZone.getTimeZone(timeZoneId)
                        if (timeZoneId == "GMT" || o.id != "GMT")
                            TimeZoneSDK22(
                                o,
                                timeZoneId,
                                mapTimeZoneIdToResourceId(timeZoneId)
                            )
                        else
                            getDefault()
                    } catch (ex: Exception) {
                        getDefault()
                    }
            }
        }

        override fun getTimeZoneShortName(context: Context): String =
            shortNameResourceId.let {
                if (it == R.string.time_zone_short_name_NONE)
                    timeZoneId
                else
                    context.getString(it)
            }

        override fun setToForm(form: TimeZoneForm) {
            if (id == getDefaultTimeZoneId())
                form.reset()
            else
                form.set(id)
        }

        abstract override val rawObject: Any

        companion object {
            fun getDefault(): TimeZone =
                Platform.sdK26Depended({
                    TimeZoneSDK26.getDefault()
                }, {
                    TimeZoneSDK22.getDefault()
                })

            fun createInstance(timeZoneId: String): TimeZone =
                Platform.sdK26Depended({
                    TimeZoneSDK26.createInstance(timeZoneId)
                }, {
                    TimeZoneSDK22.createInstance(timeZoneId)
                })

            fun getDefaultTimeZoneId(): String =
                Platform.sdK26Depended({
                    TimeZoneSDK26.getDefaultTimeZoneId()
                }, {
                    TimeZoneSDK22.getDefaultTimeZoneId()
                })
        }
    }

    protected abstract class NumericTimeZone private constructor(
        timeZoneId: String,
        private val shortTimeZoneName: String,
        private val hour: Int,
        private val minute: Int
    ) : TimeZone(timeZoneId) {
        @RequiresApi(Build.VERSION_CODES.O)
        private class TimeZoneSDK26 private constructor(
            private val o: java.time.ZoneId,
            timeZoneId: String,
            shortTimeZoneName: String,
            hour: Int,
            minute: Int
        ) : NumericTimeZone(timeZoneId, shortTimeZoneName, hour, minute) {
            override val rawObject: Any
                get() = o

            companion object {
                fun of(
                    timeZoneId: String,
                    shortTimeZoneName: String,
                    hour: Int,
                    minute: Int
                ): TimeZone =
                    TimeZoneSDK26(
                        java.time.ZoneId.of(timeZoneId),
                        timeZoneId,
                        shortTimeZoneName,
                        hour,
                        minute
                    )
            }
        }

        private class TimeZoneSDK22 private constructor(
            private val o: java.util.TimeZone,
            timeZoneId: String,
            shortTimeZoneName: String,
            hour: Int,
            minute: Int
        ) : NumericTimeZone(timeZoneId, shortTimeZoneName, hour, minute) {
            override val rawObject: Any
                get() = o

            companion object {
                fun of(
                    timeZoneId: String,
                    shortTimeZoneName: String,
                    hour: Int,
                    minute: Int
                ): TimeZone =
                    TimeZoneSDK22(
                        java.util.TimeZone.getTimeZone(timeZoneId),
                        timeZoneId,
                        shortTimeZoneName,
                        hour,
                        minute
                    )
            }
        }

        override fun getTimeZoneShortName(context: Context): String = shortTimeZoneName

        override fun setToForm(form: TimeZoneForm) {
            form.set(hour, minute)
        }

        companion object {
            // gmtFormat の桁指定子 %+03d の '3' は、符号を含めた桁数であることに注意すること
            private const val gmtFormat: String = "GMT%s%02d:%02d"
            private const val gmtFormatContainingSecond: String = "GMT%s%02d:%02d:%02d"
            private const val shortGmtFormat = "%s%02d%02d"
            private const val shortGmtFormatContainingSecond = "%s%02d%02d%02d"

            fun of(hour: Int, minute: Int, second: Int): TimeZone {
                return if (hour == 0 && minute == 0 && second == 0)
                    GMT
                else {
                    val (formatText, shortNameFormatText) =
                        if (second == 0) {
                            Pair(gmtFormat, shortGmtFormat)
                        } else {
                            Pair(gmtFormatContainingSecond, shortGmtFormatContainingSecond)
                        }
                    if (hour in 0..23 && minute in 0..59 && second in 0..59) {
                        Platform.sdK26Depended({
                            TimeZoneSDK26.of(
                                formatText.format("+", hour, minute, second),
                                shortNameFormatText.format("+", hour, minute, second),
                                hour,
                                minute
                            )
                        }, {
                            TimeZoneSDK22.of(
                                formatText.format("+", hour, minute, second),
                                shortNameFormatText.format("+", hour, minute, second),
                                hour,
                                minute
                            )
                        })
                    } else if (hour in -23..0 && minute in -59..0 && second in -59..0) {
                        Platform.sdK26Depended({
                            TimeZoneSDK26.of(
                                formatText.format("-", -hour, -minute, -second),
                                shortNameFormatText.format("-", -hour, -minute, -second),
                                hour,
                                minute
                            )
                        }, {
                            TimeZoneSDK22.of(
                                formatText.format("-", -hour, -minute, -second),
                                shortNameFormatText.format("-", -hour, -minute, -second),
                                hour,
                                minute
                            )
                        })
                    } else
                        throw IllegalArgumentException("${TimeZone::class.java.canonicalName}.of(): parameter is out of range: hour=$hour, minute=$minute, second=$second")
                }
            }
        }
    }

    abstract fun getTimeZoneShortName(context: Context): String
    abstract fun setToForm(form: TimeZoneForm)
    internal abstract val rawObject: Any

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimeZone
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "TimeZone(id='$id')"

    companion object {
        val GMT: TimeZone by lazy { of("GMT") }

        private val timeDifferenceExpressionPattern =
            Regex("^GMT([+-])?([0-9][0-9]):([0-9][0-9])(:([0-9][0-9]))?$")

        fun getDefault(): TimeZone =
            SymbolicTimeZone.getDefault()

        fun of(timeZoneId: String): TimeZone =
            timeDifferenceExpressionPattern.matchEntire(timeZoneId).let { matchResult ->
                if (matchResult == null) {
                    // 時差表現ではない場合 (システムデフォルト、または名前の表現)
                    SymbolicTimeZone.createInstance(timeZoneId)
                } else {
                    val signText = matchResult.destructured.component1()
                    val hourText = matchResult.destructured.component2()
                    val minuteText = matchResult.destructured.component3()
                    val secondText = matchResult.destructured.component5()
                    if (signText != "-") {
                        val hour = hourText.toInt()
                        val minute = minuteText.toInt()
                        val second = if (secondText.isEmpty()) 0 else secondText.toInt()
                        NumericTimeZone.of(hour, minute, second)
                    } else {
                        val hour = -hourText.toInt()
                        val minute = -minuteText.toInt()
                        val second = if (secondText.isEmpty()) 0 else -secondText.toInt()
                        NumericTimeZone.of(hour, minute, second)
                    }
                }
            }

        fun of(hours: Int, minutes: Int, seconds: Int): TimeZone =
            NumericTimeZone.of(hours, minutes, seconds)

        fun of(hours: Int, minutes: Int): TimeZone =
            of(hours, minutes, 0)

        fun ofTotalSeconds(totalSeconds: Int): TimeZone =
            if (totalSeconds >= 0) {
                (totalSeconds / 60).let { totalMinutes ->
                    of(
                        totalMinutes / 60,
                        totalMinutes % 60,
                        totalSeconds % 60
                    )
                }
            } else {
                (-totalSeconds).let { absoluteValueOfTotalSeconds ->
                    (absoluteValueOfTotalSeconds / 60).let { totalMinutes ->
                        of(
                            -(totalMinutes / 60),
                            -(totalMinutes % 60),
                            -(absoluteValueOfTotalSeconds % 60)
                        )
                    }
                }
            }

        fun of(form: TimeZoneForm): TimeZone =
            if (form.isDefault) {
                getDefault()
            } else {
                form.getSymbol().let { symbol ->
                    if (symbol != null) {
                        SymbolicTimeZone.createInstance(symbol)
                    } else {
                        form.getTimeDifference().let { timeDifference ->
                            if (timeDifference != null) {

                                NumericTimeZone.of(
                                    timeDifference.first,
                                    timeDifference.second,
                                    timeDifference.third
                                )
                            } else {
                                throw Exception("${TimeZone::class.java.canonicalName}.of(): Bad implementation of TimeZoneForm")
                            }
                        }
                    }
                }
            }

        private fun mapTimeZoneIdToResourceId(timeZoneId: String): Int = when (timeZoneId) {
            SymbolicTimeZone.getDefaultTimeZoneId() -> R.string.time_zone_short_name_DEFAULT
            "GMT" -> R.string.time_zone_short_name_GMT
            "Asia/Tokyo" -> R.string.time_zone_short_name_JST
            "America/New_York" -> R.string.time_zone_short_name_EST
            "America/Los_Angeles" -> R.string.time_zone_short_name_PST
            "Europe/Berlin" -> R.string.time_zone_short_name_CET
            "Europe/Lisbon" -> R.string.time_zone_short_name_WET
            else -> R.string.time_zone_short_name_NONE
        }
    }
}
