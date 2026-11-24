package com.msmikeescom.minesweeper.ui.view

import androidx.annotation.DrawableRes
import com.msmikeescom.minesweeper.R

/** Drawables for the 7-segment style digits used by the counter and chronometer. */
internal object DigitDrawables {
    private val digits = intArrayOf(
        R.drawable.zero_digit,
        R.drawable.one_digit,
        R.drawable.two_digit,
        R.drawable.three_digit,
        R.drawable.four_digit,
        R.drawable.five_digit,
        R.drawable.six_digit,
        R.drawable.seven_digit,
        R.drawable.eight_digit,
        R.drawable.nine_digit,
    )

    @DrawableRes
    fun forDigit(digit: Int): Int = digits[digit.coerceIn(0, 9)]
}
