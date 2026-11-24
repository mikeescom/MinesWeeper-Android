package com.msmikeescom.minesweeper.repository.local.sharepreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.msmikeescom.minesweeper.utilities.Constants.KEY_CURRENT_USER_ID
import com.msmikeescom.minesweeper.utilities.Constants.KEY_FIELD_SIZE
import com.msmikeescom.minesweeper.utilities.Constants.KEY_NUM_OF_MINES
import com.msmikeescom.minesweeper.utilities.Constants.KEY_USER_NAME
import com.msmikeescom.minesweeper.utilities.Constants.KEY_USER_PHOTO_URL
import com.msmikeescom.minesweeper.utilities.Constants.MAIN_USER_PREFS_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Per-device preferences. Hilt provides it as a singleton backed by [MAIN_USER_PREFS_NAME]. */
@Singleton
class MinesWeeperSharedPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val userPrefs: SharedPreferences =
        context.getSharedPreferences(MAIN_USER_PREFS_NAME, MODE_PRIVATE)

    fun getUserId(): String? {
        return userPrefs.getString(KEY_CURRENT_USER_ID, null)
    }

    fun saveUserId(userId: String) {
        userPrefs.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    fun getFieldSize(): Int? {
        return userPrefs.getInt(KEY_FIELD_SIZE, 0)
    }

    fun saveFieldSize(fieldSize: Int) {
        userPrefs.edit().putInt(KEY_FIELD_SIZE, fieldSize).apply()
    }

    fun getNumberOfMines(): Int? {
        return userPrefs.getInt(KEY_NUM_OF_MINES, 0)
    }

    fun saveNumberOfMines(numOfMines: Int) {
        userPrefs.edit().putInt(KEY_NUM_OF_MINES, numOfMines).apply()
    }

    fun getUserName(): String? {
        return userPrefs.getString(KEY_USER_NAME, null)
    }

    fun saveUserName(userName: String) {
        userPrefs.edit().putString(KEY_USER_NAME, userName).apply()
    }

    fun getUserPhotoUrl(): String? {
        return userPrefs.getString(KEY_USER_PHOTO_URL, null)
    }

    fun saveUserPhotoUrl(photoUrl: String) {
        userPrefs.edit().putString(KEY_USER_PHOTO_URL, photoUrl).apply()
    }
    fun deleteUserInfo() {
        userPrefs.edit().clear().apply()
    }
}