package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.msmikeescom.minesweeper.repository.MainRepository
import com.msmikeescom.minesweeper.repository.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class MainViewModel : ViewModel() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger("MainViewModel")
    }

    private lateinit var mainRepository: MainRepository
    var userInfo: MutableLiveData<UserInfoItem?> = MutableLiveData()
    private set

    fun initViewModel(context: Context) = viewModelScope.launch(
        Dispatchers.Default) {
        LOGGER.debug("initViewModel")
        try {
            mainRepository = MainRepository(MinesWeeperDatabase.instance(context))
        } catch (e: Exception) {
            LOGGER.error("initViewModel: ", e)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun loadUserInfo() = viewModelScope.launch(Dispatchers.Main) {
        LOGGER.debug("loadUserInfo")
        mainRepository.getUserInfo()?.let { userInfoItem ->
            userInfo.value = userInfoItem
        }
    }

    fun loadUserInfo(googleSignInAccount: GoogleSignInAccount?) = viewModelScope.launch(Dispatchers.Main) {
        LOGGER.debug("loadUserInfo: ${googleSignInAccount?.email}")
        mainRepository.getUserInfo().let { userInfoItem ->
            if (googleSignInAccount?.id == userInfoItem?.userId) {
                userInfo.value = userInfoItem
            } else {
                saveUserInfo(googleSignInAccount)
            }
        }
    }

    fun saveUserInfo(googleSignInAccount: GoogleSignInAccount?) {
        LOGGER.debug("saveUserInfo")
        viewModelScope.launch(Dispatchers.Main) {
            val userInfoItem = googleSignInAccount?.id?.let { id ->
                UserInfoItem(
                    userId = id,
                    displayName = googleSignInAccount.displayName,
                    email = googleSignInAccount.email,
                    photoUrl = googleSignInAccount.photoUrl.toString(),
                    fieldSize = 0,
                    numberOfMines = 0
                )
            }

            userInfoItem?.let {
                LOGGER.debug("saveUserInfo: User info saved!")
                mainRepository.insertUser(it)
                userInfo.value = it
            }
        }
    }

    fun updateFieldSize(fieldSize: Int) {
        LOGGER.debug("updateFieldSize")
        viewModelScope.launch(Dispatchers.Main) {
            userInfo.value?.userId?.let {
                mainRepository.updateFieldSize(it, fieldSize)
            }
        }
    }

    fun updateNumberOfMines(numberOfMines: Int) {
        LOGGER.debug("updateNumberOfMines")
        viewModelScope.launch(Dispatchers.Main) {
            userInfo.value?.userId?.let {
                mainRepository.updateNumberOfMines(it, numberOfMines)
            }
        }
    }

    fun deleteUser() {
        LOGGER.debug("deleteUser")
        viewModelScope.launch(Dispatchers.Main) {
            mainRepository.deleteUser()
        }
    }

}