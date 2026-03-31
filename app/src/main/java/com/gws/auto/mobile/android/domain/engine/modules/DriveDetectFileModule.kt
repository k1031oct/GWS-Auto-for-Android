package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DriveDetectFileModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val queryParam = context.resolveVariables(context.module.parameters["query"] ?: "")
            val searchType = context.module.parameters["searchType"] as? String ?: "NAME"
            val folderId = context.resolveVariables(context.module.parameters["folderId"] ?: "")
            val sortOrder = context.module.parameters["sortOrder"] as? String ?: "modifiedTime desc"
            val detectionMode = context.module.parameters["detectionMode"] as? String ?: "SIMPLE"
            
            var query = "trashed = false"
            
            if (folderId.isNotBlank()) {
                query += " and '$folderId' in parents"
            }
            
            if (queryParam.isNotBlank()) {
                when (searchType) {
                    "NAME" -> query += " and name contains '$queryParam'"
                    "CONTENT" -> query += " and fullText contains '$queryParam'"
                    else -> query += " and name contains '$queryParam'"
                }
            }

            if (detectionMode == "NEW_ONLY") {
                val lastDetectedTime = context.getState("lastDetectedTime")
                if (!lastDetectedTime.isNullOrBlank()) {
                    query += " and modifiedTime > '$lastDetectedTime'"
                }
            }

            val fileList = driveApiService.searchFiles(query, sortOrder)
            val files = fileList.files
            
            if (files.isNullOrEmpty()) {
                 return ExecutionResult.Success("No files detected matching query.")
            }
            
            // Get the first match
            val file = files.first()
            
            context.setVariable("detectedFileId", file.id)
            context.setVariable("detectedFileUrl", file.webViewLink)
            context.setVariable("detectedFileName", file.name)
            context.setVariable("detectedFileMimeType", file.mimeType)

            // Update state with the file's modifiedTime (or createdTime if preferred, but modifiedTime is safer for updates)
            // Use RFC3339 format which Drive API returns
            val newLastDetectedTime = file.modifiedTime?.toString() ?: file.createdTime?.toString()
            val updatedStates = if (newLastDetectedTime != null) {
                mapOf("lastDetectedTime" to newLastDetectedTime)
            } else {
                null
            }

            ExecutionResult.Success("Detected file: ${file.name} (${file.id})", variables = mapOf(
                "id" to file.id,
                "url" to file.webViewLink,
                "name" to file.name
            ), updatedStates = updatedStates)
        } catch (e: Exception) {
            Timber.e(e, "Failed to detect file")
            ExecutionResult.Error("Failed to detect file: ${e.message}")
        }
    }
}
