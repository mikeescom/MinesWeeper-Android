package com.msmikeescom.minesweeper.ui.activity

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.model.FieldObject
import com.msmikeescom.minesweeper.ui.view.GameChronometerView
import com.msmikeescom.minesweeper.ui.view.MinesCounterView
import com.msmikeescom.minesweeper.utilities.AlertDialogUtil
import com.msmikeescom.minesweeper.utilities.Constants.DEFAULT_LEVEL_NUMBER_MINES
import com.msmikeescom.minesweeper.utilities.Constants.EIGHT
import com.msmikeescom.minesweeper.utilities.Constants.EMPTY
import com.msmikeescom.minesweeper.utilities.Constants.FIVE
import com.msmikeescom.minesweeper.utilities.Constants.FOUR
import com.msmikeescom.minesweeper.utilities.Constants.MINE
import com.msmikeescom.minesweeper.utilities.Constants.ONE
import com.msmikeescom.minesweeper.utilities.Constants.SEVEN
import com.msmikeescom.minesweeper.utilities.Constants.SIX
import com.msmikeescom.minesweeper.utilities.Constants.SQUARE_SIZE
import com.msmikeescom.minesweeper.utilities.Constants.THREE
import com.msmikeescom.minesweeper.utilities.Constants.TWO
import java.util.*


class MineFieldActivity : BaseActivity() {

    companion object {
        private const val TAG = "MineFieldActivity"
    }

    private enum class FaceType {
        ANGRY, HAPPY, KILLED, SCARED, SMILE
    }

    private var mMinesFound = 0
    private var mDefaultNumberOfMines = 0
    private var mCounterNumberOfMines = 0
    private var mHorizontalSize = 0
    private var mVerticalSize = 0

    private var mFieldObjects = emptyArray<Array<FieldObject?>>()

    private lateinit var mGameChronometerView: GameChronometerView
    private lateinit var minesCounterView: MinesCounterView
    private lateinit var mMineFiled: GridLayout
    private lateinit var mFace: ImageView
    private lateinit var mUserPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout(R.layout.activity_mine_field, "MINE FILED", "", false)

        initView()

