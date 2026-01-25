package com.reader.app.data.repository

import com.reader.app.data.model.Book
import com.reader.app.data.model.BookSource
import com.reader.app.data.model.Chapter
import com.reader.app.data.model.ReadingProgress
import com.reader.app.data.local.database.entity.OnlineBookEntity
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>
    suspend fun getBookById(bookId: String): Book?
    suspend fun insertBook(book: Book)
    suspend fun updateBook(book: Book)
    suspend fun deleteBook(bookId: String)
}

interface ReadingProgressRepository {
    suspend fun getProgressByBookId(bookId: String): ReadingProgress?
    suspend fun saveProgress(progress: ReadingProgress)
    suspend fun deleteProgress(bookId: String)
}

interface ChapterCacheRepository {
    fun getCachedChaptersByBookId(bookId: String): Flow<List<com.reader.app.data.local.database.entity.ChapterCacheEntity>>
    suspend fun getCachedChapter(bookId: String, chapterIndex: Int): Chapter?
    suspend fun cacheChapter(bookId: String, chapter: Chapter)
    suspend fun cacheChapters(bookId: String, chapters: List<Chapter>)
    suspend fun clearCacheByBookId(bookId: String)
    suspend fun getCachedChapterCount(bookId: String): Int
    suspend fun deleteOldestChapters(bookId: String, limit: Int)
}

interface BookSourceRepository {
    fun getAllSources(): Flow<List<BookSource>>
    fun getEnabledSources(): Flow<List<BookSource>>
    suspend fun getSourceById(sourceId: String): BookSource?
    suspend fun addSource(source: BookSource)
    suspend fun updateSource(source: BookSource)
    suspend fun deleteSource(sourceId: String)
}

interface OnlineBookRepository {
    fun getAllOnlineBooks(): Flow<List<OnlineBookEntity>>
    suspend fun getOnlineBookById(bookId: String): OnlineBookEntity?
    suspend fun addOnlineBook(book: OnlineBookEntity)
    suspend fun updateOnlineBook(book: OnlineBookEntity)
    suspend fun deleteOnlineBook(bookId: String)
}