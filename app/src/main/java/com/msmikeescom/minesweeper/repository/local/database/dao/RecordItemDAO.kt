package com.msmikeescom.minesweeper.repository.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem

@Dao
interface RecordItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecordItem(item: LocalRecordItem)

    @Query("select * from record_items")
    fun loadAllRecordItems(): List<LocalRecordItem>

    @Query("select itemId from record_items")
    fun loadAllItemIds(): List<String>

    @Query("delete from record_items")
    fun deleteRecordItems()
}