package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.data.remote.SlackApiService
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import javax.inject.Inject

/**
 * A workflow module for posting a message to a Slack channel using an incoming webhook URL.
 */
class SlackPostModule @Inject constructor(
    private val slackApiService: SlackApiService
) : ModuleExecutor {

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        // Resolve the channel ID and message from the module parameters.
        val channelId = context.resolveVariables(context.module.parameters["channelId"] ?: "")
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")

        if (channelId.isBlank() || message.isBlank()) {
            return ExecutionResult(false, "Channel ID and message are required for SlackPostModule.")
        }

        val success = slackApiService.postMessage(channelId, message)

        return if (success) {
            ExecutionResult(true, "Message posted to Slack successfully.")
        } else {
            ExecutionResult(false, "Failed to post to Slack. Please check your authentication and channel ID.")
        }
    }
}
