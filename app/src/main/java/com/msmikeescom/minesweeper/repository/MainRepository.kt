package com.msmikeescom.minesweeper.repository

import com.msmikeescom.minesweeper.repository.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(database: MinesWeeperDatabase) {
    private val userInfoDAO = database.userInfoDAO()

    suspend fun getUserInfo() = withContext(Dispatchers.IO) {
        userInfoDAO.getUserInfo()
    }

    suspend fun insertUser(item: UserInfoItem) {
        withContext(Dispatchers.IO) {
            userInfoDAO.insertItem(item)
        }
    }

    suspend fun updateFieldSize(userId: String, fieldSize: Int) {
        withContext(Dispatchers.IO) {
            userInfoDAO.updateFieldSize(userId, fieldSize)
        }
    }

    suspend fun updateNumberOfMines(userId: String, numberOfMines: Int) {
        withContext(Dispatchers.IO) {
            userInfoDAO.updateNumberOfMines(userId, numberOfMines)
        }
    }

    suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            userInfoDAO.deleteAll()
        }
    }
}