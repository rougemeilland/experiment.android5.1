package com.palmtreesoftware.experimentandroid5_1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LongRangeSetTest {

    // TODO("パラメタつきテスト これを参考に。 https://qiita.com/opengl-8080/items/efe54204e25f615e322f#%E3%83%A1%E3%82%BD%E3%83%83%E3%83%89%E3%82%92%E3%82%BD%E3%83%BC%E3%82%B9%E3%81%AB%E3%81%99%E3%82%8B")

    @Test
    fun initTest() {
        assertEquals("[]", LongRangeSet().toString())
    }

    @Test
    fun initArrayOfLongTest() {
        assertEquals("[1]", LongRangeSet(1L).toString())
        assertEquals("[1]", LongRangeSet(1L, 1L).toString())
        assertEquals("[1..2]", LongRangeSet(1L, 2L).toString())
        assertEquals("[1, 3]", LongRangeSet(1L, 3L).toString())
        assertEquals("[1, 3, 5]", LongRangeSet(1L, 3L, 5L).toString())
    }

    @Test
    fun initArrayOfLongRangeTest() {
        assertEquals("[]", LongRangeSet(LongRange.EMPTY).toString())
        assertEquals("[1]", LongRangeSet(1L..1L).toString())
        assertEquals("[1..2]", LongRangeSet(1L..2L).toString())
        assertEquals("[1..4]", LongRangeSet(1L..2L, 3L..4L).toString())
        assertEquals("[1..2, 4..5]", LongRangeSet(1L..2L, 4L..5L).toString())
    }

    @Test
    fun add() {
        assertEquals(
            "(true, [1])",
            LongRangeSet().let { set ->
                set.add(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [0..1])",
            LongRangeSet(1L).also { set ->
                set.add(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1])", LongRangeSet(1L).also { set ->
                set.add(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..2])",
            LongRangeSet(1L).also { set ->
                set.add(2L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [-1, 1..3])",
            LongRangeSet(1L, 3L).also { set ->
                set.add(-1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [0..3])",
            LongRangeSet(1L, 3L).also { set ->
                set.add(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..3])",
            LongRangeSet(1L..3L).also { set ->
                set.add(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..3])",
            LongRangeSet(1L..3L).also { set ->
                set.add(2L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..3])",
            LongRangeSet(1L..3L).also { set ->
                set.add(3L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..4])",
            LongRangeSet(1L..3L).also { set ->
                set.add(4L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..3, 5])",
            LongRangeSet(1L..3L).also { set ->
                set.add(5L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [1..6])",
            LongRangeSet(1L..3L, 5L..6L).also { set ->
                set.add(4L).let { Pair(it, set) }
            }.toString()
        )
    }

    @Test
    fun addAllCollection() {
        assertEquals(
            "(false, [])",
            LongRangeSet().also { set ->
                set.addAll(listOf()).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [1..3])",
            LongRangeSet().also { set ->
                set.addAll(listOf(1, 3, 2)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [-2..-1, 1..2])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(-2, -1)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [-1..2])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(-1, 0)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [0..2])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(0, 1)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1..2])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(1, 2)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [1..3])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(2, 3)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [1..4])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(3, 4)).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(true, [1..2, 4..5])",
            LongRangeSet(1L..2L).also { set ->
                set.addAll(listOf(4, 5)).let { Pair(it, set) }
            }.toString()
        )
    }

    @Test
    fun addAllLongRangeSet() {
        TODO("テストコードを書く")
    }

    @Test
    fun addAllClosedRange() {
        TODO("テストコードを書く")
    }

    @Test
    fun clear() {
        assertEquals(
            "[]",
            LongRangeSet().also { set ->
                set.clear()
            }.toString()
        )

        assertEquals(
            "[]",
            LongRangeSet(1L..2L).also { set ->
                set.clear()
            }.toString()
        )
    }

    @Test
    fun iteratorTest() {
        LongRangeSet().iterator().also { set ->
            set.iterator().also { iterator ->
                assertEquals(false, iterator.hasNext())
            }
        }

        LongRangeSet(1L).also { set ->
            set.iterator().also { iterator ->
                assertEquals(true, iterator.hasNext())
                assertEquals(1L, iterator.next())
                assertEquals("[]", iterator.remove().let { set.toString() })
                assertEquals(false, iterator.hasNext())
            }
        }

        LongRangeSet(1L..2L).also { set ->
            set.iterator().also { iterator ->
                assertEquals(true, iterator.hasNext())
                assertEquals(1L, iterator.next())
                assertEquals("[2]", iterator.remove().let { set.toString() })
                assertEquals(true, iterator.hasNext())
                assertEquals(2L, iterator.next())
                assertEquals("[]", iterator.remove().let { set.toString() })
                assertEquals(false, iterator.hasNext())
            }
        }

        LongRangeSet(4L..5L, 1L..2L).also { set ->
            set.iterator().also { iterator ->
                assertEquals(true, iterator.hasNext())
                assertEquals(1L, iterator.next())
                assertEquals("[2, 4..5]", iterator.remove().let { set.toString() })
                assertEquals(true, iterator.hasNext())
                assertEquals(2L, iterator.next())
                assertEquals("[4..5]", iterator.remove().let { set.toString() })
                assertEquals(false, iterator.hasNext())
                assertEquals(true, iterator.hasNext())
                assertEquals(4L, iterator.next())
                assertEquals("[5]", iterator.remove().let { set.toString() })
                assertEquals(false, iterator.hasNext())
                assertEquals(true, iterator.hasNext())
                assertEquals(5L, iterator.next())
                assertEquals("[5]", iterator.remove().let { set.toString() })
                assertEquals(false, iterator.hasNext())
            }
        }
    }

    @Test
    fun remove() {
        assertEquals(
            "(false, [])",
            LongRangeSet().also { set ->
                set.remove(1L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1])",
            LongRangeSet(1L).also { set ->
                set.remove(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [])",
            LongRangeSet(1L).also { set ->
                set.remove(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1])",
            LongRangeSet(1L).also { set ->
                set.remove(2L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1..2])",
            LongRangeSet(1L..2L).also { set ->
                set.remove(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [2])",
            LongRangeSet(1L..2L).also { set ->
                set.remove(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1])",
            LongRangeSet(1L..2L).also { set ->
                set.remove(2L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..2])",
            LongRangeSet(1L..2L).also { set ->
                set.remove(3L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1..3])",
            LongRangeSet(1L..3L).also { set ->
                set.remove(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [2..3])",
            LongRangeSet(1L..3L).also { set ->
                set.remove(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1, 3])",
            LongRangeSet(1L..3L).also { set ->
                set.remove(2L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..2])",
            LongRangeSet(1L..3L).also { set ->
                set.remove(3L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..3])",
            LongRangeSet(1L..3L).also { set ->
                set.remove(4L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1..4])",
            LongRangeSet(1L..4L).also { set ->
                set.remove(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [2..4])",
            LongRangeSet(1L..4L).also { set ->
                set.remove(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1, 3..4])",
            LongRangeSet(1L..4L).also { set ->
                set.remove(2L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..2, 4])",
            LongRangeSet(1L..4L).also { set ->
                set.remove(3L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..3])",
            LongRangeSet(1L..4L).also { set ->
                set.remove(4L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..4])",
            LongRangeSet(1L..4L).also { set ->
                set.remove(5L).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1..2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(0L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(1L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(2L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(3L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..2, 5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(4L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [1..2, 4])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(5L).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(false, [1..2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.remove(6L).let { Pair(it, set) }
            }.toString()
        )
    }

    @Test
    fun removeAllCollection() {
        assertEquals(
            "(false, [1..2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.removeAll(listOf()).let { Pair(it, set) }
            }.toString()
        )

        assertEquals(
            "(false, [1..2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.removeAll(listOf(-1L, 0L)).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.removeAll(listOf(0L, 1L)).let { Pair(it, set) }
            }.toString()
        )
        assertEquals(
            "(true, [2, 4..5])",
            LongRangeSet(1L..2L, 4L..5L).also { set ->
                set.removeAll(listOf(0L, 1L)).let { Pair(it, set) }
            }.toString()
        )
    }

    @Test
    fun removeAllLongRangeSet() {
        TODO("テストを書く")
    }

    @Test
    fun removeAllClosedRange() {
        TODO("テストを書く")
    }

    @Test
    fun retainAllCollection() {
        TODO("テストを書く")
    }

    @Test
    fun retainAllLongRangeSet() {
        TODO("テストを書く")
    }

    @Test
    fun retainAllClosedRange() {
        TODO("テストを書く")
    }

    @Test
    fun getSize() {
        TODO("テストを書く")
    }

    @Test
    fun contains() {
        TODO("テストを書く")
    }

    @Test
    fun containsAllCollection() {
        TODO("テストを書く")
    }

    @Test
    fun containsAllLongRangeSet() {
        TODO("テストを書く")
    }

    @Test
    fun containsAllClosedRange() {
        TODO("テストを書く")
    }

    @Test
    fun isEmpty() {
        TODO("テストを書く")
    }
}