package com.reader.app.presentation.ui.bookshelf

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.reader.app.data.model.Book
import com.reader.app.data.repository.BookRepository
import com.reader.app.utils.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                bookRepository.getAllBooks().collect { books ->
                    _books.value = books
                }
            } catch (e: Exception) {
                _error.value = "加载书籍失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importBook(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = fileManager.importBook(uri)
                if (result.isSuccess) {
                    val importedFile = result.getOrThrow()
                    // TODO: 这里需要调用相应的解析器来解析文件并保存到数据库
                    _error.value = "文件导入成功，暂不支持自动解析"
                } else {
                    _error.value = "导入失败: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "导入失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                bookRepository.deleteBook(book.id)
                fileManager.getBookFile(book.id).delete()
                loadBooks()
            } catch (e: Exception) {
                _error.value = "删除失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                fileManager.clearCache()
                _error.value = "缓存已清除"
            } catch (e: Exception) {
                _error.value = "清除缓存失败: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}