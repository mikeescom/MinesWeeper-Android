package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.repository.MainRepository
import com.msmikeescom.minesweeper.repository.local.database.MinesWeeperDatabase
import com.msmikeescom.minesweeper.repository.local.sharepreferences.MinesWeeperSharedPreferences
import com.msmikeescom.minesweeper.repository.remote.realtimedatabase.RealTimeDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogInViewModel : ViewModel() {

    companion object {
        private const val TAG = "LogInViewModel"
    }

    /** Every player, Google or email, is signed in to Firebase Auth; its uid keys their records. */
    val auth: FirebaseAuth = Firebase.auth
    private lateinit var mainRepository: MainRepository
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            //Delay to simulate some background processing like fetching data
            delay(3000)
            //After task is done set isLoading to false to hide splash screen
            _isLoading.value = false
        }
    }

    /**
     * Sets up the repository synchronously so it is ready before the login fragment
     * checks for a signed-in user. Safe to call again after the activity is recreated.
     */
    fun initViewModel(context: Context) {
        if (::mainRepository.isInitialized) return
        Log.d(TAG, "initViewModel")
        mainRepository = MainRepository(
            sharePreferences = MinesWeeperSharedPreferences.instance(context.applicationContext),
            database = MinesWeeperDatabase.instance(context),
            realTimeDatabase = RealTimeDatabase.instance()
        )
    }

    /** The ID token it requests is what lets a Google account sign in to Firebase Auth. */
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun getLastSignedInAccount(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    /** Signs [account] in to Firebase Auth. */
    fun signInToFirebase(account: GoogleSignInAccount): Task<AuthResult> {
        val idToken = account.idToken
            ?: return Tasks.forException(IllegalStateException("Google account has no ID token"))
        return auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
    }

    /**
     * Stores the signed-in Firebase user's details. For a Google account, also copies records that
     * older versions saved under the Google account id over to the Firebase uid.
     */
    fun saveUserInfo(googleSignInAccount: GoogleSignInAccount? = null) {
        val user = auth.currentUser ?: return
        Log.d(TAG, "saveUserInfo")
        mainRepository.saveUserId(user.uid)
        (user.displayName ?: googleSignInAccount?.displayName)?.let { mainRepository.saveUserName(it) }
        (user.photoUrl ?: googleSignInAccount?.photoUrl)?.let { mainRepository.saveUserPhotoUrl(it.toString()) }
        googleSignInAccount?.id?.let { mainRepository.copyLegacyRecords(it, user.uid) }
    }

    fun saveNumberOfMines(numberOfMines: Int) {
        mainRepository.saveNumberOfMines(numberOfMines)
    }

    /** Signs out of both Firebase and Google and forgets this player's local data. */
    fun logOut(context: Context) {
        Log.d(TAG, "logOut")
        auth.signOut()
        getGoogleSignInClient(context).signOut()
        mainRepository.deleteUserInfo()
        viewModelScope.launch { mainRepository.deleteAllRecords() }
    }
}
