package com.msmikeescom.minesweeper.repository

import com.msmikeescom.minesweeper.repository.local.sharepreferences.MinesWeeperSharedPreferences

class MainRepository(sharePreferences: MinesWeeperSharedPreferences) {
    private val sharePref = sharePreferences

    fun getUserId() = sharePref.getUserId()

    fun getFieldSize() = sharePref.getFieldSize()

    fun getNumberOfMines() = sharePref.getNumberOfMines()

    fun getUserName() = sharePref.getUserName()

    fun getUserPhotoUrl() = sharePref.getUserPhotoUrl()

    fun saveUserId(userId: String) {
        sharePref.saveUserId(userId)
    }

    fun saveFieldSize(fieldSize: Int) {
        sharePref.saveFieldSize(fieldSize)
    }

    fun saveNumberOfMines(numOfMines: Int) {
        sharePref.saveNumberOfMines(numOfMines)
    }

    fun saveUserName(userName: String) {
        sharePref.saveUserName(userName)
    }

    fun saveUserPhotoUrl(userPhotoUrl: String) {
        sharePref.saveUserPhotoUrl(userPhotoUrl)
    }

    fun deleteUserInfo() {
        sharePref.deleteUserInfo()
    }
}