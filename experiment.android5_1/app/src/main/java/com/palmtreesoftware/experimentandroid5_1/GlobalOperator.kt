package com.palmtreesoftware.experimentandroid5_1

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
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Iterable<ELEMENT_TYPE_1>.crossMap(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Array<ELEMENT_TYPE_1>.crossMap(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE> Array<ELEMENT_TYPE_1>.crossMap(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.map { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Iterable<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Iterable<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Array<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Iterable<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

// 返却される要素の順番は保証されないので注意
// ※実装を見れば順番がどうなるかはわかるが、今後のアップデートも考慮して順番は保証したくない
fun <ELEMENT_TYPE_1, ELEMENT_TYPE_2, RESULT_ELEMENT_TYPE : Any> Array<ELEMENT_TYPE_1>.crossMapNotNull(
    other: Array<ELEMENT_TYPE_2>,
    transform: (ELEMENT_TYPE_1, ELEMENT_TYPE_2) -> RESULT_ELEMENT_TYPE?
): Iterable<RESULT_ELEMENT_TYPE> {
    return this.map { element1 ->
        other.mapNotNull { element2 ->
            transform(element1, element2)
        }
    }.flatten()
}

fun JSONArray.toIterableOfJSONObject(): Iterable<JSONObject> =
    (0 until length()).map { index -> getJSONObject(index) }

operator fun Long.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)
