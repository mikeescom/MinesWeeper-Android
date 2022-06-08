package com.msmikeescom.minesweeper.utilities

object Constants {
    const val GOOGLE_SIGN_IN_ACCOUNT = "GOOGLE_SIGN_IN_ACCOUNT"
    const val EASY_LEVEL_NUMBER_MINES = 36
    const val MEDIUM_LEVEL_NUMBER_MINES = 51
    const val HARD_LEVEL_NUMBER_MINES = 66
    const val EMPTY = 0
    const val MINE = 9
    const val ONE = 1
    const val TWO = 2
    const val THREE = 3
    const val FOUR = 4
    const val FIVE = 5
    const val SIX = 6
    const val SEVEN = 7
    const val EIGHT = 8
    const val SQUARE_SIZE = 90

    enum class Difficulty(val numberOfMines: Int) {
        EASY(EASY_LEVEL_NUMBER_MINES), MEDIUM(MEDIUM_LEVEL_NUMBER_MINES), HARD(HARD_LEVEL_NUMBER_MINES)
    }
}