package com.msmikeescom.minesweeper.repository.database.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
open class UserInfoItem (
    @PrimaryKey val userId: String,
    @ColumnInfo(name = "displayName") val displayName: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "photoUrl") val photoUrl: String? = null,
    @ColumnInfo(name = "difficulty") val difficulty: String? = null
)