package com.msmikeescom.minesweeper.repository.local.database.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem

@Entity(tableName = "record_items")
open class LocalRecordItem() : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<LocalRecordItem> {
            override fun createFromParcel(parcel: Parcel): LocalRecordItem {
                return LocalRecordItem(parcel)
            }

            override fun newArray(size: Int): Array<LocalRecordItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    @PrimaryKey
    @ColumnInfo(name = "itemId")
    var itemId: String = ""
    @ColumnInfo(name = "numberOfMines")
    var numberOfMines: Long = 0L
    @ColumnInfo(name = "fieldSize")
    var fieldSize: Long = 0L
    @ColumnInfo(name = "timeInSeconds")
    var timeInSeconds: Long = 0L
    @ColumnInfo(name = "timeStamp")
    var timeStamp: Long = 0L

    constructor(parcel: Parcel) : this() {
        itemId = parcel.readString() ?: ""
        numberOfMines = parcel.readLong()
        fieldSize = parcel.readLong()
        timeInSeconds = parcel.readLong()
        timeStamp = parcel.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemId)
        parcel.writeLong(numberOfMines)
        parcel.writeLong(fieldSize)
        parcel.writeLong(timeInSeconds)
        parcel.writeLong(timeStamp)
    }

    fun getRemoteRecordItem(): RemoteRecordItem {
        return RemoteRecordItem (
            this.numberOfMines,
            this.fieldSize,
            this.timeInSeconds,
            this.timeStamp
        )
    }
}