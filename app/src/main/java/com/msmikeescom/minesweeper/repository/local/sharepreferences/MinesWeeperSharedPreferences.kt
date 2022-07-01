package com.msmikeescom.minesweeper.repository.local.sharepreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.msmikeescom.minesweeper.utilities.Constants.KEY_CURRENT_USER_ID
import com.msmikeescom.minesweeper.utilities.Constants.KEY_FIELD_SIZE
import com.msmikeescom.minesweeper.utilities.Constants.KEY_NUM_OF_MINES
import com.msmikeescom.minesweeper.utilities.Constants.KEY_USER_NAME
import com.msmikeescom.minesweeper.utilities.Constants.KEY_USER_PHOTO_URL
import com.msmikeescom.minesweeper.utilities.Constants.MAIN_USER_PREFS_NAME

class MinesWeeperSharedPreferences {

    companion object {
        private val TAG = "MinesWeeperSharedPreferences"
        private var instance: MinesWeeperSharedPreferences? = null
        private var userPrefs: SharedPreferences? = null

        fun instance(context: Context): MinesWeeperSharedPreferences {
            if (instance == null) {
                instance = MinesWeeperSharedPreferences()
                Log.d(TAG, "initialized")
            }

            userPrefs = context.getSharedPreferences(MAIN_USER_PREFS_NAME ,MODE_PRIVATE)

            return instance!!
        }
    }

    fun getUserId(): String? {
        return userPrefs?.getString(KEY_CURRENT_USER_ID, null)
    }

    fun saveUserId(userId: String) {
        userPrefs?.edit()?.putString(KEY_CURRENT_USER_ID, userId)?.apply()
    }

    fun getFieldSize(): Int? {
        return userPrefs?.getInt(KEY_FIELD_SIZE, 0)
    }

    fun saveFieldSize(fieldSize: Int) {
        userPrefs?.edit()?.putInt(KEY_FIELD_SIZE, fieldSize)?.apply()
    }

    fun getNumberOfMines(): Int? {
        return userPrefs?.getInt(KEY_NUM_OF_MINES, 0)
    }

    fun saveNumberOfMines(numOfMines: Int) {
        userPrefs?.edit()?.putInt(KEY_NUM_OF_MINES, numOfMines)?.apply()
    }

    fun getUserName(): String? {
        return userPrefs?.getString(KEY_USER_NAME, null)
    }

    fun saveUserName(userName: String) {
        userPrefs?.edit()?.putString(KEY_USER_NAME, userName)?.apply()
    }

    fun getUserPhotoUrl(): String? {
        return userPrefs?.getString(KEY_USER_PHOTO_URL, null)
    }

    fun saveUserPhotoUrl(photoUrl: String) {
        userPrefs?.edit()?.putString(KEY_USER_PHOTO_URL, photoUrl)?.apply()
    }
    fun deleteUserInfo() {
        userPrefs?.edit()?.clear()?.apply()
    }
}