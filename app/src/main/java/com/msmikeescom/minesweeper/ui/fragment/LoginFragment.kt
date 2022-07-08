package com.msmikeescom.minesweeper.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.IMainActivityUIListener
import com.msmikeescom.minesweeper.ui.activity.MainActivity
import com.msmikeescom.minesweeper.utilities.Constants
import com.msmikeescom.minesweeper.viewmodel.MainViewModel

class LoginFragment : Fragment() {
    companion object {
        private val TAG = "LoginFragment"
    }

    private var mineFieldLaunched: Boolean = false
    private lateinit var mainViewModel: MainViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton
    private lateinit var listener: IMainActivityUIListener
    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setActionBar(null, null, false)
        listener = mainActivity
        listener.onShowProgress()

        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java]

        googleSignInClient = mainViewModel.getGoogleSignInClient(mainActivity)
        GoogleSignIn.getLastSignedInAccount(mainActivity)?.let {
            navigateToMineField(it)
        } ?: kotlin.run {
            initViews(view)
        }
    }

    private fun initViews(view: View) {
        Log.d(TAG, "initViews")
        listener.onHideProgress()
        signInButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.RC_SIGN_IN -> {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            Constants.RC_SIGN_OUT -> {
                listener.onHideProgress()
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            navigateToMineField(account)
        } catch (e: ApiException) {
            Log.e(TAG, "signInResult:failed code= ${e.statusCode}")
        }
    }

    private fun navigateToMineField(googleSignInAccount: GoogleSignInAccount) {
        mainViewModel.saveUserInfo(googleSignInAccount)
        findNavController().navigate(R.id.action_loginFragment_to_mineFiledFragment)
    }
}