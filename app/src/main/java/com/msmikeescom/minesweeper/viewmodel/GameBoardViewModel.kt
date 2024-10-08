package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import com.msmikeescom.minesweeper.repository.MainRepository
import com.msmikeescom.minesweeper.repository.local.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.local.database.dto.LocalRecordItem
import com.msmikeescom.minesweeper.repository.local.sharepreferences.MinesWeeperSharedPreferences
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.RealTimeDatabase
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.model.RemoteRecordItem
import com.msmikeescom.minesweeper.utilities.Security
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameBoardViewModel : ViewModel() {

    companion object {
        private const val TAG = "GameBoardViewModel"
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private var passwordSignInUser: FirebaseUser? = null // Password login
    private var googleSignInUser: GoogleSignInAccount? = null // Google login
    private lateinit var mainRepository: MainRepository
    private val _isLoading = MutableStateFlow(true);
    val isLoading = _isLoading.asStateFlow();

    var allRecordItems = MutableLiveData<List<LocalRecordItem>>()

    init {
        viewModelScope.launch {
            //Delay to simulate some background processsing like fetching data
            delay(3000)
            //After task is done set isLoading to false to hide splash screen
            _isLoading.value = false
        }
    }

    private val childEventListener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded: $snapshot, $previousChildName")
            viewModelScope.launch {
                if (!loadAllItemIds().contains(snapshot.key)) {
                    Log.d(TAG, "onChildAdded: Item doesn't exists in db. Adding to db.")
                    val map = snapshot.value as HashMap<*, *>
                    val localRecordItem = LocalRecordItem()
                    localRecordItem.itemId = snapshot.key ?: ""
                    localRecordItem.numberOfMines = map["numberOfMines"] as Long
                    localRecordItem.fieldSize = map["fieldSize"] as Long
                    localRecordItem.timeInSeconds = map["timeInSeconds"] as Long
                    localRecordItem.timeStamp = map["timeStamp"] as Long
                    mainRepository.insertRecord(localRecordItem)
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildChanged: $snapshot, $previousChildName")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            Log.d(TAG, "onChildRemoved: $snapshot")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildMoved: $snapshot, $previousChildName")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "onCancelled: $error")
        }
    }

    fun initViewModel(context: Context) = viewModelScope.launch(
        Dispatchers.Default) {
        Log.d(TAG, "initViewModel")
        try {
            mainRepository = MainRepository(
                sharePreferences = MinesWeeperSharedPreferences.instance(context),
                database = MinesWeeperDatabase.instance(context),
                realTimeDatabase = RealTimeDatabase.instance()
            )

            getUserId()?.let {
                setUserListener(it, childEventListener)
            }
        } catch (e: Exception) {
            Log.e(TAG, "initViewModel: ", e)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun getLastSignedInAccount(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getPasswordSignInUser() = Firebase.auth

    private fun getUserId() = mainRepository.getUserId()

    fun getNumberOfMines() = mainRepository.getNumberOfMines()

    fun getFieldSize() = mainRepository.getFieldSize()

    fun getUserPhotoUrl() = mainRepository.getUserPhotoUrl()

    fun saveUserInfo(googleSignInAccount: GoogleSignInAccount) {
        Log.d(TAG, "saveUserInfo: ${googleSignInAccount.id}")
        googleSignInAccount.id?.let { mainRepository.saveUserId(it) }
        googleSignInAccount.displayName?.let { mainRepository.saveUserName(it) }
        googleSignInAccount.photoUrl?.let { mainRepository.saveUserPhotoUrl(it.toString()) }
    }

    fun saveFieldSize(fieldSize: Int) {
        Log.d(TAG, "saveFieldSize: $fieldSize")
        mainRepository.saveFieldSize(fieldSize)
    }

    fun saveNumberOfMines(numberOfMines: Int) {
        Log.d(TAG, "saveNumberOfMines: $numberOfMines")
        mainRepository.saveNumberOfMines(numberOfMines)
    }

    fun deleteUserInfo() {
        Log.d(TAG, "deleteUserInfo")
        mainRepository.deleteUserInfo()
    }

    fun setNewRecord(localRecordItem: LocalRecordItem) {
        Log.d(TAG, "setNewRecord")
        viewModelScope.launch {
            localRecordItem.itemId = Security.md5(localRecordItem.toString())
            getUserId()?.let {
                setNewRecord(it, localRecordItem.itemId, localRecordItem.getRemoteRecordItem())
            }
        }
    }

    suspend fun loadAllItemIds(): List<String> {
        Log.d(TAG, "loadAllItemIds")
        return mainRepository.loadAllItemIds()
    }

    fun fetchAllRecords() {
        Log.d(TAG, "fetchAllRecords")
        viewModelScope.launch {
            allRecordItems.value = mainRepository.loadAllRecords()
        }
    }

    private fun setUserListener(userId: String, listener: ChildEventListener) {
        Log.d(TAG, "setUserListener")
        mainRepository.setUserListener(userId, listener)
    }

    private fun setNewRecord(userId: String, itemId: String, remoteRecordItem: RemoteRecordItem) {
        Log.d(TAG, "setNewRecord")
        mainRepository.setNewRecord(userId, itemId, remoteRecordItem)
    }

}