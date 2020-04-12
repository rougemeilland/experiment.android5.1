package com.palmtreesoftware.experimentandroid51


class TimeDuration private constructor(val tickCounts: Long) {
    val milliSeconds: Double
        get() = tickCounts.toDouble()

    val seconds: Double
        get() = tickCounts / 1000.toDouble()

    val minutes: Double
        get() = tickCounts / (1000.toDouble() * 60)

    val hours: Double
        get() = tickCounts / (1000.toDouble() * 60 * 60)

    val days: Double
        get() = tickCounts / (1000.toDouble() * 60 * 60 * 24)

    val weeks: Double
        get() = tickCounts / (1000.toDouble() * 60 * 60 * 24 * 7)

    operator fun plus(dateTime: DateTime): DateTime = dateTime.plus(this)

    operator fun plus(duration: TimeDuration): TimeDuration =
        TimeDuration(tickCounts + duration.tickCounts)

    operator fun minus(duration: TimeDuration): TimeDuration =
        TimeDuration(tickCounts - duration.tickCounts)

    operator fun times(multiplicand: Int): TimeDuration =
        TimeDuration(tickCounts * multiplicand)

    operator fun times(multiplicand: Double): TimeDuration =
        TimeDuration((tickCounts * multiplicand).toLong())

    operator fun div(divisor: Int): TimeDuration =
        TimeDuration(tickCounts / divisor)

    operator fun div(divisor: Double): TimeDuration =
        TimeDuration((tickCounts / divisor).toLong())

    operator fun compareTo(other: TimeDuration): Int = tickCounts.compareTo(other.tickCounts)

    operator fun unaryPlus(): TimeDuration = TimeDuration(tickCounts)

    operator fun unaryMinus(): TimeDuration = TimeDuration(-tickCounts)

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
        val ZERO: TimeDuration = TimeDuration(0)

        fun fromTickCounts(tickCount: Long): TimeDuration = TimeDuration(tickCount)

        fun fromMilliSeconds(milliSeconds: Double): TimeDuration =
            TimeDuration(milliSeconds.toLong())

        fun fromSeconds(seconds: Double): TimeDuration =
            TimeDuration((seconds * 1000).toLong())

        fun fromMinutes(seconds: Double): TimeDuration =
            TimeDuration((seconds * (1000.toDouble() * 60)).toLong())

        fun fromHours(seconds: Double): TimeDuration =
            TimeDuration((seconds * (1000.toDouble() * 60 * 60)).toLong())

        fun fromDays(seconds: Double): TimeDuration =
            TimeDuration((seconds * (1000.toDouble() * 60 * 60 * 24)).toLong())

        fun fromWeeks(seconds: Double): TimeDuration =
            TimeDuration((seconds * (1000.toDouble() * 60 * 60 * 24 * 7)).toLong())
    }
}

operator fun Int.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)

operator fun Double.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)
