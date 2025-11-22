package com.gws.auto.mobile.android.data.remote

import com.google.api.services.chat.Chat
import com.google.api.services.chat.model.Message
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.domain.service.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A service class for interacting with the Google Chat API using OAuth2.
 * It relies on [GoogleApiAuthorizer] for handling authentication.
 */
@Singleton
class ChatApiService @Inject constructor(private val googleApiAuthorizer: GoogleApiAuthorizer) {

    /**
     * Sends a text message to a specified Google Chat space.
     *
     * @param spaceName The identifier of the space, e.g., "spaces/XXXXXXXXXXX".
     * @param text The plain text content of the message.
     * @return `true` if the message was sent successfully, `false` otherwise.
     */
    suspend fun sendMessage(spaceName: String, text: String): Boolean {
        // Cannot send a message if the user is not signed in.
        if (!googleApiAuthorizer.isSignedIn()) {
            Timber.w("sendMessage called but user is not signed in.")
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                // Obtain the necessary credentials for the Chat API scope.
                val credential = googleApiAuthorizer.getCredential(
                    listOf(Scope.ChatMessages.scopeUri)
                )

                if (credential == null) {
                    Timber.e("Failed to get Google credential for Chat API.")
                    return@withContext false
                }

                // Build the Chat service client.
                val chatService = Chat.Builder(
                    googleApiAuthorizer.httpTransport,
                    googleApiAuthorizer.jsonFactory,
                    credential
                ).setApplicationName("GWS Auto for Android").build()

                // Create the message payload.
                val message = Message().setText(text)

                // Execute the API call to create the message in the specified space.
                chatService.spaces().messages().create(spaceName, message).execute()

                Timber.d("Successfully sent message to space: %s", spaceName)
                true
            } catch (e: Exception) {
                Timber.e(e, "Failed to send message to Google Chat space: %s", spaceName)
                false
            }
        }
    }
}
