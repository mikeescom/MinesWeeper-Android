package com.mikeescom.minesweeper.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.mikeescom.minesweeper.R;
import com.mikeescom.minesweeper.data.Coordinates;
import com.mikeescom.minesweeper.data.FieldObject;
import com.mikeescom.minesweeper.utilities.Constants;

import java.util.Random;

public class MineFieldActivity extends AppCompatActivity {

    private static final String TAG = "MineFieldActivity";

    private static final int HORIZONTAL_SIZE = 12;
    private static final int VERTICAL_SIZE = 20;
    private static final int EMPTY = 0;
    private static final int MINE = 9;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int SIX = 6;
    private static final int SEVEN = 7;
    private static final int EIGHT = 8;

    private GridLayout mMineFiled;
    private int mNumberOfMines;
    private FieldObject[][] mFieldObjects = new FieldObject[HORIZONTAL_SIZE][VERTICAL_SIZE];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_field);
        initData();
        initFieldObjectsArray();
        buildMineFiled();
        initView();
        initMineField();
    }

    private void initData() {
        mNumberOfMines = getIntent().getIntExtra(Constants.INTENT_SIZE_OF_FIELD, 10);
    }

    private void initView() {
        mMineFiled = findViewById(R.id.mine_field);
    }

    private void initFieldObjectsArray() {
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                mFieldObjects[i][j] = new FieldObject(null, new Coordinates(i, j), EMPTY);
            }
        }
    }

    private void buildMineFiled() {
        Random rand = new Random();
        int xMinePos;
        int yMinePos;

        for (int i = 0 ; i < mNumberOfMines ; i++) {
            xMinePos = rand.nextInt(HORIZONTAL_SIZE);
            yMinePos = rand.nextInt(VERTICAL_SIZE);
            mFieldObjects[xMinePos][yMinePos] = new FieldObject(null, new Coordinates(xMinePos, yMinePos), MINE);
            Log.d(TAG, "Mine set up at: [" + xMinePos + ", " + yMinePos + "]");
        }
        setUpFieldNumbers();
    }

    private void setUpFieldNumbers() {
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                if (mFieldObjects[i][j].getSquareImageToShow() == MINE) {
                    if ((i - 1) >= 0 && (j - 1) >= 0 && (mFieldObjects[i-1][j-1].getSquareImageToShow() != MINE)) { //Start Top position
                        mFieldObjects[i-1][j-1].setSquareImageToShow(mFieldObjects[i-1][j-1].getSquareImageToShow() + 1);
                    }
                    if ((i - 1) >= 0 && (mFieldObjects[i-1][j].getSquareImageToShow() != MINE)) { //Start position
                        mFieldObjects[i-1][j].setSquareImageToShow(mFieldObjects[i-1][j].getSquareImageToShow() + 1);
                    }
                    if ((i - 1) >= 0 && (j + 1) < 20 && (mFieldObjects[i-1][j+1].getSquareImageToShow() != MINE)) { //Start Bottom position
                        mFieldObjects[i-1][j+1].setSquareImageToShow(mFieldObjects[i-1][j+1].getSquareImageToShow() + 1);
                    }
                    if ((j - 1) >= 0 && (mFieldObjects[i][j-1].getSquareImageToShow() != MINE)) { //Top position
                        mFieldObjects[i][j-1].setSquareImageToShow(mFieldObjects[i][j-1].getSquareImageToShow() + 1);
                    }
                    if ((i + 1) < 12 && (j - 1) >= 0 && (mFieldObjects[i+1][j-1].getSquareImageToShow() != MINE)) { //Top End position
                        mFieldObjects[i+1][j-1].setSquareImageToShow(mFieldObjects[i+1][j-1].getSquareImageToShow() + 1);
                    }
                    if ((i + 1) < 12 && (mFieldObjects[i+1][j].getSquareImageToShow() != MINE)) { //End position
                        mFieldObjects[i+1][j].setSquareImageToShow(mFieldObjects[i+1][j].getSquareImageToShow() + 1);
                    }
                    if ((i + 1) < 12 && (j + 1) < 20 && (mFieldObjects[i+1][j+1].getSquareImageToShow() != MINE)) { //End Bottom position
                        mFieldObjects[i+1][j+1].setSquareImageToShow(mFieldObjects[i+1][j+1].getSquareImageToShow() + 1);
                    }
                    if ((j + 1) < 20&& (mFieldObjects[i][j+1].getSquareImageToShow() != MINE)) { //Bottom position
                        mFieldObjects[i][j+1].setSquareImageToShow(mFieldObjects[i][j+1].getSquareImageToShow() + 1);
                    }
                }
            }
        }
    }

    private void unCoverEmptySquares(int xPos, int yPos) {
        if ((xPos - 1) >= 0 && (yPos - 1) >= 0 && (mFieldObjects[xPos-1][yPos-1].getSquareImageToShow() == EMPTY)) { //Start Top position
            unCoverSquare(xPos-1, yPos-1);
            unCoverEmptySquares(xPos-1, yPos-1);
        }
        if ((xPos - 1) >= 0 && (mFieldObjects[xPos-1][yPos].getSquareImageToShow() == EMPTY)) { //Start position
            unCoverSquare(xPos-1, yPos);
            unCoverEmptySquares(xPos-1, yPos);
        }
        if ((xPos - 1) >= 0 && (yPos + 1) < 20 && (mFieldObjects[xPos-1][yPos+1].getSquareImageToShow() == EMPTY)) { //Start Bottom position
            unCoverSquare(xPos-1, yPos+1);
            unCoverEmptySquares(xPos-1, yPos+1);
        }
        if ((yPos - 1) >= 0 && (mFieldObjects[xPos][yPos-1].getSquareImageToShow() == EMPTY)) { //Top position
            unCoverSquare(xPos, yPos-1);
            unCoverEmptySquares(xPos, yPos-1);
        }
        if ((xPos + 1) < 12 && (yPos - 1) >= 0 && (mFieldObjects[xPos+1][yPos-1].getSquareImageToShow() == EMPTY)) { //Top End position
            unCoverSquare(xPos+1, yPos-1);
            unCoverEmptySquares(xPos+1, yPos-1);
        }
        if ((xPos + 1) < 12 && (mFieldObjects[xPos+1][yPos].getSquareImageToShow() == EMPTY)) { //End position
            unCoverSquare(xPos+1, yPos);
            unCoverEmptySquares(xPos+1, yPos);
        }
        if ((xPos + 1) < 12 && (yPos + 1) < 20 && (mFieldObjects[xPos+1][yPos+1].getSquareImageToShow() == EMPTY)) { //End Bottom position
            unCoverSquare(xPos+1, yPos+1);
            unCoverEmptySquares(xPos+1, yPos+1);
        }
        if ((yPos + 1) < 20 && (mFieldObjects[xPos][yPos+1].getSquareImageToShow() == EMPTY)) { //Bottom position
            unCoverSquare(xPos, yPos+1);
            unCoverEmptySquares(xPos, yPos+1);
        }
    }

    private void unCoverSquare(int x, int y) {
        ((ImageView)mFieldObjects[x][y].getSquareView().
                findViewById(R.id.image_button)).setImageDrawable(getResources().getDrawable(R.drawable.uncovered));
    }

    private void initMineField() {
        int resourceId = R.drawable.uncovered;
        mMineFiled.setColumnCount(HORIZONTAL_SIZE);
        mMineFiled.setOrientation(GridLayout.HORIZONTAL);
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                View squareView = getLayoutInflater().inflate(R.layout.square_layout, null);
                final ImageView imageView = squareView.findViewById(R.id.image_button);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.covered));
                mFieldObjects[i][j].setSquareView(squareView);
                mMineFiled.addView(squareView);

                switch (mFieldObjects[i][j].getSquareImageToShow()) {
                    case MINE : resourceId = R.drawable.mine;
                        break;
                    case ONE : resourceId = R.drawable.one;
                        break;
                    case TWO : resourceId = R.drawable.two;
                        break;
                    case THREE : resourceId = R.drawable.three;
                        break;
                    case FOUR : resourceId = R.drawable.four;
                        break;
                    case FIVE : resourceId = R.drawable.five;
                        break;
                    case SIX : resourceId = R.drawable.six;
                        break;
                    case SEVEN : resourceId = R.drawable.seven;
                        break;
                    case EIGHT : resourceId = R.drawable.eight;
                        break;
                    default:
                        resourceId = R.drawable.uncovered;
                        break;
                }

                setOnClickListener(i, j, squareView, imageView, resourceId);
                setOnLongClickListener(squareView, imageView);
            }
        }
    }

    private void setOnClickListener (final int xPos, final int yPos, final View squareView, final ImageView imageView, final int resourceId) {
        squareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resourceId == R.drawable.uncovered) {
                    //unCoverEmptySquares(xPos, yPos);
                }
                imageView.setImageDrawable(getResources().getDrawable(resourceId));
            }
        });
    }

    private void setOnLongClickListener (View itemView, final ImageView imageView) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.flaged));
                return true;
            }
        });
    }
}
