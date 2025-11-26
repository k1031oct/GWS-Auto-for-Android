package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DuplicateSpreadsheetModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val sourceId = context.resolveVariables(context.module.parameters["sourceSpreadsheetId"] ?: "")
            val newName = context.resolveVariables(context.module.parameters["newSpreadsheetName"] ?: "")
            val targetFolderId = context.resolveVariables(context.module.parameters["targetFolderId"] ?: "")
            val outputVar = context.module.parameters["outputSpreadsheetId"]

            if (sourceId.isBlank() || newName.isBlank()) {
                return ExecutionResult.Error("sourceSpreadsheetId and newSpreadsheetName are required.")
            }

            // A simple regex to extract file ID from a URL
            val fileId = "/d/([a-zA-Z0-9_-]+)".toRegex().find(sourceId)?.groupValues?.get(1) ?: sourceId

            val newFile = driveApiService.duplicateAndMoveFile(fileId, newName, targetFolderId)

            if (outputVar != null) {
                context.setVariable(outputVar, newFile.id)
                Timber.d("New spreadsheet ID ${newFile.id} saved to variable '$outputVar'")
            }

            Timber.d("Successfully duplicated spreadsheet with new ID: ${newFile.id}")
            ExecutionResult.Success("Duplicated spreadsheet: ${newFile.id}", mapOf("newFileId" to newFile.id))
        } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Failed to duplicate spreadsheet")
            ExecutionResult.Error("Failed to duplicate spreadsheet: ${e.message}")
        }
    }
}
