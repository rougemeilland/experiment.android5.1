package com.palmtreesoftware.experimentandroid5_1

// equivalent to 'floor(this.toDouble() / divisor).toInt()'
fun Int.divideFloor(divisor: Int): Int =
    this.div(divisor).let { q ->
        if (divisor >= 0) {
            if (this.rem(divisor) >= 0)
                q
            else
                q - 1
        } else {
            if (this.rem(divisor) <= 0)
                q
            else
                q - 1
        }
    }

// equivalent to 'u - u.divideFloor(v) * v'
fun Int.modulo(divisor: Int): Int =
    this.rem(divisor).let { r ->
        if (divisor >= 0) {
            if (r < 0)
                r + divisor
            else
                r
        } else {
            if (r > 0)
                r + divisor
            else
                r
        }
    }

// equivalent to 'floor(this.toDouble() / divisor).toLong()'
fun Long.divideFloor(divisor: Long): Long =
    this.div(divisor).let { q ->
        if (divisor >= 0) {
            if (this.rem(divisor) >= 0)
                q
            else
                q - 1
        } else {
            if (this.rem(divisor) <= 0)
                q
            else
                q - 1
        }
    }

// equivalent to 'u - u.divideFloor(v) * v'
fun Long.modulo(divisor: Long): Long =
    this.rem(divisor).let { r ->
        if (divisor >= 0) {
            if (r < 0)
                r + divisor
            else
                r
        } else {
            if (r > 0)
                r + divisor
            else
                r
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

operator fun Int.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)

operator fun Long.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)

operator fun Double.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)
