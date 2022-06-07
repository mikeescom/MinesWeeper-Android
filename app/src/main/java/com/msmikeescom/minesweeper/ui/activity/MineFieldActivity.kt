package com.msmikeescom.minesweeper.ui.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.model.FieldObject
import com.msmikeescom.minesweeper.ui.view.GameChronometerView
import com.msmikeescom.minesweeper.ui.view.MinesCounterView
import com.msmikeescom.minesweeper.utilities.Constants
import com.msmikeescom.minesweeper.utilities.Constants.EIGHT
import com.msmikeescom.minesweeper.utilities.Constants.EMPTY
import com.msmikeescom.minesweeper.utilities.Constants.FIVE
import com.msmikeescom.minesweeper.utilities.Constants.FOUR
import com.msmikeescom.minesweeper.utilities.Constants.GOOGLE_SIGN_IN_ACCOUNT
import com.msmikeescom.minesweeper.utilities.Constants.HORIZONTAL_SIZE
import com.msmikeescom.minesweeper.utilities.Constants.MINE
import com.msmikeescom.minesweeper.utilities.Constants.ONE
import com.msmikeescom.minesweeper.utilities.Constants.SEVEN
import com.msmikeescom.minesweeper.utilities.Constants.SIX
import com.msmikeescom.minesweeper.utilities.Constants.THREE
import com.msmikeescom.minesweeper.utilities.Constants.TWO
import com.msmikeescom.minesweeper.utilities.Constants.VERTICAL_SIZE
import com.msmikeescom.minesweeper.viewmodel.MineFieldViewModel
import java.util.*

class MineFieldActivity : AppCompatActivity() {
    private enum class FaceType {
        ANGRY, HAPPY, KILLED, SCARED, SMILE
    }

    private lateinit var googleSignInAccount: GoogleSignInAccount
    private lateinit var viewModel: MineFieldViewModel

    private var gameChronometerView: GameChronometerView? = null
    private var minesCounterView: MinesCounterView? = null
    private var mMineFiled: GridLayout? = null
    private var mFace: ImageView? = null
    private var mSettings: ImageView? = null
    private var mDifficulty: Constants.Difficulty = Constants.Difficulty.EASY
    private var mDefaultNumberOfMines = 0
    private var mCounterNumberOfMines = 0
    private var mMinesFound = 0
    private val mFieldObjects = Array(HORIZONTAL_SIZE) { arrayOfNulls<FieldObject>(VERTICAL_SIZE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine_field)

        googleSignInAccount = intent?.getParcelableExtra(GOOGLE_SIGN_IN_ACCOUNT)!!

        viewModel = ViewModelProvider(this)[MineFieldViewModel::class.java].also {
            it.initViewModel(this, googleSignInAccount)
        }

        viewModel.isViewModelInitialized.observe(this) { isInitialized ->
            if (isInitialized) {
                viewModel.loadUserInfo()
            }
        }

        viewModel.userInfo.observe(this) { userInfo ->
            userInfo?.let {
                mDefaultNumberOfMines = mDifficulty.numberOfMines
                mCounterNumberOfMines = mDifficulty.numberOfMines

                initFieldObjectsArray()
                buildMineFiled()
                initView()
                initMineField()
            }
        }
    }

    private fun initView() {
        gameChronometerView = findViewById(R.id.chronometer)
        minesCounterView = findViewById(R.id.mines_counter)
        mMineFiled = findViewById(R.id.mine_field)
        mMineFiled?.removeAllViews()
        mFace = findViewById(R.id.face)
        mFace?.setOnClickListener { viewModel.loadUserInfo() }
        mSettings = findViewById(R.id.settings)
        mSettings?.setOnClickListener { showSettingsPopupWindowClick(mMineFiled?.rootView) }
        minesCounterView?.updateCounter(mCounterNumberOfMines)
    }

