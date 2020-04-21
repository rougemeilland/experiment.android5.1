package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TimeDurationTest {

    @Test
    fun zero() {
        assertEquals(0, TimeDuration.ZERO.tickCounts)
    }

    @Test
    fun ofDays() {
        assertEquals(1000L * 60 * 60 * 24 * 10, TimeDuration.ofDays(10).tickCounts)
        assertEquals(0L, TimeDuration.ofDays(0).tickCounts)
        assertEquals(-1000L * 60 * 60 * 24 * 10, TimeDuration.ofDays(-10).tickCounts)
    }

    @Test
    fun ofHours() {
        assertEquals(1000L * 60 * 60 * 10, TimeDuration.ofHours(10).tickCounts)
        assertEquals(0L, TimeDuration.ofHours(0).tickCounts)
        assertEquals(-1000L * 60 * 60 * 10, TimeDuration.ofHours(-10).tickCounts)
    }

    @Test
    fun ofMilliSeconds() {
        assertEquals(10L, TimeDuration.ofMilliSeconds(10).tickCounts)
        assertEquals(0L, TimeDuration.ofMilliSeconds(0).tickCounts)
        assertEquals(-10L, TimeDuration.ofMilliSeconds(-10).tickCounts)
    }

    @Test
    fun ofMinutes() {
        assertEquals(1000L * 60 * 10, TimeDuration.ofMinutes(10).tickCounts)
        assertEquals(0L, TimeDuration.ofMinutes(0).tickCounts)
        assertEquals(-1000L * 60 * 10, TimeDuration.ofMinutes(-10).tickCounts)
    }

    @Test
    fun ofSeconds() {
        assertEquals(1000L * 10, TimeDuration.ofSeconds(10).tickCounts)
        assertEquals(0L, TimeDuration.ofSeconds(0).tickCounts)
        assertEquals(-1000L * 10, TimeDuration.ofSeconds(-10).tickCounts)
    }

    @Test
    fun ofTickCounts() {
        assertEquals(10L, TimeDuration.ofTickCounts(10).tickCounts)
        assertEquals(0L, TimeDuration.ofTickCounts(0).tickCounts)
        assertEquals(-10L, TimeDuration.ofTickCounts(-10).tickCounts)
    }

    @Test
    fun ofWeeks() {
        assertEquals(1000L * 60 * 60 * 24 * 7 * 10, TimeDuration.ofWeeks(10).tickCounts)
        assertEquals(0L, TimeDuration.ofWeeks(0).tickCounts)
        assertEquals(-1000L * 60 * 60 * 24 * 7 * 10, TimeDuration.ofWeeks(-10).tickCounts)
    }

    @Test
    fun getMilliSeconds() {
        assertEquals(1000L * 60 * 60 * 24 * 7 * 10, TimeDuration.ofWeeks(10).milliSeconds)
        assertEquals(0L, TimeDuration.ofWeeks(0).milliSeconds)
        assertEquals(-1000L * 60 * 60 * 24 * 7 * 10, TimeDuration.ofWeeks(-10).milliSeconds)
    }

    @Test
    fun getSeconds() {
        assertEquals(60L * 60 * 24 * 7 * 10, TimeDuration.ofWeeks(10).seconds)
        assertEquals(0L, TimeDuration.ofWeeks(0).seconds)
        assertEquals(-60L * 60 * 24 * 7 * 10, TimeDuration.ofWeeks(-10).seconds)
    }

    @Test
    fun getMinutes() {
        assertEquals(60L * 24 * 7 * 10, TimeDuration.ofWeeks(10).minutes)
        assertEquals(0L, TimeDuration.ofWeeks(0).minutes)
        assertEquals(-60L * 24 * 7 * 10, TimeDuration.ofWeeks(-10).minutes)
    }

    @Test
    fun getHours() {
        assertEquals(24L * 7 * 10, TimeDuration.ofWeeks(10).hours)
        assertEquals(0L, TimeDuration.ofWeeks(0).hours)
        assertEquals(-24L * 7 * 10, TimeDuration.ofWeeks(-10).hours)
    }

    @Test
    fun getDays() {
        assertEquals(7L * 10, TimeDuration.ofWeeks(10).days)
        assertEquals(0L, TimeDuration.ofWeeks(0).days)
        assertEquals(-7L * 10, TimeDuration.ofWeeks(-10).days)
    }

    @Test
    fun getWeeks() {
        assertEquals(10L, TimeDuration.ofWeeks(10).weeks)
        assertEquals(0L, TimeDuration.ofWeeks(0).weeks)
        assertEquals(-10L, TimeDuration.ofWeeks(-10).weeks)
    }

    @Test
    fun plusTimeDuration() {
        assertEquals(
            1500,
            (TimeDuration.ofTickCounts(1000) + TimeDuration.ofTickCounts(500)).tickCounts
        )
    }

    @Test
    fun plusDateTime() {
        assertEquals(
            110,
            (TimeDuration.ofTickCounts(10L) + DateTime.ofEpochMilliSeconds(100L)).epochMilliSeconds
        )
        assertEquals(
            0,
            (TimeDuration.ofTickCounts(-100L) + DateTime.ofEpochMilliSeconds(100L)).epochMilliSeconds
        )
        assertEquals(
            -90,
            (TimeDuration.ofTickCounts(-100L) + DateTime.ofEpochMilliSeconds(10L)).epochMilliSeconds
        )
    }

    @Test
    fun minus() {
        assertEquals(
            700,
            (TimeDuration.ofTickCounts(1000) - TimeDuration.ofTickCounts(300)).tickCounts
        )
    }

    @Test
    fun times() {
        assertEquals(3000L, (TimeDuration.ofTickCounts(1000) * 3L).tickCounts)
        assertEquals(0L, (TimeDuration.ofTickCounts(1000) * 0L).tickCounts)
        assertEquals(-3000L, (TimeDuration.ofTickCounts(1000) * -3L).tickCounts)
        assertEquals(0L, (TimeDuration.ofTickCounts(0) * 3L).tickCounts)
        assertEquals(0L, (TimeDuration.ofTickCounts(0) * 0L).tickCounts)
        assertEquals(0L, (TimeDuration.ofTickCounts(0) * -3L).tickCounts)
        assertEquals(-3000L, (TimeDuration.ofTickCounts(-1000) * 3L).tickCounts)
        assertEquals(0L, (TimeDuration.ofTickCounts(-1000) * 0L).tickCounts)
        assertEquals(3000L, (TimeDuration.ofTickCounts(-1000) * -3L).tickCounts)
    }

    @Test
    fun div() {
        assertEquals(333L, (TimeDuration.ofTickCounts(1000) / 3L).tickCounts)
        assertThrows<ArithmeticException> { TimeDuration.ofTickCounts(1000) / 0L }
        assertEquals(-333L, (TimeDuration.ofTickCounts(1000) / -3L).tickCounts)
        assertEquals(0L, (TimeDuration.ofTickCounts(0) / 3L).tickCounts)
        assertThrows<ArithmeticException> { TimeDuration.ofTickCounts(0) / 0L }
        assertEquals(0L, (TimeDuration.ofTickCounts(0) / -3L).tickCounts)
        assertEquals(-334L, (TimeDuration.ofTickCounts(-1000) / 3L).tickCounts)
        assertThrows<ArithmeticException> { TimeDuration.ofTickCounts(-1000) / 0L }
        assertEquals(334L, (TimeDuration.ofTickCounts(-1000) / -3L).tickCounts)
    }

    @Test
    fun plusMilliSeconds() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds + value,
                    timeDuration.plusMilliSeconds(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun plusSeconds() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds + value * 1000,
                    timeDuration.plusSeconds(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun plusMinutes() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds + value * 1000 * 60,
                    timeDuration.plusMinutes(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun plusHours() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds + value * 1000 * 60 * 60,
                    timeDuration.plusHours(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun plusDays() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds + value * 1000 * 60 * 60 * 24,
                    timeDuration.plusDays(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun plusWeeks() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds + value * 1000 * 60 * 60 * 24 * 7,
                    timeDuration.plusWeeks(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun minusMilliSeconds() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds - value,
                    timeDuration.minusMilliSeconds(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun minusSeconds() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds - value * 1000,
                    timeDuration.minusSeconds(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun minusMinutes() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds - value * 1000 * 60,
                    timeDuration.minusMinutes(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun minusHours() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds - value * 1000 * 60 * 60,
                    timeDuration.minusHours(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun minusDays() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds - value * 1000 * 60 * 60 * 24,
                    timeDuration.minusDays(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun minusWeeks() {
        arrayOf(
            TimeDuration.ofMilliSeconds(10L),
            TimeDuration.ofMilliSeconds(0L),
            TimeDuration.ofMilliSeconds(-10L)
        ).forEach { timeDuration ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    timeDuration.milliSeconds - value * 1000 * 60 * 60 * 24 * 7,
                    timeDuration.minusWeeks(value).milliSeconds
                )
            }
        }
    }

    @Test
    fun compareTo() {
        val duration1 = TimeDuration.ofTickCounts(-1000L)
        val duration2 = TimeDuration.ofTickCounts(0L)
        val duration3 = TimeDuration.ofTickCounts(1000L)
        assertEquals(Sign.ZERO, duration1.compareTo(duration1).toSign())
        assertEquals(Sign.NEGATIVE, duration1.compareTo(duration2).toSign())
        assertEquals(Sign.NEGATIVE, duration1.compareTo(duration3).toSign())
        assertEquals(Sign.POSITIVE, duration2.compareTo(duration1).toSign())
        assertEquals(Sign.ZERO, duration2.compareTo(duration2).toSign())
        assertEquals(Sign.NEGATIVE, duration2.compareTo(duration3).toSign())
        assertEquals(Sign.POSITIVE, duration3.compareTo(duration1).toSign())
        assertEquals(Sign.POSITIVE, duration3.compareTo(duration2).toSign())
        assertEquals(Sign.ZERO, duration3.compareTo(duration3).toSign())
    }

    @Test
    operator fun unaryPlus() {
        assertEquals(
            TimeDuration.ofTickCounts(1000).tickCounts,
            TimeDuration.ofTickCounts(1000).unaryPlus().tickCounts
        )
        assertEquals(
            TimeDuration.ofTickCounts(0).tickCounts,
            TimeDuration.ofTickCounts(0).unaryPlus().tickCounts
        )
        assertEquals(
            TimeDuration.ofTickCounts(-1000).tickCounts,
            TimeDuration.ofTickCounts(-1000).unaryPlus().tickCounts
        )
    }

    @Test
    operator fun unaryMinus() {
        assertEquals(
            TimeDuration.ofTickCounts(-1000).tickCounts,
            TimeDuration.ofTickCounts(1000).unaryMinus().tickCounts
        )
        assertEquals(
            TimeDuration.ofTickCounts(0).tickCounts,
            TimeDuration.ofTickCounts(0).unaryMinus().tickCounts
        )
        assertEquals(
            TimeDuration.ofTickCounts(1000).tickCounts,
            TimeDuration.ofTickCounts(-1000).unaryMinus().tickCounts
        )
    }

    @Test
    fun testEquals() {
        assertEquals(
            false,
            TimeDuration.ofTickCounts(-1000) == TimeDuration.ofTickCounts(1000)
        )
        assertEquals(
            true,
            TimeDuration.ofTickCounts(1000) == TimeDuration.ofTickCounts(1000)
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            "TimeDuration('2:46:39.999')",
            TimeDuration.ofTickCounts(10000000 - 1).toString()
        )
        assertEquals("TimeDuration('1:39.999')", TimeDuration.ofTickCounts(100000 - 1).toString())
        assertEquals("TimeDuration('9.999')", TimeDuration.ofTickCounts(10000 - 1).toString())
        assertEquals("TimeDuration('0.999')", TimeDuration.ofTickCounts(1000 - 1).toString())
        assertEquals("TimeDuration('0.000')", TimeDuration.ofTickCounts(0).toString())
        assertEquals("TimeDuration('-0.999')", TimeDuration.ofTickCounts(-(1000 - 1)).toString())
        assertEquals("TimeDuration('-9.999')", TimeDuration.ofTickCounts(-(10000 - 1)).toString())
        assertEquals(
            "TimeDuration('-1:39.999')",
            TimeDuration.ofTickCounts(-(100000 - 1)).toString()
        )
        assertEquals(
            "TimeDuration('-2:46:39.999')",
            TimeDuration.ofTickCounts(-(10000000 - 1)).toString()
        )
    }
}