        mainViewModel.userInfo.observe(this) { userInfo ->
            userInfo?.photoUrl.let { photoUrl ->
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(mUserPhoto)
            }
            initGame()
        }
    }

    private fun initGame() {
        setNumberOfMines()
        setMineFieldSize()
        initFieldObjectsArray()
        buildMineFiled()
        initMineField()
    }

    private fun setNumberOfMines() {
        mainViewModel.userInfo.value?.let { userInfoItem ->
            mDefaultNumberOfMines = userInfoItem.numberOfMines.takeIf { it > 0 } ?: kotlin.run { DEFAULT_LEVEL_NUMBER_MINES }
            mCounterNumberOfMines = mDefaultNumberOfMines
        } ?: kotlin.run {
            mDefaultNumberOfMines = DEFAULT_LEVEL_NUMBER_MINES
            mCounterNumberOfMines = DEFAULT_LEVEL_NUMBER_MINES
        }
        mainViewModel.updateNumberOfMines(mDefaultNumberOfMines)
        minesCounterView.updateCounter(mCounterNumberOfMines)
    }

    private fun setMineFieldSize() {
        // Calculate ActionBar height
        val tv = TypedValue()
        val actionBarHeight = if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
             TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        } else 0
        Log.d(TAG, "ActionBar height: $actionBarHeight")

        // Calculate screen width and height
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        // Calculate mine filed width and height
        val mineFieldHeight = ((screenHeight - actionBarHeight) / SQUARE_SIZE)
        val mineFieldWidth = screenWidth / SQUARE_SIZE
        mVerticalSize = mineFieldHeight
        mHorizontalSize = mineFieldWidth
        mainViewModel.updateFieldSize(mineFieldHeight * mineFieldWidth)
        Log.d(TAG, "Screen Size (height = $screenHeight, width = $screenWidth)")
        Log.d(TAG, "Mine Filed Size (height = $mineFieldHeight, width = $mineFieldWidth)")
        mFieldObjects = Array(mHorizontalSize) { arrayOfNulls(mVerticalSize) }
    }

    private fun initFieldObjectsArray() {
        for (j in 0 until mVerticalSize) {
            for (i in 0 until mHorizontalSize) {
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
            xMinePos = rand.nextInt(mHorizontalSize)
            yMinePos = rand.nextInt(mVerticalSize)
            if (mFieldObjects[xMinePos][yMinePos]?.squareImageToShow != MINE) {
                mFieldObjects[xMinePos][yMinePos] = FieldObject(null, MINE)
                i++
                Log.d(TAG, "Mine set up at: [$xMinePos, $yMinePos]")
            }
        }
        setUpFieldNumbers()
    }

    private fun initView() {
        mGameChronometerView = findViewById(R.id.chronometer)
        minesCounterView = findViewById(R.id.mines_counter)
        mMineFiled = findViewById(R.id.mine_field)
        mMineFiled.removeAllViews()
        mFace = findViewById(R.id.face)
        mUserPhoto = findViewById(R.id.user_photo)
        setFaceImage(FaceType.HAPPY, true)
        mFace.setOnClickListener {
            //TODO
        }
    }

    private fun setUpFieldNumbers() {
        for (j in 0 until mVerticalSize) {
            for (i in 0 until mHorizontalSize) {
                if (mFieldObjects[i][j]?.squareImageToShow == MINE) {
                    if (i - 1 >= 0 && j - 1 >= 0 && mFieldObjects[i - 1][j - 1]?.squareImageToShow != MINE) { //Start Top position
                        mFieldObjects[i - 1][j - 1]?.squareImageToShow = mFieldObjects[i - 1][j - 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i - 1 >= 0 && mFieldObjects[i - 1][j]?.squareImageToShow != MINE) { //Start position
                        mFieldObjects[i - 1][j]?.squareImageToShow = mFieldObjects[i - 1][j]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i - 1 >= 0 && j + 1 < mVerticalSize && mFieldObjects[i - 1][j + 1]?.squareImageToShow != MINE) { //Start Bottom position
                        mFieldObjects[i - 1][j + 1]?.squareImageToShow = mFieldObjects[i - 1][j + 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (j - 1 >= 0 && mFieldObjects[i][j - 1]?.squareImageToShow != MINE) { //Top position
                        mFieldObjects[i][j - 1]?.squareImageToShow = mFieldObjects[i][j - 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i + 1 < mHorizontalSize && j - 1 >= 0 && mFieldObjects[i + 1][j - 1]?.squareImageToShow != MINE) { //Top End position
                        mFieldObjects[i + 1][j - 1]?.squareImageToShow = mFieldObjects[i + 1][j - 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i + 1 < mHorizontalSize && mFieldObjects[i + 1][j]?.squareImageToShow != MINE) { //End position
                        mFieldObjects[i + 1][j]?.squareImageToShow = mFieldObjects[i + 1][j]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i + 1 < mHorizontalSize && j + 1 < mVerticalSize && mFieldObjects[i + 1][j + 1]?.squareImageToShow != MINE) { //End Bottom position
                        mFieldObjects[i + 1][j + 1]?.squareImageToShow = mFieldObjects[i + 1][j + 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (j + 1 < mVerticalSize && mFieldObjects[i][j + 1]?.squareImageToShow != MINE) { //Bottom position
                        mFieldObjects[i][j + 1]?.squareImageToShow = mFieldObjects[i][j + 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                }
            }
        }
    }

    private fun unCoverEmptySquares(xPos: Int, yPos: Int) {
        if (xPos - 1 >= 0 && yPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos - 1]?.squareImageToShow != MINE &&
            mFieldObjects[xPos - 1][yPos - 1]?.isCovered == true) { //Start Top position
            unCoverSquare(xPos - 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos - 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos - 1)
            }
        }
        if (xPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos]?.squareImageToShow != MINE &&
            mFieldObjects[xPos - 1][yPos]?.isCovered == true) { //Start position
            unCoverSquare(xPos - 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos]?.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos)
            }
        }
        if (xPos - 1 >= 0 && yPos + 1 < mVerticalSize && mFieldObjects[xPos - 1][yPos + 1]?.squareImageToShow != MINE &&
            mFieldObjects[xPos - 1][yPos + 1]?.isCovered == true) { //Start Bottom position
            unCoverSquare(xPos - 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos + 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos + 1)
            }
        }
        if (yPos - 1 >= 0 && mFieldObjects[xPos][yPos - 1]?.squareImageToShow != MINE &&
            mFieldObjects[xPos][yPos - 1]?.isCovered == true) { //Top position
            unCoverSquare(xPos, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos - 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos - 1)
            }
        }
        if (xPos + 1 < mHorizontalSize && yPos - 1 >= 0 && mFieldObjects[xPos + 1][yPos - 1]?.squareImageToShow != MINE &&
            mFieldObjects[xPos + 1][yPos - 1]?.isCovered == true) { //Top End position
            unCoverSquare(xPos + 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos - 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos - 1)
            }
        }
        if (xPos + 1 < mHorizontalSize && mFieldObjects[xPos + 1][yPos]?.squareImageToShow != MINE &&
            mFieldObjects[xPos + 1][yPos]?.isCovered == true) { //End position
            unCoverSquare(xPos + 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos]?.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos)
            }
        }
        if (xPos + 1 < mHorizontalSize && yPos + 1 < mVerticalSize && mFieldObjects[xPos + 1][yPos + 1]?.squareImageToShow != MINE &&
            mFieldObjects[xPos + 1][yPos + 1]?.isCovered == true) { //End Bottom position
            unCoverSquare(xPos + 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos + 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos + 1)
            }
        }
        if (yPos + 1 < mVerticalSize && mFieldObjects[xPos][yPos + 1]?.squareImageToShow != MINE &&
            mFieldObjects[xPos][yPos + 1]?.isCovered == true) { //Bottom position
            unCoverSquare(xPos, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos + 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos + 1)
            }
        }
    }

    private fun isNumberOrMineSquare(imageToShow: Int?): Boolean {
        return when (imageToShow) {
            MINE,
            ONE,
            TWO,
            THREE,
            FOUR,
            FIVE,
            SIX,
            SEVEN,
            EIGHT -> true
            else -> false
        }
    }

    private fun unCoverSquare(x: Int, y: Int) {
        mFieldObjects[x][y]?.isCovered = false
        (mFieldObjects[x][y]?.squareView?.findViewById<View>(R.id.image_button) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, getResourceId(x, y), null))
    }

    private fun getResourceId(x: Int, y: Int): Int {
        val resourceId = when (mFieldObjects[x][y]?.squareImageToShow) {
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
        mMineFiled.columnCount = mHorizontalSize
        mMineFiled.orientation = GridLayout.HORIZONTAL
        for (j in 0 until mVerticalSize) {
            for (i in 0 until mHorizontalSize) {
                val squareView = layoutInflater.inflate(R.layout.square_layout, null)
                val imageView = squareView.findViewById<ImageView>(R.id.image_button)
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                mFieldObjects[i][j]?.squareView = squareView
                mMineFiled.addView(squareView)
                setOnClickListener(i, j, mFieldObjects[i][j], imageView, getResourceId(i, j))
                setOnLongClickListener(mFieldObjects[i][j], imageView)
            }
        }
    }

    private fun setOnClickListener(xPos: Int, yPos: Int, fieldObject: FieldObject?, imageView: ImageView, resourceId: Int) {
        fieldObject?.squareView?.setOnClickListener(View.OnClickListener {
            if (resourceId == R.drawable.uncovered) {
                setFaceImage(FaceType.SCARED, true)
                unCoverEmptySquares(xPos, yPos)
            } else if (resourceId == R.drawable.mine) {
                setFaceImage(FaceType.KILLED, false)
                uncoverAllSquares()
                mGameChronometerView.stopTimer()
                showLoseMessageDialog()
                return@OnClickListener
            }
            imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
            mGameChronometerView.startTimer()
        })
    }

    private fun setOnLongClickListener(fieldObject: FieldObject?, imageView: ImageView) {
        fieldObject?.squareView?.setOnLongClickListener(OnLongClickListener {
            Log.i(TAG, "CounterNumberOfMines: $mCounterNumberOfMines")
            if (fieldObject.isFlagged) {
                Log.i(TAG, "Square unflagged")
                setFaceImage(FaceType.ANGRY, true)
                mCounterNumberOfMines++
                imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.covered, null))
                fieldObject.isFlagged = false
                minesCounterView.updateCounter(mCounterNumberOfMines)
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
                        showWinMessageDialog()
                        mGameChronometerView.stopTimer()
                        return@OnLongClickListener true
                    }
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.flaged, null))
                    fieldObject.isFlagged = true
                    minesCounterView.updateCounter(mCounterNumberOfMines)
                }
            }
            true
        })
    }

    private fun showWinMessageDialog() {
        AlertDialogUtil.createCustomAlertDialog(this,
            alertTitle = "You Won!",
            alertMessage = "You have won!. Do you want to start a new game?",
            positiveButton =  Pair("Ok") { dialog, _ ->
                dialog.dismiss()
                finish()
                lunchMineField()
            },
            negativeButton = Pair("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            })?.show()
    }

    private fun showLoseMessageDialog() {
        AlertDialogUtil.createCustomAlertDialog(this,
            alertTitle = "You Lost!",
            alertMessage = "You have lost!. Do you want to start a new game?",
            positiveButton =  Pair("Ok") { dialog, _ ->
                dialog.dismiss()
                finish()
                lunchMineField()
            },
            negativeButton = Pair("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            })?.show()
    }

    private fun uncoverAllSquares() {
        for (j in 0 until mVerticalSize) {
            for (i in 0 until mHorizontalSize) {
                unCoverSquare(i, j)
                mFieldObjects[i][j]?.squareView?.setOnClickListener(null)
                mFieldObjects[i][j]?.squareView?.setOnLongClickListener(null)
            }
        }
        mMineFiled.isClickable = false
    }

    private fun setFaceImage(faceType: FaceType, keepSmiling: Boolean) {
        val resource: Int = when (faceType) {
            FaceType.ANGRY -> R.drawable.angry
            FaceType.HAPPY -> R.drawable.happy
            FaceType.KILLED -> R.drawable.killed
            FaceType.SCARED -> R.drawable.scared
            FaceType.SMILE -> R.drawable.smile
        }
        mFace.setImageDrawable(ResourcesCompat.getDrawable(resources, resource, null))
        if (keepSmiling) {
            Handler().postDelayed({ mFace.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.smile, null)) }, 500)
        }
    }

}