    private fun initFieldObjectsArray() {
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                mFieldObjects[i][j] = FieldObject(null, EMPTY)
            }
        }
    }

    private fun buildMineFiled() {
        val rand = Random()
        var xMinePos: Int
        var yMinePos: Int
        var i = 0
        while (i < mCounterNumberOfMines) {
            xMinePos = rand.nextInt(HORIZONTAL_SIZE)
            yMinePos = rand.nextInt(VERTICAL_SIZE)
            if (mFieldObjects[xMinePos][yMinePos]!!.squareImageToShow != MINE) {
                mFieldObjects[xMinePos][yMinePos] = FieldObject(null, MINE)
                i++
                Log.d(TAG, "Mine set up at: [$xMinePos, $yMinePos]")
            }
        }
        setUpFieldNumbers()
    }

    private fun setUpFieldNumbers() {
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                if (mFieldObjects[i][j]!!.squareImageToShow == MINE) {
                    if (i - 1 >= 0 && j - 1 >= 0 && mFieldObjects[i - 1][j - 1]!!.squareImageToShow != MINE) { //Start Top position
                        mFieldObjects[i - 1][j - 1]!!.squareImageToShow = mFieldObjects[i - 1][j - 1]!!.squareImageToShow + 1
                    }
                    if (i - 1 >= 0 && mFieldObjects[i - 1][j]!!.squareImageToShow != MINE) { //Start position
                        mFieldObjects[i - 1][j]!!.squareImageToShow = mFieldObjects[i - 1][j]!!.squareImageToShow + 1
                    }
                    if (i - 1 >= 0 && j + 1 < VERTICAL_SIZE && mFieldObjects[i - 1][j + 1]!!.squareImageToShow != MINE) { //Start Bottom position
                        mFieldObjects[i - 1][j + 1]!!.squareImageToShow = mFieldObjects[i - 1][j + 1]!!.squareImageToShow + 1
                    }
                    if (j - 1 >= 0 && mFieldObjects[i][j - 1]!!.squareImageToShow != MINE) { //Top position
                        mFieldObjects[i][j - 1]!!.squareImageToShow = mFieldObjects[i][j - 1]!!.squareImageToShow + 1
                    }
                    if (i + 1 < HORIZONTAL_SIZE && j - 1 >= 0 && mFieldObjects[i + 1][j - 1]!!.squareImageToShow != MINE) { //Top End position
                        mFieldObjects[i + 1][j - 1]!!.squareImageToShow = mFieldObjects[i + 1][j - 1]!!.squareImageToShow + 1
                    }
                    if (i + 1 < HORIZONTAL_SIZE && mFieldObjects[i + 1][j]!!.squareImageToShow != MINE) { //End position
                        mFieldObjects[i + 1][j]!!.squareImageToShow = mFieldObjects[i + 1][j]!!.squareImageToShow + 1
                    }
                    if (i + 1 < HORIZONTAL_SIZE && j + 1 < VERTICAL_SIZE && mFieldObjects[i + 1][j + 1]!!.squareImageToShow != MINE) { //End Bottom position
                        mFieldObjects[i + 1][j + 1]!!.squareImageToShow = mFieldObjects[i + 1][j + 1]!!.squareImageToShow + 1
                    }
                    if (j + 1 < VERTICAL_SIZE && mFieldObjects[i][j + 1]!!.squareImageToShow != MINE) { //Bottom position
                        mFieldObjects[i][j + 1]!!.squareImageToShow = mFieldObjects[i][j + 1]!!.squareImageToShow + 1
                    }
                }
            }
        }
    }

    private fun unCoverEmptySquares(xPos: Int, yPos: Int) {
        if (xPos - 1 >= 0 && yPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos - 1][yPos - 1]!!.isCovered) { //Start Top position
            unCoverSquare(xPos - 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos - 1)
            }
        }
        if (xPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos - 1][yPos]!!.isCovered) { //Start position
            unCoverSquare(xPos - 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos)
            }
        }
        if (xPos - 1 >= 0 && yPos + 1 < VERTICAL_SIZE && mFieldObjects[xPos - 1][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos - 1][yPos + 1]!!.isCovered) { //Start Bottom position
            unCoverSquare(xPos - 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos + 1)
            }
        }
        if (yPos - 1 >= 0 && mFieldObjects[xPos][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos][yPos - 1]!!.isCovered) { //Top position
            unCoverSquare(xPos, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos - 1)
            }
        }
        if (xPos + 1 < HORIZONTAL_SIZE && yPos - 1 >= 0 && mFieldObjects[xPos + 1][yPos - 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos - 1]!!.isCovered) { //Top End position
            unCoverSquare(xPos + 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos - 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos - 1)
            }
        }
        if (xPos + 1 < HORIZONTAL_SIZE && mFieldObjects[xPos + 1][yPos]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos]!!.isCovered) { //End position
            unCoverSquare(xPos + 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos)
            }
        }
        if (xPos + 1 < HORIZONTAL_SIZE && yPos + 1 < VERTICAL_SIZE && mFieldObjects[xPos + 1][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos + 1][yPos + 1]!!.isCovered) { //End Bottom position
            unCoverSquare(xPos + 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos + 1)
            }
        }
        if (yPos + 1 < VERTICAL_SIZE && mFieldObjects[xPos][yPos + 1]!!.squareImageToShow != MINE &&
                mFieldObjects[xPos][yPos + 1]!!.isCovered) { //Bottom position
            unCoverSquare(xPos, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos + 1]!!.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos + 1)
            }
        }
    }

    private fun isNumberOrMineSquare(imageToShow: Int): Boolean {
        return when (imageToShow) {
            MINE -> true
            ONE -> true
            TWO -> true
            THREE -> true
            FOUR -> true
            FIVE -> true
            SIX -> true
            SEVEN -> true
            EIGHT -> true
            else -> false
        }
    }

    private fun unCoverSquare(x: Int, y: Int) {
        mFieldObjects[x][y]!!.isCovered = false
        (mFieldObjects[x][y]!!.squareView?.findViewById<View>(R.id.image_button) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, getResourceId(x, y), null))
    }

    private fun getResourceId(x: Int, y: Int): Int {
        val resourceId = when (mFieldObjects[x][y]!!.squareImageToShow) {
            MINE -> R.drawable.mine
            ONE -> R.drawable.one
            TWO -> R.drawable.two
            THREE -> R.drawable.three
            FOUR -> R.drawable.four
            FIVE -> R.drawable.five
            SIX -> R.drawable.six
            SEVEN -> R.drawable.seven
            EIGHT -> R.drawable.eight
            else -> R.drawable.uncovered
        }
        return resourceId
    }

    private fun initMineField() {
        mMineFiled!!.columnCount = HORIZONTAL_SIZE
        mMineFiled!!.orientation = GridLayout.HORIZONTAL
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                val squareView = layoutInflater.inflate(R.layout.square_layout, null)
                val imageView = squareView.findViewById<ImageView>(R.id.image_button)
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                mFieldObjects[i][j]!!.squareView = squareView
                mMineFiled!!.addView(squareView)
                setOnClickListener(i, j, mFieldObjects[i][j], imageView, getResourceId(i, j))
                setOnLongClickListener(mFieldObjects[i][j], imageView)
            }
        }
    }

    private fun setOnClickListener(xPos: Int, yPos: Int, fieldObject: FieldObject?, imageView: ImageView, resourceId: Int) {
        fieldObject!!.squareView?.setOnClickListener(View.OnClickListener {
            if (resourceId == R.drawable.uncovered) {
                setFaceImage(FaceType.SCARED, true)
                unCoverEmptySquares(xPos, yPos)
            } else if (resourceId == R.drawable.mine) {
                setFaceImage(FaceType.KILLED, false)
                uncoverAllSquares()
                gameChronometerView?.stopTimer()
                return@OnClickListener
            }
            imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
            if (gameChronometerView?.mTimerStarted == false) {
                gameChronometerView?.startTimer()
            }
        })
    }

    private fun setOnLongClickListener(fieldObject: FieldObject?, imageView: ImageView) {
        fieldObject!!.squareView?.setOnLongClickListener(OnLongClickListener {
            if (fieldObject.isFlagged) {
                Log.i(TAG, "Square unflagged")
                setFaceImage(FaceType.ANGRY, true)
                mCounterNumberOfMines++
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                fieldObject.isFlagged = false
                minesCounterView?.updateCounter(mCounterNumberOfMines)
                return@OnLongClickListener true
            }
            if (fieldObject.isCovered) {
                Log.i(TAG, "Square flagged")
                setFaceImage(FaceType.SCARED, true)
                mCounterNumberOfMines--
                if (mCounterNumberOfMines >= 0) {
                    if (fieldObject.squareImageToShow == MINE) {
                        mMinesFound++
                    }
                    if (mMinesFound == mDefaultNumberOfMines) {
                        Log.i(TAG, "You won!$mMinesFound")
                        setFaceImage(FaceType.HAPPY, false)
                        showFinishedGamePopupWindowClick(mMineFiled!!.rootView)
                        gameChronometerView?.stopTimer()
                        return@OnLongClickListener true
                    }
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.flaged, null))
                    fieldObject.isFlagged = true
                    minesCounterView?.updateCounter(mCounterNumberOfMines)
                }
            }
            true
        })
    }

    private fun uncoverAllSquares() {
        for (j in 0 until VERTICAL_SIZE) {
            for (i in 0 until HORIZONTAL_SIZE) {
                unCoverSquare(i, j)
                mFieldObjects[i][j]!!.squareView?.setOnClickListener(null)
                mFieldObjects[i][j]!!.squareView?.setOnLongClickListener(null)
            }
        }
        mMineFiled!!.isClickable = false
    }

    private fun setFaceImage(faceType: FaceType, keepSmiling: Boolean) {
        val resource: Int = when (faceType) {
            FaceType.ANGRY -> R.drawable.angry
            FaceType.HAPPY -> R.drawable.happy
            FaceType.KILLED -> R.drawable.killed
            FaceType.SCARED -> R.drawable.scared
            FaceType.SMILE -> R.drawable.smile
        }
        mFace!!.setImageDrawable(ResourcesCompat.getDrawable(resources, resource, null))
        if (keepSmiling) {
            Handler().postDelayed({ mFace!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.smile, null)) }, 500)
        }
    }

    private fun dismissSettingsPopupWindow(popupWindow: PopupWindow, difficulty: Constants.Difficulty, updateDifficulty: Boolean) {
        var toastText = ""
        popupWindow.dismiss()
        if (updateDifficulty) {
            this.mDifficulty = difficulty
            recreate()
            when (difficulty) {
                Constants.Difficulty.EASY -> toastText = "Easy level"
                Constants.Difficulty.MEDIUM -> toastText = "Medium level"
                Constants.Difficulty.HARD -> toastText = "Hard level"
            }
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSettingsPopupWindowClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_windows_settings_layout, null)
        val easy = popupView.findViewById<ImageView>(R.id.easy)
        val medium = popupView.findViewById<ImageView>(R.id.medium)
        val difficult = popupView.findViewById<ImageView>(R.id.difficult)
        val cancel = popupView.findViewById<Button>(R.id.cancel)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.elevation = 5.0f
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        easy.setOnClickListener { dismissSettingsPopupWindow(popupWindow, Constants.Difficulty.EASY, true) }
        medium.setOnClickListener { dismissSettingsPopupWindow(popupWindow, Constants.Difficulty.MEDIUM, true) }
        difficult.setOnClickListener { dismissSettingsPopupWindow(popupWindow, Constants.Difficulty.HARD, true) }
        cancel.setOnClickListener { dismissSettingsPopupWindow(popupWindow, Constants.Difficulty.EASY, false) }
        popupView.setOnTouchListener { _, _ ->
            dismissSettingsPopupWindow(popupWindow, Constants.Difficulty.EASY, false)
            true
        }
    }

    private fun showFinishedGamePopupWindowClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_windows_finished_game_layout, null)
        val playAgain = popupView.findViewById<Button>(R.id.play_again)
        val finishTime = popupView.findViewById<TextView>(R.id.finish_time)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.elevation = 5.0f
        val timeOfDay = gameChronometerView?.mChronometerTime?.toLong() ?: { 0 }
        val time = timeOfDay.toString()
        finishTime.text = resources.getString(R.string.your_time_was, time)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        playAgain.setOnClickListener { dismissSettingsPopupWindow(popupWindow, mDifficulty, true) }
        popupView.setOnTouchListener { _, _ ->
            dismissSettingsPopupWindow(popupWindow, mDifficulty, true)
            true
        }
    }

    companion object {
        private const val TAG = "MineFieldActivity"
    }
}