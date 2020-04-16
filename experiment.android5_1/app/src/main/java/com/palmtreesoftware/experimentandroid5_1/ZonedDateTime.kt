package com.palmtreesoftware.experimentandroid5_1

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
    abstract val dayOfYear: Int
    abstract val lengthOfMonth: Int
    abstract val lengthOfYear: Int
    abstract val hour: Int
    abstract val minute: Int
    abstract val second: Int
    abstract val milliSecond: Int
    abstract fun toDateTime(): DateTime
    abstract fun format(dateFormatString: String, locale: java.util.Locale): String
    abstract override fun toString(): String

    fun format(dateFormatString: String): String =
        format(dateFormatString, java.util.Locale.getDefault())

    companion object {
        internal val dateTimeFormatOfISO8601 =
            "yyyy-MM-dd'T'HH:mm:ss.SSS$timeZoneFormatSpecOfISO8601"

        val timeZoneFormatSpecOfISO8601: String
            get() =
                Platform.sdK26Depended({
                    ZonedDateTimeSDK26.TimeZoneFormatSpecOfISO8601
                }, {
                    ZonedDateTimeSDK22.TimeZoneFormatSpecOfISO8601
                })

        fun of(dateTime: DateTime, timeZone: TimeZone): ZonedDateTime =
            Platform.sdK26Depended({
                ZonedDateTimeSDK26.of(dateTime, timeZone)
            }, {
                ZonedDateTimeSDK22.of(dateTime, timeZone)
            })

        fun of(
            year: Int,
            month: Month,
            dayOfMonth: Int,
            hour: Int,
            minute: Int,
            second: Int,
            milliSecond: Int,
            timeZone: TimeZone
        ): ZonedDateTime =
            Platform.sdK26Depended({
                ZonedDateTimeSDK26.of(
                    year,
                    month,
                    dayOfMonth,
                    hour,
                    minute,
                    second,
                    milliSecond,
                    timeZone
                )
            }, {
                ZonedDateTimeSDK22.of(
                    year,
                    month,
                    dayOfMonth,
                    hour,
                    minute,
                    second,
                    milliSecond,
                    timeZone
                )
            })

        fun of(
            year: Int,
            month: Month,
            dayOfMonth: Int,
            hour: Int,
            minute: Int,
            second: Int,
            timeZone: TimeZone
        ): ZonedDateTime =
            of(year, month, dayOfMonth, hour, minute, second, 0, timeZone)

        fun of(
            year: Int,
            month: Month,
            dayOfMonth: Int,
            timeZone: TimeZone
        ): ZonedDateTime =
            of(year, month, dayOfMonth, 0, 0, 0, 0, timeZone)

        fun getLengthOfMonth(year: Int, month: Month): Int =
            Platform.sdK26Depended({
                ZonedDateTimeSDK26.getLengthOfMonth(year, month)
            }, {
                ZonedDateTimeSDK22.getLengthOfMonth(year, month)
            })

        fun getLengthOfYear(year: Int): Int =
            Platform.sdK26Depended({
                ZonedDateTimeSDK26.getLengthOfYear(year)
            }, {
                ZonedDateTimeSDK22.getLengthOfYear(year)
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private class ZonedDateTimeSDK26 private constructor(
        private val dateTimeLocal: java.time.ZonedDateTime,
        timeZone: TimeZone
    ) : ZonedDateTime(timeZone) {
        private val dateLocal: java.time.LocalDate = dateTimeLocal.toLocalDate()

        override val epochSeconds: Long
            get() = dateTimeLocal.toInstant().epochSecond

        override val epochMilliSeconds: Long
            get() = dateTimeLocal.toInstant().toEpochMilli()

        override val year: Int
            get() = dateTimeLocal.year

        override val month: Month
            get() = mapMonthFromNative(dateTimeLocal.month)

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
                    else -> throw Exception("ZonedDateTime.month: Bad dayOfWeek value: dayOfWeek=${dateTimeLocal.dayOfWeek}")
                }

        override val dayOfYear: Int
            get() = dateTimeLocal.dayOfYear

        override val lengthOfMonth: Int
            get() = dateLocal.lengthOfMonth()

        override val lengthOfYear: Int
            get() = dateLocal.lengthOfYear()

        override val hour: Int
            get() = dateTimeLocal.hour

        override val minute: Int
            get() = dateTimeLocal.minute

        override val second: Int
            get() = dateTimeLocal.second

        override val milliSecond: Int
            get() = dateTimeLocal.get(java.time.temporal.ChronoField.MILLI_OF_SECOND)

        override fun toDateTime(): DateTime =
            DateTime.of(
                dateTimeLocal
                    .withZoneSameInstant(gmt)
                    .toLocalDateTime()
            )

        override fun format(dateFormatString: String, locale: java.util.Locale): String =
            dateTimeLocal.format(
                java.time.format.DateTimeFormatter.ofPattern(
                    dateFormatString,
                    locale
                )
            )

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

        override fun toString(): String =
            "ZonedDateTime(dateTime='${dateTimeLocal
                .format(
                    java.time.format.DateTimeFormatter.ofPattern(
                        dateTimeFormatOfISO8601,
                        java.util.Locale.ENGLISH
                    )
                )}', timeZone='${dateTimeLocal.zone.id}')"

        companion object {
            private val gmt by lazy { java.time.ZoneId.of("GMT") }

            val TimeZoneFormatSpecOfISO8601 = "XXXXX"

            fun of(dateTime: DateTime, timeZone: TimeZone): ZonedDateTime =
                ZonedDateTimeSDK26(
                    (dateTime.rawObject as java.time.LocalDateTime)
                        .atZone(gmt)
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
                    try {
                        java.time.ZonedDateTime.of(
                            year,
                            month.value,
                            dayofMonth,
                            hour,
                            minute,
                            second,
                            milliSecond * (1000 * 1000),
                            timeZone.rawObject as java.time.ZoneId
                        )
                    } catch (ex: java.time.DateTimeException) {
                        throw IllegalArgumentException(
                            "ZonedDatetime.of(): Any parameter is out of range",
                            ex
                        )
                    },
                    timeZone
                )

            fun getLengthOfMonth(year: Int, month: Month): Int =
                java.time.YearMonth.of(year, mapMonthToNative(month)).lengthOfMonth()

            fun getLengthOfYear(year: Int): Int =
                java.time.Year.of(year).length()

            private fun mapMonthFromNative(month: java.time.Month): Month =
                when (month) {
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
                    else -> throw Exception("ZonedDateTime.mapMonthFromNative: Bad month value: month=${month}")
                }

            private fun mapMonthToNative(month: Month): java.time.Month =
                when (month) {
                    Month.JANUARY -> java.time.Month.JANUARY
                    Month.FEBRUARY -> java.time.Month.FEBRUARY
                    Month.MARCH -> java.time.Month.MARCH
                    Month.APRIL -> java.time.Month.APRIL
                    Month.MAY -> java.time.Month.MAY
                    Month.JUNE -> java.time.Month.JUNE
                    Month.JULY -> java.time.Month.JULY
                    Month.AUGUST -> java.time.Month.AUGUST
                    Month.SEPTEMBER -> java.time.Month.SEPTEMBER
                    Month.OCTOBER -> java.time.Month.OCTOBER
                    Month.NOVEMBER -> java.time.Month.NOVEMBER
                    Month.DECEMBER -> java.time.Month.DECEMBER
                }
        }
    }

    private class ZonedDateTimeSDK22 private constructor(
        private val dateTimeLocal: java.util.Calendar,
        timeZone: TimeZone
    ) : ZonedDateTime(timeZone) {
        override val epochSeconds: Long
            get() =
                dateTimeLocal.timeInMillis.divideFloor(1000)

        override val epochMilliSeconds: Long
            get() = dateTimeLocal.timeInMillis

        override val year: Int
            get() = dateTimeLocal.get(java.util.Calendar.YEAR)

        override val month: Month
            get() = mapMonthFromNative(dateTimeLocal.get(java.util.Calendar.MONTH))

        override val dayOfMonth: Int
            get() = dateTimeLocal.get(java.util.Calendar.DAY_OF_MONTH)

        override val dayOfWeek: DayOfWeek
            get() =
                when (dateTimeLocal.get(java.util.Calendar.DAY_OF_WEEK)) {
                    java.util.Calendar.SUNDAY -> DayOfWeek.SUNDAY
                    java.util.Calendar.MONDAY -> DayOfWeek.MONDAY
                    java.util.Calendar.TUESDAY -> DayOfWeek.TUESDAY
                    java.util.Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                    java.util.Calendar.THURSDAY -> DayOfWeek.THURSDAY
                    java.util.Calendar.FRIDAY -> DayOfWeek.FRIDAY
                    java.util.Calendar.SATURDAY -> DayOfWeek.SATURDAY
                    else -> throw Exception(
                        "ZonedDateTime.month: Bad dayOfWeek value: dayOfWeek=${dateTimeLocal.get(
                            java.util.Calendar.DAY_OF_WEEK
                        )}"
                    )
                }

        override val dayOfYear: Int
            get() = dateTimeLocal.get(java.util.Calendar.DAY_OF_YEAR)

        override val lengthOfMonth: Int
            get() = dateTimeLocal.getActualMaximum(java.util.Calendar.DATE)

        override val lengthOfYear: Int
            get() = dateTimeLocal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR)

        override val hour: Int
            // Calendar.HOUR は 12 時間制の値なので間違えないこと
            get() = dateTimeLocal.get(java.util.Calendar.HOUR_OF_DAY)

        override val minute: Int
            get() = dateTimeLocal.get(java.util.Calendar.MINUTE)

        override val second: Int
            get() = dateTimeLocal.get(java.util.Calendar.SECOND)

        override val milliSecond: Int
            get() = dateTimeLocal.get(java.util.Calendar.MILLISECOND)

        override fun toDateTime(): DateTime =
            DateTime.of(java.util.Calendar.getInstance().also {
                it.timeZone = gmt
                it.timeInMillis = dateTimeLocal.timeInMillis
            })

        @SuppressLint("SimpleDateFormat")
        override fun format(dateFormatString: String, locale: java.util.Locale): String =
            java.text.SimpleDateFormat(dateFormatString, locale)
                .apply { timeZone = dateTimeLocal.timeZone }
                .format(dateTimeLocal.time)

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
            "ZonedDateTime(dateTime='${java.text.SimpleDateFormat(
                dateTimeFormatOfISO8601,
                java.util.Locale.ENGLISH
            )
                .apply { timeZone = dateTimeLocal.timeZone }
                .format(dateTimeLocal.time)}', timeZone='${dateTimeLocal.timeZone.id}')"

        companion object {
            private val gmt by lazy { java.util.TimeZone.getTimeZone("GMT") }

            val TimeZoneFormatSpecOfISO8601 = "XXX"

            fun of(dateTime: DateTime, timeZone: TimeZone): ZonedDateTime =
                ZonedDateTimeSDK22(
                    java.util.Calendar.getInstance(timeZone.rawObject as java.util.TimeZone).apply {
                        timeInMillis = (dateTime.rawObject as java.util.Calendar).timeInMillis
                    },
                    timeZone
                )

            fun of(
                year: Int,
                month: Month,
                dayOfMonth: Int,
                hour: Int,
                minute: Int,
                second: Int,
                milliSecond: Int,
                timeZone: TimeZone
            ): ZonedDateTime =
                ZonedDateTimeSDK22(
                    java.util.Calendar.getInstance().apply {
                        clear()
                        set(java.util.Calendar.YEAR, year)
                        set(java.util.Calendar.MONTH, mapMonthToNative(month))
                        set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                        // Calendar.HOUR は 12 時間制の値なので間違えないこと
                        set(java.util.Calendar.HOUR_OF_DAY, hour)
                        set(java.util.Calendar.MINUTE, minute)
                        set(java.util.Calendar.SECOND, second)
                        set(java.util.Calendar.MILLISECOND, milliSecond)
                        this.timeZone = timeZone.rawObject as java.util.TimeZone
                    },
                    timeZone
                ).also {
                    // Calendar の構築時にパラメタの検査をしていないらしく、範囲外のパラメタを与えると
                    // 意図しない日時の Calendar ができることがあるので、
                    // 生成された Calendar オブジェクトの各コンポーネントを検査して与えた値と異なっていたら
                    // パラメタエラーとしている
                    // 例: 2020年1月0日を指定した場合 -> Calendar の year は 2019 、
                    // month は DECEMBER 、dayOfMonth は 31 になる
                    if (it.year != year ||
                        it.month != month ||
                        it.dayOfMonth != dayOfMonth ||
                        it.hour != hour ||
                        it.minute != minute ||
                        it.second != second ||
                        it.milliSecond != milliSecond
                    ) {
                        throw IllegalArgumentException(
                            "ZonedDatetime.of(): Any parameter is out of range"
                        )
                    }
                }

            fun getLengthOfMonth(year: Int, month: Month): Int =
                java.util.Calendar.getInstance().apply {
                    clear()
                    set(java.util.Calendar.YEAR, year)
                    set(java.util.Calendar.MONTH, mapMonthToNative(month))
                }.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

            fun getLengthOfYear(year: Int): Int =
                java.util.Calendar.getInstance().apply {
                    clear()
                    set(java.util.Calendar.YEAR, year)
                }.getActualMaximum(java.util.Calendar.DAY_OF_YEAR)

            private fun mapMonthFromNative(month: Int): Month =
                when (month) {
                    java.util.Calendar.JANUARY -> Month.JANUARY
                    java.util.Calendar.FEBRUARY -> Month.FEBRUARY
                    java.util.Calendar.MARCH -> Month.MARCH
                    java.util.Calendar.APRIL -> Month.APRIL
                    java.util.Calendar.MAY -> Month.MAY
                    java.util.Calendar.JUNE -> Month.JUNE
                    java.util.Calendar.JULY -> Month.JULY
                    java.util.Calendar.AUGUST -> Month.AUGUST
                    java.util.Calendar.SEPTEMBER -> Month.SEPTEMBER
                    java.util.Calendar.OCTOBER -> Month.OCTOBER
                    java.util.Calendar.NOVEMBER -> Month.NOVEMBER
                    java.util.Calendar.DECEMBER -> Month.DECEMBER
                    else -> throw Exception("ZonedDateTime.mapMonthFromNative: Bad month value: month=${month}")
                }

            private fun mapMonthToNative(month: Month): Int =
                when (month) {
                    Month.JANUARY -> java.util.Calendar.JANUARY
                    Month.FEBRUARY -> java.util.Calendar.FEBRUARY
                    Month.MARCH -> java.util.Calendar.MARCH
                    Month.APRIL -> java.util.Calendar.APRIL
                    Month.MAY -> java.util.Calendar.MAY
                    Month.JUNE -> java.util.Calendar.JUNE
                    Month.JULY -> java.util.Calendar.JULY
                    Month.AUGUST -> java.util.Calendar.AUGUST
                    Month.SEPTEMBER -> java.util.Calendar.SEPTEMBER
                    Month.OCTOBER -> java.util.Calendar.OCTOBER
                    Month.NOVEMBER -> java.util.Calendar.NOVEMBER
                    Month.DECEMBER -> java.util.Calendar.DECEMBER
                }
        }
    }
}