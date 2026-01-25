package com.reader.app.presentation.ui.search

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.reader.app.data.service.OnlineBookService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val onlineBookService: OnlineBookService
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<com.reader.app.data.model.SearchResult>>(emptyList())
    val searchResults: StateFlow<List<com.reader.app.data.model.SearchResult>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchBooks() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = onlineBookService.searchBooks(query)
                if (result.isSuccess) {
                    _searchResults.value = result.getOrThrow()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "搜索失败"
                }
            } catch (e: Exception) {
                _error.value = "搜索失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun refresh() {
        searchBooks()
    }
}