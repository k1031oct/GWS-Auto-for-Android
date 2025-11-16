package com.gws.auto.mobile.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainSharedViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    private val _fabClick = MutableSharedFlow<Unit>()
    val fabClick = _fabClick.asSharedFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun setSignedInStatus(isSignedIn: Boolean) {
        _isSignedIn.value = isSignedIn
    }

    fun onFabClick() {
        viewModelScope.launch {
            _fabClick.emit(Unit)
        }
    }
}
