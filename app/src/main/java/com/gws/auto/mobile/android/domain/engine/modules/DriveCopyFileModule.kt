package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DriveCopyFileModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val sourceFileId = context.resolveVariables(context.module.parameters["sourceFileId"] ?: "")
            val destFolderId = context.resolveVariables(context.module.parameters["destFolderId"] ?: "")
            val newFileName = context.resolveVariables(context.module.parameters["newFileName"] ?: "")

            if (sourceFileId.isBlank() || destFolderId.isBlank() || newFileName.isBlank()) {
                return ExecutionResult.Error("Source file ID, destination folder ID, and new file name are required.")
            }
            
            val copiedFile = driveApiService.copyFile(sourceFileId, destFolderId, newFileName)
            val outputVar = context.module.parameters["outputFileId"]
            if (outputVar != null) {
                context.setVariable(outputVar, copiedFile.id)
            }

            ExecutionResult.Success("Copied file: ${copiedFile.id}", mapOf("newFileId" to copiedFile.id))
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to copy Google Drive file")
            ExecutionResult.Error("Failed to copy file: ${e.message}")
        }
    }
}
