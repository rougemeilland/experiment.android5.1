package com.palmtreesoftware.experimentandroid5_1

class TimeDuration private constructor(val tickCounts: Long) {
    val milliSeconds: Long
        get() = tickCounts

    val seconds: Long
        get() = tickCounts.divideRound(1000)

    val minutes: Long
        get() = tickCounts.divideRound(1000 * 60)

    val hours: Long
        get() = tickCounts.divideRound(1000 * 60 * 60)

    val days: Long
        get() = tickCounts.divideRound(1000 * 60 * 60 * 24)

    val weeks: Long
        get() = tickCounts.divideRound(1000 * 60 * 60 * 24 * 7)

    operator fun plus(dateTime: DateTime): DateTime =
        dateTime.plus(this)

    operator fun plus(duration: TimeDuration): TimeDuration =
        TimeDuration(tickCounts + duration.tickCounts)

    operator fun minus(duration: TimeDuration): TimeDuration =
        TimeDuration(tickCounts - duration.tickCounts)

    operator fun times(multiplicand: Long): TimeDuration =
        TimeDuration(tickCounts * multiplicand)

    operator fun div(divisor: Long): TimeDuration =
        TimeDuration(tickCounts.divideRound(divisor))

    operator fun compareTo(other: TimeDuration): Int =
        tickCounts.compareTo(other.tickCounts)

    operator fun unaryPlus(): TimeDuration =
        TimeDuration(tickCounts)

    operator fun unaryMinus(): TimeDuration =
        TimeDuration(-tickCounts)

    fun plusMilliSeconds(milliSeconds: Long): TimeDuration =
        TimeDuration(tickCounts + milliSeconds)

    fun plusSeconds(seconds: Long): TimeDuration =
        plusMilliSeconds(seconds * 1000)

    fun plusMinutes(minutes: Long): TimeDuration =
        plusMilliSeconds(minutes * (1000 * 60))

    fun plusHours(hours: Long): TimeDuration =
        plusMilliSeconds(hours * (1000 * 60 * 60))

    fun plusDays(days: Long): TimeDuration =
        plusMilliSeconds(days * (1000 * 60 * 60 * 24))

    fun plusWeeks(weeks: Long): TimeDuration =
        plusMilliSeconds(weeks * (1000 * 60 * 60 * 24 * 7))

    fun minusMilliSeconds(milliSeconds: Long): TimeDuration =
        TimeDuration(tickCounts - milliSeconds)

    fun minusSeconds(seconds: Long): TimeDuration =
        minusMilliSeconds(seconds * 1000)

    fun minusMinutes(minutes: Long): TimeDuration =
        minusMilliSeconds(minutes * (1000 * 60))

    fun minusHours(hours: Long): TimeDuration =
        minusMilliSeconds(hours * (1000 * 60 * 60))

    fun minusDays(days: Long): TimeDuration =
        minusMilliSeconds(days * (1000 * 60 * 60 * 24))

    fun minusWeeks(weeks: Long): TimeDuration =
        minusMilliSeconds(weeks * (1000 * 60 * 60 * 24 * 7))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimeDuration
        if (tickCounts != other.tickCounts) return false
        return true
    }

    override fun hashCode(): Int = tickCounts.hashCode()

    override fun toString(): String {
        val milliSeconds: Int
        val seconds: Int
        val minutes: Int
        val hours: Long
        val sign: String
        if (tickCounts >= 0) {
            sign = ""
            milliSeconds = (tickCounts % 1000).toInt()
            (tickCounts / 1000).let { totalSeconds ->
                seconds = (totalSeconds % 60).toInt()
                (totalSeconds / 60).let { totalMinutes ->
                    minutes = (totalMinutes % 60).toInt()
                    hours = totalMinutes / 60
                }
            }
        } else {
            sign = "-"
            (-tickCounts).let { totalMilliSeconds ->
                milliSeconds = (totalMilliSeconds % 1000).toInt()
                (totalMilliSeconds / 1000).let { totalSeconds ->
                    seconds = (totalSeconds % 60).toInt()
                    (totalSeconds / 60).let { totalMinutes ->
                        minutes = (totalMinutes % 60).toInt()
                        hours = totalMinutes / 60
                    }
                }
            }
        }
        return when {
            hours != 0L -> {
                "TimeDuration('%s%d:%02d:%02d.%03d')".format(
                    sign,
                    hours,
                    minutes,
                    seconds,
                    milliSeconds
                )
            }
            minutes != 0 -> {
                "TimeDuration('%s%d:%02d.%03d')".format(
                    sign,
                    minutes,
                    seconds,
                    milliSeconds
                )
            }
            else -> {
                "TimeDuration('%s%d.%03d')".format(sign, seconds, milliSeconds)
            }
        }
    }

    companion object {
        @JvmStatic
        val ZERO: TimeDuration = TimeDuration(0)

        @JvmStatic
        fun ofTickCounts(tickCount: Long): TimeDuration = TimeDuration(tickCount)

        @JvmStatic
        fun ofMilliSeconds(milliSeconds: Long): TimeDuration =
            TimeDuration(milliSeconds)

        @JvmStatic
        fun ofSeconds(seconds: Long): TimeDuration =
            TimeDuration(seconds * 1000)

        @JvmStatic
        fun ofMinutes(seconds: Long): TimeDuration =
            TimeDuration(seconds * (1000 * 60))

        @JvmStatic
        fun ofHours(seconds: Long): TimeDuration =
            TimeDuration(seconds * (1000 * 60 * 60))

        @JvmStatic
        fun ofDays(seconds: Long): TimeDuration =
            TimeDuration(seconds * (1000 * 60 * 60 * 24))

        @JvmStatic
        fun ofWeeks(seconds: Long): TimeDuration =
            TimeDuration(seconds * (1000 * 60 * 60 * 24 * 7))
    }
}
