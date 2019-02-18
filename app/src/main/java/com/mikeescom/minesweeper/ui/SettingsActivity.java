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

    private TextView mNumberMinesTitle;
    private EditText mNumberMines;
    private Button mUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        initListeners();
    }


    private void initViews() {
        mNumberMinesTitle = findViewById(R.id.number_mines_title);
        mNumberMines = findViewById(R.id.number_mines);
        mUpdate = findViewById(R.id.update_button);
    }

    private void initListeners() {
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sizeOfField = mNumberMines.getText().toString();
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
