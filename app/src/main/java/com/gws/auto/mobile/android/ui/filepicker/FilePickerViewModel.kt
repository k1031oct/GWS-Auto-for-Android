package com.gws.auto.mobile.android.ui.filepicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.domain.service.DriveApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class FilePickerViewModel @Inject constructor(
    private val driveApiService: DriveApiService
) : ViewModel() {

    private val _files = MutableLiveData<List<File>>()
    val files: LiveData<List<File>> = _files

    private val _currentFolderName = MutableLiveData("My Drive")
    val currentFolderName: LiveData<String> = _currentFolderName

    private var currentFolderId: String = "root"

    init {
        loadFilesForCurrentFolder()
    }

    fun onFolderClicked(folderId: String, folderName: String) {
        currentFolderId = folderId
        _currentFolderName.value = folderName
        loadFilesForCurrentFolder()
    }

    fun onUpClicked() {
        viewModelScope.launch {
            if (currentFolderId != "root") {
                try {
                    val currentFolder = driveApiService.getFileDetails(currentFolderId)
                    val parentId = currentFolder?.parents?.firstOrNull()
                    if (parentId != null) {
                        val parentFolder = driveApiService.getFileDetails(parentId)
                        currentFolderId = parentId
                        _currentFolderName.value = parentFolder?.name ?: "Unknown Folder"
                        loadFilesForCurrentFolder()
                    } else {
                        // No parent, assume root
                        currentFolderId = "root"
                        _currentFolderName.value = "My Drive"
                        loadFilesForCurrentFolder()
                    }
                } catch (e: Exception) {
                    // Handle error, e.g., show a toast
                }
            }
        }
    }

    private fun loadFilesForCurrentFolder() {
        viewModelScope.launch {
            try {
                val fileList = driveApiService.listFiles(currentFolderId)
                _files.value = fileList.files
            } catch (e: Exception) {
                // Handle error, e.g., show a toast
            }
        }
    }
}
