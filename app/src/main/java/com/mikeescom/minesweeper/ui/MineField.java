package com.mikeescom.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.mikeescom.minesweeper.R;
import com.mikeescom.minesweeper.utilities.Constants;

public class MineField extends AppCompatActivity {

    private int mSizeOfField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_field);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mSizeOfField = Integer.parseInt(intent.getStringExtra(Constants.INTENT_SIZE_OF_FIELD));
    }

    private void initMineField() {

    }
}
