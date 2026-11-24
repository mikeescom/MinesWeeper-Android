package com.msmikeescom.minesweeper.game

import kotlin.random.Random

/**
 * Pure game state for one Minesweeper round. Has no Android dependencies so it can be unit tested
 * and kept in a ViewModel across configuration changes.
 *
 * Mines are placed lazily on the first reveal so the first tap is never a mine (and, when the
 * board has room, never next to one either).
 */
class Board private constructor(
    val width: Int,
    val height: Int,
    val mineCount: Int,
    private val random: Random,
    presetMines: Set<Position>?,
) {
    constructor(width: Int, height: Int, mineCount: Int, random: Random = Random.Default) :
        this(width, height, mineCount, random, null)

    data class Position(val x: Int, val y: Int)

    enum class CellState { COVERED, FLAGGED, REVEALED }

    enum class Status { READY, PLAYING, WON, LOST }

    companion object {
        /** The mines counter shows three digits. */
        const val MAX_DISPLAYABLE_MINES = 999

        /** Largest valid mine count for a board: at least one safe cell, and fits the counter. */
        fun maxMines(width: Int, height: Int): Int = minOf(width * height - 1, MAX_DISPLAYABLE_MINES)

        /** Builds a board with mines at fixed positions. For tests. */
        internal fun withMines(width: Int, height: Int, mines: Set<Position>): Board =
            Board(width, height, mines.size, Random.Default, mines)
    }

    private val size = width * height
    private val mines = BooleanArray(size)
    private val adjacent = IntArray(size)
    private val states = Array(size) { CellState.COVERED }
    private var revealedSafeCells = 0

    var status = Status.READY
        private set

    var flagsPlaced = 0
        private set

    val flagsRemaining: Int
        get() = mineCount - flagsPlaced

    val isOver: Boolean
        get() = status == Status.WON || status == Status.LOST

    init {
        require(width > 0 && height > 0) { "Board must be at least 1x1, was ${width}x$height" }
        require(mineCount in 1..maxMines(width, height)) {
            "Mine count must be in 1..${maxMines(width, height)}, was $mineCount"
        }
        if (presetMines != null) {
            presetMines.forEach { require(inBounds(it.x, it.y)) { "Mine out of bounds: $it" } }
            presetMines.forEach { mines[index(it.x, it.y)] = true }
            computeAdjacency()
            status = Status.PLAYING
        }
    }

    fun state(x: Int, y: Int): CellState = states[index(x, y)]

    fun isMine(x: Int, y: Int): Boolean = mines[index(x, y)]

    fun adjacentMines(x: Int, y: Int): Int = adjacent[index(x, y)]

    /**
     * Opens the cell at [x],[y]. Opening an empty cell flood-fills its empty neighbours.
     * Flagged or already-open cells are ignored. Returns the cells whose state changed.
     * If a mine is opened the game is lost and every cell is revealed.
     */
    fun reveal(x: Int, y: Int): List<Position> {
        if (isOver || state(x, y) != CellState.COVERED) return emptyList()
        if (status == Status.READY) {
            placeMines(x, y)
            status = Status.PLAYING
        }

        if (isMine(x, y)) {
            status = Status.LOST
            return revealAll()
        }

        val changed = mutableListOf<Position>()
        val queue = ArrayDeque<Position>()
        open(x, y, changed)
        if (adjacentMines(x, y) == 0) queue.add(Position(x, y))
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            forEachNeighbour(current.x, current.y) { nx, ny ->
                if (state(nx, ny) == CellState.COVERED && !isMine(nx, ny)) {
                    open(nx, ny, changed)
                    if (adjacentMines(nx, ny) == 0) queue.add(Position(nx, ny))
                }
            }
        }

        if (revealedSafeCells == size - mineCount) win()
        return changed
    }

    /**
     * Flags or unflags a covered cell. Returns false (and changes nothing) for open cells, when the
     * game is over, or when no flags are left. Flagging exactly every mine also wins the game.
     */
    fun toggleFlag(x: Int, y: Int): Boolean {
        if (isOver) return false
        val i = index(x, y)
        when (states[i]) {
            CellState.REVEALED -> return false
            CellState.FLAGGED -> {
                states[i] = CellState.COVERED
                flagsPlaced--
            }
            CellState.COVERED -> {
                if (flagsRemaining <= 0) return false
                states[i] = CellState.FLAGGED
                flagsPlaced++
            }
        }
        if (status == Status.PLAYING && flagsPlaced == mineCount && allFlagsOnMines()) win()
        return true
    }

    private fun open(x: Int, y: Int, changed: MutableList<Position>) {
        states[index(x, y)] = CellState.REVEALED
        revealedSafeCells++
        changed.add(Position(x, y))
    }

    private fun win() {
        status = Status.WON
    }

    private fun allFlagsOnMines(): Boolean =
        (0 until size).all { states[it] != CellState.FLAGGED || mines[it] }

    private fun revealAll(): List<Position> {
        val all = ArrayList<Position>(size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                states[index(x, y)] = CellState.REVEALED
                all.add(Position(x, y))
            }
        }
        return all
    }

    private fun placeMines(safeX: Int, safeY: Int) {
        val excluded = mutableSetOf(index(safeX, safeY))
        // Keep the first tap's neighbours clear too when there's room, so it opens an area.
        if (size - 9 >= mineCount) {
            forEachNeighbour(safeX, safeY) { nx, ny -> excluded.add(index(nx, ny)) }
        }
        (0 until size)
            .filter { it !in excluded }
            .shuffled(random)
            .take(mineCount)
            .forEach { mines[it] = true }
        computeAdjacency()
    }

    private fun computeAdjacency() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                var count = 0
                forEachNeighbour(x, y) { nx, ny -> if (isMine(nx, ny)) count++ }
                adjacent[index(x, y)] = count
            }
        }
    }

    private inline fun forEachNeighbour(x: Int, y: Int, action: (Int, Int) -> Unit) {
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy
                if (inBounds(nx, ny)) action(nx, ny)
            }
        }
    }

    private fun inBounds(x: Int, y: Int) = x in 0 until width && y in 0 until height

    private fun index(x: Int, y: Int): Int {
        require(inBounds(x, y)) { "($x, $y) is outside a ${width}x$height board" }
        return y * width + x
    }
}
