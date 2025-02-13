package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import kotlin.math.floor

enum class SixteenDirections(private val descriptionResourceId: Int) {
    N(R.string.sixteen_directions_description_N),
    NNE(R.string.sixteen_directions_description_NNE),
    NE(R.string.sixteen_directions_description_NE),
    ENE(R.string.sixteen_directions_description_ENE),
    E(R.string.sixteen_directions_description_E),
    ESE(R.string.sixteen_directions_description_ESE),
    SE(R.string.sixteen_directions_description_SE),
    SSE(R.string.sixteen_directions_description_SSE),
    S(R.string.sixteen_directions_description_S),
    SSW(R.string.sixteen_directions_description_SSW),
    SW(R.string.sixteen_directions_description_SW),
    WSW(R.string.sixteen_directions_description_WSW),
    W(R.string.sixteen_directions_description_W),
    WNW(R.string.sixteen_directions_description_WNW),
    NW(R.string.sixteen_directions_description_NW),
    NNW(R.string.sixteen_directions_description_NNW)
    ;

    fun getDescription(context: Context): String =
        context.getString(descriptionResourceId)

    companion object {
        private val directions: Array<SixteenDirections> =
            arrayOf(
                N,
                NNE,
                NNE,
                NE,
                NE,
                ENE,
                ENE,
                E,
                E,
                ESE,
                ESE,
                SE,
                SE,
                SSE,
                SSE,
                S,
                S,
                SSW,
                SSW,
                SW,
                SW,
                WSW,
                WSW,
                W,
                W,
                WNW,
                WNW,
                NW,
                NW,
                NNW,
                NNW,
                N
            )

        fun ofDegrees(degree: Double): SixteenDirections =
            directions[floor(degree / 360 * 32).toInt().modulo(32)]
    }
}