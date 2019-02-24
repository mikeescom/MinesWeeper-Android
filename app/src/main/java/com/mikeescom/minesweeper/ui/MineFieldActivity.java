package com.mikeescom.minesweeper.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mikeescom.minesweeper.R;
import com.mikeescom.minesweeper.data.FieldObject;

import java.util.Random;

public class MineFieldActivity extends AppCompatActivity {

    private static final String TAG = "MineFieldActivity";

    private static final String MINESWEEPER_PREFERENCES = "MINESWEEPER_PREFERENCES";
    private static final String SP_DIFFICULTY = "SP_DIFFICULTY";
    private static final int EASY_LEVEL_NUMBER_MINES = 36;
    private static final int MEDIUM_LEVEL_NUMBER_MINES = 51;
    private static final int HARD_LEVEL_NUMBER_MINES = 66;
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

    private SharedPreferences sharedpreferences;

    private GridLayout mMineFiled;
    private ImageView mFace;
    private ImageView mSettings;

    private CountDownTimer mCountDownTimer;
    private boolean mTimerStarted;
    private int mDefaultNumberOfMines;
    private int mNumberOfMines;
    private int mMinesFound = 0;
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
        sharedpreferences = getSharedPreferences(MINESWEEPER_PREFERENCES, Context.MODE_PRIVATE);
        mDefaultNumberOfMines = getDifficulty();
        mNumberOfMines = mDefaultNumberOfMines;
    }

    private void initView() {
        mMineFiled = findViewById(R.id.mine_field);
        mFace = findViewById(R.id.face);
        mFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        mSettings = findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsPopupWindowClick(mMineFiled.getRootView());
            }
        });
        updateCounter(mNumberOfMines);
    }

    private void initFieldObjectsArray() {
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                mFieldObjects[i][j] = new FieldObject(null, EMPTY);
            }
        }
    }

    private void buildMineFiled() {
        Random rand = new Random();
        int xMinePos;
        int yMinePos;

        int i = 0;
        while (i < mNumberOfMines) {
            xMinePos = rand.nextInt(HORIZONTAL_SIZE);
            yMinePos = rand.nextInt(VERTICAL_SIZE);
            if (mFieldObjects[xMinePos][yMinePos].getSquareImageToShow() != MINE) {
                mFieldObjects[xMinePos][yMinePos] = new FieldObject(null, MINE);
                i++;
                Log.d(TAG, "Mine set up at: [" + xMinePos + ", " + yMinePos + "]");
            }
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
        if ((xPos - 1) >= 0 && (yPos - 1) >= 0 && mFieldObjects[xPos-1][yPos-1].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos-1][yPos-1].isCovered()) { //Start Top position
                unCoverSquare(xPos - 1, yPos - 1);
            if (!isNumberOrMineSquare(mFieldObjects[xPos-1][yPos-1].getSquareImageToShow())) {
                unCoverEmptySquares(xPos - 1, yPos - 1);
            }
        }
        if ((xPos - 1) >= 0 && mFieldObjects[xPos-1][yPos].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos-1][yPos].isCovered()) { //Start position
            unCoverSquare(xPos-1, yPos);
            if (!isNumberOrMineSquare(mFieldObjects[xPos-1][yPos].getSquareImageToShow())) {
                unCoverEmptySquares(xPos - 1, yPos);
            }
        }
        if ((xPos - 1) >= 0 && (yPos + 1) < VERTICAL_SIZE && mFieldObjects[xPos-1][yPos+1].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos-1][yPos+1].isCovered()) { //Start Bottom position
            unCoverSquare(xPos-1, yPos+1);
            if (!isNumberOrMineSquare(mFieldObjects[xPos-1][yPos+1].getSquareImageToShow())) {
                unCoverEmptySquares(xPos - 1, yPos + 1);
            }
        }
        if ((yPos - 1) >= 0 && mFieldObjects[xPos][yPos-1].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos][yPos-1].isCovered()) { //Top position
            unCoverSquare(xPos, yPos-1);
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos-1].getSquareImageToShow())) {
                unCoverEmptySquares(xPos, yPos - 1);
            }
        }
        if ((xPos + 1) < HORIZONTAL_SIZE && (yPos - 1) >= 0 && mFieldObjects[xPos+1][yPos-1].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos+1][yPos-1].isCovered()) { //Top End position
            unCoverSquare(xPos+1, yPos-1);
            if (!isNumberOrMineSquare(mFieldObjects[xPos+1][yPos-1].getSquareImageToShow())) {
                unCoverEmptySquares(xPos + 1, yPos - 1);
            }
        }
        if ((xPos + 1) < HORIZONTAL_SIZE && mFieldObjects[xPos+1][yPos].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos+1][yPos].isCovered()) { //End position
            unCoverSquare(xPos+1, yPos);
            if (!isNumberOrMineSquare(mFieldObjects[xPos+1][yPos].getSquareImageToShow())) {
                unCoverEmptySquares(xPos + 1, yPos);
            }
        }
        if ((xPos + 1) < HORIZONTAL_SIZE && (yPos + 1) < VERTICAL_SIZE && mFieldObjects[xPos+1][yPos+1].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos+1][yPos+1].isCovered()) { //End Bottom position
            unCoverSquare(xPos+1, yPos+1);
            if (!isNumberOrMineSquare(mFieldObjects[xPos+1][yPos+1].getSquareImageToShow())) {
                unCoverEmptySquares(xPos + 1, yPos + 1);
            }
        }
        if ((yPos + 1) < VERTICAL_SIZE && mFieldObjects[xPos][yPos+1].getSquareImageToShow() != MINE &&
                mFieldObjects[xPos][yPos+1].isCovered()) { //Bottom position
            unCoverSquare(xPos, yPos+1);
            if (!isNumberOrMineSquare(mFieldObjects[xPos][yPos+1].getSquareImageToShow())) {
                unCoverEmptySquares(xPos, yPos + 1);
            }
        }
    }

    private boolean isNumberOrMineSquare(int imageToShow) {
        switch (imageToShow) {
            case MINE: return true;
            case ONE: return true;
            case TWO: return true;
            case THREE: return true;
            case FOUR: return true;
            case FIVE: return true;
            case SIX: return true;
            case SEVEN: return true;
            case EIGHT: return true;
            default: return false;
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
                setOnClickListener(i, j, mFieldObjects[i][j], imageView, getResourceId(i, j));
                setOnLongClickListener(mFieldObjects[i][j], imageView);
            }
        }
    }

    private void setOnClickListener (final int xPos, final int yPos, final FieldObject fieldObject, final ImageView imageView, final int resourceId) {
        fieldObject.getSquareView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resourceId == R.drawable.uncovered) {
                    setFaceImage(FaceType.SCARED, true);
                    unCoverEmptySquares(xPos, yPos);
                } else if (resourceId == R.drawable.mine) {
                    setFaceImage(FaceType.KILLED, false);
                    uncoverAllSquares();
                    stopTimer();
                    return;
                }
                imageView.setImageDrawable(getResources().getDrawable(resourceId));
                if (!mTimerStarted) {
                    startTimer();
                }
            }
        });
    }

    private void setOnLongClickListener (final FieldObject fieldObject, final ImageView imageView) {
        fieldObject.getSquareView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (fieldObject.isFlagged()) {
                    Log.i(TAG, "Square un flagged");
                    setFaceImage(FaceType.ANGRY, true);
                    mNumberOfMines++;
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.covered));
                    fieldObject.setFlagged(false);
                    updateCounter(mNumberOfMines);
                    return true;
                }
                if (fieldObject.isCovered()) {
                    Log.i(TAG, "Square flagged" );
                    setFaceImage(FaceType.SCARED, true);
                    mNumberOfMines--;
                    if (mNumberOfMines >= 0) {
                        if (fieldObject.getSquareImageToShow() == MINE) {
                            mMinesFound++;
                        }
                        if (mMinesFound == mDefaultNumberOfMines) {
                            Log.i(TAG, "You won!" + mMinesFound);
                            setFaceImage(FaceType.HAPPY, false);
                        }
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.flaged));
                        fieldObject.setFlagged(true);
                        updateCounter(mNumberOfMines);
                    }
                }
                return true;
            }
        });
    }

    private void uncoverAllSquares() {
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                unCoverSquare(i, j);
                mFieldObjects[i][j].getSquareView().setOnClickListener(null);
                mFieldObjects[i][j].getSquareView().setOnLongClickListener(null);
            }
        }
        mMineFiled.setClickable(false);
    }

    private void startTimer() {
        mTimerStarted = true;
        final ImageView tensMinutesImageView = findViewById(R.id.tens_minutes);
        final ImageView unitsMinutesImageView = findViewById(R.id.units_minutes);
        final ImageView tensSecondsImageView = findViewById(R.id.tens_seconds);
        final ImageView unitsSecondsImageView = findViewById(R.id.units_seconds);

        mCountDownTimer = new CountDownTimer(3600000, 1000) {

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
                setImageNumber(unitsMinutesImageView, 0);
                setImageNumber(tensMinutesImageView, 0);
                setImageNumber(unitsSecondsImageView, 0);
                setImageNumber(tensSecondsImageView, 0);
            }
        };
        mCountDownTimer.start();
    }

    private void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
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

    private void setFaceImage(FaceType faceType, final boolean keepSmiling) {
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFace.setImageDrawable(getResources().getDrawable(R.drawable.smile));
                }
            }, 500);
        }
    }

    private void setDifficulty(int difficulty) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(SP_DIFFICULTY, difficulty);
        editor.commit();
    }

    private int getDifficulty() {
        return sharedpreferences.getInt(SP_DIFFICULTY, EASY_LEVEL_NUMBER_MINES);
    }

    private void dismissSettingsPopupWindow(PopupWindow popupWindow, int difficulty, boolean updateDifficulty) {
        String toastText = "";
        popupWindow.dismiss();
        if (updateDifficulty) {
            setDifficulty(difficulty);
            recreate();
            switch (difficulty) {
                case EASY_LEVEL_NUMBER_MINES: toastText = "Easy level"; break;
                case MEDIUM_LEVEL_NUMBER_MINES: toastText = "Medium level"; break;
                case HARD_LEVEL_NUMBER_MINES: toastText = "Hard level"; break;
            }
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }
    }

    public void showSettingsPopupWindowClick(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_windows_settings_layout, null);
        ImageView easy = popupView.findViewById(R.id.easy);
        ImageView medium = popupView.findViewById(R.id.medium);
        ImageView difficult = popupView.findViewById(R.id.difficult);
        Button cancel = popupView.findViewById(R.id.cancel);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        view.setClickable(false);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSettingsPopupWindow(popupWindow, EASY_LEVEL_NUMBER_MINES, true);
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSettingsPopupWindow(popupWindow, MEDIUM_LEVEL_NUMBER_MINES, true);
            }
        });
        difficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSettingsPopupWindow(popupWindow, HARD_LEVEL_NUMBER_MINES, true);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSettingsPopupWindow(popupWindow, EASY_LEVEL_NUMBER_MINES, false);
            }
        });
    }
}
