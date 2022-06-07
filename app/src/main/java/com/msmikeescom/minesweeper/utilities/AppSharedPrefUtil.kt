package com.msmikeescom.minesweeper.utilities

import android.content.Context
import android.content.SharedPreferences
import com.msmikeescom.minesweeper.MinesWeeperApp

object AppSharedPrefUtil {

    private const val MINESWEEPER_PREFERENCES = "MINESWEEPER_PREFERENCES"
    private const val USER_ID = "USER_ID"
    private lateinit var sharedPreferences: SharedPreferences

    private fun getSharedPreferences(context: Context): SharedPreferences {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(MINESWEEPER_PREFERENCES, Context.MODE_PRIVATE)
        }
        return sharedPreferences
    }

    fun getUserId(): String? {
        return getSharedPreferences(MinesWeeperApp.appContext).getString(USER_ID, null)
    }

    fun setUserId(userId: String) {
        getSharedPreferences(MinesWeeperApp.appContext).edit().putString(USER_ID, userId).apply()
    }

}