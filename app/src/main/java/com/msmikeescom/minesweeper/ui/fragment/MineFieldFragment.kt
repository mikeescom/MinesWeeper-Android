package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.model.FieldObject
import com.msmikeescom.minesweeper.ui.IMainActivityUIListener
import com.msmikeescom.minesweeper.ui.activity.MainActivity
import com.msmikeescom.minesweeper.ui.view.GameChronometerView
import com.msmikeescom.minesweeper.ui.view.MinesCounterView
import com.msmikeescom.minesweeper.utilities.AlertDialogUtil
import com.msmikeescom.minesweeper.utilities.Constants
import com.msmikeescom.minesweeper.utilities.Constants.SQUARE_SIZE
import com.msmikeescom.minesweeper.utilities.Constants.TIMER_BAR_SIZE
import com.msmikeescom.minesweeper.viewmodel.MainViewModel
import java.util.*

class MineFieldFragment : Fragment() {
    companion object {
        private val TAG = "MineFieldFragment"
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var listener: IMainActivityUIListener
    private val mainActivity: MainActivity
        get() = activity as MainActivity

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine_filed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setActionBar("MINE FILED", "", false)
        listener = mainActivity
        listener.onHideProgress()

        initView(view)

        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java].also {
            it.initViewModel(mainActivity)
        }

        mainViewModel.getUserPhotoUrl()?.let { photoUrl ->
            Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .into(mUserPhoto)
        }

