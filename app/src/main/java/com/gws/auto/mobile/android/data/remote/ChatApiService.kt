package com.gws.auto.mobile.android.data.remote

import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.domain.service.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A service class for interacting with the Google Chat API using OAuth 2.0.
 * This implementation uses direct HTTP calls instead of the client library.
 */
@Singleton
class ChatApiService @Inject constructor(private val googleApiAuthorizer: GoogleApiAuthorizer) {

    /**
     * Posts a message to a specified Google Chat space.
     *
     * @param spaceId The ID of the space to post the message to (e.g., "spaces/AAAAbbb1234").
     * @param messageText The plain text content of the message.
     * @return `true` if the message was posted successfully, `false` otherwise.
     */
    suspend fun postMessage(spaceId: String, messageText: String): Result<Unit> {
        if (!googleApiAuthorizer.isSignedIn()) {
            Timber.w("postMessage called but user is not signed in.")
            return Result.failure(IllegalStateException("User is not signed in."))
        }

        val credential = googleApiAuthorizer.getCredential(listOf(Scope.ChatMessages.scopeUri))
        if (credential == null) {
            Timber.e("Failed to get Google credential for Chat API.")
            return Result.failure(IllegalStateException("Failed to get Google credential."))
        }

        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val formattedSpaceId = if (spaceId.startsWith("spaces/")) spaceId else "spaces/$spaceId"
                val url = URL("https://chat.googleapis.com/v1/$formattedSpaceId/messages")
                connection = url.openConnection() as HttpURLConnection

                val accessToken = credential.token

                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.doOutput = true

                val jsonPayload = JSONObject().apply {
                    put("text", messageText)
                }.toString()

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonPayload)
                writer.flush()
                writer.close()

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    Timber.d("Successfully posted message to space: $formattedSpaceId")
                    Result.success(Unit)
                } else {
                    val errorStream = connection.errorStream?.bufferedReader()?.readText()
                    Timber.e("Failed to post message. Response code: $responseCode, Error: $errorStream")
                    Result.failure(Exception("HTTP $responseCode: $errorStream"))
                }
            } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to post message to Google Chat.")
                Result.failure(e)
            } finally {
                connection?.disconnect()
            }
        }
    }
}
