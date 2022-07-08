package com.msmikeescom.minesweeper.repository

import com.google.firebase.database.ChildEventListener
import com.msmikeescom.minesweeper.repository.local.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import com.msmikeescom.minesweeper.repository.local.sharepreferences.MinesWeeperSharedPreferences
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.RealTimeDatabase
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class MainRepository(sharePreferences: MinesWeeperSharedPreferences,
                     database: MinesWeeperDatabase,
                     realTimeDatabase: RealTimeDatabase) {

    private val mutex: Mutex = Mutex()
    private val sharePref = sharePreferences
    private val recordItemDAO = database.recordItemDAO()
    private val realTimeDatabase = realTimeDatabase

    // Shared Preferences
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

    // Local database
    suspend fun insertRecord(item: LocalRecordItem) = withContext(Dispatchers.IO) {
        mutex.withLock {
            recordItemDAO.insertRecordItem(item)
        }
    }

    suspend fun deleteRecord() = withContext(Dispatchers.IO) {
        mutex.withLock {
            recordItemDAO.deleteRecordItems()
        }
    }

    suspend fun loadAllRecords() = withContext(Dispatchers.IO) {
        return@withContext recordItemDAO.loadAllRecordItems()
    }

    suspend fun loadAllItemIds() = withContext(Dispatchers.IO) {
        recordItemDAO.loadAllItemIds()
    }

    // Remote database
    fun setUserListener(userId: String, listener: ChildEventListener) {
        realTimeDatabase.setUserListener(userId, listener)
    }

    fun setNewRecord(userId: String, itemId: String, remoteRecordItem: RemoteRecordItem) {
        realTimeDatabase.setNewRecord(userId, itemId, remoteRecordItem)
    }
}