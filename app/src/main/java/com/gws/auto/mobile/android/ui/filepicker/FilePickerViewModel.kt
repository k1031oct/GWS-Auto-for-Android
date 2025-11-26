package com.gws.auto.mobile.android.ui.filepicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.domain.service.DriveApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class FilePickerViewModel @Inject constructor(
    private val driveApiService: DriveApiService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme = settingsRepository.theme.asLiveData()
    val highlightColor = settingsRepository.highlightColor.asLiveData()

    private val _files = MutableLiveData<List<File>>()
    val files: LiveData<List<File>> = _files

    private val _currentFolderName = MutableLiveData<String>()
    val currentFolderName: LiveData<String> = _currentFolderName

    private val _currentFolderId = MutableLiveData<String>()
    val currentFolderId: LiveData<String> = _currentFolderId

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val folderStack = Stack<Pair<String, String>>().apply {
        push("root" to "My Drive")
    }

    private var expectedType: String? = null
    private var searchQuery: String? = null

    init {
        // Initial load will happen after expectedType is set or with default null
    }

    fun setExpectedType(type: String?) {
        expectedType = type
        loadFilesForCurrentFolder()
    }

    fun onSearchQueryChanged(query: String?) {
        searchQuery = query
        loadFilesForCurrentFolder()
    }

    fun onFolderClicked(folderId: String, folderName: String) {
        folderStack.push(folderId to folderName)
        searchQuery = null // Clear search when navigating into a folder
        loadFilesForCurrentFolder()
    }

    fun onUpClicked(): Boolean {
        return if (folderStack.size > 1) {
            folderStack.pop()
            searchQuery = null // Clear search when navigating up
            loadFilesForCurrentFolder()
            true // We handled the up navigation
        } else {
            false // We are at the root, let the activity handle it
        }
    }

    private fun loadFilesForCurrentFolder() {
        val (folderId, folderName) = folderStack.peek()
        _currentFolderName.value = folderName
        _currentFolderId.value = folderId
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Map "file" or "folder" to actual mime types if needed, or pass as is if DriveApiService handles it
                // Based on DriveApiService change:
                // "folder" -> "application/vnd.google-apps.folder"
                // "file" -> anything else (but we might want to filter specific types if needed, for now let's assume "file" means "not folder" which the service handles via "file" logic if we pass a specific mime type, BUT wait.
                // The service logic I wrote:
                // if mimeType == "application/vnd.google-apps.folder" -> mimeType = 'application/vnd.google-apps.folder'
                // else -> (mimeType = '$mimeType' or mimeType = 'application/vnd.google-apps.folder')
                
                // So if expectedType is "folder", we pass "application/vnd.google-apps.folder".
                // If expectedType is "file", we pass nothing (null) to show all files, OR we could pass a specific type if we had one.
                // The requirement says "Mime type filtering (show only relevant files)".
                // For now, if expectedType is "folder", we filter for folders.
                // If expectedType is "file", we probably want to see everything? Or maybe filter out folders?
                // Actually, the previous logic in Activity was:
                // "folder" -> isFolder
                // "file" -> !isFolder
                // But we want to navigate folders even when picking a file.
                // So:
                // If expectedType == "folder", we ONLY want to see folders? Yes, probably.
                // If expectedType == "file", we want to see files AND folders (to navigate).
                
                val mimeTypeFilter = if (expectedType == "folder") "application/vnd.google-apps.folder" else null
                
                val fileList = driveApiService.listFiles(folderId, mimeTypeFilter, searchQuery)
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
