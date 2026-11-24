package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.game.Board
import com.msmikeescom.minesweeper.game.Board.CellState
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import com.msmikeescom.minesweeper.ui.activity.GameBoardActivity
import com.msmikeescom.minesweeper.ui.activity.IGameBoardUIListener
import com.msmikeescom.minesweeper.ui.view.GameChronometerView
import com.msmikeescom.minesweeper.ui.view.MinesCounterView
import com.msmikeescom.minesweeper.utilities.AlertDialogUtil
import com.msmikeescom.minesweeper.utilities.Constants
import com.msmikeescom.minesweeper.viewmodel.GameBoardViewModel

/**
 * Draws the current [Board] and forwards taps to it. All game rules live in [Board]; the board
 * and timer are kept in [GameBoardViewModel] so a game survives configuration changes.
 */
class MineFieldFragment : Fragment() {
    companion object {
        private const val TAG = "MineFieldFragment"
        private const val FACE_RESET_DELAY_MS = 500L
    }

    private lateinit var gameBoardViewModel: GameBoardViewModel
    private lateinit var listener: IGameBoardUIListener
    private val gameBoardActivity: GameBoardActivity
        get() = activity as GameBoardActivity

    private enum class FaceType {
        ANGRY, HAPPY, KILLED, SCARED, SMILE
    }

    /** Board dimensions that fit the available space, measured once the layout is done. */
    private var fittedWidth = 0
    private var fittedHeight = 0

    private var squareImages = emptyArray<ImageView>()

    private lateinit var mGameChronometerView: GameChronometerView
    private lateinit var minesCounterView: MinesCounterView
    private lateinit var mMineField: GridLayout
    private lateinit var mFace: ImageView
    private lateinit var mUserPhoto: ImageView

