package com.msmikeescom.minesweeper.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.appbar.AppBarLayout
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.ui.IMainActivityUIListener
import com.msmikeescom.minesweeper.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), IMainActivityUIListener {

    companion object {
        private val TAG = "MainActivity"
    }

    private val appBarLayout: AppBarLayout by lazy {
        findViewById(R.id.app_bar_layout)
    }

    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    private val fragmentContainerView: FragmentContainerView by lazy {
        findViewById(R.id.nav_host_fragment)
    }

    private lateinit var mainViewModel: MainViewModel
    private var nextRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java].also {
            it.initViewModel(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_logout -> {
                logOut()
                true
            }
            else -> {
                val navController = findNavController(R.id.nav_host_fragment)
                return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            }
        }
    }

    fun setActionBar(actionBarTitle: String?, actionBarSubtitle: String?, displayHome: Boolean) {
        actionBarTitle?.let {
            appBarLayout.visibility = View.VISIBLE
            setSupportActionBar(findViewById(R.id.toolbar))
            val actionbar: ActionBar? = supportActionBar
            actionbar?.apply {
                displayOptions = ActionBar.DISPLAY_SHOW_TITLE
                this.title = it
                actionBarSubtitle?.let { this.subtitle = it }
                setDisplayHomeAsUpEnabled(displayHome)
            }
        } ?: run {
            appBarLayout.visibility = View.GONE
        }
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

    private fun logOut() {
        mainViewModel.getGoogleSignInClient(this).signOut()
        mainViewModel.deleteUserInfo()
        findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
    }

    private fun getNextRequestCode(): Int {
        nextRequestCode++
        return nextRequestCode
    }
}