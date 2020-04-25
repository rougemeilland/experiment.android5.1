package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.ceil
import kotlin.math.floor

internal class GlobalOperatorKtTest {

    @Test
    fun crossMapIterableIterable() {
        assertEquals(
            "[1+4, 1+5, 1+6, 2+4, 2+5, 2+6, 3+4, 3+5, 3+6]",
            listOf(3, 2, 1).crossMap(listOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf(3, 2, 1).crossMap(listOf<Int>()) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf<Int>().crossMap(listOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapIterableArray() {
        assertEquals(
            "[1+4, 1+5, 1+6, 2+4, 2+5, 2+6, 3+4, 3+5, 3+6]",
            listOf(3, 2, 1).crossMap(arrayOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf(3, 2, 1).crossMap(arrayOf<Int>()) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf<Int>().crossMap(arrayOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapArrayIterable() {
        assertEquals(
            "[1+4, 1+5, 1+6, 2+4, 2+5, 2+6, 3+4, 3+5, 3+6]",
            arrayOf(3, 2, 1).crossMap(listOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf(3, 2, 1).crossMap(listOf<Int>()) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf<Int>().crossMap(listOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapArrayArray() {
        assertEquals(
            "[1+4, 1+5, 1+6, 2+4, 2+5, 2+6, 3+4, 3+5, 3+6]",
            arrayOf(3, 2, 1).crossMap(arrayOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf(3, 2, 1).crossMap(arrayOf<Int>()) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf<Int>().crossMap(arrayOf(4, 5, 6)) { x, y -> "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapNotNullIterableIterable() {
        assertEquals(
            "[2+4, 2+6]",
            listOf(3, 2, 1).crossMapNotNull(
                listOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf(
                3,
                2,
                1
            ).crossMapNotNull(listOf<Int>()) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }
                .sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf<Int>().crossMapNotNull(
                listOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapNotNullIterableArray() {
        assertEquals(
            "[2+4, 2+6]",
            listOf(3, 2, 1).crossMapNotNull(
                arrayOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf(
                3,
                2,
                1
            ).crossMapNotNull(arrayOf<Int>()) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }
                .sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            listOf<Int>().crossMapNotNull(
                arrayOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapNotNullArrayIterable() {
        assertEquals(
            "[2+4, 2+6]",
            arrayOf(3, 2, 1).crossMapNotNull(
                listOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf(
                3,
                2,
                1
            ).crossMapNotNull(listOf<Int>()) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }
                .sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf<Int>().crossMapNotNull(
                listOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun crossMapNotNullArrayArray() {
        assertEquals(
            "[2+4, 2+6]",
            arrayOf(3, 2, 1).crossMapNotNull(
                arrayOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf(
                3,
                2,
                1
            ).crossMapNotNull(arrayOf<Int>()) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }
                .sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
        assertEquals(
            "[]",
            arrayOf<Int>().crossMapNotNull(
                arrayOf(
                    4,
                    5,
                    6
                )
            ) { x, y -> if (x % 2 != 0 || y % 2 != 0) null else "$x+$y" }.sorted()
                .joinToString(prefix = "[", separator = ", ", postfix = "]")
        )
    }

    @Test
    fun divideRoundInt() {
        arrayOf(11, 10, 9, 0, -9, -10, -11).crossMap(arrayOf(10, -10)) { u, v ->
            assertEquals(
                if (v >= 0)
                    floor(u.toDouble().div(v)).toInt()
                else
                    ceil(u.toDouble().div(v)).toInt(),
                u.divideRound(v),
                "$u.divideRound($v)"
            )
        }
    }

    @Test
    fun moduloInt() {
        arrayOf(11, 10, 9, 0, -9, -10, -11).crossMap(arrayOf(10, -10)) { u, v ->
            assertEquals(
                u - (if (v >= 0)
                    floor(u.toDouble().div(v)).toInt()
                else
                    ceil(u.toDouble().div(v)).toInt()) * v,
                u.modulo(v),
                "$u.modulo($v)"
            )
        }
    }

    @Test
    fun divideFloorLong() {
        arrayOf(11L, 10L, 9L, 0L, -9L, -10L, -11L).crossMap(arrayOf(10L, -10L)) { u, v ->
            assertEquals(
                if (v >= 0)
                    floor(u.toDouble().div(v)).toLong()
                else
                    ceil(u.toDouble().div(v)).toLong(),
                u.divideRound(v),
                "$u.divideRound($v)"
            )
        }
    }

    @Test
    fun moduloLong() {
        arrayOf(11L, 10L, 9L, 0L, -9L, -10L, -11L).crossMap(arrayOf(10L, -10L)) { u, v ->
            assertEquals(
                u - (
                        if (v >= 0)
                            floor(u.toDouble().div(v)).toLong()
                        else
                            ceil(u.toDouble().div(v)).toLong()
                        ) * v,
                u.modulo(v),
                "$u.modulo($v)"
            )
        }
    }

    @Test
    fun timesTimeDuration() {
        assertEquals(-3000L, (-3L * TimeDuration.ofTickCounts(1000)).tickCounts)
        assertEquals(0L, (0L * TimeDuration.ofTickCounts(1000)).tickCounts)
        assertEquals(3000L, (3L * TimeDuration.ofTickCounts(1000)).tickCounts)
    }

    @Test
    fun toHankaku() {
        assertEquals("ああ !\"#$%&'嗚呼", "ああ　!”＃＄％＆’嗚呼".toHankaku())
        assertEquals("ああ()*+,-./嗚呼", "ああ（）＊＋，‐．／嗚呼".toHankaku())
        assertEquals("ああ01234567嗚呼", "ああ０１２３４５６７嗚呼".toHankaku())
        assertEquals("ああ89:;<=>?嗚呼", "ああ８９：；＜＝＞？嗚呼".toHankaku())
        assertEquals("ああ@ABCDEFG嗚呼", "ああ＠ＡＢＣＤＥＦＧ嗚呼".toHankaku())
        assertEquals("ああHIJKLMNO嗚呼", "ああＨＩＪＫＬＭＮＯ嗚呼".toHankaku())
        assertEquals("ああPQRSTUVW嗚呼", "ああＰＱＲＳＴＵＶＷ嗚呼".toHankaku())
        assertEquals("ああXYZ[\\]^_嗚呼", "ああＸＹＺ[＼]＾＿嗚呼".toHankaku())
        assertEquals("ああ`abcdefg嗚呼", "ああ‘ａｂｃｄｅｆｇ嗚呼".toHankaku())
        assertEquals("ああhijklmno嗚呼", "ああｈｉｊｋｌｍｎｏ嗚呼".toHankaku())
        assertEquals("ああpqrstuvw嗚呼", "ああｐｑｒｓｔｕｖｗ嗚呼".toHankaku())
        assertEquals("ああxyz{|}~嗚呼", "ああｘｙｚ｛｜｝～嗚呼".toHankaku())

        assertEquals("ああ !\"#$%&'嗚呼", "ああ !\"#$%&'嗚呼".toHankaku())
        assertEquals("ああ()*+,-./嗚呼", "ああ()*+,‐./嗚呼".toHankaku())
        assertEquals("ああ01234567嗚呼", "ああ01234567嗚呼".toHankaku())
        assertEquals("ああ89:;<=>?嗚呼", "ああ89:;<=>?嗚呼".toHankaku())
        assertEquals("ああ@ABCDEFG嗚呼", "ああ@ABCDEFG嗚呼".toHankaku())
        assertEquals("ああHIJKLMNO嗚呼", "ああHIJKLMNO嗚呼".toHankaku())
        assertEquals("ああPQRSTUVW嗚呼", "ああPQRSTUVW嗚呼".toHankaku())
        assertEquals("ああXYZ[\\]^_嗚呼", "ああXYZ[\\]^_嗚呼".toHankaku())
        assertEquals("ああ`abcdefg嗚呼", "ああ‘abcdefg嗚呼".toHankaku())
        assertEquals("ああhijklmno嗚呼", "ああhijklmno嗚呼".toHankaku())
        assertEquals("ああpqrstuvw嗚呼", "ああpqrstuvw嗚呼".toHankaku())
        assertEquals("ああxyz{|}~嗚呼", "ああxyz{|}～嗚呼".toHankaku())
    }
}