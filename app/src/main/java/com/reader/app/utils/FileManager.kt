package com.reader.app.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val booksDir: File
        get() = File(context.filesDir, "books").apply { mkdirs() }

    private val cacheDir: File
        get() = File(context.filesDir, "cache").apply { mkdirs() }

    suspend fun importBook(uri: Uri): Result<File> = withContext(Dispatchers.IO) {
        try {
            val fileName = getFileName(uri) ?: "book_${UUID.randomUUID()}"
            val file = File(booksDir, fileName)
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(Exception("无法打开文件"))

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getFileName(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) {
                cursor.getString(index)
            } else {
                null
            }
        }
    }

    fun getBookFile(bookId: String): File {
        return File(booksDir, bookId)
    }

    fun getCachedChapterFile(bookId: String, chapterIndex: Int): File {
        val chapterDir = File(cacheDir, bookId).apply { mkdirs() }
        return File(chapterDir, "chapter_$chapterIndex.txt")
    }

    suspend fun clearCache(): Unit = withContext(Dispatchers.IO) {
        cacheDir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
        }
    }

    suspend fun clearBookCache(bookId: String): Unit = withContext(Dispatchers.IO) {
        val bookCacheDir = File(cacheDir, bookId)
        if (bookCacheDir.exists()) {
            bookCacheDir.deleteRecursively()
        }
    }

    fun getCacheSize(): Long {
        return cacheDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }
}
