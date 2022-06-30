package com.msmikeescom.minesweeper.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msmikeescom.minesweeper.repository.database.dao.UserInfoDAO
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem
import org.slf4j.LoggerFactory

@Database(entities = [UserInfoItem::class], version = 1, exportSchema = false)
abstract class MinesWeeperDatabase : RoomDatabase() {
    abstract fun userInfoDAO(): UserInfoDAO

    companion object {
        private val LOGGER = LoggerFactory.getLogger("MinesWeeperDatabase")
        private const val DATABASE_NAME = "minesweeperdatabase"
        private var mInstance: MinesWeeperDatabase? = null

        /**
         * @param context [Context]
         */
        fun instance(context: Context): MinesWeeperDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(context.applicationContext, MinesWeeperDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigrationFrom(1).build()
                LOGGER.debug("initialized")
            }

            return mInstance!!
        }
    }
}