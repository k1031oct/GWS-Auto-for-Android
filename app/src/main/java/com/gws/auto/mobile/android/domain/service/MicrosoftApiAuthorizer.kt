package com.gws.auto.mobile.android.domain.service

import android.app.Activity
import android.content.Context
import com.gws.auto.mobile.android.R
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Handles authentication with Microsoft services using MSAL (Microsoft Authentication Library).
 * This class is responsible for acquiring, managing, and refreshing access tokens for Microsoft Graph and other APIs.
 */
@Singleton
class MicrosoftApiAuthorizer @Inject constructor(@ApplicationContext private val context: Context) {

    private var msalApplication: ISingleAccountPublicClientApplication? = null

    init {
        // Initialize the MSAL application instance from the config file.
        PublicClientApplication.createSingleAccountPublicClientApplication(
            context,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    msalApplication = application
                    Timber.d("MSAL application created successfully.")
                }

                override fun onError(exception: MsalException) {
                    msalApplication = null
                    Timber.e(exception, "Failed to create MSAL application.")
                }
            }
        )
    }

    /**
     * Checks if a user is currently signed in.
     */
    suspend fun isSignedIn(): Boolean {
        return getCurrentAccount() != null
    }

    /**
     * Acquires an access token interactively. This will show a login UI to the user.
     *
     * @param activity The activity context required to show the authentication UI.
     * @param scopes The list of permissions (scopes) required.
     * @return The authentication result containing the access token, or null on failure.
     */
    suspend fun signInAndAcquireToken(activity: Activity, scopes: List<String>): IAuthenticationResult? {
        val app = msalApplication ?: run {
            Timber.e("MSAL application is not initialized.")
            return null
        }
        return suspendCancellableCoroutine { continuation ->
            val callback = object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    Timber.d("Successfully acquired token interactively.")
                    continuation.resume(authenticationResult)
                }
                override fun onError(exception: MsalException) {
                    Timber.e(exception, "Failed to acquire token interactively.")
                    continuation.resume(null)
                }
                override fun onCancel() {
                    Timber.w("User cancelled interactive token acquisition.")
                    continuation.resume(null)
                }
            }
            app.signIn(activity, null, scopes.toTypedArray(), callback)
        }
    }

    /**
     * Acquires an access token silently. This will not show any UI.
     *
     * @param scopes The list of permissions (scopes) required.
     * @return The authentication result containing the access token, or null on failure.
     */
    suspend fun acquireTokenSilent(scopes: List<String>): IAuthenticationResult? {
        val app = msalApplication ?: run {
            Timber.e("MSAL application is not initialized.")
            return null
        }
        val account = getCurrentAccount() ?: run {
            Timber.w("No account is signed in for silent token acquisition.")
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val callback = object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    Timber.d("Successfully acquired token silently.")
                    continuation.resume(authenticationResult)
                }
                override fun onError(exception: MsalException) {
                    Timber.e(exception, "Failed to acquire token silently. Interactive sign-in may be required.")
                    continuation.resume(null)
                }
                override fun onCancel() {
                    continuation.resume(null)
                }
            }
            app.acquireTokenSilentAsync(scopes.toTypedArray(), account.authority, callback)
        }
    }

    /**
     * Signs the current user out of the application.
     */
    suspend fun signOut(): Boolean {
        val app = msalApplication ?: return false
        return suspendCancellableCoroutine { continuation ->
            app.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                override fun onSignOut() {
                    Timber.d("User signed out successfully.")
                    continuation.resume(true)
                }
                override fun onError(exception: MsalException) {
                    Timber.e(exception, "Failed to sign out.")
                    continuation.resume(false)
                }
            })
        }
    }

    /**
     * Retrieves the currently signed-in account asynchronously.
     */
    private suspend fun getCurrentAccount(): IAccount? {
        val app = msalApplication ?: return null
        return suspendCancellableCoroutine { continuation ->
            app.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    continuation.resume(activeAccount)
                }
                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    continuation.resume(currentAccount)
                }
                override fun onError(exception: MsalException) {
                    Timber.e(exception, "Failed to get current account.")
                    continuation.resume(null)
                }
            })
        }
    }
}
