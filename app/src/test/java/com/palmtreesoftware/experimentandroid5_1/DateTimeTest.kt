package com.palmtreesoftware.experimentandroid51

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.util.*

// TODO("単体テストでは実行環境のSDKバージョンに依存したコードのテストはできない。工夫が要る模様。")
// TODO("https://proandroiddev.com/build-version-in-unit-testing-4e963940dae7")

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
            IllegalArgumentException::class.java,
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
            IllegalArgumentException::class.java,
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
        assertEquals(1000, DateTime.fromEpochMilliSeconds(1000000).epochSeconds)
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

        Platform.sdK26Depended({
            throw Exception("x")
        }, {
            throw Exception("y")
        })
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