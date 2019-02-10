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
import com.mikeescom.minesweeper.utilities.Constants;

import java.util.Random;

public class MineFieldActivity extends AppCompatActivity {

    private static final String TAG = "MineFieldActivity";

    private static final int HORIZONTAL_SIZE = 12;
    private static final int VERTICAL_SIZE = 20;
    private static final int EMPTY = -1;
    private static final int MINE = 100;
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
    private int[][] mFieldData = new int[12][20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_field);
        initData();
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

    private void buildMineFiled() {
        Random rand = new Random();
        int xMinePos;
        int yMinePos;

        for (int i = 0 ; i < mNumberOfMines ; i++) {
            xMinePos = rand.nextInt(HORIZONTAL_SIZE);
            yMinePos = rand.nextInt(VERTICAL_SIZE);
            mFieldData[xMinePos][yMinePos] = MINE;
            Log.d(TAG, "Mine set up at: [" + xMinePos + ", " + yMinePos + "]");
        }
        setUpFieldNumbers();
    }

    private void setUpFieldNumbers() {
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                if (mFieldData[i][j] == MINE) {
                    if ((i - 1) >= 0 && (j - 1) >= 0 && (mFieldData[i-1][j-1] != MINE)) { //Start Top position
                        mFieldData[i-1][j-1] += 1;
                    }
                    if ((i - 1) >= 0 && (mFieldData[i-1][j] != MINE)) { //Start position
                        mFieldData[i-1][j] += 1;
                    }
                    if ((i - 1) >= 0 && (j + 1) < 20 && (mFieldData[i-1][j+1] != MINE)) { //Start Bottom position
                        mFieldData[i-1][j+1] += 1;
                    }
                    if ((j - 1) >= 0 && (mFieldData[i][j-1] != MINE)) { //Top position
                        mFieldData[i][j-1] += 1;
                    }
                    if ((i + 1) < 12 && (j - 1) >= 0 && (mFieldData[i+1][j-1] != MINE)) { //Top End position
                        mFieldData[i+1][j-1] += 1;
                    }
                    if ((i + 1) < 12 && (mFieldData[i+1][j] != MINE)) { //End position
                        mFieldData[i+1][j] += 1;
                    }
                    if ((i + 1) < 12 && (j + 1) < 20 && (mFieldData[i+1][j+1] != MINE)) { //End Bottom position
                        mFieldData[i+1][j+1] += 1;
                    }
                    if ((j + 1) < 12&& (mFieldData[i][j+1] != MINE)) { //Bottom position
                        mFieldData[i][j+1] += 1;
                    }
                }
            }
        }
    }

    private void initMineField() {
        mMineFiled.setColumnCount(HORIZONTAL_SIZE);
        mMineFiled.setOrientation(GridLayout.HORIZONTAL);
        for (int j = 0; j < VERTICAL_SIZE ; j++) {
            for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
                View itemView = getLayoutInflater().inflate(R.layout.square_layout, null);
                final ImageView imageView = itemView.findViewById(R.id.image_button);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.covered));
                mMineFiled.addView(itemView);

                switch (mFieldData[i][j]) {
                    case MINE : setOnClickListener(itemView, imageView, R.drawable.mine);
                        break;
                    case ONE : setOnClickListener(itemView, imageView, R.drawable.one);
                        break;
                    case TWO : setOnClickListener(itemView, imageView, R.drawable.two);
                        break;
                    case THREE : setOnClickListener(itemView, imageView, R.drawable.three);
                        break;
                    case FOUR : setOnClickListener(itemView, imageView, R.drawable.four);
                        break;
                    case FIVE : setOnClickListener(itemView, imageView, R.drawable.five);
                        break;
                    case SIX : setOnClickListener(itemView, imageView, R.drawable.six);
                        break;
                    case SEVEN : setOnClickListener(itemView, imageView, R.drawable.seven);
                        break;
                    case EIGHT : setOnClickListener(itemView, imageView, R.drawable.eight);
                        break;
                    default:
                        setOnClickListener(itemView, imageView, R.drawable.uncovered);
                        break;
                }

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.flaged));
                        return true;
                    }
                });
            }
        }
    }

    private void setOnClickListener (View itemView, final ImageView imageView, final int resourceId) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(getResources().getDrawable(resourceId));
            }
        });
    }
}
