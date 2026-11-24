package com.msmikeescom.minesweeper.utilities

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Draws edge to edge, pads [content] clear of the system bars, and hides the status bar and
 * action bar. The status bar comes back temporarily on a swipe from the top edge.
 */
fun AppCompatActivity.setUpFullScreen(content: View) {
    enableEdgeToEdge()
    ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
    WindowCompat.getInsetsController(window, window.decorView).apply {
        hide(WindowInsetsCompat.Type.statusBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    supportActionBar?.hide()
}
