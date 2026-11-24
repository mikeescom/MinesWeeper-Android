package com.msmikeescom.minesweeper.game

import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem

/**
 * Orders records best first: higher mine density (mines per square) wins, and among equally
 * dense games the faster time wins. The iOS app ranks records the same way.
 */
object RecordRanking {

    fun density(item: LocalRecordItem): Double =
        if (item.fieldSize > 0) item.numberOfMines.toDouble() / item.fieldSize.toDouble() else 0.0

    fun rank(items: List<LocalRecordItem>): List<LocalRecordItem> =
        items.sortedWith(
            compareByDescending<LocalRecordItem> { density(it) }
                .thenBy { it.timeInSeconds }
                .thenByDescending { it.timeStamp }
        )
}
