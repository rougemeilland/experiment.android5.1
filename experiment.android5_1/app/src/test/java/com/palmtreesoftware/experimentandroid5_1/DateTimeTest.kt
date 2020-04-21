package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class DateTimeTest {

    @Test
    fun epoch() {
        assertEquals(0, DateTime.EPOCH.epochMilliSeconds)
    }

    @Test
    fun ofEpochMilliSeconds() {
        assertEquals(100000L, DateTime.ofEpochMilliSeconds(100000).epochMilliSeconds)
        assertEquals(99999L, DateTime.ofEpochMilliSeconds(99999).epochMilliSeconds)
        assertEquals(0L, DateTime.ofEpochMilliSeconds(0).epochMilliSeconds)
        assertEquals(-99999L, DateTime.ofEpochMilliSeconds(-99999).epochMilliSeconds)
        assertEquals(-100000L, DateTime.ofEpochMilliSeconds(-100000).epochMilliSeconds)
    }

    @Test
    fun ofEpochSeconds() {
        assertEquals(100000000L, DateTime.ofEpochSeconds(100000).epochMilliSeconds)
        assertEquals(99999000L, DateTime.ofEpochSeconds(99999).epochMilliSeconds)
        assertEquals(0L, DateTime.ofEpochSeconds(0).epochMilliSeconds)
        assertEquals(-99999000L, DateTime.ofEpochSeconds(-99999).epochMilliSeconds)
        assertEquals(-100000000L, DateTime.ofEpochSeconds(-100000).epochMilliSeconds)
    }

    @Test
    fun now() {
        val expected = Calendar.getInstance().timeInMillis
        val actual = DateTime.now().epochMilliSeconds
        assertTrue((expected - actual).let { it >= -100 && it < 100 })
    }

    @Test
    fun getEpochSeconds() {
        assertEquals(1000L, DateTime.ofEpochMilliSeconds(1000000).epochSeconds)
        assertEquals(999L, DateTime.ofEpochMilliSeconds(999999).epochSeconds)
        assertEquals(0L, DateTime.ofEpochMilliSeconds(0).epochSeconds)
        assertEquals(-1000L, DateTime.ofEpochMilliSeconds(-999999).epochSeconds)
        assertEquals(-1000L, DateTime.ofEpochMilliSeconds(-1000000).epochSeconds)
    }

    @Test
    fun atZoneTimeZone() {
        DateTime.ofEpochMilliSeconds(0).atZone(TimeZone.GMT)
            .let { actual ->
                assertEquals(0L, actual.epochMilliSeconds)
                assertEquals("GMT", actual.timeZone.id)
            }
        DateTime.ofEpochMilliSeconds(10000).atZone(TimeZone.GMT)
            .let { actual ->
                assertEquals(10000L, actual.epochMilliSeconds)
                assertEquals("GMT", actual.timeZone.id)
            }
        DateTime.ofEpochMilliSeconds(0).atZone(TimeZone.of("Asia/Tokyo"))
            .let { actual ->
                assertEquals(0L, actual.epochMilliSeconds)
                assertEquals("Asia/Tokyo", actual.timeZone.id)
            }
        DateTime.ofEpochMilliSeconds(10000).atZone(TimeZone.of("Asia/Tokyo"))
            .let { actual ->
                assertEquals(10000L, actual.epochMilliSeconds)
                assertEquals("Asia/Tokyo", actual.timeZone.id)
            }
    }

    @Test
    fun plus() {
        assertEquals(
            110L,
            (DateTime.ofEpochMilliSeconds(10L) + TimeDuration.ofTickCounts(100L)).epochMilliSeconds
        )
        assertEquals(
            0L,
            (DateTime.ofEpochMilliSeconds(100L) + TimeDuration.ofTickCounts(-100L)).epochMilliSeconds
        )
        assertEquals(
            -90L,
            (DateTime.ofEpochMilliSeconds(-100L) + TimeDuration.ofTickCounts(10L)).epochMilliSeconds
        )
    }

    @Test
    fun minusDateTime() {
        assertEquals(
            90L,
            (DateTime.ofEpochMilliSeconds(100L) - DateTime.ofEpochMilliSeconds(10L)).tickCounts
        )
        assertEquals(
            0L,
            (DateTime.ofEpochMilliSeconds(100L) - DateTime.ofEpochMilliSeconds(100L)).tickCounts
        )
        assertEquals(
            -90L,
            (DateTime.ofEpochMilliSeconds(10L) - DateTime.ofEpochMilliSeconds(100L)).tickCounts
        )
    }

    @Test
    fun minusTimeDuration() {
        assertEquals(
            90L,
            (DateTime.ofEpochMilliSeconds(100L) - TimeDuration.ofTickCounts(10L)).epochMilliSeconds
        )
        assertEquals(
            0L,
            (DateTime.ofEpochMilliSeconds(100L) - TimeDuration.ofTickCounts(100L)).epochMilliSeconds
        )
        assertEquals(
            -90L,
            (DateTime.ofEpochMilliSeconds(10L) - TimeDuration.ofTickCounts(100L)).epochMilliSeconds
        )
    }

    @Test
    fun plusMilliSeconds() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds + value,
                    dateTime.plusMilliSeconds(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun plusSeconds() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds + value * 1000,
                    dateTime.plusSeconds(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun plusMinutes() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds + value * 1000 * 60,
                    dateTime.plusMinutes(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun plusHours() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds + value * 1000 * 60 * 60,
                    dateTime.plusHours(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun plusDays() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds + value * 1000 * 60 * 60 * 24,
                    dateTime.plusDays(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun plusWeeks() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds + value * 1000 * 60 * 60 * 24 * 7,
                    dateTime.plusWeeks(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun minusMilliSeconds() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds - value,
                    dateTime.minusMilliSeconds(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun minusSeconds() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds - value * 1000,
                    dateTime.minusSeconds(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun minusMinutes() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds - value * 1000 * 60,
                    dateTime.minusMinutes(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun minusHours() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds - value * 1000 * 60 * 60,
                    dateTime.minusHours(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun minusDays() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds - value * 1000 * 60 * 60 * 24,
                    dateTime.minusDays(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun minusWeeks() {
        arrayOf(
            DateTime.ofEpochMilliSeconds(10L),
            DateTime.ofEpochMilliSeconds(0L),
            DateTime.ofEpochMilliSeconds(-10L)
        ).forEach { dateTime ->
            arrayOf(10L, 0L, -10L).forEach { value ->
                assertEquals(
                    dateTime.epochMilliSeconds - value * 1000 * 60 * 60 * 24 * 7,
                    dateTime.minusWeeks(value).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun atStartOfDay() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00"),
            TimeZone.of("GMT-01:00")
        ).forEach { timeZone ->
            val expected =
                ZonedDateTime.of(2020, Month.APRIL, 20, 0, 0, 0, 0, timeZone).toDateTime()
            arrayOf(
                ZonedDateTime.of(2020, Month.APRIL, 20, 0, 0, 0, 0, timeZone).toDateTime(),
                ZonedDateTime.of(2020, Month.APRIL, 20, 23, 59, 59, 59, timeZone).toDateTime()
            ).forEach { actual ->
                assertEquals(
                    expected.epochMilliSeconds,
                    actual.atStartOfDay(timeZone).epochMilliSeconds
                )
            }
        }
    }

    @Test
    fun compareTo() {
        val dateTime1 = DateTime.ofEpochMilliSeconds(-1000L)
        val dateTime2 = DateTime.ofEpochMilliSeconds(0L)
        val dateTime3 = DateTime.ofEpochMilliSeconds(1000L)
        assertEquals(Sign.ZERO, dateTime1.compareTo(dateTime1).toSign())
        assertEquals(Sign.NEGATIVE, dateTime1.compareTo(dateTime2).toSign())
        assertEquals(Sign.NEGATIVE, dateTime1.compareTo(dateTime3).toSign())
        assertEquals(Sign.POSITIVE, dateTime2.compareTo(dateTime1).toSign())
        assertEquals(Sign.ZERO, dateTime2.compareTo(dateTime2).toSign())
        assertEquals(Sign.NEGATIVE, dateTime2.compareTo(dateTime3).toSign())
        assertEquals(Sign.POSITIVE, dateTime3.compareTo(dateTime1).toSign())
        assertEquals(Sign.POSITIVE, dateTime3.compareTo(dateTime2).toSign())
        assertEquals(Sign.ZERO, dateTime3.compareTo(dateTime3).toSign())
    }

    @Test
    fun equalsDateTime() {
        assertEquals(
            false,
            DateTime.ofEpochMilliSeconds(100) == DateTime.ofEpochMilliSeconds(10)
        )
        assertEquals(
            true,
            DateTime.ofEpochMilliSeconds(100) == DateTime.ofEpochMilliSeconds(100)
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            "DateTime(dateTime='1970-01-01T02:46:40Z', epochMilliSeconds='10000000')",
            DateTime.ofEpochMilliSeconds(10000000).toString()
        )
        assertEquals(
            "DateTime(dateTime='1970-01-01T02:46:39.999Z', epochMilliSeconds='9999999')",
            DateTime.ofEpochMilliSeconds(9999999).toString()
        )
        assertEquals(
            "DateTime(dateTime='1970-01-01T00:00:00Z', epochMilliSeconds='0')",
            DateTime.ofEpochMilliSeconds(0).toString()
        )
        assertEquals(
            "DateTime(dateTime='1969-12-31T21:13:20.001Z', epochMilliSeconds='-9999999')",
            DateTime.ofEpochMilliSeconds(-9999999).toString()
        )
        assertEquals(
            "DateTime(dateTime='1969-12-31T21:13:20Z', epochMilliSeconds='-10000000')",
            DateTime.ofEpochMilliSeconds(-10000000).toString()
        )
    }
}