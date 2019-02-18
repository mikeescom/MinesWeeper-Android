package com.mikeescom.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;

import com.mikeescom.minesweeper.R;
import com.mikeescom.minesweeper.utilities.Constants;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private TextView mSizeOfFieldtitle;
    private EditText mSizeOfText;
    private Button mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        initListeners();
    }


    private void initViews() {
        mSizeOfFieldtitle = findViewById(R.id.tv_size_of_field_title);
        mSizeOfText = findViewById(R.id.et_size_of_field);
        mPlay = findViewById(R.id.bt_play);
    }

    private void initListeners() {
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sizeOfField = mSizeOfText.getText().toString();
                if (TextUtils.isEmpty(sizeOfField) ||
                        (Integer.parseInt(sizeOfField) <= 0) ||
                        (Integer.parseInt(sizeOfField) >=200)) {
                    Toast.makeText(SettingsActivity.this,
                            "You should add a number grater than zero and less than 200", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(SettingsActivity.this, MineFieldActivity.class);
                    intent.putExtra(Constants.INTENT_SIZE_OF_FIELD, Integer.parseInt(sizeOfField));
                    startActivity(intent);
                }
            }
        });
    }
}
