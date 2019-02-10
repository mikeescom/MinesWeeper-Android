package com.mikeescom.minesweeper.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.mikeescom.minesweeper.R;

public class MineField extends AppCompatActivity {

    private static final int mHorizontalSizeOfField = 12;
    private GridLayout mMineFiled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_field);
        initView();
        initMineField();
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
            final ImageView imageView = view.findViewById(R.id.image_button);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(finalWidth, finalWidth);
            view.setLayoutParams(params);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.covered));
            mMineFiled.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.uncovered));
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.flaged));
                    return true;
                }
            });
        }
    }
}
