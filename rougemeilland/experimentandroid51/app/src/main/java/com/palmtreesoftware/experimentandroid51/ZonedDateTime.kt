package com.palmtreesoftware.experimentandroid51

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi

abstract class ZonedDateTime protected constructor(val timeZone: TimeZone) {
    abstract val epochSeconds: Long
    abstract val epochMilliSeconds: Long
    abstract val year: Int
    abstract val month: Month
    abstract val dayOfMonth: Int
    abstract val dayOfWeek: DayOfWeek
    abstract val hour: Int
    abstract val minute: Int
    abstract val second: Int
    abstract val millSecond: Int
    abstract fun toDateTime(): DateTime
    abstract fun format(dateFormatString: String): String
    abstract override fun toString(): String

    companion object {
        fun of(dateTime: DateTime, timeZone: TimeZone): ZonedDateTime =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ZonedDateTimeSDK26.of(dateTime, timeZone)
            } else {
                ZonedDateTimeSDK22.of(dateTime, timeZone)
            }

        fun of(
            year: Int,
            month: Month,
            dayofMonth: Int,
            hour: Int,
            minute: Int,
            second: Int,
            milliSecond: Int,
            timeZone: TimeZone
        ): ZonedDateTime =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ZonedDateTimeSDK26.of(
                    year,
                    month,
                    dayofMonth,
                    hour,
                    minute,
                    second,
                    milliSecond,
                    timeZone
                )
            } else {
                ZonedDateTimeSDK22.of(
                    year,
                    month,
                    dayofMonth,
                    hour,
                    minute,
                    second,
                    milliSecond,
                    timeZone
                )
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private class ZonedDateTimeSDK26 private constructor(
        private val dateTimeLocal: java.time.ZonedDateTime,
        timeZone: TimeZone
    ) : ZonedDateTime(timeZone) {
        override val epochSeconds: Long
            get() = dateTimeLocal.toInstant().epochSecond

        override val epochMilliSeconds: Long
            get() = dateTimeLocal.toInstant().toEpochMilli()

        override val year: Int
            get() = dateTimeLocal.year

        override val month: Month
            get() =
                when (dateTimeLocal.month) {
                    java.time.Month.JANUARY -> Month.JANUARY
                    java.time.Month.FEBRUARY -> Month.FEBRUARY
                    java.time.Month.MARCH -> Month.MARCH
                    java.time.Month.APRIL -> Month.APRIL
                    java.time.Month.MAY -> Month.MAY
                    java.time.Month.JUNE -> Month.JUNE
                    java.time.Month.JULY -> Month.JULY
                    java.time.Month.AUGUST -> Month.AUGUST
                    java.time.Month.SEPTEMBER -> Month.SEPTEMBER
                    java.time.Month.OCTOBER -> Month.OCTOBER
                    java.time.Month.NOVEMBER -> Month.NOVEMBER
                    java.time.Month.DECEMBER -> Month.DECEMBER
                    else -> throw Exception("${javaClass.canonicalName}.month: Bad month value: month=${dateTimeLocal.month}")
                }

        override val dayOfMonth: Int
            get() = dateTimeLocal.dayOfMonth

        override val dayOfWeek: DayOfWeek
            get() =
                when (dateTimeLocal.dayOfWeek) {
                    java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
                    java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
                    java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
                    java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
                    java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
                    java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
                    java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
                    else -> throw Exception("${javaClass.canonicalName}.month: Bad dayOfWeek value: dayOfWeek=${dateTimeLocal.dayOfWeek}")
                }

        override val hour: Int
            get() = dateTimeLocal.hour

        override val minute: Int
            get() = dateTimeLocal.minute

        override val second: Int
            get() = dateTimeLocal.second

        override val millSecond: Int
            get() = dateTimeLocal.get(java.time.temporal.ChronoField.MILLI_OF_SECOND)

        override fun toDateTime(): DateTime = DateTime.of(dateTimeLocal.toLocalDateTime())

        override fun format(dateFormatString: String): String =
            dateTimeLocal.format(java.time.format.DateTimeFormatter.ofPattern(dateFormatString))

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as ZonedDateTimeSDK26
            if (dateTimeLocal != other.dateTimeLocal)
                return false
            return true
        }

        override fun hashCode(): Int {
            return dateTimeLocal.hashCode()
        }

        override fun toString(): String {
            return "DateTime(dateTime='${dateTimeLocal
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm.ss.SSS OOOO"))}', timeZone='${dateTimeLocal.zone.id}')"
        }

        companion object {
            private val gmt by lazy { java.time.ZoneId.of("GMT") }

            fun of(dateTime: DateTime, timeZone: TimeZone): ZonedDateTime =
                ZonedDateTimeSDK26(
                    (dateTime.rawObject as java.time.LocalDateTime).atZone(gmt)
                        .withZoneSameInstant(timeZone.rawObject as java.time.ZoneId),
                    timeZone
                )

            fun of(
                year: Int,
                month: Month,
                dayofMonth: Int,
                hour: Int,
                minute: Int,
                second: Int,
                milliSecond: Int,
                timeZone: TimeZone
            ): ZonedDateTime =
                ZonedDateTimeSDK26(
                    java.time.ZonedDateTime.of(
                        year,
                        month.value,
                        dayofMonth,
                        hour,
                        minute,
                        second,
                        milliSecond,
                        timeZone.rawObject as java.time.ZoneId
                    ),
                    timeZone
                )
        }
    }

    private class ZonedDateTimeSDK22 private constructor(
        private val dateTimeLocal: java.util.Calendar,
        timeZone: TimeZone
    ) : ZonedDateTime(timeZone) {
        override val epochSeconds: Long
            get() = dateTimeLocal.timeInMillis / 1000

        override val epochMilliSeconds: Long
            get() = dateTimeLocal.timeInMillis

        override val year: Int
            get() = dateTimeLocal.get(java.util.Calendar.YEAR)

        override val month: Month
            get() =
                when (dateTimeLocal.get(java.util.Calendar.MONTH)) {
                    0 -> Month.JANUARY
                    1 -> Month.FEBRUARY
                    2 -> Month.MARCH
                    3 -> Month.APRIL
                    4 -> Month.MAY
                    5 -> Month.JUNE
                    6 -> Month.JULY
                    7 -> Month.AUGUST
                    8 -> Month.SEPTEMBER
                    9 -> Month.OCTOBER
                    10 -> Month.NOVEMBER
                    11 -> Month.DECEMBER
                    else -> throw Exception(
                        "${javaClass.canonicalName}.month: Bad month value: month=${dateTimeLocal.get(
                            java.util.Calendar.MONTH
                        )}"
                    )
                }
        override val dayOfMonth: Int
            get() = dateTimeLocal.get(java.util.Calendar.DAY_OF_MONTH)

        override val dayOfWeek: DayOfWeek
            get() =
                when (dateTimeLocal.get(java.util.Calendar.DAY_OF_WEEK)) {
                    1 -> DayOfWeek.SUNDAY
                    2 -> DayOfWeek.MONDAY
                    3 -> DayOfWeek.TUESDAY
                    4 -> DayOfWeek.WEDNESDAY
                    5 -> DayOfWeek.THURSDAY
                    6 -> DayOfWeek.FRIDAY
                    7 -> DayOfWeek.SATURDAY
                    else -> throw Exception(
                        "${javaClass.canonicalName}.month: Bad dayOfWeek value: dayOfWeek=${dateTimeLocal.get(
                            java.util.Calendar.DAY_OF_WEEK
                        )}"
                    )
                }

        override val hour: Int
            get() = dateTimeLocal.get(java.util.Calendar.HOUR)

        override val minute: Int
            get() = dateTimeLocal.get(java.util.Calendar.MINUTE)

        override val second: Int
            get() = dateTimeLocal.get(java.util.Calendar.SECOND)

        override val millSecond: Int
            get() = dateTimeLocal.get(java.util.Calendar.MILLISECOND)

        override fun toDateTime(): DateTime =
            DateTime.of(java.util.Calendar.getInstance().also {
                it.timeZone = gmt
                it.timeInMillis = dateTimeLocal.timeInMillis
            })

        @SuppressLint("SimpleDateFormat")
        override fun format(dateFormatString: String): String {
            return java.text.SimpleDateFormat(dateFormatString)
                .also { it.timeZone = dateTimeLocal.timeZone }
                .format(dateTimeLocal.time)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as ZonedDateTimeSDK22
            if (dateTimeLocal.timeZone.id != other.dateTimeLocal.timeZone.id)
                return false
            if (dateTimeLocal.timeInMillis != other.dateTimeLocal.timeInMillis)
                return false
            return true
        }

        override fun hashCode(): Int {
            return dateTimeLocal.timeZone.id.hashCode() * 32 + dateTimeLocal.timeInMillis.hashCode()
        }

        @SuppressLint("SimpleDateFormat")
        override fun toString(): String =
            "DateTime(dateTime='${java.text.SimpleDateFormat("yyyy/MM/dd HH:mm.ss.SSS z")
                .also { it.timeZone = dateTimeLocal.timeZone }
                .format(dateTimeLocal.time)}', timeZone='${dateTimeLocal.timeZone.id}')"

        companion object {
            private val gmt by lazy { java.util.TimeZone.getTimeZone("GMT") }

            fun of(dateTime: DateTime, timeZone: TimeZone): ZonedDateTime =
                ZonedDateTimeSDK22(
                    java.util.Calendar.getInstance(timeZone.rawObject as java.util.TimeZone).also {
                        it.timeInMillis = (dateTime.rawObject as java.util.Calendar).timeInMillis
                    },
                    timeZone
                )

            fun of(
                year: Int,
                month: Month,
                dayofMonth: Int,
                hour: Int,
                minute: Int,
                second: Int,
                milliSecond: Int,
                timeZone: TimeZone
            ): ZonedDateTime =
                ZonedDateTimeSDK22(
                    java.util.Calendar.getInstance().also {
                        it.clear()
                        it.set(java.util.Calendar.YEAR, year)
                        it.set(
                            java.util.Calendar.MONTH, when (month) {
                                Month.JANUARY -> 0
                                Month.FEBRUARY -> 1
                                Month.MARCH -> 2
                                Month.APRIL -> 3
                                Month.MAY -> 4
                                Month.JUNE -> 5
                                Month.JULY -> 6
                                Month.AUGUST -> 7
                                Month.SEPTEMBER -> 8
                                Month.OCTOBER -> 9
                                Month.NOVEMBER -> 10
                                Month.DECEMBER -> 11
                            }
                        )
                        it.set(java.util.Calendar.DAY_OF_MONTH, dayofMonth)
                        it.set(java.util.Calendar.HOUR, hour)
                        it.set(java.util.Calendar.MINUTE, minute)
                        it.set(java.util.Calendar.SECOND, second)
                        it.set(java.util.Calendar.MILLISECOND, milliSecond)
                        it.timeZone = timeZone.rawObject as java.util.TimeZone
                    },
                    timeZone
                )
        }
    }
}