        initGame()
    }

    private fun initGame() {
        setNumberOfMines()
        setMineFieldSize()
        initFieldObjectsArray()
        buildMineFiled()
        initMineField()
    }

    private fun setNumberOfMines() {
        mainViewModel.getNumberOfMines()?.let { numberOfMines ->
            mDefaultNumberOfMines = numberOfMines.takeIf { it > 0 } ?: kotlin.run { Constants.DEFAULT_LEVEL_NUMBER_MINES }
            mCounterNumberOfMines = mDefaultNumberOfMines
        } ?: kotlin.run {
            mDefaultNumberOfMines = Constants.DEFAULT_LEVEL_NUMBER_MINES
            mCounterNumberOfMines = Constants.DEFAULT_LEVEL_NUMBER_MINES
        }
        mainViewModel.saveNumberOfMines(mDefaultNumberOfMines)
        minesCounterView.updateCounter(mCounterNumberOfMines)
    }

    private fun setMineFieldSize() {
        // Calculate ActionBar height
        val tv = TypedValue()
        val actionBarHeight = if (mainActivity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        } else 0
        Log.d(TAG, "ActionBar height: $actionBarHeight")

        // Calculate timer's bar
        val timerBarSize = (TIMER_BAR_SIZE * resources.displayMetrics.density).toInt()
        Log.d(TAG, "Timer bar size: $timerBarSize")

        // Calculate square size
        val squareSize = (SQUARE_SIZE * resources.displayMetrics.density).toInt()
        Log.d(TAG, "Square size: $squareSize")

        // Calculate screen width and height
        val displayMetrics = DisplayMetrics()
        mainActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        // Calculate mine filed width and height
        val mineFieldHeight = ((screenHeight - actionBarHeight - timerBarSize) / squareSize)
        val mineFieldWidth = screenWidth / squareSize
        mVerticalSize = mineFieldHeight.minus(1)
        mHorizontalSize = mineFieldWidth
        mainViewModel.saveFieldSize(mineFieldHeight * mineFieldWidth)
        Log.d(TAG, "Screen Size (height = $screenHeight, width = $screenWidth)")
        Log.d(TAG, "Mine Filed Size (height = $mineFieldHeight, width = $mineFieldWidth)")
        mFieldObjects = Array(mHorizontalSize) { arrayOfNulls(mVerticalSize) }
    }

    private fun initFieldObjectsArray() {
        for (j in 0 until mVerticalSize) {
            for (i in 0 until mHorizontalSize) {
                mFieldObjects[i][j] = FieldObject(null, Constants.EMPTY)
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
            if (mFieldObjects[xMinePos][yMinePos]?.squareImageToShow != Constants.MINE) {
                mFieldObjects[xMinePos][yMinePos] = FieldObject(null, Constants.MINE)
                i++
                Log.d(TAG, "Mine set up at: [$xMinePos, $yMinePos]")
            }
        }
        setUpFieldNumbers()
    }

    private fun initView(view: View) {
        mGameChronometerView = view.findViewById(R.id.chronometer)
        minesCounterView = view.findViewById(R.id.mines_counter)
        mMineFiled = view.findViewById(R.id.mine_field)
        mMineFiled.removeAllViews()
        mFace = view.findViewById(R.id.face)
        mUserPhoto = view.findViewById(R.id.user_photo)
        setFaceImage(FaceType.HAPPY, true)
        mFace.setOnClickListener {
            AlertDialogUtil.showGameWillBeRestartedDialog(requireContext(),
                { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(
                        R.id.mineFiledFragment,
                        arguments,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.mineFiledFragment, true)
                            .build()
                    )
                },
                { dialog, _ ->
                    dialog.dismiss()
                })
        }
    }

    private fun setUpFieldNumbers() {
        for (j in 0 until mVerticalSize) {
            for (i in 0 until mHorizontalSize) {
                if (mFieldObjects[i][j]?.squareImageToShow == Constants.MINE) {
                    if (i - 1 >= 0 && j - 1 >= 0 && mFieldObjects[i - 1][j - 1]?.squareImageToShow != Constants.MINE) { //Start Top position
                        mFieldObjects[i - 1][j - 1]?.squareImageToShow = mFieldObjects[i - 1][j - 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i - 1 >= 0 && mFieldObjects[i - 1][j]?.squareImageToShow != Constants.MINE) { //Start position
                        mFieldObjects[i - 1][j]?.squareImageToShow = mFieldObjects[i - 1][j]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i - 1 >= 0 && j + 1 < mVerticalSize && mFieldObjects[i - 1][j + 1]?.squareImageToShow != Constants.MINE) { //Start Bottom position
                        mFieldObjects[i - 1][j + 1]?.squareImageToShow = mFieldObjects[i - 1][j + 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (j - 1 >= 0 && mFieldObjects[i][j - 1]?.squareImageToShow != Constants.MINE) { //Top position
                        mFieldObjects[i][j - 1]?.squareImageToShow = mFieldObjects[i][j - 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i + 1 < mHorizontalSize && j - 1 >= 0 && mFieldObjects[i + 1][j - 1]?.squareImageToShow != Constants.MINE) { //Top End position
                        mFieldObjects[i + 1][j - 1]?.squareImageToShow = mFieldObjects[i + 1][j - 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i + 1 < mHorizontalSize && mFieldObjects[i + 1][j]?.squareImageToShow != Constants.MINE) { //End position
                        mFieldObjects[i + 1][j]?.squareImageToShow = mFieldObjects[i + 1][j]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (i + 1 < mHorizontalSize && j + 1 < mVerticalSize && mFieldObjects[i + 1][j + 1]?.squareImageToShow != Constants.MINE) { //End Bottom position
                        mFieldObjects[i + 1][j + 1]?.squareImageToShow = mFieldObjects[i + 1][j + 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                    if (j + 1 < mVerticalSize && mFieldObjects[i][j + 1]?.squareImageToShow != Constants.MINE) { //Bottom position
                        mFieldObjects[i][j + 1]?.squareImageToShow = mFieldObjects[i][j + 1]?.squareImageToShow?.let { it + 1 } ?: 0
                    }
                }
            }
        }
    }

    private fun unCoverEmptySquares(xPos: Int, yPos: Int) {
        if (xPos - 1 >= 0 && yPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos - 1]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos - 1][yPos - 1]?.isCovered == true) { //Start Top position
            unCoverSquare(xPos - 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos - 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos - 1)
            }
        }
        if (xPos - 1 >= 0 && mFieldObjects[xPos - 1][yPos]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos - 1][yPos]?.isCovered == true) { //Start position
            unCoverSquare(xPos - 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos]?.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos)
            }
        }
        if (xPos - 1 >= 0 && yPos + 1 < mVerticalSize && mFieldObjects[xPos - 1][yPos + 1]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos - 1][yPos + 1]?.isCovered == true) { //Start Bottom position
            unCoverSquare(xPos - 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos - 1][yPos + 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos - 1, yPos + 1)
            }
        }
        if (yPos - 1 >= 0 && mFieldObjects[xPos][yPos - 1]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos][yPos - 1]?.isCovered == true) { //Top position
            unCoverSquare(xPos, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos - 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos - 1)
            }
        }
        if (xPos + 1 < mHorizontalSize && yPos - 1 >= 0 && mFieldObjects[xPos + 1][yPos - 1]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos + 1][yPos - 1]?.isCovered == true) { //Top End position
            unCoverSquare(xPos + 1, yPos - 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos - 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos - 1)
            }
        }
        if (xPos + 1 < mHorizontalSize && mFieldObjects[xPos + 1][yPos]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos + 1][yPos]?.isCovered == true) { //End position
            unCoverSquare(xPos + 1, yPos)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos]?.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos)
            }
        }
        if (xPos + 1 < mHorizontalSize && yPos + 1 < mVerticalSize && mFieldObjects[xPos + 1][yPos + 1]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos + 1][yPos + 1]?.isCovered == true) { //End Bottom position
            unCoverSquare(xPos + 1, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos + 1][yPos + 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos + 1, yPos + 1)
            }
        }
        if (yPos + 1 < mVerticalSize && mFieldObjects[xPos][yPos + 1]?.squareImageToShow != Constants.MINE &&
            mFieldObjects[xPos][yPos + 1]?.isCovered == true) { //Bottom position
            unCoverSquare(xPos, yPos + 1)
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos + 1]?.squareImageToShow)) {
                unCoverEmptySquares(xPos, yPos + 1)
            }
        }
    }

    private fun isNumberOrMineSquare(imageToShow: Int?): Boolean {
        return when (imageToShow) {
            Constants.MINE,
            Constants.ONE,
            Constants.TWO,
            Constants.THREE,
            Constants.FOUR,
            Constants.FIVE,
            Constants.SIX,
            Constants.SEVEN,
            Constants.EIGHT -> true
            else -> false
        }
    }

    private fun unCoverSquare(x: Int, y: Int) {
        mFieldObjects[x][y]?.isCovered = false
        (mFieldObjects[x][y]?.squareView?.findViewById<View>(R.id.image_button) as ImageView).setImageDrawable(
            ResourcesCompat.getDrawable(resources, getResourceId(x, y), null))
    }

    private fun getResourceId(x: Int, y: Int): Int {
        val resourceId = when (mFieldObjects[x][y]?.squareImageToShow) {
            Constants.MINE -> R.drawable.mine
            Constants.ONE -> R.drawable.one
            Constants.TWO -> R.drawable.two
            Constants.THREE -> R.drawable.three
            Constants.FOUR -> R.drawable.four
            Constants.FIVE -> R.drawable.five
            Constants.SIX -> R.drawable.six
            Constants.SEVEN -> R.drawable.seven
            Constants.EIGHT -> R.drawable.eight
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
                AlertDialogUtil.showLoseMessageDialog(requireContext(),
                    { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigate(
                            R.id.mineFiledFragment,
                            arguments,
                            NavOptions.Builder()
                                .setPopUpTo(R.id.mineFiledFragment, true)
                                .build()
                        )
                    },
                    { dialog, _ ->
                        dialog.dismiss()
                    })
                return@OnClickListener
            }
            imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, resourceId, null))
            mGameChronometerView.startTimer()
        })
    }

    private fun setOnLongClickListener(fieldObject: FieldObject?, imageView: ImageView) {
        fieldObject?.squareView?.setOnLongClickListener(View.OnLongClickListener {
            Log.d(TAG, "CounterNumberOfMines: $mCounterNumberOfMines")
            if (fieldObject.isFlagged) {
                Log.d(TAG, "Square unflagged")
                setFaceImage(FaceType.ANGRY, true)
                mCounterNumberOfMines++
                imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.covered,
                        null
                    )
                )
                fieldObject.isFlagged = false
                minesCounterView.updateCounter(mCounterNumberOfMines)
                return@OnLongClickListener true
            }
            if (fieldObject.isCovered) {
                Log.d(TAG, "Square flagged")
                setFaceImage(FaceType.SCARED, true)
                mCounterNumberOfMines--
                if (mCounterNumberOfMines >= 0) {
                    if (fieldObject.squareImageToShow == Constants.MINE) {
                        mMinesFound++
                    }
                    if (mMinesFound == mDefaultNumberOfMines) {
                        Log.d(TAG, "You won!$mMinesFound")
                        setFaceImage(FaceType.HAPPY, false)
                        AlertDialogUtil.showWinMessageDialog(requireContext(),
                            { dialog, _ ->
                                dialog.dismiss()
                                findNavController().navigate(
                                    R.id.mineFiledFragment,
                                    arguments,
                                    NavOptions.Builder()
                                        .setPopUpTo(R.id.mineFiledFragment, true)
                                        .build()
                                )
                            },
                            { dialog, _ ->
                                dialog.dismiss()
                            })
                        mGameChronometerView.stopTimer()
                        return@OnLongClickListener true
                    }
                    imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.flaged,
                            null
                        )
                    )
                    fieldObject.isFlagged = true
                    minesCounterView.updateCounter(mCounterNumberOfMines)
                }
            }
            true
        })
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