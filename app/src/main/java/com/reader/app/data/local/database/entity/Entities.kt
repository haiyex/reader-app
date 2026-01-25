package com.reader.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val filePath: String,
    val fileType: String,
    val coverPath: String?,
    val totalPages: Int,
    val addedTime: Long,
    val lastReadTime: Long,
    val isOnline: Boolean
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val bookId: String,
    val currentChapter: Int,
    val currentPage: Int,
    val progress: Float,
    val lastUpdateTime: Long
)

@Entity(tableName = "chapter_cache")
data class ChapterCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: String,
    val chapterIndex: Int,
    val chapterTitle: String,
    val chapterContent: String,
    val cachedTime: Long
)

@Entity(tableName = "book_sources")
data class BookSourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val baseUrl: String,
    val searchPathPattern: String,
    val chapterListSelector: String,
    val contentSelector: String,
    val titleSelector: String,
    val authorSelector: String,
    val bookUrlSelector: String,
    val bookItemSelector: String,
    val enabled: Boolean
)

@Entity(tableName = "online_books")
data class OnlineBookEntity(
    @PrimaryKey val id: String,
    val sourceId: String,
    val bookUrl: String,
    val title: String,
    val author: String,
    val description: String?,
    val coverUrl: String?,
    val addedTime: Long
)
