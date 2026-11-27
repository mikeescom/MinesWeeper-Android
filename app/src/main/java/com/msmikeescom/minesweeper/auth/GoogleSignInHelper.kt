package com.msmikeescom.minesweeper.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.msmikeescom.minesweeper.R

/** Sign in with Google through Credential Manager, shared by sign-in and account deletion. */
object GoogleSignInHelper {

    /**
     * Shows the "Sign in with Google" sheet and returns a Firebase credential for the chosen
     * account. [context] must be an Activity context. Throws
     * GetCredentialCancellationException if the player cancels.
     */
    suspend fun firebaseCredential(context: Context): AuthCredential {
        val option = GetSignInWithGoogleOption.Builder(context.getString(R.string.default_web_client_id)).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val credential = CredentialManager.create(context).getCredential(context, request).credential
        check(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Unexpected credential type ${credential.type}"
        }
        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        return GoogleAuthProvider.getCredential(idToken, null)
    }
}
