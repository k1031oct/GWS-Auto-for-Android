package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.data.remote.ChatApiService

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.model.Module
import dagger.Binds
import dagger.multibindings.IntoMap
import javax.inject.Inject

/**
 * A workflow module executor for posting a message to Google Chat.
 * This implementation uses the [ChatApiService] which relies on OAuth 2.0.
 */
class ChatPostModule @Inject constructor(
    private val chatApiService: ChatApiService
) : ModuleExecutor {

    companion object {
        private const val PARAM_SPACE_ID = "spaceId"
        private const val PARAM_MESSAGE = "message"
    }

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        val module = context.module
        // Retrieve parameters, ensuring they are not null or empty.
        val spaceId = module.parameters[PARAM_SPACE_ID]
        val message = module.parameters[PARAM_MESSAGE]

        if (spaceId.isNullOrEmpty() || message.isNullOrEmpty()) {
            // Missing required parameters for this module.
            return ExecutionResult.Error("Missing required parameters for ChatPostModule.")
        }

        // Use the modern ChatApiService to post the message.
        val result = chatApiService.postMessage(spaceId, message)
        return if (result.isSuccess) {
            ExecutionResult.Success("Message posted to $spaceId")
        } else {
            val error = result.exceptionOrNull()
            ExecutionResult.Error("Failed to post message: ${error?.message}")
        }
    }
}
