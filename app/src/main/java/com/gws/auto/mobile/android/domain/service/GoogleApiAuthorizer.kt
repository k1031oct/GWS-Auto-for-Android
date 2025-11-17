package com.gws.auto.mobile.android.domain.service

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleApiAuthorizer @Inject constructor(@ApplicationContext private val context: Context) {

    internal val httpTransport: HttpTransport by lazy { NetHttpTransport() }
    internal val jsonFactory: JsonFactory by lazy { GsonFactory.getDefaultInstance() }

    private fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun isSignedIn(): Boolean {
        return getLastSignedInAccount() != null
    }

    suspend fun getCredential(scopes: List<String>): GoogleAccountCredential? = withContext(Dispatchers.IO) {
        val account = getLastSignedInAccount()
        if (account == null) {
            null
        } else {
            GoogleAccountCredential.usingOAuth2(context, scopes).apply {
                selectedAccount = account.account
            }
        }
    }

    fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val signInClient = GoogleSignIn.getClient(context, gso)
        return signInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?, onComplete: () -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            Timber.d("Signed in as: ${account?.email}")
            onComplete()
        } catch (e: ApiException) {
            Timber.w(e, "Sign-in failed")
        }
    }

    fun signOut(onComplete: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val signInClient = GoogleSignIn.getClient(context, gso)
        signInClient.signOut().addOnCompleteListener { onComplete() }
    }
}
