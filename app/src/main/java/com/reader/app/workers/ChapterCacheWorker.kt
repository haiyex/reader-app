package com.reader.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.reader.app.data.local.database.dao.BookDao
import com.reader.app.data.local.database.dao.BookSourceDao
import com.reader.app.data.local.database.dao.ChapterCacheDao
import com.reader.app.data.local.database.dao.OnlineBookDao
import com.reader.app.data.model.BookSource
import com.reader.app.parser.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ChapterCacheWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val bookDao: BookDao,
    private val bookSourceDao: BookSourceDao,
    private val chapterCacheDao: ChapterCacheDao,
    private val onlineBookParser: OnlineBookParser
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val bookId = inputData.getString("book_id") ?: return Result.failure()
            val sourceId = inputData.getString("source_id") ?: return Result.failure()
            
            val source = bookSourceDao.getSourceById(sourceId) ?: return Result.failure()
            val onlineBook = onlineBookDao.getOnlineBookById(bookId)
            
            onlineBook ?: return Result.failure()
            
            val currentCacheCount = chapterCacheDao.getCachedChapterCount(bookId)
            val maxCacheCount = 10
            
            if (currentCacheCount < maxCacheCount) {
                val chapters = onlineBookParser.getChapterList(onlineBook.bookUrl, source)
                    .getOrNull() ?: return Result.failure()
                
                val startIndex = currentCacheCount
                val endIndex = minOf(startIndex + maxCacheCount - currentCacheCount, chapters.size)
                
                for (i in startIndex until endIndex) {
                    if (isStopped) break
                    
                    val chapter = chapters[i]
                    val content = onlineBookParser.getChapterContent(chapter.url, source)
                        .getOrNull() ?: ""
                    
                    if (content.length > 100000) {
                        continue
                    }
                    
                    chapterCacheDao.insertChapter(
                        ChapterCacheEntity(
                            bookId = bookId,
                            chapterIndex = chapter.index,
                            chapterTitle = chapter.title,
                            chapterContent = content,
                            cachedTime = System.currentTimeMillis()
                        )
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun createWorkRequest(bookId: String, sourceId: String): OneTimeWorkRequest {
            val data = Data.Builder()
                .putString("book_id", bookId)
                .putString("source_id", sourceId)
                .build()
            
            return OneTimeWorkRequestBuilder<ChapterCacheWorker>()
                .setInputData(data)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build()
        }
    }
}
