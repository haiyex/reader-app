package com.reader.app.data.local.database.dao

import androidx.room.*
import com.reader.app.data.local.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY lastReadTime DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    suspend fun getProgressByBookId(bookId: String): ReadingProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ReadingProgressEntity)

    @Update
    suspend fun updateProgress(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun deleteProgressByBookId(bookId: String)
}

@Dao
interface ChapterCacheDao {
    @Query("SELECT * FROM chapter_cache WHERE bookId = :bookId ORDER BY chapterIndex ASC")
    fun getCachedChaptersByBookId(bookId: String): Flow<List<ChapterCacheEntity>>

    @Query("SELECT * FROM chapter_cache WHERE bookId = :bookId AND chapterIndex = :chapterIndex")
    suspend fun getCachedChapter(bookId: String, chapterIndex: Int): ChapterCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterCacheEntity>)

    @Query("DELETE FROM chapter_cache WHERE bookId = :bookId")
    suspend fun clearCacheByBookId(bookId: String)

    @Query("SELECT COUNT(*) FROM chapter_cache WHERE bookId = :bookId")
    suspend fun getCachedChapterCount(bookId: String): Int

    @Query("DELETE FROM chapter_cache WHERE id IN (SELECT id FROM chapter_cache WHERE bookId = :bookId ORDER BY chapterIndex LIMIT :limit)")
    suspend fun deleteOldestChapters(bookId: String, limit: Int)
}

@Dao
interface BookSourceDao {
    @Query("SELECT * FROM book_sources ORDER BY name ASC")
    fun getAllSources(): Flow<List<BookSourceEntity>>

    @Query("SELECT * FROM book_sources WHERE enabled = 1 ORDER BY name ASC")
    fun getEnabledSources(): Flow<List<BookSourceEntity>>

    @Query("SELECT * FROM book_sources WHERE id = :sourceId")
    suspend fun getSourceById(sourceId: String): BookSourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: BookSourceEntity)

    @Update
    suspend fun updateSource(source: BookSourceEntity)

    @Delete
    suspend fun deleteSource(source: BookSourceEntity)

    @Query("DELETE FROM book_sources WHERE id = :sourceId")
    suspend fun deleteSourceById(sourceId: String)
}

@Dao
interface OnlineBookDao {
    @Query("SELECT * FROM online_books ORDER BY addedTime DESC")
    fun getAllOnlineBooks(): Flow<List<OnlineBookEntity>>

    @Query("SELECT * FROM online_books WHERE id = :bookId")
    suspend fun getOnlineBookById(bookId: String): OnlineBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnlineBook(book: OnlineBookEntity)

    @Update
    suspend fun updateOnlineBook(book: OnlineBookEntity)

    @Delete
    suspend fun deleteOnlineBook(book: OnlineBookEntity)

    @Query("DELETE FROM online_books WHERE id = :bookId")
    suspend fun deleteOnlineBookById(bookId: String)
}
