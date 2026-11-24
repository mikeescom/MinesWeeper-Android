package com.msmikeescom.minesweeper.ui.view

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.msmikeescom.minesweeper.R

/**
 * Shows elapsed time as MM:SS. The start time is supplied by the caller (and kept in the
 * ViewModel) so the display survives the view being recreated. Ticking stops automatically when
 * the view is detached, so no timer outlives its screen.
 */
class GameChronometerView : ConstraintLayout {

    companion object {
        private const val MAX_SECONDS = 99 * 60 + 59
        private const val TICK_MILLIS = 250L
    }

    private lateinit var tensMinutes: ImageView
    private lateinit var unitsMinutes: ImageView
    private lateinit var tensSeconds: ImageView
    private lateinit var unitsSeconds: ImageView

    private var startedAt: Long? = null

    private val ticker = object : Runnable {
        override fun run() {
            val start = startedAt ?: return
            show(((SystemClock.elapsedRealtime() - start) / 1000).toInt())
            postDelayed(this, TICK_MILLIS)
        }
    }

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
        inflate(context, R.layout.game_chronometer_view_layout, this)
        tensMinutes = findViewById(R.id.tens_minutes)
        unitsMinutes = findViewById(R.id.units_minutes)
        tensSeconds = findViewById(R.id.tens_seconds)
        unitsSeconds = findViewById(R.id.units_seconds)
    }

    /** Starts ticking from [startedAtElapsedRealtime], a [SystemClock.elapsedRealtime] value. */
    fun start(startedAtElapsedRealtime: Long) {
        startedAt = startedAtElapsedRealtime
        removeCallbacks(ticker)
        ticker.run()
    }

    /** Stops ticking and shows [seconds]. */
    fun stop(seconds: Int) {
        startedAt = null
        removeCallbacks(ticker)
        show(seconds)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (startedAt != null) {
            removeCallbacks(ticker)
            ticker.run()
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(ticker)
        super.onDetachedFromWindow()
    }

    private fun show(seconds: Int) {
        val clamped = seconds.coerceIn(0, MAX_SECONDS)
        val minutes = clamped / 60
        val secs = clamped % 60
        setImageNumber(tensMinutes, minutes / 10)
        setImageNumber(unitsMinutes, minutes % 10)
        setImageNumber(tensSeconds, secs / 10)
        setImageNumber(unitsSeconds, secs % 10)
    }

    private fun setImageNumber(imageView: ImageView, digit: Int) {
        imageView.setImageResource(DigitDrawables.forDigit(digit))
    }
}
