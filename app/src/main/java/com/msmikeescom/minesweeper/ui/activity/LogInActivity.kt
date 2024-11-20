package com.msmikeescom.minesweeper.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_IN
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_OUT
import com.msmikeescom.minesweeper.viewmodel.LogInViewModel

class LogInActivity : AppCompatActivity(), ILogInUIListener {

    companion object {
        private const val TAG = "LogInActivity"
    }

    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    private val fragmentContainerView: FragmentContainerView by lazy {
        findViewById(R.id.nav_host_fragment)
    }

    private lateinit var logInViewModel: LogInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app_container_frame)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        logInViewModel = ViewModelProvider(this)[LogInViewModel::class.java].also {
            it.initViewModel(this)
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition{
                logInViewModel.isLoading.value
            }
        }

        setActionBar()
    }

    private fun setActionBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
    }

    override fun onShowProgressBar() {
        showProgress()
    }

    override fun onHideProgressBar() {
        hideProgress()
    }

    override fun sendGoogleSignInIntent(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, RC_SIGN_IN)
    }

    private fun showProgress() {
        Log.d(TAG, "showProgress")
        fragmentContainerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        Log.d(TAG, "hideProgress")
        progressBar.visibility = View.GONE
        fragmentContainerView.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: (requestCode = $requestCode), (resultCode = $resultCode)")
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSignInResult(task)
            }
            RC_SIGN_OUT -> {
                if (resultCode == RESULT_OK) {
                    logOut()
                }
            }
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.d(TAG, "handleGoogleSignInResult")
        try {
            navigateToMineField(completedTask.getResult(ApiException::class.java))
        } catch (e: ApiException) {
            Log.e(TAG, "handleGoogleSignInResult:failed code= ${e.statusCode}")
        }
    }

    override fun navigateToMineField(googleSignInAccount: GoogleSignInAccount?) {
        Log.d(TAG, "navigateToMineField")
        googleSignInAccount?.let {
            logInViewModel.saveUserInfo(it)
        }
        startActivityForResult(Intent(this, GameBoardActivity::class.java), RC_SIGN_OUT)
    }

    private fun logOut() {
        if (logInViewModel.getPasswordSignInUser()?.currentUser != null) {
            logInViewModel.getPasswordSignInUser()?.signOut()
        } else {
            logInViewModel.getGoogleSignInClient(this).signOut()
        }
        logInViewModel.deleteUserInfo()
        findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
    }

}