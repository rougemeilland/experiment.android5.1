package com.palmtreesoftware.experimentandroid5_1

import android.os.Build
import androidx.annotation.RequiresApi

abstract class DateTime protected constructor() {
    abstract val epochSeconds: Long
    abstract val epochMilliSeconds: Long

    fun atZone(timeZone: TimeZone): ZonedDateTime = ZonedDateTime.of(this, timeZone)

    abstract operator fun plus(duration: TimeDuration): DateTime
    abstract operator fun minus(dateTime: DateTime): TimeDuration
    abstract operator fun minus(duration: TimeDuration): DateTime
    abstract operator fun compareTo(other: DateTime): Int
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    abstract override fun toString(): String
    abstract val rawObject: Any

    companion object {
        fun now(): DateTime =
            Platform.sdK26Depended({
                DateTimeSDK26.now()
            }, {
                DateTimeSDK22.now()
            })

        fun fromEpochMilliSeconds(milliSseconds: Long): DateTime =
            Platform.sdK26Depended({
                DateTimeSDK26.fromEpochMilliSeconds(milliSseconds)
            }, {
                DateTimeSDK22.fromEpochMilliSeconds(milliSseconds)
            })

        fun fromEpochSeconds(seconds: Long): DateTime =
            fromEpochMilliSeconds(seconds * 1000)

        @RequiresApi(Build.VERSION_CODES.O)
        internal fun of(dateTime: java.time.LocalDateTime): DateTime =
            DateTimeSDK26.of(dateTime)

        internal fun of(dateTime: java.util.Calendar): DateTime =
            DateTimeSDK22.of(dateTime)

        val EPOCH: DateTime by lazy {
            fromEpochMilliSeconds(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private class DateTimeSDK26 private constructor(
        private val dateTimeUTC: java.time.LocalDateTime
    ) : DateTime() {
        override val epochSeconds: Long
            get() = dateTimeUTC.toInstant(java.time.ZoneOffset.UTC).epochSecond

        override val epochMilliSeconds: Long
            get() = dateTimeUTC.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()

        override val rawObject: Any
            get() = dateTimeUTC

        override operator fun plus(duration: TimeDuration): DateTime =
            DateTimeSDK26(dateTimeUTC.plus(java.time.Duration.ofMillis(duration.tickCounts)))

        override operator fun minus(dateTime: DateTime): TimeDuration =
            TimeDuration.fromTickCounts(
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
            "DateTime(epochMilliSeconds='${dateTimeUTC.toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli()}')"

        companion object {
            private val gmt by lazy { java.time.ZoneId.of("GMT") }

            fun now(): DateTime =
                DateTimeSDK26(java.time.LocalDateTime.now(gmt))

            fun fromEpochMilliSeconds(milliSseconds: Long): DateTime {
                val second = milliSseconds / 1000
                val milliSecond = (milliSseconds % 1000).toInt()

                return DateTimeSDK26(
                    if (milliSecond >= 0) {
                        java.time.LocalDateTime.ofEpochSecond(
                            second,
                            milliSecond * (1000 * 1000),
                            java.time.ZoneOffset.UTC
                        )
                    } else {
                        java.time.LocalDateTime.ofEpochSecond(
                            second - 1,
                            (milliSecond + 1000) * (1000 * 1000),
                            java.time.ZoneOffset.UTC
                        )
                    }
                )
            }

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

        override val epochSeconds: Long
            get() =
                dateTimeUTC.timeInMillis.let { milliSeconds ->
                    (milliSeconds / 1000).let { seconds ->
                        if (milliSeconds % 1000 >= 0)
                            seconds
                        else
                            seconds - 1
                    }
                }

        override val epochMilliSeconds: Long
            get() = dateTimeUTC.timeInMillis

        override operator fun plus(duration: TimeDuration): DateTime =
            DateTimeSDK22(
                java.util.Calendar.getInstance().also {
                    it.timeZone = dateTimeUTC.timeZone
                    it.timeInMillis = dateTimeUTC.timeInMillis + duration.tickCounts
                })

        override operator fun minus(dateTime: DateTime): TimeDuration =
            TimeDuration.fromTickCounts(
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
            "DateTime(epochMilliSeconds='${dateTimeUTC.timeInMillis}')"

        override val rawObject: Any
            get() = dateTimeUTC

        companion object {
            private val gmt by lazy { java.util.TimeZone.getTimeZone("GMT") }

            fun now(): DateTime =
                DateTimeSDK22(java.util.Calendar.getInstance(gmt))

            fun fromEpochMilliSeconds(
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
}