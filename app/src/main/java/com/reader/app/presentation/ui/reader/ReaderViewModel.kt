package com.reader.app.presentation.ui.reader

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reader.app.data.local.preferences.ReaderPreferences
import com.reader.app.data.model.Book
import com.reader.app.data.model.Chapter
import com.reader.app.data.repository.BookRepository
import com.reader.app.data.repository.ChapterCacheRepository
import com.reader.app.data.repository.ReadingProgressRepository
import com.reader.app.presentation.theme.ReaderTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookRepository: BookRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val chapterCacheRepository: ChapterCacheRepository,
    private val preferences: ReaderPreferences,
    private val workManager: androidx.work.WorkManager
) : ViewModel() {

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()

    private val _currentChapterIndex = MutableStateFlow(0)
    val currentChapterIndex: StateFlow<Int> = _currentChapterIndex.asStateFlow()

    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    val chapters: StateFlow<List<Chapter>> = _chapters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentTime = MutableStateFlow(getCurrentTime())
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _batteryLevel = MutableStateFlow(getBatteryLevel())
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _isShowMenu = MutableStateFlow(false)
    val isShowMenu: StateFlow<Boolean> = _isShowMenu.asStateFlow()

    val theme = preferences.themeMode
    val fontSize = preferences.fontSize

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    init {
        updateTimeAndBattery()
    }

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val book = bookRepository.getBookById(bookId)
                if (book != null) {
                    _book.value = book
                    
                    val progress = readingProgressRepository.getProgressByBookId(bookId)
                    if (progress != null) {
                        _currentChapterIndex.value = progress.currentChapter
                    }

                    loadChapterContent(bookId, _currentChapterIndex.value)
                } else {
                    _error.value = "书籍不存在"
                }
            } catch (e: Exception) {
                _error.value = "加载书籍失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadChapter(bookId: String, chapterIndex: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cachedChapter = chapterCacheRepository.getCachedChapter(bookId, chapterIndex)
                if (cachedChapter != null) {
                    _chapters.value = listOf(cachedChapter)
                    _currentChapterIndex.value = chapterIndex
                } else {
                    _error.value = "章节未找到"
                }
                
                saveProgress(bookId, chapterIndex)
            } catch (e: Exception) {
                _error.value = "加载章节失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadChapterContent(bookId: String, chapterIndex: Int) {
        viewModelScope.launch {
            try {
                val cachedChapter = chapterCacheRepository.getCachedChapter(bookId, chapterIndex)
                if (cachedChapter != null) {
                    _chapters.value = listOf(cachedChapter)
                } else {
                    _error.value = "章节未找到，请确保书籍已正确导入"
                }
            } catch (e: Exception) {
                _error.value = "加载章节内容失败: ${e.message}"
            }
        }
    }

    fun nextChapter() {
        val book = _book.value ?: return
        val currentIndex = _currentChapterIndex.value
        
        if (currentIndex < _chapters.value.size - 1) {
            loadChapter(book.id, currentIndex + 1)
        }
    }

    fun previousChapter() {
        val book = _book.value ?: return
        val currentIndex = _currentChapterIndex.value
        
        if (currentIndex > 0) {
            loadChapter(book.id, currentIndex - 1)
        }
    }

    fun jumpToChapter(chapterIndex: Int) {
        val book = _book.value ?: return
        
        if (chapterIndex >= 0 && chapterIndex < _chapters.value.size) {
            loadChapter(book.id, chapterIndex)
        }
    }

    private fun saveProgress(bookId: String, chapterIndex: Int) {
        viewModelScope.launch {
            val progress = com.reader.app.data.model.ReadingProgress(
                bookId = bookId,
                currentChapter = chapterIndex,
                currentPage = 0,
                progress = chapterIndex.toFloat() / _chapters.value.size.coerceAtLeast(1),
                lastUpdateTime = System.currentTimeMillis()
            )
            readingProgressRepository.saveProgress(progress)
            
            bookRepository.getBookById(bookId)?.let { book ->
                bookRepository.updateBook(book.copy(lastReadTime = System.currentTimeMillis()))
            }
        }
    }

    fun toggleMenu() {
        _isShowMenu.value = !_isShowMenu.value
    }

    fun hideMenu() {
        _isShowMenu.value = false
    }

    suspend fun setTheme(theme: ReaderTheme) {
        preferences.setThemeMode(theme.id)
        preferences.setBackgroundColor(colorToString(theme.backgroundColor))
        preferences.setTextColor(colorToString(theme.textColor))
    }

    suspend fun setFontSize(size: Int) {
        preferences.setFontSize(size)
    }

    private fun updateTimeAndBattery() {
        _currentTime.value = getCurrentTime()
        _batteryLevel.value = getBatteryLevel()
        
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60000)
                _currentTime.value = getCurrentTime()
                _batteryLevel.value = getBatteryLevel()
            }
        }
    }

    private fun getCurrentTime(): String {
        return timeFormat.format(Date())
    }

    private fun getBatteryLevel(): Int {
        val batteryStatus: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

    private fun colorToString(color: androidx.compose.ui.graphics.Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return "#${red.toString(16).padStart(2, '0')}${
        green.toString(16).padStart(2, '0')
        }${blue.toString(16).padStart(2, '0')}"
    }

    fun clearError() {
        _error.value = null
    }
}
