package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.activity.ILogInUIListener
import com.msmikeescom.minesweeper.ui.activity.LogInActivity
import com.msmikeescom.minesweeper.utilities.AlertDialogUtil
import com.msmikeescom.minesweeper.viewmodel.LogInViewModel

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {
    companion object {
        private const val TAG = "CreateAccountFragment"
    }

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var returnToLogInButton: Button

    private val logInViewModel: LogInViewModel by activityViewModels()
    private lateinit var listener: ILogInUIListener
    private val logInActivity: LogInActivity
        get() = activity as LogInActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        listener = logInActivity
        listener.onShowProgressBar()


        initViews(view)
    }

    private fun initViews(view: View) {
        Log.d(TAG, "initViews")
        listener.onHideProgressBar()
        emailEditText = view.findViewById(R.id.email_edit_text)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        createAccountButton = view.findViewById(R.id.create_account_button)
        createAccountButton.setOnClickListener {
            createAccountWithEmail()
        }
        returnToLogInButton = view.findViewById(R.id.return_to_log_in_button)
        returnToLogInButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun createAccountWithEmail() {
        Log.d(TAG, "createAccountWithEmail")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            logInViewModel.auth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(logInActivity) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createAccountWithEmail: Success")
                        findNavController().navigateUp()
                    } else {
                        AlertDialogUtil.showEmailAddressIsAlreadyUsedDialog(logInActivity){ dialogInterface, _ ->
                            dialogInterface.dismiss()
                            findNavController().navigateUp()
                        }
                        Log.e(TAG, "createAccountWithEmail: Error creating account")
                    }
                }
        } else {
            Log.e(TAG, "createAccountWithEmail: A required field is empty")
            AlertDialogUtil.showEmailOrPasswordFieldCannotBeEmptyDialog(logInActivity) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
    }

}