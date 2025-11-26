package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DriveCreateFolderModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val parentFolderId = context.resolveVariables(context.module.parameters["parentFolderId"] ?: "")
            val newFolderName = context.resolveVariables(context.module.parameters["newFolderName"] ?: "")

            if (newFolderName.isBlank()) {
                return ExecutionResult.Error("New folder name is required.")
            }

            val newFolder = driveApiService.createFolder(newFolderName, parentFolderId)
            val outputVar = context.module.parameters["outputFolderId"]
            if (outputVar != null) {
                context.setVariable(outputVar, newFolder.id)
                Timber.d("New folder ID ${newFolder.id} saved to variable '$outputVar'")
            }

            ExecutionResult.Success("Successfully created folder with ID: ${newFolder.id}", mapOf("folderId" to newFolder.id))
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to create Google Drive folder")
            ExecutionResult.Error("Failed to create folder: ${e.message}")
        }
    }
}
