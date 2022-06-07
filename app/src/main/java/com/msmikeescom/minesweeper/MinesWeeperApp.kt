package com.msmikeescom.minesweeper

import android.app.Application
import android.content.Context

open class MinesWeeperApp : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this.applicationContext
    }
}