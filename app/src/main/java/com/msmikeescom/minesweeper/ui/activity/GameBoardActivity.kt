package com.msmikeescom.minesweeper.ui.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.setUpFullScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)
        setUpFullScreen(findViewById(R.id.app_container_frame))
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