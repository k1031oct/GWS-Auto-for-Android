package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import javax.inject.Inject

class ChatPostModule @Inject constructor(
    private val httpClient: OkHttpClient
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        val webhookUrl = context.resolveVariables(context.module.parameters["webhookUrl"] ?: "")
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")

        if (webhookUrl.isBlank() || message.isBlank()) {
            return ExecutionResult(false, "Webhook URL and message are required.")
        }

        return withContext(Dispatchers.IO) {
            try {
                val json = "{\"text\":\"$message\"}"
                val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(webhookUrl)
                    .post(requestBody)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Timber.d("Successfully posted message to Google Chat.")
                        ExecutionResult(true, "Message posted successfully.")
                    } else {
                        val errorBody = response.body?.string()
                        Timber.e("Failed to post message to Google Chat: ${response.code} - $errorBody")
                        ExecutionResult(false, "Failed to post message: ${response.code} - $errorBody")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception while posting message to Google Chat")
                ExecutionResult(false, "Exception: ${e.message}")
            }
        }
    }
}
