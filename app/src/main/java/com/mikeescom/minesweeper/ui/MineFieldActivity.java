package com.mikeescom.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.mikeescom.minesweeper.R;
import com.mikeescom.minesweeper.data.Coordinates;
import com.mikeescom.minesweeper.data.FieldObject;

import java.util.Random;

public class MineFieldActivity extends AppCompatActivity {

    private static final String TAG = "MineFieldActivity";

    private static final int DEFAULT_NUMBER_MINES = 25;
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

    private enum FaceType {
        ANGRY,
        HAPPY,
        KILLED,
        SCARED,
        SMILE
    }

    private GridLayout mMineFiled;
    private ImageView mFace;
    private ImageView mSettings;

    private boolean mTimerStarted;
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
        mNumberOfMines = DEFAULT_NUMBER_MINES;
    }

    private void initView() {
        mMineFiled = findViewById(R.id.mine_field);
        mFace = findViewById(R.id.face);
        mSettings = findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineFieldActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        updateCounter(mNumberOfMines);
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
                    if ((i - 1) >= 0 && (j + 1) < VERTICAL_SIZE && (mFieldObjects[i-1][j+1].getSquareImageToShow() != MINE)) { //Start Bottom position
                        mFieldObjects[i-1][j+1].setSquareImageToShow(mFieldObjects[i-1][j+1].getSquareImageToShow() + 1);
                    }
                    if ((j - 1) >= 0 && (mFieldObjects[i][j-1].getSquareImageToShow() != MINE)) { //Top position
                        mFieldObjects[i][j-1].setSquareImageToShow(mFieldObjects[i][j-1].getSquareImageToShow() + 1);
                    }
                    if ((i + 1) < HORIZONTAL_SIZE && (j - 1) >= 0 && (mFieldObjects[i+1][j-1].getSquareImageToShow() != MINE)) { //Top End position
                        mFieldObjects[i+1][j-1].setSquareImageToShow(mFieldObjects[i+1][j-1].getSquareImageToShow() + 1);
                    }
                    if ((i + 1) < HORIZONTAL_SIZE && (mFieldObjects[i+1][j].getSquareImageToShow() != MINE)) { //End position
                        mFieldObjects[i+1][j].setSquareImageToShow(mFieldObjects[i+1][j].getSquareImageToShow() + 1);
                    }
                    if ((i + 1) < HORIZONTAL_SIZE && (j + 1) < VERTICAL_SIZE && (mFieldObjects[i+1][j+1].getSquareImageToShow() != MINE)) { //End Bottom position
                        mFieldObjects[i+1][j+1].setSquareImageToShow(mFieldObjects[i+1][j+1].getSquareImageToShow() + 1);
                    }
                    if ((j + 1) < VERTICAL_SIZE && (mFieldObjects[i][j+1].getSquareImageToShow() != MINE)) { //Bottom position
                        mFieldObjects[i][j+1].setSquareImageToShow(mFieldObjects[i][j+1].getSquareImageToShow() + 1);
                    }
                }
            }
        }
    }

    private void unCoverEmptySquares(int xPos, int yPos) {
        if ((xPos - 1) >= 0 && (yPos - 1) >= 0 &&
                (isEmptySquare(mFieldObjects[xPos-1][yPos-1].getSquareImageToShow())) &&
                mFieldObjects[xPos-1][yPos-1].isCovered()) { //Start Top position
            unCoverSquare(xPos-1, yPos-1);
            unCoverEmptySquares(xPos-1, yPos-1);
        }
        if ((xPos - 1) >= 0 &&
                (isEmptySquare(mFieldObjects[xPos-1][yPos].getSquareImageToShow())) &&
                mFieldObjects[xPos-1][yPos].isCovered()) { //Start position
            unCoverSquare(xPos-1, yPos);
            unCoverEmptySquares(xPos-1, yPos);
        }
        if ((xPos - 1) >= 0 && (yPos + 1) < VERTICAL_SIZE &&
                (isEmptySquare(mFieldObjects[xPos-1][yPos+1].getSquareImageToShow())) &&
                mFieldObjects[xPos-1][yPos+1].isCovered()) { //Start Bottom position
            unCoverSquare(xPos-1, yPos+1);
            unCoverEmptySquares(xPos-1, yPos+1);
        }
        if ((yPos - 1) >= 0 &&
                (isEmptySquare(mFieldObjects[xPos][yPos-1].getSquareImageToShow())) &&
                mFieldObjects[xPos][yPos-1].isCovered()) { //Top position
            unCoverSquare(xPos, yPos-1);
            unCoverEmptySquares(xPos, yPos-1);
        }
        if ((xPos + 1) < HORIZONTAL_SIZE && (yPos - 1) >= 0 &&
                (isEmptySquare(mFieldObjects[xPos+1][yPos-1].getSquareImageToShow())) &&
                mFieldObjects[xPos+1][yPos-1].isCovered()) { //Top End position
            unCoverSquare(xPos+1, yPos-1);
            unCoverEmptySquares(xPos+1, yPos-1);
        }
        if ((xPos + 1) < HORIZONTAL_SIZE &&
                (isEmptySquare(mFieldObjects[xPos+1][yPos].getSquareImageToShow())) &&
                mFieldObjects[xPos+1][yPos].isCovered()) { //End position
            unCoverSquare(xPos+1, yPos);
            unCoverEmptySquares(xPos+1, yPos);
        }
        if ((xPos + 1) < HORIZONTAL_SIZE && (yPos + 1) < VERTICAL_SIZE &&
                (isEmptySquare(mFieldObjects[xPos+1][yPos+1].getSquareImageToShow())) &&
                mFieldObjects[xPos+1][yPos+1].isCovered()) { //End Bottom position
            unCoverSquare(xPos+1, yPos+1);
            unCoverEmptySquares(xPos+1, yPos+1);
        }
        if ((yPos + 1) < VERTICAL_SIZE &&
                (isEmptySquare(mFieldObjects[xPos][yPos+1].getSquareImageToShow())) &&
                mFieldObjects[xPos][yPos+1].isCovered()) { //Bottom position
            unCoverSquare(xPos, yPos+1);
            unCoverEmptySquares(xPos, yPos+1);
        }
    }

    private boolean isEmptySquare(int imageToShow) {
        switch (imageToShow) {
            case MINE: return false;
            case ONE: return false;
            case TWO: return false;
            case THREE: return false;
            case FOUR: return false;
            case FIVE: return false;
            case SIX: return false;
            case SEVEN: return false;
            case EIGHT: return false;
            default: return true;
        }
    }

    private void unCoverSquare(int x, int y) {
        mFieldObjects[x][y].setCovered(false);
        ((ImageView)mFieldObjects[x][y].getSquareView().
                findViewById(R.id.image_button)).setImageDrawable(getResources().getDrawable(getResourceId(x, y)));
    }

    private int getResourceId(int x, int y) {
        int resourceId = R.drawable.uncovered;
        switch (mFieldObjects[x][y].getSquareImageToShow()) {
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
        return resourceId;
    }

    private void initMineField() {
        mMineFiled.setColumnCount(HORIZONTAL_SIZE);
        mMineFiled.setOrientation(GridLayout.HORIZONTAL);
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                View squareView = getLayoutInflater().inflate(R.layout.square_layout, null);
                final ImageView imageView = squareView.findViewById(R.id.image_button);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.covered));
                mFieldObjects[i][j].setSquareView(squareView);
                mMineFiled.addView(squareView);
                setOnClickListener(i, j, squareView, imageView, getResourceId(i, j));
                setOnLongClickListener(squareView, imageView);
            }
        }
    }

    private void setOnClickListener (final int xPos, final int yPos, final View squareView, final ImageView imageView, final int resourceId) {
        squareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFaceImage(FaceType.SCARED, true);
                if (resourceId == R.drawable.uncovered) {
                    unCoverEmptySquares(xPos, yPos);
                }
                imageView.setImageDrawable(getResources().getDrawable(resourceId));
                if (!mTimerStarted) {
                    startTimer();
                }
            }
        });
    }

    private void setOnLongClickListener (View itemView, final ImageView imageView) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setFaceImage(FaceType.SCARED, true);
                mNumberOfMines--;
                if (mNumberOfMines >= 0) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.flaged));
                    updateCounter(mNumberOfMines);
                }
                return true;
            }
        });
    }

    private void startTimer() {
        mTimerStarted = true;
        final ImageView tensMinutesImageView = findViewById(R.id.tens_minutes);
        final ImageView unitsMinutesImageView = findViewById(R.id.units_minutes);
        final ImageView tensSecondsImageView = findViewById(R.id.tens_seconds);
        final ImageView unitsSecondsImageView = findViewById(R.id.units_seconds);

        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "millisUntilFinished: " + millisUntilFinished);
                Log.i(TAG, "millisSinceStarted: " + (3600000 - millisUntilFinished));
                int seconds = 0;
                int minutes = 0;
                int chronometerTime = (int)((3600000 - millisUntilFinished) / 1000);
                Log.i(TAG, "Time: " + chronometerTime );
                minutes = chronometerTime / 60;
                seconds = chronometerTime - (minutes * 60);
                if ((chronometerTime % 60) != 0) {
                    int units = seconds % 10;
                    seconds = seconds / 10;
                    int tens = seconds % 10;
                    setImageNumber(unitsSecondsImageView, units);
                    setImageNumber(tensSecondsImageView, tens);
                } else {
                    int units = minutes % 10;
                    minutes = minutes / 10;
                    int tens = minutes % 10;
                    setImageNumber(unitsMinutesImageView, units);
                    setImageNumber(tensMinutesImageView, tens);
                    setImageNumber(unitsSecondsImageView, 0);
                    setImageNumber(tensSecondsImageView, 0);
                }
            }

            public void onFinish() {

            }
        }.start();
    }

    private void updateCounter(int number) {
        ImageView hundredsImageView = findViewById(R.id.counter_hundreds);
        ImageView tensImageView = findViewById(R.id.counter_tens);
        ImageView unitsImageView = findViewById(R.id.counter_units);

        int units = number % 10;
        number = number / 10;
        int tens = number % 10;
        number = number / 10;
        int hundreds = number % 10;

        setImageNumber(unitsImageView, units);
        setImageNumber(tensImageView, tens);
        setImageNumber(hundredsImageView, hundreds);
    }

    private void setImageNumber(ImageView imageView, int digit) {
        switch (digit) {
            case 0: imageView.setImageDrawable(getResources().getDrawable(R.drawable.zero_digit)); break;
            case 1: imageView.setImageDrawable(getResources().getDrawable(R.drawable.one_digit)); break;
            case 2: imageView.setImageDrawable(getResources().getDrawable(R.drawable.two_digit)); break;
            case 3: imageView.setImageDrawable(getResources().getDrawable(R.drawable.three_digit)); break;
            case 4: imageView.setImageDrawable(getResources().getDrawable(R.drawable.four_digit)); break;
            case 5: imageView.setImageDrawable(getResources().getDrawable(R.drawable.five_digit)); break;
            case 6: imageView.setImageDrawable(getResources().getDrawable(R.drawable.six_digit)); break;
            case 7: imageView.setImageDrawable(getResources().getDrawable(R.drawable.seven_digit)); break;
            case 8: imageView.setImageDrawable(getResources().getDrawable(R.drawable.eight_digit)); break;
            case 9: imageView.setImageDrawable(getResources().getDrawable(R.drawable.nine_digit)); break;
        }
    }

    private void setFaceImage(FaceType faceType, boolean keepSmiling) {
        int resource = 0;
        switch (faceType) {
            case ANGRY: resource = R.drawable.angry; break;
            case HAPPY: resource = R.drawable.happy; break;
            case KILLED: resource = R.drawable.killed; break;
            case SCARED: resource = R.drawable.scared; break;
            case SMILE: resource = R.drawable.smile; break;
        }
        mFace.setImageDrawable(getResources().getDrawable(resource));
        if(keepSmiling) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFace.setImageDrawable(getResources().getDrawable(R.drawable.smile));
                }
            }, 500);
        }
    }
}
