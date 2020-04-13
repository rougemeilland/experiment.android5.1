package com.palmtreesoftware.experimentandroid5_1

operator fun Int.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)

operator fun Double.times(multiplicand: TimeDuration): TimeDuration =
    multiplicand.times(this)
