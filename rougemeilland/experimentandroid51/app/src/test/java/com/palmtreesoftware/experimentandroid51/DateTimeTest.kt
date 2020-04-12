package com.palmtreesoftware.experimentandroid51

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.util.*

class DateTimeTest {

    @Test
    fun EPOCH() {
        assertEquals(0, DateTime.EPOCH.epochMilliSeconds)
    }

    @Test
    fun fromEpochMilliSecondsPositive() {
        assertEquals(100000, DateTime.fromEpochMilliSeconds(100000).epochMilliSeconds)
    }

    @Test
    fun fromEpochMilliSecondsZero() {
        assertEquals(0, DateTime.fromEpochMilliSeconds(0).epochMilliSeconds)
    }

    @Test
    fun fromEpochMilliSecondsNegative() {
        assertThrows(
            Exception::class.java,
            { DateTime.fromEpochMilliSeconds(-100000).epochMilliSeconds })
    }

    @Test
    fun fromEpochSecondsPositive() {
        assertEquals(100000000, DateTime.fromEpochSeconds(100000).epochMilliSeconds)
    }

    @Test
    fun fromEpochSecondsZero() {
        assertEquals(0, DateTime.fromEpochSeconds(0).epochMilliSeconds)
    }

    @Test
    fun fromEpochSecondssNegative() {
        assertThrows(
            Exception::class.java,
            { DateTime.fromEpochSeconds(-100000).epochMilliSeconds })
    }

    @Test
    fun now() {
        val expected = Calendar.getInstance().timeInMillis
        val actual = DateTime.now().epochMilliSeconds
        assertTrue((expected - actual).let { it >= -100 && it < 100 })
    }

    @Test
    fun getEpochSeconds() {
        assertEquals(0, DateTime.fromEpochMilliSeconds(0).epochSeconds)
        assertEquals(1000000, DateTime.fromEpochMilliSeconds(1000).epochSeconds)
    }

    @Test
    fun atZone() {
        assertEquals(
            Pair(0, "GMT"),
            DateTime.fromEpochMilliSeconds(0).atZone(TimeZone.GMT)
                .let { Pair(it.epochMilliSeconds, it.timeZone.id) })
        assertEquals(
            1000000,
            DateTime.fromEpochMilliSeconds(1000000).atZone(TimeZone.GMT).epochMilliSeconds
        )
        assertEquals(
            0,
            DateTime.fromEpochMilliSeconds(0).atZone(TimeZone.of("Asia/Tokyo")).epochMilliSeconds
        )
        assertEquals(
            1000000,
            DateTime.fromEpochMilliSeconds(1000000)
                .atZone(TimeZone.of("Asia/Tokyo")).epochMilliSeconds
        )
    }

    @Test
    fun plus() {

    }

    @Test
    fun minus() {
    }

    @Test
    fun testMinus() {
    }

    @Test
    fun compareTo() {
    }

    @Test
    fun testEquals() {
    }

    @Test
    fun testHashCode() {
    }

    @Test
    fun testToString() {
    }

    @Test
    fun getRawObject() {
    }
}