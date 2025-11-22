package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

/**
 * A workflow module for posting a message to a Slack channel using an incoming webhook URL.
 */
class SlackPostModule @Inject constructor(
    private val httpClient: OkHttpClient
) : ModuleExecutor {

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        // Resolve the webhook URL and message from the module parameters.
        val webhookUrl = context.resolveVariables(context.module.parameters["webhookUrl"] ?: "")
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")

        if (webhookUrl.isBlank() || message.isBlank()) {
            return ExecutionResult(false, "Webhook URL and message are required for SlackPostModule.")
        }

        return withContext(Dispatchers.IO) {
            try {
                // Construct the JSON payload required by Slack webhooks.
                val jsonPayload = JSONObject().put("text", message).toString()
                val requestBody = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())

                // Build the POST request.
                val request = Request.Builder()
                    .url(webhookUrl)
                    .post(requestBody)
                    .build()

                // Execute the request and handle the response.
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Timber.d("Successfully posted message to Slack.")
                        ExecutionResult(true, "Message posted to Slack successfully.")
                    } else {
                        val errorBody = response.body?.string()
                        Timber.e("Failed to post message to Slack: ${response.code} - $errorBody")
                        ExecutionResult(false, "Failed to post to Slack: ${response.code} - $errorBody")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception while posting message to Slack.")
                ExecutionResult(false, "An exception occurred: ${e.message}")
            }
        }
    }
}
