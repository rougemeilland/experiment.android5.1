package com.palmtreesoftware.experimentandroid5_1

enum class Sign {
    NEGATIVE,
    ZERO,
    POSITIVE,
}

fun Int.toSign(): Sign =
    when {
        this > 0 -> Sign.POSITIVE
        this == 0 -> Sign.ZERO
        else -> Sign.NEGATIVE
    }