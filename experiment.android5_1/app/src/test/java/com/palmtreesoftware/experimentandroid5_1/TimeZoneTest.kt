package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TimeZoneTest {

    @Test
    fun ofTimeZoneId() {
        arrayOf(
            Pair("GMT-01:02:03", "GMT-01:02:03"),
            Pair("GMT-01:02", "GMT-01:02"),
            Pair("GMT+00:00:00", "GMT"),
            Pair("GMT+00:00", "GMT"),
            Pair("GMT+01:02:03", "GMT+01:02:03"),
            Pair("GMT+01:02", "GMT+01:02"),
            Pair("Asia/Tokyo", "Asia/Tokyo"),
            Pair("GMT", "GMT"),
            Pair("UTC", "UTC")
        ).forEach {
            assertEquals(it.second, TimeZone.of(it.first).id)
        }
    }

    @Test
    fun ofHourMinuteSecond() {
        assertEquals("GMT-01:02:03", TimeZone.of(-1, -2, -3).id)
        assertEquals("GMT-01:02", TimeZone.of(-1, -2, 0).id)
        assertEquals("GMT", TimeZone.of(0, 0, 0).id)
        assertEquals("GMT+01:02:03", TimeZone.of(1, 2, 3).id)
        assertEquals("GMT+01:02", TimeZone.of(1, 2, 0).id)
    }

    @Test
    fun ofHourMinute() {
        assertEquals("GMT+01:02", TimeZone.of(1, 2).id)
        assertEquals("GMT", TimeZone.of(0, 0).id)
        assertEquals("GMT-01:02", TimeZone.of(-1, -2).id)
    }

    @Test
    fun ofTotalSeconds() {
        assertEquals("GMT+01:02:03", TimeZone.ofTotalSeconds(1 * 3600 + 2 * 60 + 3).id)
        assertEquals("GMT+01:02", TimeZone.ofTotalSeconds(1 * 3600 + 2 * 60).id)
        assertEquals("GMT", TimeZone.ofTotalSeconds(0).id)
        assertEquals("GMT-01:02:03", TimeZone.ofTotalSeconds(-(1 * 3600 + 2 * 60 + 3)).id)
        assertEquals("GMT-01:02", TimeZone.ofTotalSeconds(-(1 * 3600 + 2 * 60)).id)
    }

    @Test
    fun getShortName() {
        arrayOf(
            Pair("GMT", "GMT"),
            Pair("UTC", "UTC"),
            Pair("Asia/Tokyo", "JST"),
            Pair("America/New_York", "EST"),
            Pair("America/Los_Angeles", "PST"),
            Pair("Europe/Berlin", "CET"),
            Pair("Europe/Paris", "CET"),
            Pair("Europe/London", "WET")
        ).forEach {
            assertEquals(it.second, TimeZone.of(it.first).shortName)
        }
    }

    @Test
    fun testEquals() {
        assertEquals(true, TimeZone.ofTotalSeconds(0) == TimeZone.GMT)
        assertEquals(true, TimeZone.of(0, 0) == TimeZone.GMT)
        assertEquals(true, TimeZone.of(0, 0, 0) == TimeZone.GMT)
        assertEquals(true, TimeZone.of("GMT") == TimeZone.GMT)
        assertEquals(
            true,
            TimeZone.of("GMT+00:00") == TimeZone.GMT,
            "${TimeZone.of("GMT+00:00")}==${TimeZone.GMT}"
        )
        assertEquals(true, TimeZone.of("GMT+00:00:00") == TimeZone.GMT)
        assertEquals(true, TimeZone.of(1, 2, 3) == TimeZone.of("GMT+01:02:03"))
        assertEquals(false, TimeZone.of("GMT") == TimeZone.of("GMT+00:02"))
        assertEquals(false, TimeZone.of("GMT") == TimeZone.of("Asia/Tokyo"))
        assertEquals(false, TimeZone.of("GMT") == TimeZone.of("Europe/London"))
        assertEquals(false, TimeZone.of("GMT+00:02") == TimeZone.of("Asia/Tokyo"))
        assertEquals(false, TimeZone.of("GMT+00:02") == TimeZone.of("Europe/London"))
        assertEquals(false, TimeZone.of("Asia/Tokyo") == TimeZone.of("Europe/London"))
    }

    @Test
    fun testToString() {
        arrayOf(
            "GMT",
            "GMT+01:02:03",
            "Asia/Tokyo"
        ).forEach { timeZoneId ->
            assertEquals(
                "TimeZone(id='$timeZoneId')",
                TimeZone.of(timeZoneId).toString()
            )
        }
    }
}