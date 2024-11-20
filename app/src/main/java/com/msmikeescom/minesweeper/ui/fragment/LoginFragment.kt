package com.msmikeescom.minesweeper.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.SignInButton
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.activity.ILogInUIListener
import com.msmikeescom.minesweeper.ui.activity.LogInActivity
import com.msmikeescom.minesweeper.utilities.AlertDialogUtil
import com.msmikeescom.minesweeper.utilities.Constants
import com.msmikeescom.minesweeper.utilities.ToastUtil
import com.msmikeescom.minesweeper.viewmodel.LogInViewModel

class LoginFragment : Fragment() {
    companion object {
        private const val TAG = "LoginFragment"
    }

    private lateinit var googleSignInButton: SignInButton
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var resetPasswordButton: Button
    private lateinit var createAccountButton: Button

    private lateinit var logInViewModel: LogInViewModel
    private lateinit var listener: ILogInUIListener
    private val logInActivity: LogInActivity
        get() = activity as LogInActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        listener = logInActivity
        listener.onShowProgressBar()

        logInViewModel = ViewModelProvider(logInActivity)[LogInViewModel::class.java]

        val googleSignInAccount = logInViewModel.getLastSignedInAccount(logInActivity)
        val passwordSignInAccount = logInViewModel.getPasswordSignInUser()?.currentUser

        when {
            googleSignInAccount != null -> {
                Log.d(TAG, "onViewCreated: Google signing account was found. Navigate to Game Board")
                listener.navigateToMineField(googleSignInAccount)
            }
            passwordSignInAccount != null -> {
                Log.d(TAG, "onViewCreated: Password signing account was found. Navigate to Game Board")
                listener.navigateToMineField()
            }
            else -> {
                Log.d(TAG, "onViewCreated: No Google or Password signing accounts were found. Show login view")
                initViews(view)
                listener.onHideProgressBar()
            }
        }
    }

    private fun initViews(view: View) {
        Log.d(TAG, "initViews")
        googleSignInButton = view.findViewById(R.id.google_sign_in_button)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
        emailEditText = view.findViewById(R.id.email_edit_text)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        signInButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signInWithPassword()
        }
        resetPasswordButton = view.findViewById(R.id.reset_password_button)
        resetPasswordButton.setOnClickListener {
            sendResetPasswordLink()
        }
        createAccountButton = view.findViewById(R.id.create_account_button)
        createAccountButton.setOnClickListener {
            navigateToCreateAccount()
        }
    }

    private fun signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle")
        val signInIntent = logInViewModel.getGoogleSignInClient(logInActivity).signInIntent
        listener.sendGoogleSignInIntent(signInIntent, Constants.RC_SIGN_IN)
    }

    private fun signInWithPassword() {
        Log.d(TAG, "signInWithPassword")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            logInViewModel.getPasswordSignInUser()
                ?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(logInActivity) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithPassword: Success Login. Navigate to Game Board")
                        listener.navigateToMineField()
                    } else {
                        Log.e(TAG, "signInWithPassword: A required field is empty")
                        AlertDialogUtil.showInvalidCredentialsDialog(logInActivity) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                    }
                }
        } else {
            Log.e(TAG, "signInWithPassword: A required field is empty")
            AlertDialogUtil.showEmailOrPasswordFieldCannotBeEmptyDialog(logInActivity) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
    }

    private fun sendResetPasswordLink() {
        Log.d(TAG, "sendResetPasswordLink")
        val email = emailEditText.text.toString()
        if (email.isNotEmpty()) {
            logInViewModel.getPasswordSignInUser()
                ?.sendPasswordResetEmail(email)
                ?.addOnSuccessListener {
                    Log.d(TAG, "sendResetPasswordLink: Reset password link was sent.")
                    emailEditText.setText("")
                    passwordEditText.setText("")
                    ToastUtil.showResetLinkSentToast(logInActivity)
                }
                ?.addOnFailureListener {
                    AlertDialogUtil.showErrorSendingResetLinkDialog(logInActivity) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                }
        } else {
            Log.e(TAG, "signInWithPassword: A required field is empty")
            AlertDialogUtil.showEmailFieldCannotBeEmptyDialog(logInActivity) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
    }

    private fun navigateToCreateAccount() {
        Log.d(TAG, "navigateToCreateAccount")
        findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
    }
}