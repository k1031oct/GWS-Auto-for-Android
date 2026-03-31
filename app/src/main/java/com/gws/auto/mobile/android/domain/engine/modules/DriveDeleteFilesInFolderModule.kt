package com.gws.auto.mobile.android.domain.engine.modules

import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import com.gws.auto.mobile.android.domain.service.DriveApiService
import timber.log.Timber
import javax.inject.Inject

class DriveDeleteFilesInFolderModule @Inject constructor(
    private val driveApiService: DriveApiService
) : ModuleExecutor {
    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        return try {
            val folderId = context.resolveVariables(context.module.parameters["folderId"] ?: "")
            val filterType = context.module.parameters["filterType"] as? String ?: "ALL"

            if (folderId.isBlank()) {
                return ExecutionResult.Error("Folder ID is required.")
            }

            var query = "'$folderId' in parents and trashed = false"
            
            // Apply filter
            when (filterType) {
                "IMAGES" -> query += " and mimeType contains 'image/'"
                "DOCUMENTS" -> query += " and mimeType = 'application/vnd.google-apps.document'"
                "SPREADSHEETS" -> query += " and mimeType = 'application/vnd.google-apps.spreadsheet'"
                "PRESENTATIONS" -> query += " and mimeType = 'application/vnd.google-apps.presentation'"
                "FOLDERS" -> query += " and mimeType = 'application/vnd.google-apps.folder'"
                "ALL" -> {} // No additional filter
                else -> {
                     // Custom mime type or unknown, maybe treat as ALL or log warning?
                     // For now, treat as ALL
                }
            }

            val fileList = driveApiService.searchFiles(query)
            val files = fileList.files
            
            if (files.isNullOrEmpty()) {
                return ExecutionResult.Success("No files found to delete in folder: $folderId")
            }

            var deletedCount = 0
            files.forEach { file ->
                driveApiService.deleteFile(file.id)
                deletedCount++
            }

            ExecutionResult.Success("Deleted $deletedCount files in folder: $folderId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete files in folder")
            ExecutionResult.Error("Failed to delete files: ${e.message}")
        }
    }
}
