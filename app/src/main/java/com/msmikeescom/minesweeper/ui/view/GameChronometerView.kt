package com.msmikeescom.minesweeper.ui.view

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.msmikeescom.minesweeper.R

class GameChronometerView: ConstraintLayout {

    companion object {
        private const val TAG = "GameChronometerView"
    }

    private var tensMinutes: ImageView? = null
    private var unitsMinutes: ImageView? = null
    private var tensSeconds: ImageView? = null
    private var unitsSeconds: ImageView? = null

    var mChronometerTime = 0
        private set
    var mTimerStarted = false
        private set
    private var mCountDownTimer: CountDownTimer? = null

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
        inflate(context, R.layout.game_chronometer_view_layout, this)
        tensMinutes = findViewById(R.id.tens_minutes)
        unitsMinutes = findViewById(R.id.units_minutes)
        tensSeconds = findViewById(R.id.tens_seconds)
        unitsSeconds = findViewById(R.id.units_seconds)
    }

    fun startTimer() {
        if (mTimerStarted) {
            return
        }
        mTimerStarted = true
        val tensMinutesImageView = findViewById<ImageView>(R.id.tens_minutes)
        val unitsMinutesImageView = findViewById<ImageView>(R.id.units_minutes)
        val tensSecondsImageView = findViewById<ImageView>(R.id.tens_seconds)
        val unitsSecondsImageView = findViewById<ImageView>(R.id.units_seconds)
        mCountDownTimer = object : CountDownTimer(3600000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "millisUntilFinished: $millisUntilFinished")
                Log.d(TAG, "millisSinceStarted: " + (3600000 - millisUntilFinished))
                val millisSinceStarted = 3600000 - millisUntilFinished
                val secondsSinceStarted = (millisSinceStarted - millisSinceStarted % 1000) / 1000
                var seconds: Int
                mChronometerTime = secondsSinceStarted.toInt()
                Log.d(TAG, "Time: $mChronometerTime")
                var minutes: Int = mChronometerTime / 60
                seconds = mChronometerTime - minutes * 60
                if (mChronometerTime % 60 != 0) {
                    val units = seconds % 10
                    seconds /= 10
                    val tens = seconds % 10
                    setImageNumber(unitsSecondsImageView, units)
                    setImageNumber(tensSecondsImageView, tens)
                } else {
                    val units = minutes % 10
                    minutes /= 10
                    val tens = minutes % 10
                    setImageNumber(unitsMinutesImageView, units)
                    setImageNumber(tensMinutesImageView, tens)
                    setImageNumber(unitsSecondsImageView, 0)
                    setImageNumber(tensSecondsImageView, 0)
                }
            }

            override fun onFinish() {
                setImageNumber(unitsMinutesImageView, 0)
                setImageNumber(tensMinutesImageView, 0)
                setImageNumber(unitsSecondsImageView, 0)
                setImageNumber(tensSecondsImageView, 0)
            }
        }
        mCountDownTimer?.start()
    }

    fun stopTimer() {
        if (!mTimerStarted) {
            return
        }
        mTimerStarted = false
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
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