package com.reader.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reader.app.data.local.database.dao.*
import com.reader.app.data.local.database.entity.*

@Database(
    entities = [
        BookEntity::class,
        ReadingProgressEntity::class,
        ChapterCacheEntity::class,
        BookSourceEntity::class,
        OnlineBookEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ReaderDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun chapterCacheDao(): ChapterCacheDao
    abstract fun bookSourceDao(): BookSourceDao
    abstract fun onlineBookDao(): OnlineBookDao
}
