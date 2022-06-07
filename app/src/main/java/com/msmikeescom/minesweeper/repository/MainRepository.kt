package com.msmikeescom.minesweeper.repository

import com.msmikeescom.minesweeper.repository.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(database: MinesWeeperDatabase) {
    private val userInfoDAO = database.userInfoDAO()

    suspend fun getUserInfo(userId: String) = withContext(Dispatchers.IO) {
        userInfoDAO.getUserInfo(userId)
    }

    suspend fun insertItem(item: UserInfoItem) {
        withContext(Dispatchers.IO) {
            userInfoDAO.insertItem(item)
        }
    }
}