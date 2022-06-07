package com.msmikeescom.minesweeper.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants.GOOGLE_SIGN_IN_ACCOUNT


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 1
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getGoogleSignInClient()
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun getGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun updateUI(googleSignInAccount: GoogleSignInAccount?) {
        googleSignInAccount?.let {
            loadMineField(googleSignInAccount)
        } ?: kotlin.run {
            initViews()
        }
    }

    private fun loadMineField(googleSignInAccount: GoogleSignInAccount?) {
        val intent = Intent(this, MineFieldActivity::class.java).apply {
            putExtra(GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount)
        }
        startActivity(intent)
    }

    private fun initViews() {
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
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }
}