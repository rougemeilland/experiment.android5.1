package com.palmtreesoftware.experimentandroid5_1

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows

class ZonedDateTimeTest {

    @Test
    fun of() {
        // dayOfMonth の範囲テスト (下限)
        assertThrows(IllegalArgumentException::class.java) {
            ZonedDateTime.of(
                2019,
                Month.APRIL,
                0,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            )
        }

        // dayOfMonth の範囲テスト (30日の月の上限)
        assertThrows(IllegalArgumentException::class.java)
        {
            ZonedDateTime.of(
                2019,
                Month.APRIL,
                31,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            )
        }
        // dayOfMonth の範囲テスト (31日の月の上限)
        assertThrows(
            IllegalArgumentException::
            class.java
        )
        {
            ZonedDateTime.of(
                2019,
                Month.JANUARY,
                32,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            )
        }
        // dayOfMonth の範囲テスト (28日の月の上限)
        assertThrows(
            IllegalArgumentException::
            class.java
        )
        {
            ZonedDateTime.of(
                2019,
                Month.FEBRUARY,
                29,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            )
        }
        // dayOfMonth の範囲テスト (うるう年の2月29日が正しく認識できること)
        assertEquals(
            29,
            ZonedDateTime.of(
                2020,
                Month.FEBRUARY,
                29,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).dayOfMonth
        )
        // hour の範囲テスト (下限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                -1,
                0,
                0,
                0,
                TimeZone.GMT
            )
        }
        // hour の範囲テスト (上限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                25,
                0,
                0,
                0,
                TimeZone.GMT
            )
        }
        // minute の範囲テスト (下限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                -1,
                0,
                0,
                TimeZone.GMT
            )
        }
        // minute の範囲テスト (上限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                60,
                0,
                0,
                TimeZone.GMT
            )
        }
        // second の範囲テスト (下限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                0,
                -1,
                0,
                TimeZone.GMT
            )
        }
        // second の範囲テスト (上限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                0,
                60,
                0,
                TimeZone.GMT
            )
        }
        // milliSecond の範囲テスト (下限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                0,
                0,
                -1,
                TimeZone.GMT
            )
        }
        // milliSecond の範囲テスト (上限)
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                0,
                0,
                1000,
                TimeZone.GMT
            )
        }

        // month のテスト (JANUARY)
        assertEquals(
            "DateTime(dateTime='2020/01/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.JANUARY,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (FEBRUARY)
        assertEquals(
            "DateTime(dateTime='2020/02/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.FEBRUARY,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (MARCH)
        assertEquals(
            "DateTime(dateTime='2020/03/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.MARCH,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (APRIL)
        assertEquals(
            "DateTime(dateTime='2020/04/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.APRIL,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (MAY)
        assertEquals(
            "DateTime(dateTime='2020/05/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.MAY,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (JUNE)
        assertEquals(
            "DateTime(dateTime='2020/06/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.JUNE,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (JULY)
        assertEquals(
            "DateTime(dateTime='2020/07/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.JULY,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (AUGUST)
        assertEquals(
            "DateTime(dateTime='2020/08/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.AUGUST,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (SEPTEMBER)
        assertEquals(
            "DateTime(dateTime='2020/09/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.SEPTEMBER,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (OCTOBER)
        assertEquals(
            "DateTime(dateTime='2020/10/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.OCTOBER,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (NOVEMBER)
        assertEquals(
            "DateTime(dateTime='2020/11/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.NOVEMBER,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        // month のテスト (DECEMBER)
        assertEquals(
            "DateTime(dateTime='2020/12/01 00:00:00.000 Z', timeZone='GMT')",
            ZonedDateTime.of(
                2020,
                Month.DECEMBER,
                1,
                0,
                0,
                0,
                0,
                TimeZone.GMT
            ).toString()
        )

        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("GMT+00:00")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/04/10 01:02:03.004 Z', timeZone='${
                Platform.sdK26Depended(
                    { "GMT" },
                    { "GMT+00:00" }
                )}')",
                it.toString()
            )
            assertEquals("GMT+00:00", it.timeZone.id)
            assertEquals(1586480523004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            4,
            TimeZone.GMT
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/04/10 01:02:03.004 Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586480523004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("GMT")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/04/10 01:02:03.004 Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586480523004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("UTC")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/04/10 01:02:03.004 Z', timeZone='UTC')",
                it.toString()
            )
            assertEquals("UTC", it.timeZone.id)
            assertEquals(1586480523004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("Europe/London")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/04/10 01:02:03.004 +01:00', timeZone='Europe/London')",
                it.toString()
            )
            assertEquals("Europe/London", it.timeZone.id)
            assertEquals(1586480523004 - 60 * 60 * 1000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("Asia/Tokyo")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/04/10 01:02:03.004 +09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1586480523004 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("GMT+00:00")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/01/10 01:02:03.004 Z', timeZone='${
                Platform.sdK26Depended(
                    { "GMT" },
                    { "GMT+00:00" }
                )}')",
                it.toString()
            )
            assertEquals("GMT+00:00", it.timeZone.id)
            assertEquals(1578618123004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            4,
            TimeZone.GMT
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/01/10 01:02:03.004 Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578618123004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("GMT")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/01/10 01:02:03.004 Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578618123004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("UTC")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/01/10 01:02:03.004 Z', timeZone='UTC')",
                it.toString()
            )
            assertEquals("UTC", it.timeZone.id)
            assertEquals(1578618123004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("Europe/London")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/01/10 01:02:03.004 Z', timeZone='Europe/London')",
                it.toString()
            )
            assertEquals("Europe/London", it.timeZone.id)
            assertEquals(1578618123004, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            4,
            TimeZone.of("Asia/Tokyo")
        ).also {
            assertEquals(
                "DateTime(dateTime='2020/01/10 01:02:03.004 +09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1578618123004 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
    }

    @Test
    fun ofDateTime() {
        DateTime.fromEpochMilliSeconds(1586480523004).also { dateTime ->
            ZonedDateTime.of(dateTime, TimeZone.GMT).also { zonedDateTime ->
                assertEquals(
                    "DateTime(dateTime='2020/04/10 01:02:03.004 Z', timeZone='GMT')",
                    zonedDateTime.toString()
                )
                assertEquals(2020, zonedDateTime.year)
                assertEquals(Month.APRIL, zonedDateTime.month)
                assertEquals(10, zonedDateTime.dayOfMonth)
                assertEquals(1, zonedDateTime.hour)
                assertEquals(2, zonedDateTime.minute)
                assertEquals(3, zonedDateTime.second)
                assertEquals(4, zonedDateTime.millSecond)
                assertEquals("GMT", zonedDateTime.timeZone.id)
                assertEquals(1586480523, zonedDateTime.epochSeconds)
                assertEquals(1586480523004, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Asia/Tokyo")).also { zonedDateTime ->
                assertEquals(
                    "DateTime(dateTime='2020/04/10 10:02:03.004 +09:00', timeZone='Asia/Tokyo')",
                    zonedDateTime.toString()
                )
                assertEquals(2020, zonedDateTime.year)
                assertEquals(Month.APRIL, zonedDateTime.month)
                assertEquals(10, zonedDateTime.dayOfMonth)
                assertEquals(10, zonedDateTime.hour)
                assertEquals(2, zonedDateTime.minute)
                assertEquals(3, zonedDateTime.second)
                assertEquals(4, zonedDateTime.millSecond)
                assertEquals("Asia/Tokyo", zonedDateTime.timeZone.id)
                assertEquals(1586480523, zonedDateTime.epochSeconds)
                assertEquals(1586480523004, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Pacific/Pago_Pago")).also { zonedDateTime ->
                assertEquals(
                    "DateTime(dateTime='2020/04/09 14:02:03.004 -11:00', timeZone='Pacific/Pago_Pago')",
                    zonedDateTime.toString()
                )
                assertEquals(2020, zonedDateTime.year)
                assertEquals(Month.APRIL, zonedDateTime.month)
                assertEquals(9, zonedDateTime.dayOfMonth)
                assertEquals(14, zonedDateTime.hour)
                assertEquals(2, zonedDateTime.minute)
                assertEquals(3, zonedDateTime.second)
                assertEquals(4, zonedDateTime.millSecond)
                assertEquals("Pacific/Pago_Pago", zonedDateTime.timeZone.id)
                assertEquals(1586480523, zonedDateTime.epochSeconds)
                assertEquals(1586480523004, zonedDateTime.epochMilliSeconds)
            }
        }
        DateTime.fromEpochMilliSeconds(0).also { dateTime ->
            ZonedDateTime.of(dateTime, TimeZone.GMT).also { zonedDateTime ->
                assertEquals(1970, zonedDateTime.year)
                assertEquals(Month.JANUARY, zonedDateTime.month)
                assertEquals(1, zonedDateTime.dayOfMonth)
                assertEquals(0, zonedDateTime.hour)
                assertEquals(0, zonedDateTime.minute)
                assertEquals(0, zonedDateTime.second)
                assertEquals(0, zonedDateTime.millSecond)
                assertEquals("GMT", zonedDateTime.timeZone.id)
                assertEquals(0, zonedDateTime.epochSeconds)
                assertEquals(0, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Asia/Tokyo")).also { zonedDateTime ->
                assertEquals(1970, zonedDateTime.year)
                assertEquals(Month.JANUARY, zonedDateTime.month)
                assertEquals(1, zonedDateTime.dayOfMonth)
                assertEquals(9, zonedDateTime.hour)
                assertEquals(0, zonedDateTime.minute)
                assertEquals(0, zonedDateTime.second)
                assertEquals(0, zonedDateTime.millSecond)
                assertEquals("Asia/Tokyo", zonedDateTime.timeZone.id)
                assertEquals(0, zonedDateTime.epochSeconds)
                assertEquals(0, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Pacific/Pago_Pago")).also { zonedDateTime ->
                assertEquals(1969, zonedDateTime.year)
                assertEquals(Month.DECEMBER, zonedDateTime.month)
                assertEquals(31, zonedDateTime.dayOfMonth)
                assertEquals(13, zonedDateTime.hour)
                assertEquals(0, zonedDateTime.minute)
                assertEquals(0, zonedDateTime.second)
                assertEquals(0, zonedDateTime.millSecond)
                assertEquals("Pacific/Pago_Pago", zonedDateTime.timeZone.id)
                assertEquals(0, zonedDateTime.epochSeconds)
                assertEquals(0, zonedDateTime.epochMilliSeconds)
            }
        }
        DateTime.fromEpochMilliSeconds(-1).also { dateTime ->
            ZonedDateTime.of(dateTime, TimeZone.GMT).also { zonedDateTime ->
                assertEquals(1969, zonedDateTime.year)
                assertEquals(Month.DECEMBER, zonedDateTime.month)
                assertEquals(31, zonedDateTime.dayOfMonth)
                assertEquals(23, zonedDateTime.hour)
                assertEquals(59, zonedDateTime.minute)
                assertEquals(59, zonedDateTime.second)
                assertEquals(999, zonedDateTime.millSecond)
                assertEquals("GMT", zonedDateTime.timeZone.id)
                assertEquals(-1, zonedDateTime.epochSeconds)
                assertEquals(-1, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Asia/Tokyo")).also { zonedDateTime ->
                assertEquals(1970, zonedDateTime.year)
                assertEquals(Month.JANUARY, zonedDateTime.month)
                assertEquals(1, zonedDateTime.dayOfMonth)
                assertEquals(8, zonedDateTime.hour)
                assertEquals(59, zonedDateTime.minute)
                assertEquals(59, zonedDateTime.second)
                assertEquals(999, zonedDateTime.millSecond)
                assertEquals("Asia/Tokyo", zonedDateTime.timeZone.id)
                assertEquals(-1, zonedDateTime.epochSeconds)
                assertEquals(-1, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Pacific/Pago_Pago")).also { zonedDateTime ->
                assertEquals(1969, zonedDateTime.year)
                assertEquals(Month.DECEMBER, zonedDateTime.month)
                assertEquals(31, zonedDateTime.dayOfMonth)
                assertEquals(12, zonedDateTime.hour)
                assertEquals(59, zonedDateTime.minute)
                assertEquals(59, zonedDateTime.second)
                assertEquals(999, zonedDateTime.millSecond)
                assertEquals("Pacific/Pago_Pago", zonedDateTime.timeZone.id)
                assertEquals(-1, zonedDateTime.epochSeconds)
                assertEquals(-1, zonedDateTime.epochMilliSeconds)
            }
        }
    }

    @Test
    fun getEpochSeconds() {
        
    }

    @Test
    fun getEpochMilliSeconds() {
    }

    @Test
    fun getYear() {
    }

    @Test
    fun getMonth() {
        //TODO("Monthを全検査")
    }

    @Test
    fun getDayOfMonth() {
    }

    @Test
    fun getDayOfWeek() {
        //TODO("DayOfWeekを全検査")
    }

    @Test
    fun getHour() {
    }

    @Test
    fun getMinute() {
    }

    @Test
    fun getSecond() {
    }

    @Test
    fun getMillSecond() {
    }

    @Test
    fun toDateTime() {
    }

    @Test
    fun format() {
    }

    @Test
    fun testToString() {
    }
}