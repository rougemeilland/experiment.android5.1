package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

abstract class DateTime protected constructor() {
    @RequiresApi(Build.VERSION_CODES.O)
    private class DateTimeSDK26 private constructor(
        private val dateTimeUTC: java.time.LocalDateTime
    ) : DateTime() {
        override val rawObject: Any
            get() = dateTimeUTC

        override val epochSeconds: Long
            get() = dateTimeUTC.toInstant(java.time.ZoneOffset.UTC).epochSecond

        override val epochMilliSeconds: Long
            get() = dateTimeUTC.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()

        override operator fun plus(duration: TimeDuration): DateTime =
            DateTimeSDK26(dateTimeUTC.plus(java.time.Duration.ofMillis(duration.tickCounts)))

        override operator fun minus(dateTime: DateTime): TimeDuration =
            TimeDuration.ofTickCounts(
                java.time.Duration.between(
                    (dateTime as DateTimeSDK26).dateTimeUTC,
                    this.dateTimeUTC
                ).toMillis()
            )

        override operator fun minus(duration: TimeDuration): DateTime =
            DateTimeSDK26(dateTimeUTC.minus(java.time.Duration.ofMillis(duration.tickCounts)))

        override fun compareTo(other: DateTime): Int =
            this.dateTimeUTC.compareTo((other as DateTimeSDK26).dateTimeUTC)

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as DateTimeSDK26
            if (dateTimeUTC != other.dateTimeUTC)
                return false
            return true
        }

        override fun hashCode(): Int = dateTimeUTC.hashCode()

