package com.msmikeescom.minesweeper.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.setUpFullScreen
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

    /** The game screen finishes with RESULT_OK when the player logs out. */
    private val gameBoardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            logOut()
        } else {
            // Back was pressed on the game screen: leave the app rather than show a spinner.
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpFullScreen(findViewById(R.id.app_container_frame))

        logInViewModel = ViewModelProvider(this)[LogInViewModel::class.java].also {
            it.initViewModel(this)
        }
    }

    override fun onShowProgressBar() {
        Log.d(TAG, "showProgress")
        fragmentContainerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun onHideProgressBar() {
        Log.d(TAG, "hideProgress")
        progressBar.visibility = View.GONE
        fragmentContainerView.visibility = View.VISIBLE
    }

    override fun navigateToMineField() {
        Log.d(TAG, "navigateToMineField")
        logInViewModel.saveUserInfo()
        gameBoardLauncher.launch(Intent(this, GameBoardActivity::class.java))
    }

    private fun logOut() {
        logInViewModel.logOut(applicationContext)
        findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
    }
}
