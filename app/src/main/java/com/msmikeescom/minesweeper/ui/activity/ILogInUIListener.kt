package com.msmikeescom.minesweeper.ui.activity

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface ILogInUIListener {
    fun onShowProgressBar()
    fun onHideProgressBar()
    fun sendGoogleSignInIntent(intent: Intent, requestCode: Int)
    fun navigateToMineField(googleSignInAccount: GoogleSignInAccount? = null)
}