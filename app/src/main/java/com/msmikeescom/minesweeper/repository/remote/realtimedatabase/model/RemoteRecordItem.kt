package com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class RemoteRecordItem (
    var numberOfMines: Long = 0L,
    var fieldSize: Long = 0L,
    var timeInSeconds: Long = 0L,
    var timeStamp: Long = 0L
)