package com.msmikeescom.minesweeper.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
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

    companion object {
        private const val TAG = "SettingsFragment"
    }

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
        view.findViewById<Button>(R.id.settings_delete_account_button).setOnClickListener {
            confirmDeleteAccount()
        }
        view.findViewById<TextView>(R.id.settings_version).text =
            getString(R.string.settings_version, BuildConfig.VERSION_NAME)
    }

    /** Asks for confirmation (and the password, for email accounts), then deletes the account. */
    private fun confirmDeleteAccount() {
        val needsPassword = gameBoardViewModel.usesEmailSignIn
        val passwordField = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = getString(R.string.prompt_password_confirm)
        }
        AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AlertDialogTheme))
            .setTitle(R.string.settings_delete_account_title)
            .setMessage(
                if (needsPassword) R.string.settings_delete_account_message_password
                else R.string.settings_delete_account_message
            )
            .apply { if (needsPassword) setView(passwordField) }
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.settings_delete_account_confirm) { _, _ ->
                deleteAccount(passwordField.text.toString().takeIf { needsPassword })
            }
            .show()
    }

    private fun deleteAccount(password: String?) {
        val view = requireView()
        val progress = view.findViewById<ProgressBar>(R.id.settings_progress)
        val deleteButton = view.findViewById<Button>(R.id.settings_delete_account_button)
        progress.visibility = View.VISIBLE
        deleteButton.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                gameBoardViewModel.deleteAccount(requireActivity(), password)
                // Logging out clears this device and returns to the login screen.
                (requireActivity() as IGameBoardUIListener).logOut()
            } catch (e: GetCredentialCancellationException) {
                progress.visibility = View.GONE
                deleteButton.isEnabled = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "deleteAccount failed", e)
                progress.visibility = View.GONE
                deleteButton.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_delete_account_failed, e.localizedMessage ?: e.javaClass.simpleName),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
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
