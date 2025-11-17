package com.gws.auto.mobile.android.domain.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    fun signOut(onComplete: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val signInClient = GoogleSignIn.getClient(context, gso)
        signInClient.signOut().addOnCompleteListener { onComplete() }
    }
}
