package com.palmtreesoftware.experimentandroid51

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

abstract class TimeZone protected constructor(val id: String) {

    init {
        if (id == timeZoneIdOfTimeDifferenceExpression)
            throw Exception("AppWidgetTimeZone.init: Detected bad id: id=$id")
    }

    abstract fun getTimeZoneShortName(context: Context): String
    abstract fun setForm(form: TimeZoneForm)
    internal abstract val rawObject: Any

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimeZone
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "TimeZone(id=$id)"

    companion object {
        val GMT: TimeZone by lazy { of("GMT") }

        private val timeDifferenceExpressionPattern = Regex("^GMT([+-][0-9][0-9]):([0-9][0-9])$")

        // gmtFormat の桁指定子 %+03d の '3' は、符号を含めた桁数であることに注意すること
        private const val gmtFormat: String = "GMT%+03d:%02d"
        private const val shortGmtFormat = "%+03d%02d"

        // android の NumberPicker は負数は扱えない模様
        // ※ minValue に負の数を入れてみたら、例外 (java.lang.IllegalArgumentException: minValue must be >= 0) が発生した
        // この問題を回避するため、以下の対策を行った
        // 1) NumberPicker に与える value, minValue, maxValue に innerOffsetOfHourAndMinute だけのゲタをはかせる
        // 2) 時の数値をタイムゾーンIDに変換するときはゲタを元に戻す。逆にタイムゾーンIDから時を取得するときは再びゲタをはかせる
        private const val innerOffsetOfHour: Int = 100

        const val maxTimeZoneHour: Int = 14 + innerOffsetOfHour
        const val minTimeZoneHour: Int = -12 + innerOffsetOfHour
        const val maxTimeZoneMinute: Int = 59
        const val minTimeZoneMinute: Int = 0
        const val timeZoneIdOfSystemDefault: String = "-"
        const val timeZoneIdOfTimeDifferenceExpression: String = "#"

        fun getDefault(): TimeZone = DefaultTimeZone.createInstance()

        fun of(timeZoneId: String): TimeZone =
            if (timeZoneId == timeZoneIdOfSystemDefault) {
                getDefault()
            } else {
                timeDifferenceExpressionPattern.matchEntire(timeZoneId).let { matchResult ->
                    if (matchResult == null) {
                        // 時差表現ではない場合 (システムデフォルト、または名前の表現)
                        SymbolicTimeZone.createInstance(timeZoneId)
                    } else {
                        try {
                            NumericTimeZone.createInstance(
                                timeZoneId,
                                matchResult.destructured.component1().toInt() + innerOffsetOfHour,
                                matchResult.destructured.component2().toInt()
                            )
                        } catch (ex: Exception) {
                            getDefault()
                        }
                    }
                }
            }

        fun of(form: TimeZoneForm): TimeZone {
            if (form.id == timeZoneIdOfSystemDefault) {
                return getDefault()
            } else if (form.id == timeZoneIdOfTimeDifferenceExpression) {
                return NumericTimeZone.createInstance(form.hour, form.minute)
            } else
                return SymbolicTimeZone.createInstance(form.id)
        }

        fun of(duration: TimeDuration): TimeZone {
            val (hour, minute) =
                duration.tickCounts.let { tickCount ->
                    if (tickCount >= 0) {
                        (tickCount / 1000 / 60).let { totalMinites ->
                            Pair(totalMinites / 60 + innerOffsetOfHour, totalMinites % 60)
                        }
                    } else {
                        ((-tickCount) / 1000 / 60).let { totalMinites ->
                            Pair(-(totalMinites / 60) + innerOffsetOfHour, totalMinites % 60)
                        }
                    }
                }
            return NumericTimeZone.createInstance(hour.toInt(), minute.toInt())
        }

        fun getHourStringOnForm(format: String, hourOnForm: Int): String =
            String.format(format, hourOnForm - innerOffsetOfHour)

        fun getMinuteStringOnForm(format: String, minuteOnForm: Int): String =
            String.format(format, minuteOnForm)

        private fun mapTimeZoneIdToResourceId(timeZoneId: String): Int = when (timeZoneId) {
            timeZoneIdOfSystemDefault -> R.string.time_zone_short_name_DEFAULT
            "GMT" -> R.string.time_zone_short_name_GMT
            "Asia/Tokyo" -> R.string.time_zone_short_name_JST
            "America/New_York" -> R.string.time_zone_short_name_EST
            "America/Los_Angeles" -> R.string.time_zone_short_name_PST
            "Europe/Berlin" -> R.string.time_zone_short_name_CET
            "Europe/Lisbon" -> R.string.time_zone_short_name_WET
            else -> {
                throw Exception("AppWidgetTimeZone.mapTimeZoneIdToResourceId: Cannot resolve time zone id: timeZoneId='$timeZoneId'")
            }
        }
    }

