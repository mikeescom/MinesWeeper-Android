package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.msmikeescom.minesweeper.auth.GoogleSignInHelper
import com.msmikeescom.minesweeper.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

@HiltViewModel
class LogInViewModel @Inject constructor(
    /** Every player, Google or email, is signed in to Firebase Auth; its uid keys their records. */
    val auth: FirebaseAuth,
    private val mainRepository: MainRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    companion object {
        private const val TAG = "LogInViewModel"
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    /**
     * Shows the "Sign in with Google" sheet and signs the chosen account in to Firebase Auth.
     * [context] must be an Activity context. Throws if the player cancels or sign-in fails.
     */
    suspend fun signInWithGoogle(context: Context) {
        auth.signInWithCredential(GoogleSignInHelper.firebaseCredential(context)).await()
    }

    /**
     * Stores the signed-in Firebase user's details. For a Google account, also copies records that
     * older versions saved under the Google account id over to the Firebase uid.
     */
    fun saveUserInfo() {
        val user = auth.currentUser ?: return
        Log.d(TAG, "saveUserInfo")
        mainRepository.saveUserId(user.uid)
        user.displayName?.let { mainRepository.saveUserName(it) }
        user.photoUrl?.let { mainRepository.saveUserPhotoUrl(it.toString()) }
        user.providerData
            .firstOrNull { it.providerId == GoogleAuthProvider.PROVIDER_ID }
            ?.uid
            ?.let { googleAccountId -> mainRepository.copyLegacyRecords(googleAccountId, user.uid) }
    }

    /** Signs out of Firebase and Google and forgets this player's local data. */
    fun logOut() {
        Log.d(TAG, "logOut")
        auth.signOut()
        mainRepository.deleteUserInfo()
        viewModelScope.launch {
            try {
                CredentialManager.create(appContext).clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "logOut: could not clear credential state", e)
            }
            mainRepository.deleteAllRecords()
        }
    }
}
