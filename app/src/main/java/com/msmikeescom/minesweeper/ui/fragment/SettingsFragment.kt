package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.msmikeescom.minesweeper.BuildConfig
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.activity.IGameBoardUIListener
import com.msmikeescom.minesweeper.viewmodel.GameBoardViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Number of mines, the signed-in account with a log out button, and the app version.
 * Opened from the account menu on the game screen.
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val gameBoardViewModel: GameBoardViewModel by activityViewModels()

    private lateinit var numOfMinesLayout: TextInputLayout
    private lateinit var numOfMinesEditText: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val maxMines = gameBoardViewModel.maxMines()

        numOfMinesLayout = view.findViewById(R.id.num_of_mine_text_layout)
        numOfMinesLayout.helperText = getString(R.string.settings_mines_helper, maxMines)
        numOfMinesEditText = view.findViewById(R.id.num_of_mine_edit_text)
        if (savedInstanceState == null) {
            numOfMinesEditText.setText(gameBoardViewModel.getNumberOfMines()?.takeIf { it > 0 }?.toString())
        }
        numOfMinesEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) save(maxMines)
            false
        }
        view.findViewById<Button>(R.id.settings_save_button).setOnClickListener { save(maxMines) }

        view.findViewById<TextView>(R.id.settings_signed_in_as).text =
            gameBoardViewModel.userEmail?.let { getString(R.string.settings_signed_in_as, it) }
        view.findViewById<Button>(R.id.settings_logout_button).setOnClickListener {
            (requireActivity() as IGameBoardUIListener).logOut()
        }
        view.findViewById<TextView>(R.id.settings_version).text =
            getString(R.string.settings_version, BuildConfig.VERSION_NAME)
    }

    private fun save(maxMines: Int) {
        val text = numOfMinesEditText.text?.toString().orEmpty()
        val numberOfMines = text.toIntOrNull()
        when {
            text.isEmpty() ->
                numOfMinesLayout.error = getString(R.string.settings_no_mines_entered)
            numberOfMines == null || numberOfMines !in 1..maxMines ->
                numOfMinesLayout.error = getString(R.string.settings_invalid_number_of_mines, maxMines)
            else -> {
                numOfMinesLayout.error = null
                gameBoardViewModel.updateNumberOfMines(numberOfMines)
                findNavController().navigateUp()
            }
        }
    }
}
