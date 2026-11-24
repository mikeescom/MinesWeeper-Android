package com.msmikeescom.minesweeper.game

import com.msmikeescom.minesweeper.game.Board.CellState
import com.msmikeescom.minesweeper.game.Board.Position
import com.msmikeescom.minesweeper.game.Board.Status
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BoardTest {

    private fun board(width: Int, height: Int, vararg mines: Pair<Int, Int>) =
        Board.withMines(width, height, mines.map { Position(it.first, it.second) }.toSet())

    @Test
    fun adjacentCountsAreComputed() {
        val b = board(3, 3, 0 to 0, 2 to 0)
        assertEquals(1, b.adjacentMines(0, 1))
        assertEquals(2, b.adjacentMines(1, 0))
        assertEquals(2, b.adjacentMines(1, 1))
        assertEquals(0, b.adjacentMines(1, 2))
    }

    @Test
    fun firstRevealIsNeverAMineAndPlacesAllMines() {
        repeat(200) { seed ->
            val b = Board(5, 5, 20, Random(seed))
            b.reveal(2, 2)
            assertFalse(b.isMine(2, 2))
            assertTrue(b.status != Status.LOST)
            var mines = 0
            for (y in 0 until 5) for (x in 0 until 5) if (b.isMine(x, y)) mines++
            assertEquals(20, mines)
        }
    }

    @Test
    fun firstRevealOpensAnAreaWhenThereIsRoom() {
        repeat(50) { seed ->
            val b = Board(9, 9, 10, Random(seed))
            b.reveal(4, 4)
            assertEquals(0, b.adjacentMines(4, 4))
        }
    }

    @Test
    fun maximumMineCountStillTerminates() {
        val b = Board(4, 4, Board.maxMines(4, 4), Random(1))
        b.reveal(0, 0)
        assertEquals(Status.WON, b.status)
    }

    @Test(expected = IllegalArgumentException::class)
    fun tooManyMinesIsRejected() {
        Board(3, 3, 9)
    }

    @Test
    fun maxMinesIsCappedByCounterDigits() {
        assertEquals(8, Board.maxMines(3, 3))
        assertEquals(999, Board.maxMines(40, 40))
    }

    @Test
    fun revealingEmptyCellFloodFillsUpToNumbers() {
        val b = board(4, 4, 3 to 3)
        val changed = b.reveal(0, 0)
        assertEquals(15, changed.size)
        assertEquals(CellState.COVERED, b.state(3, 3))
        assertEquals(Status.WON, b.status)
    }

    @Test
    fun revealingNumberOpensOnlyThatCell() {
        val b = board(3, 3, 0 to 0)
        val changed = b.reveal(1, 1)
        assertEquals(listOf(Position(1, 1)), changed)
        assertEquals(CellState.REVEALED, b.state(1, 1))
    }

    @Test
    fun revealingMineLosesAndRevealsEverything() {
        val b = board(3, 3, 0 to 0)
        val changed = b.reveal(0, 0)
        assertEquals(Status.LOST, b.status)
        assertEquals(9, changed.size)
        assertTrue(b.reveal(1, 1).isEmpty())
        assertFalse(b.toggleFlag(2, 2))
    }

    @Test
    fun flaggedCellCannotBeRevealed() {
        val b = board(3, 3, 0 to 0, 2 to 2)
        assertTrue(b.toggleFlag(0, 0))
        assertTrue(b.reveal(0, 0).isEmpty())
        assertEquals(Status.PLAYING, b.status)
        assertEquals(CellState.FLAGGED, b.state(0, 0))
    }

    @Test
    fun revealedCellCannotBeFlagged() {
        val b = board(3, 3, 0 to 0, 2 to 2)
        b.reveal(1, 1)
        assertFalse(b.toggleFlag(1, 1))
        assertEquals(0, b.flagsPlaced)
    }

    @Test
    fun reflaggingSameMineDoesNotWin() {
        val b = board(3, 3, 0 to 0, 2 to 2)
        repeat(5) {
            b.toggleFlag(0, 0)
            b.toggleFlag(0, 0)
        }
        b.toggleFlag(0, 0)
        assertEquals(Status.PLAYING, b.status)
        assertEquals(1, b.flagsRemaining)
    }

    @Test
    fun flagsCannotExceedMinesAndAreNotLost() {
        val b = board(3, 3, 0 to 0)
        assertTrue(b.toggleFlag(1, 1))
        assertFalse(b.toggleFlag(2, 2))
        assertFalse(b.toggleFlag(2, 2))
        assertEquals(0, b.flagsRemaining)
        assertTrue(b.toggleFlag(1, 1))
        assertEquals(1, b.flagsRemaining)
        assertTrue(b.toggleFlag(0, 0))
        assertEquals(Status.WON, b.status)
    }

    @Test
    fun wrongFlagsDoNotWin() {
        val b = board(3, 3, 0 to 0, 2 to 2)
        b.toggleFlag(0, 0)
        b.toggleFlag(1, 1)
        assertEquals(Status.PLAYING, b.status)
    }

    @Test
    fun revealingAllSafeCellsWins() {
        val b = board(3, 1, 0 to 0)
        b.reveal(1, 0)
        assertEquals(Status.PLAYING, b.status)
        b.reveal(2, 0)
        assertEquals(Status.WON, b.status)
    }

    @Test
    fun flagsBeforeFirstRevealCannotWin() {
        val b = Board(2, 1, 1, Random(0))
        b.toggleFlag(0, 0)
        assertEquals(Status.READY, b.status)
        b.toggleFlag(0, 0)
        b.reveal(0, 0)
        assertEquals(Status.WON, b.status)
    }
}
