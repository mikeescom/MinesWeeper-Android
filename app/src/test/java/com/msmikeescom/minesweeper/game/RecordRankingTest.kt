package com.msmikeescom.minesweeper.game

import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RecordRankingTest {

    private fun record(id: String, mines: Long, fieldSize: Long, seconds: Long, timeStamp: Long = 0) =
        LocalRecordItem().apply {
            itemId = id
            numberOfMines = mines
            this.fieldSize = fieldSize
            timeInSeconds = seconds
            this.timeStamp = timeStamp
        }

    @Test
    fun denserGamesRankFirstThenFasterTimes() {
        val ranked = RecordRanking.rank(listOf(
            record("easy-fast", mines = 10, fieldSize = 200, seconds = 20),
            record("hard-slow", mines = 40, fieldSize = 200, seconds = 300),
            record("hard-fast", mines = 40, fieldSize = 200, seconds = 120),
        ))
        assertEquals(listOf("hard-fast", "hard-slow", "easy-fast"), ranked.map { it.itemId })
    }

    @Test
    fun tiesGoToTheNewerRecord() {
        val ranked = RecordRanking.rank(listOf(
            record("older", 10, 100, 30, timeStamp = 1_000),
            record("newer", 10, 100, 30, timeStamp = 2_000),
        ))
        assertEquals(listOf("newer", "older"), ranked.map { it.itemId })
    }

    @Test
    fun zeroFieldSizeDoesNotDivideByZero() {
        assertEquals(0.0, RecordRanking.density(record("bad", 10, 0, 30)), 0.0)
    }
}
