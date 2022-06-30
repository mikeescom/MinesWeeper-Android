package com.msmikeescom.minesweeper.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants
import com.msmikeescom.minesweeper.viewmodel.MainViewModel

class SettingsActivity : BaseActivity() {

    companion object {
        private const val TAG = "SettingsActivity"
    }

    private lateinit var settingsSaveButton: Button
    private lateinit var numOfMineEditText: TextInputEditText
    private lateinit var numOfMinesLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout(R.layout.activity_settings, "SETTINGS", "", false)

        initViews()

        mainViewModel.userInfo.observe(this) { userInfo ->
            userInfo?.let {
                initViews()
            }
        }
    }

    private fun initViews() {
        numOfMinesLayout = findViewById(R.id.num_of_mine_text_layout)
        numOfMineEditText = findViewById(R.id.num_of_mine_edit_text)
        settingsSaveButton = findViewById(R.id.settings_save_button)
        settingsSaveButton.setOnClickListener {
            val numOfMines = numOfMineEditText.text.toString()
            if (numOfMines.isEmpty()) {
                numOfMinesLayout.error = getString(R.string.settings_no_mines_entered)
            } else {
                mainViewModel.updateNumberOfMines(numOfMines.toInt())
                setResult(Constants.RESULT_SETTINGS_SAVED)
                finish()
            }
        }
    }
}