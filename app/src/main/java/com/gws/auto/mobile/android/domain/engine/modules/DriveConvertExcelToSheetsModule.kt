package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DriveConvertExcelToSheetsModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val sourceFileId = context.resolveVariables(context.module.parameters["sourceFileId"] ?: "")
            val newFileName = context.resolveVariables(context.module.parameters["newFileName"] ?: "")
            val parentFolderId = context.resolveVariables(context.module.parameters["parentFolderId"] ?: "")

            if (sourceFileId.isBlank() || newFileName.isBlank()) {
                return ExecutionResult.Error("Source file ID and new file name are required.")
            }

            val convertedFile = driveApiService.convertExcelToSheets(sourceFileId, newFileName, parentFolderId)
            
            val outputVar = context.module.parameters["outputFileId"]
            if (outputVar != null) {
                context.setVariable(outputVar, convertedFile.id)
            }

            ExecutionResult.Success("Converted Excel to Sheets: ${convertedFile.id}", mapOf("newFileId" to convertedFile.id))
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert Excel to Sheets")
            ExecutionResult.Error("Failed to convert file: ${e.message}")
        }
    }
}
