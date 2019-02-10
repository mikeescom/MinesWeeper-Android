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
    private static final int ONE = 101;
    private static final int TWO = 102;
    private static final int THREE = 103;
    private static final int FOUR = 104;
    private static final int FIVE = 105;
    private static final int SIX = 106;
    private static final int SEVEN = 107;
    private static final int EIGHT = 108;

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
        int x;
        int y;

        for (int i = 0 ; i < mNumberOfMines ; i++) {
            x = rand.nextInt(HORIZONTAL_SIZE);
            y = rand.nextInt(VERTICAL_SIZE);
            mFieldData[x][y] = MINE;
            Log.d(TAG, "Mine set up at: [" + x + ", " + y + "]");
        }
    }

    private void initMineField() {
        mMineFiled.setColumnCount(HORIZONTAL_SIZE);
        for (int i = 0; i < HORIZONTAL_SIZE ; i++) {
            for (int j = 0; j < VERTICAL_SIZE ; j++) {
                View itemView = getLayoutInflater().inflate(R.layout.square_layout, null);
                final ImageView imageView = itemView.findViewById(R.id.image_button);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.covered));
                mMineFiled.addView(itemView);

                if (mFieldData[i][j] == MINE) {
                    setButtonListener(itemView, imageView, R.drawable.mine);
                } else {
                    setButtonListener(itemView, imageView, R.drawable.uncovered);
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

    private void setButtonListener (View itemView, final ImageView imageView, final int resourceId) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(getResources().getDrawable(resourceId));
            }
        });
    }
}
