package com.gws.auto.mobile.android.domain.engine.modules

import com.google.api.client.util.Base64
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import com.gws.auto.mobile.android.domain.service.GmailApiService
import timber.log.Timber
import javax.inject.Inject

class GmailSaveAttachmentsModule @Inject constructor(
    private val gmailApiService: GmailApiService,
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val query = context.resolveVariables(context.module.parameters["query"] ?: "")
            val destFolderId = context.resolveVariables(context.module.parameters["destFolderId"] ?: "")

            if (query.isBlank() || destFolderId.isBlank()) {
                return ExecutionResult.Error("Search Query and Destination Folder ID are required.")
            }

            val messages = gmailApiService.searchMessages(query)
            if (messages.isEmpty()) {
                return ExecutionResult.Success("No emails found matching query: $query")
            }

            var savedCount = 0
            val savedFiles = mutableListOf<String>()

            for (msgHeader in messages) {
                val message = gmailApiService.getMessage(msgHeader.id)
                val parts = message.payload.parts ?: continue

                for (part in parts) {
                    if (!part.filename.isNullOrBlank() && part.body.attachmentId != null) {
                        val attachment = gmailApiService.getAttachment(message.id, part.body.attachmentId)
                        val data = Base64.decodeBase64(attachment.data)
                        
                        val file = driveApiService.createFile(
                            part.filename,
                            destFolderId,
                            data,
                            part.mimeType ?: "application/octet-stream"
                        )
                        savedCount++
                        savedFiles.add(file.name)
                    }
                }
            }

            ExecutionResult.Success("Saved $savedCount attachments: ${savedFiles.joinToString(", ")}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save attachments")
            ExecutionResult.Error("Failed to save attachments: ${e.message}")
        }
    }
}
