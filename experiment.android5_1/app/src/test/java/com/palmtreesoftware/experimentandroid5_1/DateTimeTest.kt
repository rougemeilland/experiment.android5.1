package com.palmtreesoftware.experimentandroid5_1

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

class DateTimeTest {

    @Test
    fun epoch() {
        assertEquals(0, DateTime.EPOCH.epochMilliSeconds)
    }

    @Test
    fun fromEpochMilliSeconds() {
        assertEquals(100000, DateTime.fromEpochMilliSeconds(100000).epochMilliSeconds)
        assertEquals(99999, DateTime.fromEpochMilliSeconds(99999).epochMilliSeconds)
        assertEquals(0, DateTime.fromEpochMilliSeconds(0).epochMilliSeconds)
        assertEquals(-99999, DateTime.fromEpochMilliSeconds(-99999).epochMilliSeconds)
        assertEquals(-100000, DateTime.fromEpochMilliSeconds(-100000).epochMilliSeconds)
    }

    @Test
    fun fromEpochSeconds() {
        assertEquals(100000000, DateTime.fromEpochSeconds(100000).epochMilliSeconds)
        assertEquals(99999000, DateTime.fromEpochSeconds(99999).epochMilliSeconds)
        assertEquals(0, DateTime.fromEpochSeconds(0).epochMilliSeconds)
        assertEquals(-99999000, DateTime.fromEpochSeconds(-99999).epochMilliSeconds)
        assertEquals(-100000000, DateTime.fromEpochSeconds(-100000).epochMilliSeconds)
    }

    @Test
    fun now() {
        val expected = Calendar.getInstance().timeInMillis
        val actual = DateTime.now().epochMilliSeconds
        assertTrue((expected - actual).let { it >= -100 && it < 100 })
    }

    @Test
    fun getEpochSeconds() {
        assertEquals(1000, DateTime.fromEpochMilliSeconds(1000000).epochSeconds)
        assertEquals(999, DateTime.fromEpochMilliSeconds(999999).epochSeconds)
        assertEquals(0, DateTime.fromEpochMilliSeconds(0).epochSeconds)
        assertEquals(-1000, DateTime.fromEpochMilliSeconds(-999999).epochSeconds)
        assertEquals(-1000, DateTime.fromEpochMilliSeconds(-1000000).epochSeconds)
    }

    @Test
    fun atZone() {

        DateTime.fromEpochMilliSeconds(0).atZone(TimeZone.GMT)
            .let { actual ->
                assertEquals(0, actual.epochMilliSeconds)
                assertEquals("GMT", actual.timeZone.id)
            }
        DateTime.fromEpochMilliSeconds(10000).atZone(TimeZone.GMT)
            .let { actual ->
                assertEquals(10000, actual.epochMilliSeconds)
                assertEquals("GMT", actual.timeZone.id)
            }
        DateTime.fromEpochMilliSeconds(0).atZone(TimeZone.of("Asia/Tokyo"))
            .let { actual ->
                assertEquals(0, actual.epochMilliSeconds)
                assertEquals("Asia/Tokyo", actual.timeZone.id)
            }
        DateTime.fromEpochMilliSeconds(10000).atZone(TimeZone.of("Asia/Tokyo"))
            .let { actual ->
                assertEquals(10000, actual.epochMilliSeconds)
                assertEquals("Asia/Tokyo", actual.timeZone.id)
            }
    }

    @Test
    fun plus() {
        assertEquals(
            110,
            (DateTime.fromEpochMilliSeconds(10L) + TimeDuration.fromTickCounts(100L)).epochMilliSeconds
        )
        assertEquals(
            0,
            (DateTime.fromEpochMilliSeconds(100L) + TimeDuration.fromTickCounts(-100L)).epochMilliSeconds
        )
        assertEquals(
            -90,
            (DateTime.fromEpochMilliSeconds(-100L) + TimeDuration.fromTickCounts(10L)).epochMilliSeconds
        )
    }

    @Test
    fun minusDateTime() {
        assertEquals(
            90,
            (DateTime.fromEpochMilliSeconds(100L) - DateTime.fromEpochMilliSeconds(10L)).tickCounts
        )
        assertEquals(
            0,
            (DateTime.fromEpochMilliSeconds(100L) - DateTime.fromEpochMilliSeconds(100L)).tickCounts
        )
        assertEquals(
            -90,
            (DateTime.fromEpochMilliSeconds(10L) - DateTime.fromEpochMilliSeconds(100L)).tickCounts
        )
    }

    @Test
    fun minusTimeDuration() {
        assertEquals(
            90,
            (DateTime.fromEpochMilliSeconds(100L) - TimeDuration.fromTickCounts(10L)).epochMilliSeconds
        )
        assertEquals(
            0,
            (DateTime.fromEpochMilliSeconds(100L) - TimeDuration.fromTickCounts(100L)).epochMilliSeconds
        )
        assertEquals(
            -90,
            (DateTime.fromEpochMilliSeconds(10L) - TimeDuration.fromTickCounts(100L)).epochMilliSeconds
        )
    }

    @Test
    fun compareTo() {
        assertTrue(
            DateTime.fromEpochMilliSeconds(100) > DateTime.fromEpochMilliSeconds(10)
        )
        assertTrue(
            DateTime.fromEpochMilliSeconds(100).compareTo(DateTime.fromEpochMilliSeconds(100)) == 0
        )
        assertTrue(
            DateTime.fromEpochMilliSeconds(10) < DateTime.fromEpochMilliSeconds(100)
        )
    }

    @Test
    fun equalsDateTime() {
        assertEquals(
            false,
            DateTime.fromEpochMilliSeconds(100) == DateTime.fromEpochMilliSeconds(10)
        )
        assertEquals(
            true,
            DateTime.fromEpochMilliSeconds(100) == DateTime.fromEpochMilliSeconds(100)
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            "DateTime(epochMilliSeconds='10000000')",
            DateTime.fromEpochMilliSeconds(10000000).toString()
        )
        assertEquals(
            "DateTime(epochMilliSeconds='9999999')",
            DateTime.fromEpochMilliSeconds(9999999).toString()
        )
        assertEquals(
            "DateTime(epochMilliSeconds='0')",
            DateTime.fromEpochMilliSeconds(0).toString()
        )
        assertEquals(
            "DateTime(epochMilliSeconds='-9999999')",
            DateTime.fromEpochMilliSeconds(-9999999).toString()
        )
        assertEquals(
            "DateTime(epochMilliSeconds='-10000000')",
            DateTime.fromEpochMilliSeconds(-10000000).toString()
        )
    }
}