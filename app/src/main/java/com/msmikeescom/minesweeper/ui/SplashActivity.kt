package com.msmikeescom.minesweeper.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.msmikeescom.minesweeper.ui.MineFieldActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MineFieldActivity::class.java)
        startActivity(intent)
        finish()
    }
}