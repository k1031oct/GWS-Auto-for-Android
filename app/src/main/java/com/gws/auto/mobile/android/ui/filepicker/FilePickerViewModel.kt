package com.gws.auto.mobile.android.ui.filepicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.domain.service.DriveApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilePickerViewModel @Inject constructor(
    private val driveApiService: DriveApiService
) : ViewModel() {

    private val _files = MutableLiveData<List<File>>()
    val files: LiveData<List<File>> = _files

    private var currentFolderId: String? = "root"

    init {
        loadFiles()
    }

    fun onUpClicked() {
        if (currentFolderId != null && currentFolderId != "root") {
            viewModelScope.launch {
                // This is a simplified approach. A real implementation would need to manage the parent hierarchy.
                // For now, we just go back to the root.
                currentFolderId = "root"
                loadFiles()
            }
        }
    }

    private fun loadFiles() {
        viewModelScope.launch {
            try {
                val fileList = driveApiService.listFiles(currentFolderId ?: "root")
                _files.value = fileList.files
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
