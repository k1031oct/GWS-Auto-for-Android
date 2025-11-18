package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DriveMoveFileModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val sourceFileUrl = context.resolveVariables(context.module.parameters["sourceFileUrl"] ?: "")
            val destinationFolderUrl = context.resolveVariables(context.module.parameters["destinationFolderUrl"] ?: "")

            if (sourceFileUrl.isBlank() || destinationFolderUrl.isBlank()) {
                return ExecutionResult(false, "Source file URL and destination folder URL are required.")
            }

            val sourceFileId = extractFileId(sourceFileUrl)
            val destinationFolderId = extractFileId(destinationFolderUrl)

            driveApiService.moveFile(sourceFileId, destinationFolderId)

            ExecutionResult(true, "Successfully moved file.")
        } catch (e: Exception) {
            Timber.e(e, "Failed to move Google Drive file")
            ExecutionResult(false, e.message)
        }
    }

    private fun extractFileId(source: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(source)?.groupValues?.get(1) ?: source
    }
}