        override fun toString(): String =
            "DateTime(dateTime='${dateTimeUTC
                .atZone(gmt).let {
                    it.format(
                        java.time.format.DateTimeFormatter.ofPattern(
                            if (it.get(java.time.temporal.ChronoField.MILLI_OF_SECOND) == 0)
                                ZonedDateTime.dateTimeFormatOfISO8601
                            else
                                ZonedDateTime.dateTimeFormatOfISO8601ContainingMilliSecond,
                            java.util.Locale.ENGLISH
                        )
                    )
                }}', epochMilliSeconds='${dateTimeUTC.toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli()}')"

        override fun plusMilliSeconds(milliSeconds: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.plusNanos(milliSeconds * (1000 * 1000)))

        override fun plusSeconds(seconds: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.plusSeconds(seconds))

        override fun plusMinutes(minutes: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.plusMinutes(minutes))

        override fun plusHours(hours: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.plusHours(hours))

        override fun plusDays(days: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.plusDays(days))

        override fun plusWeeks(weeks: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.plusWeeks(weeks))

        override fun minusMilliSeconds(milliSeconds: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.minusNanos(milliSeconds * (1000 * 1000)))

        override fun minusSeconds(seconds: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.minusSeconds(seconds))

        override fun minusMinutes(minutes: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.minusMinutes(minutes))

        override fun minusHours(hours: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.minusHours(hours))

        override fun minusDays(days: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.minusDays(days))

        override fun minusWeeks(weeks: Long): DateTime =
            DateTimeSDK26(dateTimeUTC.minusWeeks(weeks))

        companion object {
            private val gmt by lazy { java.time.ZoneId.of("GMT") }

            fun now(): DateTime =
                DateTimeSDK26(java.time.LocalDateTime.now(gmt))

            fun ofEpochMilliSeconds(milliSseconds: Long): DateTime =
                DateTimeSDK26(
                    java.time.LocalDateTime.ofEpochSecond(
                        milliSseconds.divideRound(1000),
                        milliSseconds.modulo(1000).toInt() * (1000 * 1000),
                        java.time.ZoneOffset.UTC
                    )
                )

            fun of(dateTime: java.time.LocalDateTime): DateTime =
                DateTimeSDK26(dateTime)
        }
    }

    private class DateTimeSDK22 private constructor(private val dateTimeUTC: java.util.Calendar) :
        DateTime() {
        init {
            if (dateTimeUTC.timeZone.id != "GMT")
                throw Exception(javaClass.canonicalName + ".init: dateTimeUTC must be 'GMT'")
        }

        override val rawObject: Any
            get() = dateTimeUTC

        override val epochSeconds: Long
            get() =
                dateTimeUTC.timeInMillis.divideRound(1000)

        override val epochMilliSeconds: Long
            get() = dateTimeUTC.timeInMillis

        override operator fun plus(duration: TimeDuration): DateTime =
            DateTimeSDK22(
                java.util.Calendar.getInstance().also {
                    it.timeZone = dateTimeUTC.timeZone
                    it.timeInMillis = dateTimeUTC.timeInMillis + duration.tickCounts
                })

        override operator fun minus(dateTime: DateTime): TimeDuration =
            TimeDuration.ofTickCounts(
                this.dateTimeUTC.timeInMillis -
                        (dateTime as DateTimeSDK22).dateTimeUTC.timeInMillis
            )

        override operator fun minus(duration: TimeDuration): DateTime =
            DateTimeSDK22(
                java.util.Calendar.getInstance().also {
                    it.timeZone = dateTimeUTC.timeZone
                    it.timeInMillis = dateTimeUTC.timeInMillis - duration.tickCounts
                })

        override fun compareTo(other: DateTime): Int =
            this.dateTimeUTC.timeInMillis.compareTo((other as DateTimeSDK22).dateTimeUTC.timeInMillis)

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as DateTimeSDK22
            if (dateTimeUTC.timeInMillis != other.dateTimeUTC.timeInMillis)
                return false
            return true
        }

        override fun hashCode(): Int = dateTimeUTC.timeInMillis.hashCode()

        override fun toString(): String =
            "DateTime(dateTime='${java.text.SimpleDateFormat(
                if (dateTimeUTC.get(java.util.Calendar.MILLISECOND) == 0)
                    ZonedDateTime.dateTimeFormatOfISO8601
                else
                    ZonedDateTime.dateTimeFormatOfISO8601ContainingMilliSecond,
                java.util.Locale.ENGLISH
            )
                .apply { timeZone = gmt }
                .format(dateTimeUTC.time)}', epochMilliSeconds='${dateTimeUTC.timeInMillis}')"

        override fun plusMilliSeconds(milliSeconds: Long): DateTime =
            DateTimeSDK22(
                java.util.Calendar.getInstance().also {
                    it.timeZone = dateTimeUTC.timeZone
                    it.timeInMillis = dateTimeUTC.timeInMillis + milliSeconds
                })

        override fun plusSeconds(seconds: Long): DateTime =
            plusMilliSeconds(seconds * 1000)

        override fun plusMinutes(minutes: Long): DateTime =
            plusMilliSeconds(minutes * (1000 * 60))

        override fun plusHours(hours: Long): DateTime =
            plusMilliSeconds(hours * (1000 * 60 * 60))

        override fun plusDays(days: Long): DateTime =
            plusMilliSeconds(days * (1000 * 60 * 60 * 24))

        override fun plusWeeks(weeks: Long): DateTime =
            plusMilliSeconds(weeks * (1000 * 60 * 60 * 24 * 7))

        override fun minusMilliSeconds(milliSeconds: Long): DateTime =
            DateTimeSDK22(
                java.util.Calendar.getInstance().also {
                    it.timeZone = dateTimeUTC.timeZone
                    it.timeInMillis = dateTimeUTC.timeInMillis - milliSeconds
                })

        override fun minusSeconds(seconds: Long): DateTime =
            minusMilliSeconds(seconds * 1000)

        override fun minusMinutes(minutes: Long): DateTime =
            minusMilliSeconds(minutes * (1000 * 60))

        override fun minusHours(hours: Long): DateTime =
            minusMilliSeconds(hours * (1000 * 60 * 60))

        override fun minusDays(days: Long): DateTime =
            minusMilliSeconds(days * (1000 * 60 * 60 * 24))

        override fun minusWeeks(weeks: Long): DateTime =
            minusMilliSeconds(weeks * (1000 * 60 * 60 * 24 * 7))

        companion object {
            private val gmt by lazy { java.util.TimeZone.getTimeZone("GMT") }

            fun now(): DateTime =
                DateTimeSDK22(java.util.Calendar.getInstance(gmt))

            fun ofEpochMilliSeconds(
                milliSseconds: Long
            ): DateTime =
                DateTimeSDK22(
                    java.util.Calendar.getInstance().also {
                        it.timeZone = gmt
                        it.timeInMillis = milliSseconds
                    })

            fun of(dateTime: java.util.Calendar): DateTime =
                DateTimeSDK22(dateTime)
        }
    }

    abstract val rawObject: Any
    abstract val epochSeconds: Long
    abstract val epochMilliSeconds: Long
    abstract operator fun plus(duration: TimeDuration): DateTime
    abstract operator fun minus(dateTime: DateTime): TimeDuration
    abstract operator fun minus(duration: TimeDuration): DateTime
    abstract operator fun compareTo(other: DateTime): Int
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    abstract override fun toString(): String
    abstract fun plusMilliSeconds(milliSeconds: Long): DateTime
    abstract fun plusSeconds(seconds: Long): DateTime
    abstract fun plusMinutes(minutes: Long): DateTime
    abstract fun plusHours(hours: Long): DateTime
    abstract fun plusDays(days: Long): DateTime
    abstract fun plusWeeks(weeks: Long): DateTime
    abstract fun minusMilliSeconds(milliSeconds: Long): DateTime
    abstract fun minusSeconds(seconds: Long): DateTime
    abstract fun minusMinutes(minutes: Long): DateTime
    abstract fun minusHours(hours: Long): DateTime
    abstract fun minusDays(days: Long): DateTime
    abstract fun minusWeeks(weeks: Long): DateTime

    fun atZone(timeZone: TimeZone): ZonedDateTime =
        ZonedDateTime.of(this, timeZone)

    fun atStartOfDay(timeZone: TimeZone): DateTime =
        ZonedDateTime.of(this, timeZone)
            .atStartOfDay()

    fun formatRelativeTime(context: Context, baseDateTime: DateTime, timeZone: TimeZone): String {
        val days = minus(baseDateTime.atStartOfDay(timeZone)).days
        val duration = minus(baseDateTime)
        val localTime =
            atZone(timeZone).format(context.getString(R.string.date_time_format_relative_date_time_time))
        return when {
            days >= 2L ->
                context.getString(
                    R.string.date_time_format_relative_date_time_days_later,
                    localTime,
                    days
                )
            days == 1L ->
                context.getString(
                    R.string.date_time_format_relative_date_time_tomorrow,
                    localTime
                )
            days == 0L -> {
                when {
                    duration >= TimeDuration.ofHours(1) -> {
                        duration.hours.toInt().let {
                            "$localTime (${context.resources.getQuantityString(
                                R.plurals.date_time_format_relative_date_time_hours_later,
                                it,
                                it
                            )})"
                        }
                    }
                    duration >= TimeDuration.ofMinutes(1) -> {
                        duration.minutes.toInt().let {
                            "$localTime (${context.resources.getQuantityString(
                                R.plurals.date_time_format_relative_date_time_minutes_later,
                                it,
                                it
                            )})"
                        }
                    }
                    duration > TimeDuration.ofMinutes(-1) -> {
                        localTime
                    }
                    duration > TimeDuration.ofHours(-1) -> {
                        (-duration).minutes.toInt().let {
                            "$localTime (${context.resources.getQuantityString(
                                R.plurals.date_time_view_format_relative_date_time_minutes_ago,
                                it,
                                it
                            )})"
                        }
                    }
                    else ->
                        (-duration).hours.toInt().let {
                            "$localTime (${context.resources.getQuantityString(
                                R.plurals.date_time_format_relative_date_time_hours_ago,
                                it,
                                it
                            )})"
                        }
                }
            }
            days == -1L ->
                context.getString(
                    R.string.date_time_format_relative_date_time_yesterday,
                    localTime
                )
            else ->
                context.getString(
                    R.string.date_time_format_relative_date_time_days_ago,
                    localTime,
                    -days
                )
        }
    }

    companion object {
        @JvmStatic
        fun now(): DateTime =
            Platform.sdK26Depended({
                DateTimeSDK26.now()
            }, {
                DateTimeSDK22.now()
            })

        @JvmStatic
        fun ofEpochMilliSeconds(milliSseconds: Long): DateTime =
            Platform.sdK26Depended({
                DateTimeSDK26.ofEpochMilliSeconds(milliSseconds)
            }, {
                DateTimeSDK22.ofEpochMilliSeconds(milliSseconds)
            })

        @JvmStatic
        fun ofEpochSeconds(seconds: Long): DateTime =
            ofEpochMilliSeconds(seconds * 1000)

        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.O)
        internal fun of(dateTime: java.time.LocalDateTime): DateTime =
            DateTimeSDK26.of(dateTime)

        @JvmStatic
        internal fun of(dateTime: java.util.Calendar): DateTime =
            DateTimeSDK22.of(dateTime)

        @JvmStatic
        val EPOCH: DateTime by lazy {
            ofEpochMilliSeconds(0)
        }
    }

}