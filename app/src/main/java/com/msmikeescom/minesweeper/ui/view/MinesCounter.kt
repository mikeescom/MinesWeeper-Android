package com.msmikeescom.minesweeper.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.msmikeescom.minesweeper.R

class MinesCounter : ConstraintLayout {
    companion object {
        private const val TAG = "MinesCounter"
    }

    private var hundredsImageView: ImageView? = null
    private var tensImageView: ImageView? = null
    private var unitsImageView: ImageView? = null

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context){
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.mines_counter_layout, this)
        hundredsImageView = findViewById(R.id.counter_hundreds)
        tensImageView = findViewById(R.id.counter_tens)
        unitsImageView = findViewById(R.id.counter_units)
    }

    fun updateCounter(number: Int) {
        var numberTemp = number
        val units = numberTemp % 10
        numberTemp /= 10
        val tens = numberTemp % 10
        numberTemp /= 10
        val hundreds = numberTemp % 10
        unitsImageView?.let { setImageNumber(it, units) }
        tensImageView?.let { setImageNumber(it, tens) }
        hundredsImageView?.let { setImageNumber(it, hundreds) }
    }

    private fun setImageNumber(imageView: ImageView, digit: Int) {
        when (digit) {
            0 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.zero_digit, null))
            1 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.one_digit, null))
            2 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.two_digit, null))
            3 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.three_digit, null))
            4 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.four_digit, null))
            5 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.five_digit, null))
            6 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.six_digit, null))
            7 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.seven_digit, null))
            8 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.eight_digit, null))
            9 -> imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.nine_digit, null))
        }
    }
}