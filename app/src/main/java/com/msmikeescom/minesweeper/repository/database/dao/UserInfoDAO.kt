package com.msmikeescom.minesweeper.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem

@Dao
interface UserInfoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: UserInfoItem)

    @Query("select * from user_info where userId = :userId")
    fun getUserInfo(userId: String) : UserInfoItem?

    @Query("delete from user_info")
    fun deleteAll()
}