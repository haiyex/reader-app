package com.reader.app.data.repository.impl

import com.reader.app.data.local.database.dao.BookDao
import com.reader.app.data.local.database.dao.ReadingProgressDao
import com.reader.app.data.local.database.dao.ChapterCacheDao
import com.reader.app.data.local.database.dao.BookSourceDao
import com.reader.app.data.local.database.dao.OnlineBookDao
import com.reader.app.data.local.database.entity.*
import com.reader.app.data.model.*
import com.reader.app.data.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao
) : BookRepository {

    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { entities ->
            entities.map { it.toBook() }
        }
    }

    override suspend fun getBookById(bookId: String): Book? {
        return bookDao.getBookById(bookId)?.toBook()
    }

    override suspend fun insertBook(book: Book) {
        bookDao.insertBook(book.toEntity())
    }

    override suspend fun updateBook(book: Book) {
        bookDao.updateBook(book.toEntity())
    }

    override suspend fun deleteBook(bookId: String) {
        bookDao.deleteBookById(bookId)
    }
}

@ViewModelScoped
class ReadingProgressRepositoryImpl @Inject constructor(
    private val readingProgressDao: ReadingProgressDao
) : ReadingProgressRepository {

    override suspend fun getProgressByBookId(bookId: String): ReadingProgress? {
        return readingProgressDao.getProgressByBookId(bookId)?.toReadingProgress()
    }

    override suspend fun saveProgress(progress: ReadingProgress) {
        readingProgressDao.insertProgress(progress.toEntity())
    }

    override suspend fun deleteProgress(bookId: String) {
        readingProgressDao.deleteProgressByBookId(bookId)
    }
}

@ViewModelScoped
class ChapterCacheRepositoryImpl @Inject constructor(
    private val chapterCacheDao: ChapterCacheDao
) : ChapterCacheRepository {

    override fun getCachedChaptersByBookId(bookId: String): Flow<List<ChapterCacheEntity>> {
        return chapterCacheDao.getCachedChaptersByBookId(bookId)
    }

    override suspend fun getCachedChapter(bookId: String, chapterIndex: Int): Chapter? {
        return chapterCacheDao.getCachedChapter(bookId, chapterIndex)?.toChapter()
    }

    override suspend fun cacheChapter(bookId: String, chapter: Chapter) {
        chapterCacheDao.insertChapter(
            ChapterCacheEntity(
                bookId = bookId,
                chapterIndex = chapter.index,
                chapterTitle = chapter.title,
                chapterContent = chapter.content,
                cachedTime = System.currentTimeMillis()
            )
        )
    }

    override suspend fun cacheChapters(bookId: String, chapters: List<Chapter>) {
        val entities = chapters.map { chapter ->
            ChapterCacheEntity(
                bookId = bookId,
                chapterIndex = chapter.index,
                chapterTitle = chapter.title,
                chapterContent = chapter.content,
                cachedTime = System.currentTimeMillis()
            )
        }
        chapterCacheDao.insertChapters(entities)
    }

    override suspend fun clearCacheByBookId(bookId: String) {
        chapterCacheDao.clearCacheByBookId(bookId)
    }

    override suspend fun getCachedChapterCount(bookId: String): Int {
        return chapterCacheDao.getCachedChapterCount(bookId)
    }

    override suspend fun deleteOldestChapters(bookId: String, limit: Int) {
        chapterCacheDao.deleteOldestChapters(bookId, limit)
    }
}

@ViewModelScoped
class BookSourceRepositoryImpl @Inject constructor(
    private val bookSourceDao: BookSourceDao
) : BookSourceRepository {

    override fun getAllSources(): Flow<List<BookSource>> {
        return bookSourceDao.getAllSources().map { entities ->
            entities.map { it.toBookSource() }
        }
    }

    override fun getEnabledSources(): Flow<List<BookSource>> {
        return bookSourceDao.getEnabledSources().map { entities ->
            entities.map { it.toBookSource() }
        }
    }

    override suspend fun getSourceById(sourceId: String): BookSource? {
        return bookSourceDao.getSourceById(sourceId)?.toBookSource()
    }

    override suspend fun addSource(source: BookSource) {
        bookSourceDao.insertSource(source.toEntity())
    }

    override suspend fun updateSource(source: BookSource) {
        bookSourceDao.updateSource(source.toEntity())
    }

    override suspend fun deleteSource(sourceId: String) {
        bookSourceDao.deleteSourceById(sourceId)
    }
}

@ViewModelScoped
class OnlineBookRepositoryImpl @Inject constructor(
    private val onlineBookDao: OnlineBookDao
) : OnlineBookRepository {

    override fun getAllOnlineBooks(): Flow<List<OnlineBookEntity>> {
        return onlineBookDao.getAllOnlineBooks()
    }

    override suspend fun getOnlineBookById(bookId: String): OnlineBookEntity? {
        return onlineBookDao.getOnlineBookById(bookId)
    }

    override suspend fun addOnlineBook(book: OnlineBookEntity) {
        onlineBookDao.insertOnlineBook(book)
    }

    override suspend fun updateOnlineBook(book: OnlineBookEntity) {
        onlineBookDao.updateOnlineBook(book)
    }

    override suspend fun deleteOnlineBook(bookId: String) {
        onlineBookDao.deleteOnlineBookById(bookId)
    }
}