package com.msmikeescom.minesweeper.ui.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.viewmodel.GameBoardViewModel

class GameBoardActivity : AppCompatActivity(), IGameBoardUIListener {
    companion object {
        private const val TAG = "GameBoardActivity"
    }

    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    private val fragmentContainerView: FragmentContainerView by lazy {
        findViewById(R.id.nav_host_fragment)
    }

    private lateinit var gameBoardViewModel: GameBoardViewModel

    private var nextRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_board)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app_container_frame)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameBoardViewModel = ViewModelProvider(this)[GameBoardViewModel::class.java].also {
            it.initViewModel(this)
        }

        setActionBar()
    }

    private fun setActionBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
    }

    override fun onShowProgress() {
        showProgress()
    }

    override fun onHideProgress() {
        hideProgress()
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

    override fun logOut() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}