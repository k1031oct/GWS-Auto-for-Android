package com.gws.auto.mobile.android.ui.filepicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.domain.service.DriveApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class FilePickerViewModel @Inject constructor(
    private val driveApiService: DriveApiService
) : ViewModel() {

    private val _files = MutableLiveData<List<File>>()
    val files: LiveData<List<File>> = _files

    private val _currentFolderName = MutableLiveData<String>()
    val currentFolderName: LiveData<String> = _currentFolderName

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val folderStack = Stack<Pair<String, String>>().apply {
        push("root" to "My Drive")
    }

    init {
        loadFilesForCurrentFolder()
    }

    fun onFolderClicked(folderId: String, folderName: String) {
        folderStack.push(folderId to folderName)
        loadFilesForCurrentFolder()
    }

    fun onUpClicked(): Boolean {
        return if (folderStack.size > 1) {
            folderStack.pop()
            loadFilesForCurrentFolder()
            true // We handled the up navigation
        } else {
            false // We are at the root, let the activity handle it
        }
    }

    private fun loadFilesForCurrentFolder() {
        val (folderId, folderName) = folderStack.peek()
        _currentFolderName.value = folderName
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileList = driveApiService.listFiles(folderId)
                _files.postValue(fileList.files)
            } catch (e: Exception) {
                Timber.e(e, "Failed to load files for folder: $folderId.")
                _error.postValue("Failed to load files. Please re-login and grant permissions.")
                _files.postValue(emptyList()) // Clear the list on error
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }
}
