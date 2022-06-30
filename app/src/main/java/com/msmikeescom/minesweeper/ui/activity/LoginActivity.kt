package com.msmikeescom.minesweeper.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_IN
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_OUT
import com.msmikeescom.minesweeper.viewmodel.MainViewModel

class LoginActivity : BaseActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    private var googleSignInAccount: GoogleSignInAccount? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout(R.layout.activity_login, null, null, false)

        showProgress()

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java].also {
            it.initViewModel(this)
        }

        googleSignInClient = mainViewModel.getGoogleSignInClient(this)

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

        if (googleSignInAccount == null) {
            initViews()
        } else {
            mainViewModel.loadUserInfo(googleSignInAccount)
        }

        mainViewModel.userInfo.observe(this) { userInfo ->
            userInfo?.let {
                if (it.userId == googleSignInAccount?.id) {
                    lunchMineField()
                } else {
                    mainViewModel.saveUserInfo(googleSignInAccount)
                }
            } ?: kotlin.run {
                mainViewModel.saveUserInfo(googleSignInAccount)
            }
        }
    }

    private fun initViews() {
        Log.d(TAG, "initViews")
        hideProgress()
        signInButton = findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            RC_SIGN_OUT -> {
                hideProgress()
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            googleSignInAccount = account
            mainViewModel.saveUserInfo(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }
}