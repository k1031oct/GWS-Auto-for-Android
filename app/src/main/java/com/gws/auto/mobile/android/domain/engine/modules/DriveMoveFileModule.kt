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
                return ExecutionResult.Error("Source file URL and destination folder URL are required.")
            }

            val fileId = extractFileId(sourceFileUrl)
            val folderId = extractFileId(destinationFolderUrl)

            driveApiService.moveFile(fileId, folderId)

            ExecutionResult.Success("Moved file $fileId to folder $folderId")
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to move Google Drive file")
            ExecutionResult.Error("Failed to move file: ${e.message}")
        }
    }

    private fun extractFileId(urlOrId: String): String {
        return "/d/([a-zA-Z0-9_-]+)".toRegex().find(urlOrId)?.groupValues?.get(1) ?: urlOrId
    }
}
