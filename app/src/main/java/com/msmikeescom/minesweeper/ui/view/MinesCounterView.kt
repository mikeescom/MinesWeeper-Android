package com.msmikeescom.minesweeper.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.msmikeescom.minesweeper.R

class MinesCounterView : ConstraintLayout {

    private lateinit var hundredsImageView: ImageView
    private lateinit var tensImageView: ImageView
    private lateinit var unitsImageView: ImageView

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.mines_counter_view_layout, this)
        hundredsImageView = findViewById(R.id.counter_hundreds)
        tensImageView = findViewById(R.id.counter_tens)
        unitsImageView = findViewById(R.id.counter_units)
    }

    /** Shows [number], clamped to the 0..999 the three digits can display. */
    fun updateCounter(number: Int) {
        val value = number.coerceIn(0, 999)
        unitsImageView.setImageResource(DigitDrawables.forDigit(value % 10))
        tensImageView.setImageResource(DigitDrawables.forDigit(value / 10 % 10))
        hundredsImageView.setImageResource(DigitDrawables.forDigit(value / 100))
    }
}