    private val resetFace = Runnable { mFace.setImageResource(R.drawable.smile) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = gameBoardActivity
        listener.onHideProgress()

        gameBoardViewModel = ViewModelProvider(gameBoardActivity)[GameBoardViewModel::class.java]

        initView(view)

        gameBoardViewModel.getUserPhotoUrl()?.let { photoUrl ->
            Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .into(mUserPhoto)
        }

        // Size the board from the space the layout actually gives it.
        val container = view.findViewById<View>(R.id.mine_field_container)
        container.doOnLayout {
            val squareSize = resources.getDimensionPixelSize(R.dimen.square_size)
            val bottomMargin = (mMineField.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            fittedWidth = (it.width / squareSize).coerceAtLeast(2)
            fittedHeight = ((it.height - bottomMargin) / squareSize).coerceAtLeast(2)
            Log.d(TAG, "Mine field fits ${fittedWidth}x$fittedHeight squares")

            // Keep a game in progress; a game nobody has tapped yet is resized to the new space.
            val existing = gameBoardViewModel.board
            val fits = existing != null && existing.width == fittedWidth && existing.height == fittedHeight
            if (existing != null && (fits || existing.status != Board.Status.READY)) {
                showBoard(existing)
            } else {
                startNewGame()
            }
        }
    }

    override fun onDestroyView() {
        mFace.removeCallbacks(resetFace)
        super.onDestroyView()
    }

    private fun initView(view: View) {
        mGameChronometerView = view.findViewById(R.id.chronometer)
        minesCounterView = view.findViewById(R.id.mines_counter)
        mMineField = view.findViewById(R.id.mine_field)
        mMineField.removeAllViews()
        mFace = view.findViewById(R.id.face)
        mUserPhoto = view.findViewById(R.id.user_photo)
        mUserPhoto.setOnClickListener {
            showAccountOptionsPopup(it)
        }
        mFace.setOnClickListener {
            AlertDialogUtil.showGameWillBeRestartedDialog(requireContext(),
                { dialog, _ ->
                    dialog.dismiss()
                    startNewGame()
                },
                { dialog, _ ->
                    dialog.dismiss()
                })
        }
    }

    private fun showAccountOptionsPopup(view: View) {
        val popup = PopupMenu(requireActivity(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_account_settings, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.numberOfMines -> showEditNumberOfMinesDialog()
                R.id.action_logout -> listener.logOut()
            }
            true
        }
        popup.show()
    }

    private fun showEditNumberOfMinesDialog() {
        val maxMines = Board.maxMines(fittedWidth, fittedHeight)
        val editText = EditText(requireContext()).also {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        AlertDialogUtil.showEditNumberOfMinesDialog(requireContext(),
            editText,
            { dialog, _ ->
                dialog.dismiss()
                val text = editText.text.toString()
                val numberOfMines = text.toIntOrNull()
                when {
                    text.isEmpty() -> showToast(getString(R.string.settings_no_mines_entered))
                    numberOfMines == null || numberOfMines !in 1..maxMines ->
                        showToast(getString(R.string.settings_invalid_number_of_mines, maxMines))
                    else -> {
                        gameBoardViewModel.saveNumberOfMines(numberOfMines)
                        startNewGame()
                    }
                }
            },
            { dialog, _ ->
                dialog.dismiss()
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun startNewGame() {
        if (fittedWidth == 0 || fittedHeight == 0) return
        val maxMines = Board.maxMines(fittedWidth, fittedHeight)
        val numberOfMines = (gameBoardViewModel.getNumberOfMines()?.takeIf { it > 0 }
            ?: Constants.DEFAULT_LEVEL_NUMBER_MINES).coerceAtMost(maxMines)
        gameBoardViewModel.saveNumberOfMines(numberOfMines)
        gameBoardViewModel.saveFieldSize(fittedWidth * fittedHeight)
        showBoard(gameBoardViewModel.newGame(fittedWidth, fittedHeight, numberOfMines))
    }

    /** Builds the grid for [board] and draws its current state. */
    private fun showBoard(board: Board) {
        mMineField.removeAllViews()
        mMineField.columnCount = board.width
        mMineField.orientation = GridLayout.HORIZONTAL
        squareImages = Array(board.width * board.height) { i ->
            val x = i % board.width
            val y = i / board.width
            val squareView = layoutInflater.inflate(R.layout.square_layout, mMineField, false)
            mMineField.addView(squareView)
            squareView.setOnClickListener { onSquareClicked(board, x, y) }
            squareView.setOnLongClickListener { onSquareLongClicked(board, x, y) }
            squareView.findViewById<ImageView>(R.id.image_button)
        }
        for (y in 0 until board.height) {
            for (x in 0 until board.width) {
                renderSquare(board, x, y)
            }
        }
        minesCounterView.updateCounter(board.flagsRemaining)

        mFace.removeCallbacks(resetFace)
        when (board.status) {
            Board.Status.WON -> setFaceImage(FaceType.HAPPY, false)
            Board.Status.LOST -> setFaceImage(FaceType.KILLED, false)
            else -> setFaceImage(FaceType.HAPPY, true)
        }
        val startedAt = gameBoardViewModel.timerStartedAt
        if (gameBoardViewModel.isTimerRunning && startedAt != null) {
            mGameChronometerView.start(startedAt)
        } else {
            mGameChronometerView.stop(gameBoardViewModel.elapsedSeconds())
        }
    }

    private fun onSquareClicked(board: Board, x: Int, y: Int) {
        if (board.isOver || board.state(x, y) != CellState.COVERED) return
        val changed = board.reveal(x, y)
        changed.forEach { renderSquare(board, it.x, it.y) }

        when (board.status) {
            Board.Status.LOST -> onGameLost()
            Board.Status.WON -> onGameWon(board)
            else -> {
                setFaceImage(FaceType.SCARED, true)
                startTimer()
            }
        }
    }

    private fun onSquareLongClicked(board: Board, x: Int, y: Int): Boolean {
        val wasFlagged = board.state(x, y) == CellState.FLAGGED
        if (!board.toggleFlag(x, y)) return true
        renderSquare(board, x, y)
        minesCounterView.updateCounter(board.flagsRemaining)
        if (board.status == Board.Status.WON) {
            onGameWon(board)
        } else {
            setFaceImage(if (wasFlagged) FaceType.ANGRY else FaceType.SCARED, true)
        }
        return true
    }

    private fun startTimer() {
        gameBoardViewModel.startTimer()
        gameBoardViewModel.timerStartedAt?.let { mGameChronometerView.start(it) }
    }

    private fun stopTimer() {
        gameBoardViewModel.stopTimer()
        mGameChronometerView.stop(gameBoardViewModel.elapsedSeconds())
    }

    private fun onGameLost() {
        setFaceImage(FaceType.KILLED, false)
        stopTimer()
        AlertDialogUtil.showLoseMessageDialog(requireContext(),
            { dialog, _ ->
                dialog.dismiss()
                startNewGame()
            },
            { dialog, _ ->
                dialog.dismiss()
            })
    }

    private fun onGameWon(board: Board) {
        Log.d(TAG, "You won!")
        setFaceImage(FaceType.HAPPY, false)
        // The first tap can win a very small board, before the timer was ever started.
        gameBoardViewModel.startTimer()
        stopTimer()

        val localRecordItem = LocalRecordItem()
        localRecordItem.numberOfMines = board.mineCount.toLong()
        localRecordItem.fieldSize = (board.width * board.height).toLong()
        localRecordItem.timeInSeconds = gameBoardViewModel.elapsedSeconds().toLong()
        localRecordItem.timeStamp = System.currentTimeMillis()
        gameBoardViewModel.setNewRecord(localRecordItem)

        AlertDialogUtil.showWinMessageDialog(requireContext(),
            { dialog, _ ->
                dialog.dismiss()
                startNewGame()
            },
            { dialog, _ ->
                dialog.dismiss()
            })
    }

    private fun renderSquare(board: Board, x: Int, y: Int) {
        squareImages[y * board.width + x].setImageResource(squareDrawable(board, x, y))
    }

    @DrawableRes
    private fun squareDrawable(board: Board, x: Int, y: Int): Int = when (board.state(x, y)) {
        CellState.COVERED -> R.drawable.covered
        CellState.FLAGGED -> R.drawable.flaged
        CellState.REVEALED -> if (board.isMine(x, y)) R.drawable.mine else when (board.adjacentMines(x, y)) {
            1 -> R.drawable.one
            2 -> R.drawable.two
            3 -> R.drawable.three
            4 -> R.drawable.four
            5 -> R.drawable.five
            6 -> R.drawable.six
            7 -> R.drawable.seven
            8 -> R.drawable.eight
            else -> R.drawable.uncovered
        }
    }

    private fun setFaceImage(faceType: FaceType, keepSmiling: Boolean) {
        val resource: Int = when (faceType) {
            FaceType.ANGRY -> R.drawable.angry
            FaceType.HAPPY -> R.drawable.happy
            FaceType.KILLED -> R.drawable.killed
            FaceType.SCARED -> R.drawable.scared
            FaceType.SMILE -> R.drawable.smile
        }
        mFace.removeCallbacks(resetFace)
        mFace.setImageResource(resource)
        if (keepSmiling) {
            mFace.postDelayed(resetFace, FACE_RESET_DELAY_MS)
        }
    }
}
