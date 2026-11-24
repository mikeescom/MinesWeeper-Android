package com.msmikeescom.minesweeper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Hilt's entry point: it builds the app-wide dependency graph. */
@HiltAndroidApp
class MinesWeeperApp : Application()
