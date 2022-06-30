package com.msmikeescom.minesweeper.utilities

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.util.LruCache

class TypefaceSpan(context: Context, typefaceName: String?) :
    MetricAffectingSpan() {
    private var mTypeface: Typeface?
    override fun updateMeasureState(p: TextPaint) {
        p.typeface = mTypeface
        p.flags = p.flags or Paint.SUBPIXEL_TEXT_FLAG
    }

    override fun updateDrawState(tp: TextPaint) {
        tp.typeface = mTypeface
        tp.flags = tp.flags or Paint.SUBPIXEL_TEXT_FLAG
    }

    companion object {
        private val sTypefaceCache: LruCache<String, Typeface> = LruCache<String, Typeface>(12)
    }

    init {
        mTypeface = sTypefaceCache.get(typefaceName)
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(
                context.applicationContext.assets, String.format("fonts/%s", typefaceName)
            )

            sTypefaceCache.put(typefaceName, mTypeface)
        }
    }
}