package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TimeDurationTest {

    @Test
    fun zero() {
        assertEquals(0, TimeDuration.ZERO.tickCounts)
    }

    @Test
    fun fromDays() {
        assertEquals(1000L * 60 * 60 * 24 * 10, TimeDuration.fromDays(10.0).tickCounts)
        assertEquals(0L, TimeDuration.fromDays(0.0).tickCounts)
        assertEquals(-1000L * 60 * 60 * 24 * 10, TimeDuration.fromDays(-10.0).tickCounts)
    }

    @Test
    fun fromHours() {
        assertEquals(1000L * 60 * 60 * 10, TimeDuration.fromHours(10.0).tickCounts)
        assertEquals(0L, TimeDuration.fromHours(0.0).tickCounts)
        assertEquals(-1000L * 60 * 60 * 10, TimeDuration.fromHours(-10.0).tickCounts)
    }

    @Test
    fun fromMilliSeconds() {
        assertEquals(10L, TimeDuration.fromMilliSeconds(10.0).tickCounts)
        assertEquals(0L, TimeDuration.fromMilliSeconds(0.0).tickCounts)
        assertEquals(-10L, TimeDuration.fromMilliSeconds(-10.0).tickCounts)
    }

    @Test
    fun fromMinutes() {
        assertEquals(1000L * 60 * 10, TimeDuration.fromMinutes(10.0).tickCounts)
        assertEquals(0L, TimeDuration.fromMinutes(0.0).tickCounts)
        assertEquals(-1000L * 60 * 10, TimeDuration.fromMinutes(-10.0).tickCounts)
    }

    @Test
    fun fromSeconds() {
        assertEquals(1000L * 10, TimeDuration.fromSeconds(10.0).tickCounts)
        assertEquals(0L, TimeDuration.fromSeconds(0.0).tickCounts)
        assertEquals(-1000L * 10, TimeDuration.fromSeconds(-10.0).tickCounts)
    }

    @Test
    fun fromTickCounts() {
        assertEquals(10L, TimeDuration.fromTickCounts(10).tickCounts)
        assertEquals(0L, TimeDuration.fromTickCounts(0).tickCounts)
        assertEquals(-10L, TimeDuration.fromTickCounts(-10).tickCounts)
    }

    @Test
    fun fromWeeks() {
        assertEquals(1000L * 60 * 60 * 24 * 7 * 10, TimeDuration.fromWeeks(10.0).tickCounts)
        assertEquals(0L, TimeDuration.fromWeeks(0.0).tickCounts)
        assertEquals(-1000L * 60 * 60 * 24 * 7 * 10, TimeDuration.fromWeeks(-10.0).tickCounts)
    }

    @Test
    fun getMilliSeconds() {
        assertEquals(1000.0 * 60 * 60 * 24 * 7 * 10, TimeDuration.fromWeeks(10.0).milliSeconds, 1.0)
        assertEquals(0.0, TimeDuration.fromWeeks(0.0).milliSeconds, 0.0)
        assertEquals(
            -1000.0 * 60 * 60 * 24 * 7 * 10,
            TimeDuration.fromWeeks(-10.0).milliSeconds,
            1.0
        )
    }

    @Test
    fun getSeconds() {
        assertEquals(60.0 * 60 * 24 * 7 * 10, TimeDuration.fromWeeks(10.0).seconds, 1.0 / 1000)
        assertEquals(0.0, TimeDuration.fromWeeks(0.0).seconds, 0.0)
        assertEquals(-60.0 * 60 * 24 * 7 * 10, TimeDuration.fromWeeks(-10.0).seconds, 1.0 / 1000)
    }

    @Test
    fun getMinutes() {
        assertEquals(60.0 * 24 * 7 * 10, TimeDuration.fromWeeks(10.0).minutes, 1.0 / 1000 / 60)
        assertEquals(0.0, TimeDuration.fromWeeks(0.0).minutes, 0.0)
        assertEquals(-60.0 * 24 * 7 * 10, TimeDuration.fromWeeks(-10.0).minutes, 1.0 / 1000 / 60)
    }

    @Test
    fun getHours() {
        assertEquals(24.0 * 7 * 10, TimeDuration.fromWeeks(10.0).hours, 1.0 / 1000 / 60 / 60)
        assertEquals(0.0, TimeDuration.fromWeeks(0.0).hours, 0.0)
        assertEquals(-24.0 * 7 * 10, TimeDuration.fromWeeks(-10.0).hours, 1.0 / 1000 / 60 / 60)
    }

    @Test
    fun getDays() {
        assertEquals(7.0 * 10, TimeDuration.fromWeeks(10.0).days, 1.0 / 1000 / 60 / 60 / 24)
        assertEquals(0.0, TimeDuration.fromWeeks(0.0).days, 0.0)
        assertEquals(-7.0 * 10, TimeDuration.fromWeeks(-10.0).days, 1.0 / 1000 / 60 / 60 / 24)
    }

    @Test
    fun getWeeks() {
        assertEquals(10.0, TimeDuration.fromWeeks(10.0).weeks, 1.0 / 1000 / 60 / 60 / 24 / 7)
        assertEquals(0.0, TimeDuration.fromWeeks(0.0).weeks, 0.0)
        assertEquals(-10.0, TimeDuration.fromWeeks(-10.0).weeks, 1.0 / 1000 / 60 / 60 / 24 / 7)
    }

    @Test
    fun plusTimeDuration() {
        assertEquals(
            1500,
            (TimeDuration.fromTickCounts(1000) + TimeDuration.fromTickCounts(500)).tickCounts
        )
    }

    @Test
    fun plusDateTime() {
        assertEquals(
            110,
            (TimeDuration.fromTickCounts(10L) + DateTime.fromEpochMilliSeconds(100L)).epochMilliSeconds
        )
        assertEquals(
            0,
            (TimeDuration.fromTickCounts(-100L) + DateTime.fromEpochMilliSeconds(100L)).epochMilliSeconds
        )
        assertEquals(
            -90,
            (TimeDuration.fromTickCounts(-100L) + DateTime.fromEpochMilliSeconds(10L)).epochMilliSeconds
        )
    }

    @Test
    fun minus() {
        assertEquals(
            700,
            (TimeDuration.fromTickCounts(1000) - TimeDuration.fromTickCounts(300)).tickCounts
        )
    }

    @Test
    fun times() {
        assertEquals(3000, (TimeDuration.fromTickCounts(1000) * 3).tickCounts)
        assertEquals(3000, (TimeDuration.fromTickCounts(1000) * 3L).tickCounts)
        assertEquals(3000, (TimeDuration.fromTickCounts(1000) * 3.0).tickCounts)
    }

    @Test
    fun div() {
        assertEquals(1000, (TimeDuration.fromTickCounts(3000) / 3).tickCounts)
        assertEquals(1000, (TimeDuration.fromTickCounts(3000) / 3.0).tickCounts)
    }

    @Test
    fun compareTo() {
        assertTrue(
            TimeDuration.fromTickCounts(1000).compareTo(TimeDuration.fromTickCounts(1000)) == 0
        )
        assertTrue(
            TimeDuration.fromTickCounts(1000) > TimeDuration.fromTickCounts(-1000)
        )
        assertTrue(
            TimeDuration.fromTickCounts(-1000) < TimeDuration.fromTickCounts(1000)
        )
    }

    @Test
    operator fun unaryPlus() {
        assertEquals(
            TimeDuration.fromTickCounts(1000).tickCounts,
            TimeDuration.fromTickCounts(1000).unaryPlus().tickCounts
        )
        assertEquals(
            TimeDuration.fromTickCounts(0).tickCounts,
            TimeDuration.fromTickCounts(0).unaryPlus().tickCounts
        )
        assertEquals(
            TimeDuration.fromTickCounts(-1000).tickCounts,
            TimeDuration.fromTickCounts(-1000).unaryPlus().tickCounts
        )
    }

    @Test
    operator fun unaryMinus() {
        assertEquals(
            TimeDuration.fromTickCounts(-1000).tickCounts,
            TimeDuration.fromTickCounts(1000).unaryMinus().tickCounts
        )
        assertEquals(
            TimeDuration.fromTickCounts(0).tickCounts,
            TimeDuration.fromTickCounts(0).unaryMinus().tickCounts
        )
        assertEquals(
            TimeDuration.fromTickCounts(1000).tickCounts,
            TimeDuration.fromTickCounts(-1000).unaryMinus().tickCounts
        )
    }

    @Test
    fun testEquals() {
        assertEquals(
            false,
            TimeDuration.fromTickCounts(-1000) == TimeDuration.fromTickCounts(1000)
        )
        assertEquals(
            true,
            TimeDuration.fromTickCounts(1000) == TimeDuration.fromTickCounts(1000)
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            "TimeDuration('2:46:39.999')",
            TimeDuration.fromTickCounts(10000000 - 1).toString()
        )
        assertEquals("TimeDuration('1:39.999')", TimeDuration.fromTickCounts(100000 - 1).toString())
        assertEquals("TimeDuration('9.999')", TimeDuration.fromTickCounts(10000 - 1).toString())
        assertEquals("TimeDuration('0.999')", TimeDuration.fromTickCounts(1000 - 1).toString())
        assertEquals("TimeDuration('0.000')", TimeDuration.fromTickCounts(0).toString())
        assertEquals("TimeDuration('-0.999')", TimeDuration.fromTickCounts(-(1000 - 1)).toString())
        assertEquals("TimeDuration('-9.999')", TimeDuration.fromTickCounts(-(10000 - 1)).toString())
        assertEquals(
            "TimeDuration('-1:39.999')",
            TimeDuration.fromTickCounts(-(100000 - 1)).toString()
        )
        assertEquals(
            "TimeDuration('-2:46:39.999')",
            TimeDuration.fromTickCounts(-(10000000 - 1)).toString()
        )
    }
}