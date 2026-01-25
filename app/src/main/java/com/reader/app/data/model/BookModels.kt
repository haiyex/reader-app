package com.reader.app.data.model

import com.reader.app.data.local.database.entity.*

enum class FileType {
    EPUB,
    TXT,
    MOBI
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val filePath: String,
    val fileType: FileType,
    val coverPath: String?,
    val totalPages: Int,
    val addedTime: Long,
    val lastReadTime: Long,
    val isOnline: Boolean
)

data class Chapter(
    val index: Int,
    val title: String,
    val content: String
)

data class ReadingProgress(
    val bookId: String,
    val currentChapter: Int,
    val currentPage: Int,
    val progress: Float,
    val lastUpdateTime: Long
)

data class BookSource(
    val id: String,
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

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        filePath = filePath,
        fileType = fileType.name,
        coverPath = coverPath,
        totalPages = totalPages,
        addedTime = addedTime,
        lastReadTime = lastReadTime,
        isOnline = isOnline
    )
}

fun BookEntity.toBook(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        filePath = filePath,
        fileType = FileType.valueOf(fileType),
        coverPath = coverPath,
        totalPages = totalPages,
        addedTime = addedTime,
        lastReadTime = lastReadTime,
        isOnline = isOnline
    )
}

fun ReadingProgress.toEntity(): ReadingProgressEntity {
    return ReadingProgressEntity(
        bookId = bookId,
        currentChapter = currentChapter,
        currentPage = currentPage,
        progress = progress,
        lastUpdateTime = lastUpdateTime
    )
}

fun ReadingProgressEntity.toReadingProgress(): ReadingProgress {
    return ReadingProgress(
        bookId = bookId,
        currentChapter = currentChapter,
        currentPage = currentPage,
        progress = progress,
        lastUpdateTime = lastUpdateTime
    )
}

fun BookSource.toEntity(): BookSourceEntity {
    return BookSourceEntity(
        id = id,
        name = name,
        baseUrl = baseUrl,
        searchPathPattern = searchPathPattern,
        chapterListSelector = chapterListSelector,
        contentSelector = contentSelector,
        titleSelector = titleSelector,
        authorSelector = authorSelector,
        bookUrlSelector = bookUrlSelector,
        bookItemSelector = bookItemSelector,
        enabled = enabled
    )
}

fun BookSourceEntity.toBookSource(): BookSource {
    return BookSource(
        id = id,
        name = name,
        baseUrl = baseUrl,
        searchPathPattern = searchPathPattern,
        chapterListSelector = chapterListSelector,
        contentSelector = contentSelector,
        titleSelector = titleSelector,
        authorSelector = authorSelector,
        bookUrlSelector = bookUrlSelector,
        bookItemSelector = bookItemSelector,
        enabled = enabled
    )
}

fun ChapterCacheEntity.toChapter(): Chapter {
    return Chapter(
        index = chapterIndex,
        title = chapterTitle,
        content = chapterContent
    )
}