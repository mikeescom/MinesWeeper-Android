package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.msmikeescom.minesweeper.repository.MainRepository
import com.msmikeescom.minesweeper.repository.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.database.dto.UserInfoItem
import com.msmikeescom.minesweeper.utilities.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class MineFieldViewModel : ViewModel() {
    companion object {
        private const val TAG = "MineFieldViewModel"
        private val LOGGER = LoggerFactory.getLogger("MineFieldViewModel")
    }

    private var googleSignInAccount: GoogleSignInAccount? = null
    private lateinit var mainRepository: MainRepository
    var unCoverSquare: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
        private set
    var userInfo: MutableLiveData<UserInfoItem> = MutableLiveData()
        private set
    var isViewModelInitialized: MutableLiveData<Boolean> = MutableLiveData()
        private set

    fun initViewModel(context: Context, account: GoogleSignInAccount?) = viewModelScope.launch(Dispatchers.Default) {
        LOGGER.debug("initViewModel")
        googleSignInAccount = account
        try {
            mainRepository = MainRepository(MinesWeeperDatabase.instance(context))
            isViewModelInitialized.postValue(true)
        } catch (e: Exception) {
            LOGGER.error("initViewModel: ", e)
            isViewModelInitialized.postValue(false)
        }
    }

    fun loadUserInfo() = viewModelScope.launch(Dispatchers.Main) {
        LOGGER.debug("fetchUserInfo")
        googleSignInAccount?.id?.let { id ->
            mainRepository.getUserInfo(id)?.let { userInfoItem ->
                userInfo.value = userInfoItem
            } ?: kotlin.run {
                LOGGER.debug("fetchUserInfo: User not found. Save new user.")
                saveUserInfo()
            }
        } ?: kotlin.run {
            LOGGER.debug("fetchUserInfo: userId is null. Cannot continue.")
        }
    }

    private fun saveUserInfo() {
        LOGGER.debug("saveUserInfo")
        viewModelScope.launch(Dispatchers.Main) {
            val userInfoItem = googleSignInAccount?.id?.let { id ->
                UserInfoItem(
                    userId = id,
                    displayName = googleSignInAccount?.displayName,
                    email = googleSignInAccount?.email,
                    photoUrl = googleSignInAccount?.photoUrl.toString(),
                    difficulty = Constants.Difficulty.EASY.name
                )
            }

            userInfoItem?.let {
                LOGGER.debug("saveUserInfo: User info saved!")
                mainRepository.insertItem(it)
                userInfo.value = it
            }
        }
    }

}