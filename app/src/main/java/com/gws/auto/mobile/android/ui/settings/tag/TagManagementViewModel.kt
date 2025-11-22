package com.gws.auto.mobile.android.ui.settings.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.TagRepository
import com.gws.auto.mobile.android.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagManagementViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    val tags: StateFlow<List<Tag>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addTag(tagName: String) {
        if (tagName.isBlank()) return
        viewModelScope.launch {
            tagRepository.addTag(Tag(name = tagName.trim()))
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.deleteTag(tag)
        }
    }

    fun updateTag(oldTag: Tag, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            tagRepository.updateTag(oldTag.copy(name = newName.trim()))
        }
    }
}
