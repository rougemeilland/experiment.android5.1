package com.palmtreesoftware.experimentandroid5_1

import android.location.Address
import android.location.Location
import org.json.JSONArray
import org.json.JSONObject

// divisor >= 0 : equivalent to 'floor(this.toDouble() / divisor).toLong()'
// divisor < 0 : equivalent to 'ceil(this.toDouble() / divisor).toLong()'
fun Int.divideRound(divisor: Int): Int =
// 計算コストを下げるために this.rem(divisor) の計算を削除できないか？
// => どのような計算方法を採用するにしろ、「 this が divisorで割り切れるかどうか 」の判別は不可避なので、
//    this.rem(divisor) は削除できない。
    this.div(divisor).let { q ->
        when {
            this.rem(divisor) >= 0 -> q
            divisor >= 0 -> q - 1
            else -> q + 1
        }
    }

// equivalent to 'u - u.divideRound(v) * v'
fun Int.modulo(divisor: Int): Int =
    this.rem(divisor).let { r ->
        when {
            r >= 0 -> r
            divisor >= 0 -> r + divisor
            else -> r - divisor
        }
    }

// divisor >= 0 : equivalent to 'floor(this.toDouble() / divisor).toLong()'
// divisor < 0 : equivalent to 'ceil(this.toDouble() / divisor).toLong()'
fun Long.divideRound(divisor: Long): Long =
// 計算コストを下げるために this.rem(divisor) の計算を削除できないか？
// => どのような計算方法を採用するにしろ、「 this が divisorで割り切れるかどうか 」の判別は不可避なので、
//    this.rem(divisor) は削除できない。
    this.div(divisor).let { q ->
        when {
            this.rem(divisor) >= 0 -> q
            divisor >= 0 -> q - 1
            else -> q + 1
        }
    }

// equivalent to 'u - u.divideRound(v) * v'
fun Long.modulo(divisor: Long): Long =
    this.rem(divisor).let { r ->
        when {
            r >= 0 -> r
            divisor >= 0 -> r + divisor
            else -> r - divisor
        }
    }

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Iterable<ELEMENT_TYPE_1>.crossMap(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Iterable<ELEMENT_TYPE_1>.crossMap(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Array<ELEMENT_TYPE_1>.crossMap(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Array<ELEMENT_TYPE_1>.crossMap(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Iterable<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Iterable<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Array<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Array<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> =
    this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()

fun JSONArray.toIterableOfJSONObject(): Iterable<JSONObject> =
    (0 until length()).map { index -> getJSONObject(index) }

operator fun Long.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)

internal abstract class CharacterMap {
    companion object {
        private val fromZenkakuToHankaku: Map<Char, Char> by lazy {
            arrayOf(
                Pair('　', ' '),
                Pair('！', '!'),
                Pair('”', '\"'),
                Pair('＃', '#'),
                Pair('＄', '$'),
                Pair('％', '%'),
                Pair('＆', '&'),
                Pair('’', '\''),
                Pair('（', '('),
                Pair('）', ')'),
                Pair('＊', '*'),
                Pair('＋', '+'),
                Pair('，', ','),
                Pair('‐', '-'),
                Pair('．', '.'),
                Pair('／', '/'),
                Pair('０', '0'),
                Pair('１', '1'),
                Pair('２', '2'),
                Pair('３', '3'),
                Pair('４', '4'),
                Pair('５', '5'),
                Pair('６', '6'),
                Pair('７', '7'),
                Pair('８', '8'),
                Pair('９', '9'),
                Pair('：', ':'),
                Pair('；', ';'),
                Pair('＜', '<'),
                Pair('＝', '='),
                Pair('＞', '>'),
                Pair('？', '?'),
                Pair('＠', '@'),
                Pair('Ａ', 'A'),
                Pair('Ｂ', 'B'),
                Pair('Ｃ', 'C'),
                Pair('Ｄ', 'D'),
                Pair('Ｅ', 'E'),
                Pair('Ｆ', 'F'),
                Pair('Ｇ', 'G'),
                Pair('Ｈ', 'H'),
                Pair('Ｉ', 'I'),
                Pair('Ｊ', 'J'),
                Pair('Ｋ', 'K'),
                Pair('Ｌ', 'L'),
                Pair('Ｍ', 'M'),
                Pair('Ｎ', 'N'),
                Pair('Ｏ', 'O'),
                Pair('Ｐ', 'P'),
                Pair('Ｑ', 'Q'),
                Pair('Ｒ', 'R'),
                Pair('Ｓ', 'S'),
                Pair('Ｔ', 'T'),
                Pair('Ｕ', 'U'),
                Pair('Ｖ', 'V'),
                Pair('Ｗ', 'W'),
                Pair('Ｘ', 'X'),
                Pair('Ｙ', 'Y'),
                Pair('Ｚ', 'Z'),
                Pair('［', '['),
                Pair('＼', '\\'),
                Pair('］', ']'),
                Pair('＾', '^'),
                Pair('＿', '_'),
                Pair('‘', '`'),
                Pair('ａ', 'a'),
                Pair('ｂ', 'b'),
                Pair('ｃ', 'c'),
                Pair('ｄ', 'd'),
                Pair('ｅ', 'e'),
                Pair('ｆ', 'f'),
                Pair('ｇ', 'g'),
                Pair('ｈ', 'h'),
                Pair('ｉ', 'i'),
                Pair('ｊ', 'j'),
                Pair('ｋ', 'k'),
                Pair('ｌ', 'l'),
                Pair('ｍ', 'm'),
                Pair('ｎ', 'n'),
                Pair('ｏ', 'o'),
                Pair('ｐ', 'p'),
                Pair('ｑ', 'q'),
                Pair('ｒ', 'r'),
                Pair('ｓ', 's'),
                Pair('ｔ', 't'),
                Pair('ｕ', 'u'),
                Pair('ｖ', 'v'),
                Pair('ｗ', 'w'),
                Pair('ｘ', 'x'),
                Pair('ｙ', 'y'),
                Pair('ｚ', 'z'),
                Pair('｛', '{'),
                Pair('｜', '|'),
                Pair('｝', '}'),
                Pair('～', '~')
            ).toMap()
        }

        fun fromZenkakuToHankaku(s: String): String =
            StringBuilder().also { sb ->
                s.forEach { sourceChar ->
                    sb.append(fromZenkakuToHankaku(sourceChar))
                }
            }.toString()

        private fun fromZenkakuToHankaku(c: Char): Char =
            fromZenkakuToHankaku[c] ?: c
    }
}


fun String.toHankaku(): String =
    CharacterMap.fromZenkakuToHankaku(this)

fun Address.distanceTo(other: Address): Double? =
    FloatArray(3)
        .also {
            Location.distanceBetween(
                this.latitude,
                this.longitude,
                other.latitude,
                other.longitude,
                it
            )
        }.run {
            if (size < 1)
                null
            else
                this[0].toDouble()
        }

