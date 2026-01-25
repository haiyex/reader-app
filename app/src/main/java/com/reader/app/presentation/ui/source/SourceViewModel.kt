package com.reader.app.presentation.ui.source

import androidx.lifecycle.ViewModel
import com.reader.app.data.model.BookSource
import com.reader.app.data.repository.BookSourceRepository
import com.reader.app.data.service.OnlineBookService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SourceViewModel @Inject constructor(
    private val bookSourceRepository: BookSourceRepository,
    private val onlineBookService: OnlineBookService
) : ViewModel() {

    val sources: StateFlow<List<BookSource>> = bookSourceRepository.getEnabledSources()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isShowingAddDialog = MutableStateFlow(false)
    val isShowingAddDialog: StateFlow<Boolean> = _isShowingAddDialog

    private val _editingSource = MutableStateFlow<BookSource?>(null)
    val editingSource: StateFlow<BookSource?> = _editingSource

    suspend fun autoDetectSource(url: String): Result<BookSource> {
        return onlineBookService.autoDetectSource(url)
    }

    suspend fun addSource(source: BookSource) {
        try {
            bookSourceRepository.addSource(source)
            _isShowingAddDialog.value = false
        } catch (e: Exception) {
            _error.value = "添加书源失败: ${e.message}"
        }
    }

    suspend fun updateSource(source: BookSource) {
        try {
            bookSourceRepository.updateSource(source)
            _editingSource.value = null
        } catch (e: Exception) {
            _error.value = "更新书源失败: ${e.message}"
        }
    }

    suspend fun deleteSource(sourceId: String) {
        try {
            bookSourceRepository.deleteSource(sourceId)
        } catch (e: Exception) {
            _error.value = "删除书源失败: ${e.message}"
        }
    }

    suspend fun toggleSourceEnabled(sourceId: String, enabled: Boolean) {
        try {
            val source = bookSourceRepository.getSourceById(sourceId)
            source?.let {
                bookSourceRepository.updateSource(it.copy(enabled = enabled))
            }
        } catch (e: Exception) {
            _error.value = "更新书源失败: ${e.message}"
        }
    }

    fun showAddDialog() {
        _isShowingAddDialog.value = true
    }

    fun hideAddDialog() {
        _isShowingAddDialog.value = false
        _editingSource.value = null
    }

    fun editSource(source: BookSource) {
        _editingSource.value = source
        _isShowingAddDialog.value = true
    }

    fun clearError() {
        _error.value = null
    }
}