package com.mikeescom.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.mikeescom.minesweeper.R;
import com.mikeescom.minesweeper.utilities.Constants;

import java.util.zip.Inflater;

public class MineField extends AppCompatActivity {

    private int mHorizontalSizeOfField;
    private GridLayout mMineFiled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_field);
        initData();
        initView();
        initMineField();

    }

    private void initData() {
        Intent intent = getIntent();
        mHorizontalSizeOfField = Integer.parseInt(intent.getStringExtra(Constants.INTENT_SIZE_OF_FIELD));
    }

    private void initView() {
        mMineFiled = findViewById(R.id.mine_field);
    }

    private void initMineField() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mMineFiled.setColumnCount(mHorizontalSizeOfField);
        int squareSize = displayMetrics.widthPixels / mHorizontalSizeOfField;
        int verticalNumberOfSquares = displayMetrics.heightPixels / squareSize;
        int finalWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, squareSize, displayMetrics);
        for (int i = 0; i < (mHorizontalSizeOfField * verticalNumberOfSquares) ; i++) {
            View view = getLayoutInflater().inflate(R.layout.square_layout, null);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(finalWidth, finalWidth);
            view.setLayoutParams(params);
            mMineFiled.addView(view);
        }
    }
}
