package com.msmikeescom.minesweeper.repository

import android.content.Context
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

    companion object {
        fun create(context: Context) = MainRepository(
            sharePreferences = MinesWeeperSharedPreferences.instance(context.applicationContext),
            database = MinesWeeperDatabase.instance(context),
            realTimeDatabase = RealTimeDatabase.instance()
        )
    }

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

    suspend fun deleteAllRecords() = withContext(Dispatchers.IO) {
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
    fun addUserListener(userId: String, listener: ChildEventListener) {
        realTimeDatabase.addUserListener(userId, listener)
    }

    fun removeUserListener(userId: String, listener: ChildEventListener) {
        realTimeDatabase.removeUserListener(userId, listener)
    }

    fun addNewRecord(userId: String, remoteRecordItem: RemoteRecordItem): String? =
        realTimeDatabase.addNewRecord(userId, remoteRecordItem)

    fun copyLegacyRecords(fromUserId: String, toUserId: String) {
        realTimeDatabase.copyLegacyRecords(fromUserId, toUserId)
    }
}
