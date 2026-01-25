package com.reader.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "reader_settings")

@Singleton
class ReaderPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SIZE = intPreferencesKey("font_size")
        val BACKGROUND_COLOR = stringPreferencesKey("background_color")
        val TEXT_COLOR = stringPreferencesKey("text_color")
        val LINE_HEIGHT = floatPreferencesKey("line_height")
        val AUTO_CACHE_ENABLED = booleanPreferencesKey("auto_cache_enabled")
        val MAX_CACHE_SIZE = intPreferencesKey("max_cache_size")
    }

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "default"
        }

    val fontSize: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] ?: 16
        }

    val backgroundColor: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.BACKGROUND_COLOR] ?: "#FFFFFF"
        }

    val textColor: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TEXT_COLOR] ?: "#000000"
        }

    val lineHeight: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LINE_HEIGHT] ?: 1.6f
        }

    val autoCacheEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_CACHE_ENABLED] ?: true
        }

    val maxCacheSize: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.MAX_CACHE_SIZE] ?: 10
        }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size
        }
    }

    suspend fun setBackgroundColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_COLOR] = color
        }
    }

    suspend fun setTextColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEXT_COLOR] = color
        }
    }

    suspend fun setLineHeight(height: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LINE_HEIGHT] = height
        }
    }

    suspend fun setAutoCacheEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_CACHE_ENABLED] = enabled
        }
    }

    suspend fun setMaxCacheSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MAX_CACHE_SIZE] = size
        }
    }
}
