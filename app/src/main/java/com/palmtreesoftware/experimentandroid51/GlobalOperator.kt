package com.palmtreesoftware.experimentandroid51

operator fun Int.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)

operator fun Double.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)
