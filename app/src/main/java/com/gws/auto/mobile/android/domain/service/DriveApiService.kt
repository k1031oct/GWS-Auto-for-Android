package com.gws.auto.mobile.android.domain.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class DriveApiService @Inject constructor(private val authorizer: GoogleApiAuthorizer) {

    private fun getService(): Drive {
        val credential = authorizer.getCredential(listOf(DriveScopes.DRIVE)) ?: throw IllegalStateException("User not authenticated or required Drive scope not granted.")
        return Drive.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }

    @Throws(IOException::class)
    suspend fun getFileDetails(fileId: String): File? {
        return getService().files().get(fileId).setFields("id, name, mimeType, parents").execute()
    }

    @Throws(IOException::class)
    suspend fun listFiles(folderId: String): FileList {
        val service = getService()
        return service.files().list()
            .setQ("'$folderId' in parents and trashed = false")
            .setFields("files(id, name, mimeType)")
            .execute()
    }

    @Throws(IOException::class)
    suspend fun copyFile(sourceFileId: String, destFolderId: String, newFileName: String): File {
        val driveService = getService()
        val newFileMetadata = File().setName(newFileName).setParents(listOf(destFolderId))
        return driveService.files().copy(sourceFileId, newFileMetadata).execute()
    }

    @Throws(IOException::class)
    suspend fun createFolder(folderName: String, parentFolderId: String?): File {
        val driveService = getService()
        val folderMetadata = File()
            .setName(folderName)
            .setMimeType("application/vnd.google-apps.folder")
        if (!parentFolderId.isNullOrBlank()) {
            folderMetadata.parents = listOf(parentFolderId)
        }
        return driveService.files().create(folderMetadata).setFields("id").execute()
    }

    @Throws(IOException::class)
    suspend fun moveFile(fileId: String, toFolderId: String): File {
        val driveService = getService()
        val file = driveService.files().get(fileId).setFields("parents").execute()
        val previousParents = file.parents.joinToString(",")
        return driveService.files().update(fileId, null)
            .setAddParents(toFolderId)
            .setRemoveParents(previousParents)
            .setFields("id, parents")
            .execute()
    }

    @Throws(IOException::class)
    suspend fun duplicateAndMoveFile(sourceFileId: String, newFileName: String, targetFolderId: String?): File {
        val driveService = getService()

        // 1. Create a copy of the file
        val newFileMetadata = File().setName(newFileName)
        val copiedFile = driveService.files().copy(sourceFileId, newFileMetadata).execute()
        Timber.d("File duplicated with ID: ${copiedFile.id}")

        // 2. If a target folder is specified, move the file
        if (!targetFolderId.isNullOrBlank()) {
            val file = driveService.files().get(copiedFile.id).setFields("parents").execute()
            val previousParents = file.parents.joinToString(",")

            driveService.files().update(copiedFile.id, null)
                .setAddParents(targetFolderId)
                .setRemoveParents(previousParents)
                .setFields("id, parents")
                .execute()
            Timber.d("File ${copiedFile.id} moved to folder $targetFolderId")
        }
        
        return copiedFile
    }
}
