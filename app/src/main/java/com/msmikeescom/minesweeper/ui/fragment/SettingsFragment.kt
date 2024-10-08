package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.activity.GameBoardActivity
import com.msmikeescom.minesweeper.ui.activity.IGameBoardUIListener
import com.msmikeescom.minesweeper.viewmodel.LogInViewModel

class SettingsFragment : Fragment() {
    companion object {
        private const val TAG = "SettingsFragment"
    }

    private lateinit var logInViewModel: LogInViewModel
    private lateinit var listener: IGameBoardUIListener
    private val gameBoardActivity: GameBoardActivity
        get() = activity as GameBoardActivity

    private lateinit var settingsSaveButton: Button
    private lateinit var numOfMineEditText: TextInputEditText
    private lateinit var numOfMinesLayout: TextInputLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = gameBoardActivity

        logInViewModel = ViewModelProvider(gameBoardActivity)[LogInViewModel::class.java]

        initViews(view)
    }

    private fun initViews(view: View) {
        numOfMinesLayout = view.findViewById(R.id.num_of_mine_text_layout)
        numOfMineEditText = view.findViewById(R.id.num_of_mine_edit_text)
        settingsSaveButton = view.findViewById(R.id.settings_save_button)
        settingsSaveButton.setOnClickListener {
            val numOfMines = numOfMineEditText.text.toString()
            if (numOfMines.isEmpty()) {
                numOfMinesLayout.error = getString(R.string.settings_no_mines_entered)
            } else {
                logInViewModel.saveNumberOfMines(numOfMines.toInt())
                //findNavController().navigate(R.id.action_settingsFragment_to_mineFiledFragment)
            }
        }
    }
}