package com.reader.app.presentation.ui.settings

import androidx.lifecycle.ViewModel
import com.reader.app.data.local.preferences.ReaderPreferences
import com.reader.app.presentation.theme.ReaderTheme
import com.reader.app.utils.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: ReaderPreferences,
    private val fileManager: FileManager
) : ViewModel() {

    val fontSize: StateFlow<Int> = preferences.fontSize
    val themeMode: StateFlow<String> = preferences.themeMode
    val backgroundColor: StateFlow<String> = preferences.backgroundColor
    val textColor: StateFlow<String> = preferences.textColor
    val autoCacheEnabled: StateFlow<Boolean> = preferences.autoCacheEnabled
    val maxCacheSize: StateFlow<Int> = preferences.maxCacheSize

    private val _cacheSize = MutableStateFlow(0L)
    val cacheSize: StateFlow<Long> = _cacheSize

    private val _isClearingCache = MutableStateFlow(false)
    val isClearingCache: StateFlow<Boolean> = _isClearingCache

    init {
        loadCacheSize()
    }

    fun loadCacheSize() {
        _cacheSize.value = fileManager.getCacheSize()
    }

    suspend fun setFontSize(size: Int) {
        preferences.setFontSize(size)
    }

    suspend fun setTheme(theme: ReaderTheme) {
        preferences.setThemeMode(theme.id)
        preferences.setBackgroundColor(colorToString(theme.backgroundColor))
        preferences.setTextColor(colorToString(theme.textColor))
    }

    suspend fun setAutoCacheEnabled(enabled: Boolean) {
        preferences.setAutoCacheEnabled(enabled)
    }

    suspend fun setMaxCacheSize(size: Int) {
        preferences.setMaxCacheSize(size)
    }

    suspend fun clearCache() {
        _isClearingCache.value = true
        try {
            fileManager.clearCache()
            _cacheSize.value = 0L
        } finally {
            _isClearingCache.value = false
        }
    }

    private fun colorToString(color: androidx.compose.ui.graphics.Color): String {
        return "#${color.red.toString(16).padStart(2, '0')}${
        color.green.toString(16).padStart(2, '0')
        }${color.blue.toString(16).padStart(2, '0')}"
    }
}