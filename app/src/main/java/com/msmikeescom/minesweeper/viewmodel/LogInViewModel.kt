package com.msmikeescom.minesweeper.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.repository.MainRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LogInViewModel : ViewModel() {

    companion object {
        private const val TAG = "LogInViewModel"
    }

    /** Every player, Google or email, is signed in to Firebase Auth; its uid keys their records. */
    val auth: FirebaseAuth = Firebase.auth
    private lateinit var mainRepository: MainRepository

    /**
     * Sets up the repository synchronously so it is ready before the login fragment
     * checks for a signed-in user. Safe to call again after the activity is recreated.
     */
    fun initViewModel(context: Context) {
        if (::mainRepository.isInitialized) return
        Log.d(TAG, "initViewModel")
        mainRepository = MainRepository.create(context)
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    /**
     * Shows the "Sign in with Google" sheet and signs the chosen account in to Firebase Auth.
     * [context] must be an Activity context. Throws if the player cancels or sign-in fails.
     */
    suspend fun signInWithGoogle(context: Context) {
        val option = GetSignInWithGoogleOption.Builder(context.getString(R.string.default_web_client_id)).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val credential = CredentialManager.create(context).getCredential(context, request).credential
        check(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Unexpected credential type ${credential.type}"
        }
        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
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
    fun logOut(appContext: Context) {
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
