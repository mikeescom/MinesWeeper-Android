package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.msmikeescom.minesweeper.repository.MainRepository
import com.msmikeescom.minesweeper.repository.local.sharepreferences.MinesWeeperSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    companion object {
        private val TAG = "MainViewModel"
    }

    private lateinit var mainRepository: MainRepository

    fun initViewModel(context: Context) = viewModelScope.launch(
        Dispatchers.Default) {
        Log.d(TAG, "initViewModel")
        try {
            mainRepository = MainRepository(
                sharePreferences = MinesWeeperSharedPreferences.instance(context)
            )
        } catch (e: Exception) {
            Log.d(TAG, "initViewModel: ", e)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun getNumberOfMines() = mainRepository.getNumberOfMines()

    fun getUserPhotoUrl() = mainRepository.getUserPhotoUrl()

    fun saveUserInfo(googleSignInAccount: GoogleSignInAccount) {
        Log.d(TAG, "saveUserInfo")
        googleSignInAccount.id?.let { mainRepository.saveUserId(it) }
        googleSignInAccount.displayName?.let { mainRepository.saveUserName(it) }
        googleSignInAccount.photoUrl?.let { mainRepository.saveUserPhotoUrl(it.toString()) }
    }

    fun saveFieldSize(fieldSize: Int) {
        Log.d(TAG, "saveFieldSize")
        mainRepository.saveFieldSize(fieldSize)
    }

    fun saveNumberOfMines(numberOfMines: Int) {
        Log.d(TAG, "saveNumberOfMines")
        mainRepository.saveNumberOfMines(numberOfMines)
    }

    fun deleteUserInfo() {
        Log.d(TAG, "deleteUserInfo")
        mainRepository.deleteUserInfo()
    }

}