package com.msmikeescom.minesweeper.repository.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msmikeescom.minesweeper.repository.local.database.dao.RecordItemDAO
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem

@Database(entities = [LocalRecordItem::class], version = 1, exportSchema = false)
abstract class MinesWeeperDatabase : RoomDatabase() {
    abstract fun recordItemDAO(): RecordItemDAO

    companion object {
        private const val TAG = "MinesWeeperDatabase"
        private const val DATABASE_NAME = "minesweeperdatabase"
        private var mInstance: MinesWeeperDatabase? = null

        fun instance(context: Context): MinesWeeperDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(context.applicationContext, MinesWeeperDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigrationFrom(1).build()
                Log.d(TAG, "initialized")
            }

            return mInstance!!
        }
    }
}