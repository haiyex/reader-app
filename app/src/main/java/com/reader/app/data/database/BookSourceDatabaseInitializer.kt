package com.reader.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reader.app.data.local.database.ReaderDatabase
import com.reader.app.data.local.database.dao.BookSourceDao
import com.reader.app.data.local.database.entity.BookSourceEntity
import com.reader.app.utils.DatabaseCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.persistence.GenerationType

@Database(entities = [com.reader.app.data.local.database.entity.BookSourceEntity::class], version = 1)
@TypeConverters(com.reader.app.data.model.FileTypeConverter::class)
abstract class BookSourceDatabase : RoomDatabase() {
    abstract fun bookSourceDao(): BookSourceDao
}

class BookSourceDatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: BookSourceDatabase,
    private val scope: CoroutineScope
) : DatabaseCallback(context, database, scope) {

    override fun onDatabaseCreated() {
        scope.launch {
            val bookSourceDao = database.bookSourceDao()
            
            if (bookSourceDao.getSourcesCount() == 0) {
                val defaultSources = listOf(
                    BookSourceEntity(
                        id = "source_1",
                        name = "示例小说网站1",
                        baseUrl = "https://www.example1.com",
                        searchPathPattern = "/search?q={keyword}",
                        chapterListSelector = ".chapter-list a",
                        contentSelector = "#chapter-content",
                        titleSelector = ".book-title h1",
                        authorSelector = ".book-author",
                        bookUrlSelector = "a@href",
                        bookItemSelector = ".book-item",
                        enabled = false
                    ),
                    BookSourceEntity(
                        id = "source_2", 
                        name = "示例小说网站2",
                        baseUrl = "https://www.example2.com",
                        searchPathPattern = "/search?keyword={keyword}",
                        chapterListSelector = "#chapters a",
                        contentSelector = ".content-text",
                        titleSelector = "#book-name",
                        authorSelector = "#author-name",
                        bookUrlSelector = "a@href",
                        bookItemSelector = ".search-result",
                        enabled = false
                    )
                )
                
                bookSourceDao.insertSources(defaultSources)
            }
        }
    }
}