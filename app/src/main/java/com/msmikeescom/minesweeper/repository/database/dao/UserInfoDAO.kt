package com.msmikeescom.minesweeper.repository.database.dao

import androidx.room.*
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem

@Dao
interface UserInfoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: UserInfoItem)

    @Query("update user_info set fieldSize = :fieldSize where userId = :userId")
    fun updateFieldSize(userId: String, fieldSize: Int)

    @Query("update user_info set numberOfMines = :numberOfMines where userId = :userId")
    fun updateNumberOfMines(userId: String, numberOfMines: Int)

    @Query("select * from user_info")
    fun getUserInfo() : UserInfoItem?

    @Query("delete from user_info")
    fun deleteAll()
}