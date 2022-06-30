package com.msmikeescom.minesweeper.ui.activity

import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_OUT
import com.msmikeescom.minesweeper.utilities.Constants.RESULT_SETTINGS_NOT_SAVED
import com.msmikeescom.minesweeper.utilities.Constants.RESULT_SETTINGS_SAVED
import com.msmikeescom.minesweeper.utilities.TypefaceSpan
import com.msmikeescom.minesweeper.viewmodel.MainViewModel

open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    lateinit var mainViewModel: MainViewModel

    private var nextRequestCode = 100

    private val settingsRequestCode = getNextRequestCode()

    private val appBarLayout: AppBarLayout by lazy {
        findViewById(R.id.app_bar_layout)
    }

    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    private val contentFrame: FrameLayout by lazy {
        findViewById(R.id.content_frame)
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
            R.id.action_settings -> {
                launchSettings()
                true
            }
            /*R.id.action_records -> {
                launchRecords()
                true
            }*/
            R.id.action_logout -> {
                logOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun initLayout(layoutID: Int, theActionBarTitle: String?, theActionBarSubtitle: String?, displayHome: Boolean) {
        setContentView(R.layout.generic_layout_app_bar)

        theActionBarTitle?.let {
            setActionBar(theActionBarTitle, theActionBarSubtitle, displayHome)
        } ?: run {
            appBarLayout.visibility = View.GONE
        }

        val layoutToLoad = LayoutInflater.from(this).inflate(layoutID, null)
        layoutToLoad?.let {
            contentFrame.addView(it)
        }

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java].also {
            it.initViewModel(this)
            it.loadUserInfo()
        }
    }

    fun showProgress() {
        Log.d(TAG, "showProgress")
        contentFrame.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgress() {
        Log.d(TAG, "hideProgress")
        progressBar.visibility = View.GONE
        contentFrame.visibility = View.VISIBLE
    }

    private fun logOut() {
        mainViewModel.getGoogleSignInClient(this).signOut()
        mainViewModel.deleteUser()
        setResult(RC_SIGN_OUT)
        finish()
    }

    private fun setActionBar(title: String, subtitle: String?, displayHome: Boolean) {
        val s = SpannableString(title)
        s.setSpan(TypefaceSpan(this, "Freedom-10eM.ttf"), 0, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        setSupportActionBar(findViewById(R.id.toolbar))
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_TITLE
            this.title = s
            subtitle?.let { this.subtitle = it }

            setDisplayHomeAsUpEnabled(displayHome)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back)
        }
    }

    fun lunchMineField() {
        val intent = Intent(this, MineFieldActivity::class.java)
        startActivityForResult(intent, RC_SIGN_OUT)
    }

    private fun launchSettings() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), settingsRequestCode)
    }

    private fun launchRecords() {
        startActivity(Intent(this, RecordsActivity::class.java))
    }

    private fun getNextRequestCode(): Int {
        nextRequestCode++
        return nextRequestCode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            settingsRequestCode -> {
                when (resultCode) {
                    RESULT_SETTINGS_SAVED -> {
                        lunchMineField()
                    }
                    RESULT_SETTINGS_NOT_SAVED -> {
                        //TODO
                    }
                }
            }
        }
    }
}