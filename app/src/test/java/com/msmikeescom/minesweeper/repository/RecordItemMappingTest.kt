package com.msmikeescom.minesweeper.repository

import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RecordItemMappingTest {

    @Test
    fun remoteRecordRoundTripsThroughLocal() {
        val remote = RemoteRecordItem(numberOfMines = 15, fieldSize = 240, timeInSeconds = 73, timeStamp = 1_700_000_000_000)
        val local = LocalRecordItem.fromRemote("-NabcPushKey", remote)
        assertEquals("-NabcPushKey", local.itemId)
        assertEquals(remote, local.getRemoteRecordItem())
    }
}
