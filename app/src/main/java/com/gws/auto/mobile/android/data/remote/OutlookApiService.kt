package com.gws.auto.mobile.android.data.remote

import com.gws.auto.mobile.android.domain.service.MicrosoftApiAuthorizer
import com.gws.auto.mobile.android.domain.service.MicrosoftScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service class for interacting with the Microsoft Graph API, specifically for Outlook mail operations.
 * It uses [MicrosoftApiAuthorizer] to handle authentication.
 */
@Singleton
class OutlookApiService @Inject constructor(
    private val microsoftApiAuthorizer: MicrosoftApiAuthorizer,
    private val httpClient: OkHttpClient
) {

    private val graphApiEndpoint = "https://graph.microsoft.com/v1.0/me/sendMail"

    /**
     * Sends an email using the Microsoft Graph API on behalf of the signed-in user.
     *
     * @param toRecipient The email address of the recipient.
     * @param subject The subject of the email.
     * @param body The HTML content of the email body.
     * @return `true` if the email was sent successfully, `false` otherwise.
     */
    suspend fun sendEmail(toRecipient: String, subject: String, body: String): Boolean {
        if (!microsoftApiAuthorizer.isSignedIn()) {
            Timber.w("Cannot send email, user is not signed into a Microsoft account.")
            return false
        }

        // Acquire the token silently.
        val authResult = microsoftApiAuthorizer.acquireTokenSilent(listOf(MicrosoftScope.MAIL_SEND))
        if (authResult == null) {
            Timber.e("Failed to acquire silent token for Microsoft Graph API.")
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                // Construct the JSON payload for the email.
                val emailJson = JSONObject()
                    .put("message", JSONObject()
                        .put("subject", subject)
                        .put("body", JSONObject()
                            .put("contentType", "HTML")
                            .put("content", body))
                        .put("toRecipients", JSONObject()
                            .put("emailAddress", JSONObject().put("address", toRecipient))))
                    .put("saveToSentItems", "true")
                    .toString()

                val requestBody = emailJson.toRequestBody("application/json; charset=utf-8".toMediaType())

                // Build the HTTP request with the authorization header.
                val request = Request.Builder()
                    .url(graphApiEndpoint)
                    .header("Authorization", "Bearer ${authResult.accessToken}")
                    .post(requestBody)
                    .build()

                // Execute the request.
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Timber.d("Successfully sent email via Microsoft Graph.")
                        true
                    } else {
                        Timber.e("Failed to send email. Code: ${response.code}, Body: ${response.body?.string()}")
                        false
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception occurred while sending email via Microsoft Graph.")
                false
            }
        }
    }
    /**
     * Creates a draft email using the Microsoft Graph API on behalf of the signed-in user.
     *
     * @param toRecipient The email address of the recipient.
     * @param subject The subject of the email.
     * @param body The HTML content of the email body.
     * @param cc The CC email address (optional).
     * @param bcc The BCC email address (optional).
     * @return The ID of the created draft if successful, null otherwise.
     */
    suspend fun createDraft(toRecipient: String, subject: String, body: String, cc: String? = null, bcc: String? = null): String? {
        if (!microsoftApiAuthorizer.isSignedIn()) {
            Timber.w("Cannot create draft, user is not signed into a Microsoft account.")
            return null
        }

        // Acquire the token silently.
        val authResult = microsoftApiAuthorizer.acquireTokenSilent(listOf(MicrosoftScope.MAIL_READWRITE))
        if (authResult == null) {
            Timber.e("Failed to acquire silent token for Microsoft Graph API (createDraft).")
            return null
        }

        return withContext(Dispatchers.IO) {
            try {
                // Construct the JSON payload for the email.
                val messageJson = JSONObject()
                    .put("subject", subject)
                    .put("body", JSONObject()
                        .put("contentType", "HTML")
                        .put("content", body))
                    .put("toRecipients", createRecipientsArray(toRecipient))

                if (!cc.isNullOrBlank()) {
                    messageJson.put("ccRecipients", createRecipientsArray(cc))
                }
                if (!bcc.isNullOrBlank()) {
                    messageJson.put("bccRecipients", createRecipientsArray(bcc))
                }

                val requestBody = messageJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                // Build the HTTP request with the authorization header.
                // Endpoint for creating a draft is POST /me/messages
                val request = Request.Builder()
                    .url("https://graph.microsoft.com/v1.0/me/messages")
                    .header("Authorization", "Bearer ${authResult.accessToken}")
                    .post(requestBody)
                    .build()

                // Execute the request.
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val id = jsonResponse.optString("id")
                        Timber.d("Successfully created draft via Microsoft Graph. ID: $id")
                        id
                    } else {
                        Timber.e("Failed to create draft. Code: ${response.code}, Body: ${response.body?.string()}")
                        null
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception occurred while creating draft via Microsoft Graph.")
                null
            }
        }
    }

    private fun createRecipientsArray(emails: String): org.json.JSONArray {
        val jsonArray = org.json.JSONArray()
        val emailList = emails.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        for (email in emailList) {
            jsonArray.put(JSONObject().put("emailAddress", JSONObject().put("address", email)))
        }
        return jsonArray
    }
}