    private abstract class DefaultTimeZone protected constructor() :
        TimeZone(
            timeZoneIdOfSystemDefault
        ) {
        override fun getTimeZoneShortName(context: Context): String = ""

        override fun setForm(form: TimeZoneForm) {
            form.id = timeZoneIdOfSystemDefault
        }

        abstract override val rawObject: Any

        companion object {
            fun createInstance(): TimeZone {


                return

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    TimeZoneSDK26.createInstance()
                } else {
                    TimeZoneSDK22.createInstance()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private open class TimeZoneSDK26 private constructor(private val o: java.time.ZoneId) :
            DefaultTimeZone() {

            override val rawObject: Any
                get() = o

            companion object {
                fun createInstance(): TimeZone = TimeZoneSDK26(java.time.ZoneId.systemDefault())
            }

        }

        private class TimeZoneSDK22 private constructor(private val o: java.util.TimeZone) :
            DefaultTimeZone() {

            override val rawObject: Any
                get() = o

            companion object {
                fun createInstance(): TimeZone = TimeZoneSDK22(java.util.TimeZone.getDefault())
            }
        }
    }

    protected abstract class SymbolicTimeZone private constructor(
        timeZoneId: String,
        private val shortNameResourceId: Int
    ) : TimeZone(timeZoneId) {
        override fun getTimeZoneShortName(context: Context): String =
            context.getString(shortNameResourceId)

        override fun setForm(form: TimeZoneForm) {
            form.id = id
        }

        abstract override val rawObject: Any

        companion object {
            fun createInstance(timeZoneId: String): TimeZone =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    TimeZoneSDK26.createInstance(timeZoneId)
                } else {
                    TimeZoneSDK22.createInstance(timeZoneId)
                }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private class TimeZoneSDK26 private constructor(
            private val o: java.time.ZoneId,
            timeZoneId: String,
            shortNameResourceId: Int
        ) : SymbolicTimeZone(timeZoneId, shortNameResourceId) {
            override val rawObject: Any
                get() = o

            companion object {
                fun createInstance(timeZoneId: String): TimeZone = try {
                    TimeZoneSDK26(
                        java.time.ZoneId.of(timeZoneId),
                        timeZoneId,
                        mapTimeZoneIdToResourceId(timeZoneId)
                    )
                } catch (ex: Exception) {
                    getDefault()
                }
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
                fun createInstance(timeZoneId: String): TimeZone = try {
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
    }

    protected abstract class NumericTimeZone private constructor(
        timeZoneId: String,
        private val hour: Int,
        private val minute: Int
    ) :
        TimeZone(timeZoneId) {
        override fun getTimeZoneShortName(context: Context): String {
            return shortGmtFormat.format(hour - innerOffsetOfHour, minute)
        }

        override fun setForm(form: TimeZoneForm) {
            form.id = timeZoneIdOfTimeDifferenceExpression
            form.hour = hour
            form.minute = minute
        }

        companion object {
            fun createInstance(timeZoneId: String, hour: Int, minute: Int): TimeZone = when {
                hour < minTimeZoneHour || hour > maxTimeZoneHour -> {
                    getDefault()
                }
                minute < minTimeZoneMinute || minute > maxTimeZoneMinute -> {
                    getDefault()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    TimeZoneSDK26.createInstance(timeZoneId, hour, minute)
                }
                else -> {
                    TimeZoneSDK22.createInstance(timeZoneId, hour, minute)
                }
            }

            fun createInstance(hour: Int, minute: Int): TimeZone =
                createInstance(gmtFormat.format(hour - innerOffsetOfHour, minute), hour, minute)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private class TimeZoneSDK26 private constructor(
            private val o: java.time.ZoneId,
            timeZoneId: String,
            hour: Int,
            minute: Int
        ) : NumericTimeZone(timeZoneId, hour, minute) {
            override val rawObject: Any
                get() = o

            companion object {
                fun createInstance(timeZoneId: String, hour: Int, minute: Int): TimeZone {
                    return TimeZoneSDK26(
                        java.time.ZoneId.of(timeZoneId),
                        timeZoneId,
                        hour,
                        minute
                    )
                }
            }
        }

        private class TimeZoneSDK22 private constructor(
            private val o: java.util.TimeZone,
            timeZoneId: String,
            hour: Int,
            minute: Int
        ) : NumericTimeZone(timeZoneId, hour, minute) {
            override val rawObject: Any
                get() = o

            companion object {
                fun createInstance(timeZoneId: String, hour: Int, minute: Int): TimeZone {
                    return TimeZoneSDK22(
                        java.util.TimeZone.getTimeZone(timeZoneId),
                        timeZoneId,
                        hour,
                        minute
                    )
                }
            }
        }
    }
}
