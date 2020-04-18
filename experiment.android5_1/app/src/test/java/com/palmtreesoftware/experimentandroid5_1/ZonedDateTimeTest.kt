package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ZonedDateTimeTest {

    @Test
    fun ofUntilMilliSecond() {
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

        // month のテスト
        Month.values().forEach { month ->
            assertEquals(
                "ZonedDateTime(dateTime='2020-%02d-01T00:00:00.000Z', timeZone='GMT')".format(month.value),
                ZonedDateTime.of(
                    2020,
                    month,
                    1,
                    0,
                    0,
                    0,
                    0,
                    TimeZone.GMT
                ).toString()
            )
        }

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
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.004Z', timeZone='GMT')",
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
            TimeZone.GMT
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.004Z', timeZone='GMT')",
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
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.004Z', timeZone='GMT')",
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
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.004Z', timeZone='UTC')",
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
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.004+01:00', timeZone='Europe/London')",
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
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.004+09:00', timeZone='Asia/Tokyo')",
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
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.004Z', timeZone='GMT')",
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
            TimeZone.GMT
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.004Z', timeZone='GMT')",
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
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.004Z', timeZone='GMT')",
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
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.004Z', timeZone='UTC')",
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
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.004Z', timeZone='Europe/London')",
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
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.004+09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1578618123004 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
    }

    @Test
    fun ofUntilSecond() {
        // dayOfMonth の範囲テスト (下限)
        assertThrows(IllegalArgumentException::class.java) {
            ZonedDateTime.of(
                2019,
                Month.APRIL,
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
                TimeZone.GMT
            )
        }

        // month のテスト
        Month.values().forEach { month ->
            assertEquals(
                "ZonedDateTime(dateTime='2020-%02d-01T00:00:00.000Z', timeZone='GMT')".format(month.value),
                ZonedDateTime.of(
                    2020,
                    month,
                    1,
                    0,
                    0,
                    0,
                    TimeZone.GMT
                ).toString()
            )
        }

        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            TimeZone.of("GMT+00:00")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586480523000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            TimeZone.GMT
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586480523000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            TimeZone.of("GMT")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586480523000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            TimeZone.of("UTC")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.000Z', timeZone='UTC')",
                it.toString()
            )
            assertEquals("UTC", it.timeZone.id)
            assertEquals(1586480523000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            TimeZone.of("Europe/London")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.000+01:00', timeZone='Europe/London')",
                it.toString()
            )
            assertEquals("Europe/London", it.timeZone.id)
            assertEquals(1586480523000 - 60 * 60 * 1000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            1,
            2,
            3,
            TimeZone.of("Asia/Tokyo")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T01:02:03.000+09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1586480523000 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            TimeZone.of("GMT+00:00")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578618123000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            TimeZone.GMT
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578618123000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            TimeZone.of("GMT")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578618123000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            TimeZone.of("UTC")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.000Z', timeZone='UTC')",
                it.toString()
            )
            assertEquals("UTC", it.timeZone.id)
            assertEquals(1578618123000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            TimeZone.of("Europe/London")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.000Z', timeZone='Europe/London')",
                it.toString()
            )
            assertEquals("Europe/London", it.timeZone.id)
            assertEquals(1578618123000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            1,
            2,
            3,
            TimeZone.of("Asia/Tokyo")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T01:02:03.000+09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1578618123000 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
    }

    @Test
    fun ofUntilDay() {
        // dayOfMonth の範囲テスト (下限)
        assertThrows(IllegalArgumentException::class.java) {
            ZonedDateTime.of(
                2019,
                Month.APRIL,
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
                TimeZone.GMT
            ).dayOfMonth
        )

        // month のテスト
        Month.values().forEach { month ->
            assertEquals(
                "ZonedDateTime(dateTime='2020-%02d-01T00:00:00.000Z', timeZone='GMT')".format(month.value),
                ZonedDateTime.of(
                    2020,
                    month,
                    1,
                    TimeZone.GMT
                ).toString()
            )
        }

        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            TimeZone.of("GMT+00:00")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T00:00:00.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586476800000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            TimeZone.GMT
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T00:00:00.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586476800000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            TimeZone.of("GMT")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T00:00:00.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1586476800000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            TimeZone.of("UTC")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T00:00:00.000Z', timeZone='UTC')",
                it.toString()
            )
            assertEquals("UTC", it.timeZone.id)
            assertEquals(1586476800000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            TimeZone.of("Europe/London")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T00:00:00.000+01:00', timeZone='Europe/London')",
                it.toString()
            )
            assertEquals("Europe/London", it.timeZone.id)
            assertEquals(1586476800000 - 60 * 60 * 1000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.APRIL,
            10,
            TimeZone.of("Asia/Tokyo")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-04-10T00:00:00.000+09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1586476800000 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            TimeZone.of("GMT+00:00")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T00:00:00.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578614400000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            TimeZone.GMT
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T00:00:00.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578614400000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            TimeZone.of("GMT")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T00:00:00.000Z', timeZone='GMT')",
                it.toString()
            )
            assertEquals("GMT", it.timeZone.id)
            assertEquals(1578614400000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            TimeZone.of("UTC")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T00:00:00.000Z', timeZone='UTC')",
                it.toString()
            )
            assertEquals("UTC", it.timeZone.id)
            assertEquals(1578614400000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            TimeZone.of("Europe/London")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T00:00:00.000Z', timeZone='Europe/London')",
                it.toString()
            )
            assertEquals("Europe/London", it.timeZone.id)
            assertEquals(1578614400000, it.epochMilliSeconds)
        }
        ZonedDateTime.of(
            2020,
            Month.JANUARY,
            10,
            TimeZone.of("Asia/Tokyo")
        ).also {
            assertEquals(
                "ZonedDateTime(dateTime='2020-01-10T00:00:00.000+09:00', timeZone='Asia/Tokyo')",
                it.toString()
            )
            assertEquals("Asia/Tokyo", it.timeZone.id)
            assertEquals(1578614400000 - 9 * 60 * 60 * 1000, it.epochMilliSeconds)
        }
    }

    @Test
    fun ofDateTimeTimeZone() {
        DateTime.fromEpochMilliSeconds(1586480523004).also { dateTime ->
            ZonedDateTime.of(dateTime, TimeZone.GMT).also { zonedDateTime ->
                assertEquals(
                    "ZonedDateTime(dateTime='2020-04-10T01:02:03.004Z', timeZone='GMT')",
                    zonedDateTime.toString()
                )
                assertEquals(2020, zonedDateTime.year)
                assertEquals(Month.APRIL, zonedDateTime.month)
                assertEquals(10, zonedDateTime.dayOfMonth)
                assertEquals(1, zonedDateTime.hour)
                assertEquals(2, zonedDateTime.minute)
                assertEquals(3, zonedDateTime.second)
                assertEquals(4, zonedDateTime.milliSecond)
                assertEquals("GMT", zonedDateTime.timeZone.id)
                assertEquals(1586480523, zonedDateTime.epochSeconds)
                assertEquals(1586480523004, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Asia/Tokyo")).also { zonedDateTime ->
                assertEquals(
                    "ZonedDateTime(dateTime='2020-04-10T10:02:03.004+09:00', timeZone='Asia/Tokyo')",
                    zonedDateTime.toString()
                )
                assertEquals(2020, zonedDateTime.year)
                assertEquals(Month.APRIL, zonedDateTime.month)
                assertEquals(10, zonedDateTime.dayOfMonth)
                assertEquals(10, zonedDateTime.hour)
                assertEquals(2, zonedDateTime.minute)
                assertEquals(3, zonedDateTime.second)
                assertEquals(4, zonedDateTime.milliSecond)
                assertEquals("Asia/Tokyo", zonedDateTime.timeZone.id)
                assertEquals(1586480523, zonedDateTime.epochSeconds)
                assertEquals(1586480523004, zonedDateTime.epochMilliSeconds)
            }
            ZonedDateTime.of(dateTime, TimeZone.of("Pacific/Pago_Pago")).also { zonedDateTime ->
                assertEquals(
                    "ZonedDateTime(dateTime='2020-04-09T14:02:03.004-11:00', timeZone='Pacific/Pago_Pago')",
                    zonedDateTime.toString()
                )
                assertEquals(2020, zonedDateTime.year)
                assertEquals(Month.APRIL, zonedDateTime.month)
                assertEquals(9, zonedDateTime.dayOfMonth)
                assertEquals(14, zonedDateTime.hour)
                assertEquals(2, zonedDateTime.minute)
                assertEquals(3, zonedDateTime.second)
                assertEquals(4, zonedDateTime.milliSecond)
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
                assertEquals(0, zonedDateTime.milliSecond)
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
                assertEquals(0, zonedDateTime.milliSecond)
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
                assertEquals(0, zonedDateTime.milliSecond)
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
                assertEquals(999, zonedDateTime.milliSecond)
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
                assertEquals(999, zonedDateTime.milliSecond)
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
                assertEquals(999, zonedDateTime.milliSecond)
                assertEquals("Pacific/Pago_Pago", zonedDateTime.timeZone.id)
                assertEquals(-1, zonedDateTime.epochSeconds)
                assertEquals(-1, zonedDateTime.epochMilliSeconds)
            }
        }
    }

    @Test
    fun getLengthOfMonthYear() {
        Month.values().crossMap(arrayOf(1995, 1996, 1900, 2000)) { month, year ->
            assertEquals(
                calculateDayOfMonth(year, month),
                ZonedDateTime.getLengthOfMonth(year, month)
            )
        }
    }

    @Test
    fun getLengthOfYearYearMonth() {
        arrayOf(1995, 1996, 1900, 2000).forEach { year ->
            assertEquals(calculateDayOfYear(year), ZonedDateTime.getLengthOfYear(year))
        }
    }

    @Test
    fun getEpochMilliSeconds() {
        TimeZone.GMT.let { timeZone ->
            val offset = 0L
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60 * 60 * 24,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    2,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    1,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    1,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    1,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset - 1,
                ZonedDateTime.of(
                    1969,
                    Month.DECEMBER,
                    31,
                    23,
                    59,
                    59,
                    999,
                    timeZone
                ).epochMilliSeconds
            )
        }
        TimeZone.of("GMT+01:00").let { timeZone ->
            val offset = -1000L * 60 * 60
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60 * 60 * 24,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    2,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    1,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    1,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    1,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset - 1,
                ZonedDateTime.of(
                    1969,
                    Month.DECEMBER,
                    31,
                    23,
                    59,
                    59,
                    999,
                    timeZone
                ).epochMilliSeconds
            )
        }
        TimeZone.of("GMT-01:00").let { timeZone ->
            val offset = +1000L * 60 * 60
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60 * 60 * 24,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    2,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    1,
                    0,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1000,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    1,
                    0,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset + 1,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    1,
                    timeZone
                ).epochMilliSeconds
            )
            assertEquals(
                offset - 1,
                ZonedDateTime.of(
                    1969,
                    Month.DECEMBER,
                    31,
                    23,
                    59,
                    59,
                    999,
                    timeZone
                ).epochMilliSeconds
            )
        }
    }

    @Test
    fun getEpochSeconds() {
        TimeZone.GMT.let { timeZone ->
            val offset = 0L
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60 * 60 * 24,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    2,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    1,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 1,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    1,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    1,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset - 1,
                ZonedDateTime.of(
                    1969,
                    Month.DECEMBER,
                    31,
                    23,
                    59,
                    59,
                    999,
                    timeZone
                ).epochSeconds
            )
        }
        TimeZone.of("GMT+01:00").let { timeZone ->
            val offset = -60L * 60
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60 * 60 * 24,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    2,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    1,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 1,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    1,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    1,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset - 1,
                ZonedDateTime.of(
                    1969,
                    Month.DECEMBER,
                    31,
                    23,
                    59,
                    59,
                    999,
                    timeZone
                ).epochSeconds
            )
        }
        TimeZone.of("GMT-01:00").let { timeZone ->
            val offset = +60L * 60
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60 * 60 * 24,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    2,
                    0,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60 * 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 60,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    1,
                    0,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 1,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    1,
                    0,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset + 0,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    1,
                    timeZone
                ).epochSeconds
            )
            assertEquals(
                offset - 1,
                ZonedDateTime.of(
                    1969,
                    Month.DECEMBER,
                    31,
                    23,
                    59,
                    59,
                    999,
                    timeZone
                ).epochSeconds
            )
        }
    }

    @Test
    fun getYear() {
        arrayOf(1969, 1970, 1971).crossMap(
            arrayOf(
                TimeZone.GMT,
                TimeZone.of("GMT+01:00"),
                TimeZone.of("GMT-01:00")
            )
        ) { year, timeZone ->
            assertEquals(
                year,
                ZonedDateTime.of(year, Month.JANUARY, 1, 0, 0, 0, timeZone).year
            )
            assertEquals(
                year,
                ZonedDateTime.of(year, Month.DECEMBER, 31, 23, 59, 59, timeZone).year
            )
        }
    }

    @Test
    fun getMonth() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(2020).forEach { year ->
                Month.values().forEach { month ->
                    assertEquals(
                        month,
                        ZonedDateTime.of(2020, month, 1, timeZone).month
                    )
                }
            }
        }
    }

    @Test
    fun getDayOfMonth() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(1995, 1996, 1900, 2000).forEach { year ->
                Month.values().forEach { month ->
                    assertEquals(
                        1,
                        ZonedDateTime.of(year, month, 1, timeZone).dayOfMonth
                    )
                    calculateDayOfMonth(year, month)
                        .let { dayOfMonth ->
                            assertEquals(
                                dayOfMonth,
                                ZonedDateTime.of(year, month, dayOfMonth, timeZone).dayOfMonth
                            )
                        }
                }
            }
        }
    }

    @Test
    fun getDayOfWeek() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            DayOfWeek.values().forEach { dayOfWeek ->
                assertEquals(
                    dayOfWeek,
                    ZonedDateTime.of(
                        2020,
                        Month.APRIL,
                        19 + dayOfWeek.value - DayOfWeek.SUNDAY.value,
                        timeZone
                    ).dayOfWeek
                )
            }
        }
    }

    @Test
    fun getDayOfYear() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(1995, 1996, 1900, 2000).forEach { year ->
                Month.values().forEach { month ->
                    val dayOfYear = Month.values()
                        .filter { it < month }
                        .sumBy { calculateDayOfMonth(year, it) } + 1
                    assertEquals(
                        dayOfYear,
                        ZonedDateTime.of(year, month, 1, timeZone).dayOfYear,
                        "dayOfYear($year, ${month.value}, 1)"
                    )
                    calculateDayOfMonth(year, month).let { dayOfMonth ->
                        assertEquals(
                            dayOfYear + dayOfMonth - 1,
                            ZonedDateTime.of(year, month, dayOfMonth, timeZone).dayOfYear,
                            "dayOfYear($year, ${month.value}, $dayOfMonth)"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun getLengthOfMonth() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            Month.values().crossMap(arrayOf(1995, 1996, 1900, 2000)) { month, year ->
                assertEquals(
                    calculateDayOfMonth(year, month),
                    ZonedDateTime.of(year, month, 1, timeZone).lengthOfMonth,
                    "lengthOfMonth($year, ${month.value})"
                )
            }
        }
    }

    @Test
    fun getLengthOfYear() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(1995, 1996, 1900, 2000).forEach { year ->
                assertEquals(
                    calculateDayOfYear(year),
                    ZonedDateTime.of(year, Month.JANUARY, 1, timeZone).lengthOfYear
                )
            }
        }
    }

    @Test
    fun getHour() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:30:00"),
            TimeZone.of("GMT-01:30:00")
        ).forEach { timeZone ->
            arrayOf(0, 1, 11, 12, 13, 22, 23).forEach { hour ->
                assertEquals(
                    hour,
                    ZonedDateTime.of(
                        2020,
                        Month.JANUARY,
                        1,
                        hour,
                        0,
                        0,
                        timeZone
                    ).hour
                )
                assertEquals(
                    hour,
                    ZonedDateTime.of(
                        2019,
                        Month.DECEMBER,
                        31,
                        hour,
                        59,
                        59,
                        timeZone
                    ).hour
                )
            }
        }
    }

    @Test
    fun getMinute() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(0, 1, 30, 58, 59).forEach { minute ->
                assertEquals(
                    minute,
                    ZonedDateTime.of(
                        2020,
                        Month.JANUARY,
                        1,
                        0,
                        minute,
                        0,
                        timeZone
                    ).minute
                )
                assertEquals(
                    minute,
                    ZonedDateTime.of(
                        2019,
                        Month.DECEMBER,
                        31,
                        23,
                        minute,
                        59,
                        timeZone
                    ).minute
                )
            }
        }
    }

    @Test
    fun getSecond() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(0, 1, 30, 58, 59).forEach { second ->
                assertEquals(
                    second,
                    ZonedDateTime.of(
                        2020,
                        Month.JANUARY,
                        1,
                        0,
                        0,
                        second,
                        timeZone
                    ).second
                )
                assertEquals(
                    second,
                    ZonedDateTime.of(
                        2019,
                        Month.DECEMBER,
                        31,
                        23,
                        59,
                        second,
                        timeZone
                    ).second
                )
            }
        }
    }

    @Test
    fun getMilliSecond() {
        arrayOf(
            TimeZone.GMT,
            TimeZone.of("GMT+01:00:00"),
            TimeZone.of("GMT-01:00:00")
        ).forEach { timeZone ->
            arrayOf(0, 1, 500, 998, 999).forEach { milliSecond ->
                assertEquals(
                    milliSecond,
                    ZonedDateTime.of(
                        2020,
                        Month.JANUARY,
                        1,
                        0,
                        0,
                        0,
                        milliSecond,
                        timeZone
                    ).milliSecond
                )
                assertEquals(
                    milliSecond,
                    ZonedDateTime.of(
                        2019,
                        Month.DECEMBER,
                        31,
                        23,
                        59,
                        59,
                        milliSecond,
                        timeZone
                    ).milliSecond
                )
            }
        }
    }

    @Test
    fun toDateTime() {
        arrayOf(TimeZone.GMT, TimeZone.of("GMT+01:00"), TimeZone.of("GMT-01:00"))
            .forEach { timeZone ->
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    timeZone
                ).let {
                    assertEquals(it.epochMilliSeconds, it.toDateTime().epochMilliSeconds)
                }
            }
    }

    @Test
    fun format() {
        arrayOf(
            FormatParameter(
                "1970年01月01日(木曜日)00時00分00.000秒Z",
                "yyyy年MM月dd日(EEEE)HH時mm分ss.SSS秒${ZonedDateTime.timeZoneFormatSpecOfISO8601}",
                TimeZone.GMT,
                java.util.Locale.JAPAN
            ),
            FormatParameter(
                "1970年01月01日(木曜日)00時00分00.000秒+01:00",
                "yyyy年MM月dd日(EEEE)HH時mm分ss.SSS秒${ZonedDateTime.timeZoneFormatSpecOfISO8601}",
                TimeZone.of("GMT+01:00"),
                java.util.Locale.JAPAN
            ),
            FormatParameter(
                "1970年01月01日(木曜日)00時00分00.000秒-01:00",
                "yyyy年MM月dd日(EEEE)HH時mm分ss.SSS秒${ZonedDateTime.timeZoneFormatSpecOfISO8601}",
                TimeZone.of("GMT-01:00"),
                java.util.Locale.JAPAN
            ),
            FormatParameter(
                "Thursday, January 01 1970 at 00:00:00.000Z",
                "EEEE, MMMM dd yyyy 'at' HH:mm:ss.SSS${ZonedDateTime.timeZoneFormatSpecOfISO8601}",
                TimeZone.GMT,
                java.util.Locale.US
            ),
            FormatParameter(
                "Thursday, January 01 1970 at 00:00:00.000+01:00",
                "EEEE, MMMM dd yyyy 'at' HH:mm:ss.SSS${ZonedDateTime.timeZoneFormatSpecOfISO8601}",
                TimeZone.of("GMT+01:00"),
                java.util.Locale.US
            ),
            FormatParameter(
                "Thursday, January 01 1970 at 00:00:00.000-01:00",
                "EEEE, MMMM dd yyyy 'at' HH:mm:ss.SSS${ZonedDateTime.timeZoneFormatSpecOfISO8601}",
                TimeZone.of("GMT-01:00"),
                java.util.Locale.US
            )
        ).forEach { param ->
            ZonedDateTime.of(
                1970,
                Month.JANUARY,
                1,
                0,
                0,
                0,
                param.timeZone
            ).let {
                assertEquals(
                    param.expected,
                    it.format(param.format, param.locale),
                    "format(dateTime=[$it], timeZone='${param.timeZone.id}', locale='${param.locale.displayName}')"
                )
            }
        }
    }

    @Test
    fun testToString() {
        arrayOf(
            Pair(
                "ZonedDateTime(dateTime='1970-01-01T00:00:00.000Z', timeZone='GMT')",
                TimeZone.GMT
            ),
            Pair(
                "ZonedDateTime(dateTime='1970-01-01T00:00:00.000+01:00', timeZone='GMT+01:00')",
                TimeZone.of("GMT+01:00")
            ),
            Pair(
                "ZonedDateTime(dateTime='1970-01-01T00:00:00.000-01:00', timeZone='GMT-01:00')",
                TimeZone.of("GMT-01:00")
            ),
            Pair(
                "ZonedDateTime(dateTime='1970-01-01T00:00:00.000+09:00', timeZone='Asia/Tokyo')",
                TimeZone.of("Asia/Tokyo")
            ),
            Pair(
                "ZonedDateTime(dateTime='1970-01-01T00:00:00.000Z', timeZone='UTC')",
                TimeZone.of("UTC")
            )
        ).forEach { param ->
            assertEquals(
                param.first,
                ZonedDateTime.of(
                    1970,
                    Month.JANUARY,
                    1,
                    0,
                    0,
                    0,
                    param.second
                ).toString()
            )
        }
    }

    private class FormatParameter(
        val expected: String,
        val format: String,
        val timeZone: TimeZone,
        val locale: java.util.Locale
    )

    private fun calculateDayOfMonth(
        year: Int,
        month: Month
    ): Int =
        when (month) {
            Month.JANUARY -> 31
            Month.FEBRUARY -> {
                when {
                    year % 400 == 0 -> 29
                    year % 100 == 0 -> 28
                    year % 4 == 0 -> 29
                    else -> 28
                }
            }
            Month.MARCH -> 31
            Month.APRIL -> 30
            Month.MAY -> 31
            Month.JUNE -> 30
            Month.JULY -> 31
            Month.AUGUST -> 31
            Month.SEPTEMBER -> 30
            Month.OCTOBER -> 31
            Month.NOVEMBER -> 30
            Month.DECEMBER -> 31
        }

    private fun calculateDayOfYear(
        year: Int
    ): Int =
        when {
            year % 400 == 0 -> 366
            year % 100 == 0 -> 365
            year % 4 == 0 -> 366
            else -> 365
        }
}