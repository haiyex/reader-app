package com.reader.app.data.service

import com.reader.app.data.model.BookSource
import com.reader.app.data.model.OnlineBookEntity
import com.reader.app.data.repository.BookSourceRepository
import com.reader.app.data.repository.OnlineBookRepository
import com.reader.app.parser.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineBookService @Inject constructor(
    private val bookSourceRepository: BookSourceRepository,
    private val onlineBookRepository: OnlineBookRepository,
    private val onlineBookParser: OnlineBookParser
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    suspend fun searchBooks(keyword: String): Result<List<SearchResult>> {
        if (keyword.isBlank()) {
            return Result.failure(Exception("搜索关键词不能为空"))
        }

        return try {
            val enabledSources = bookSourceRepository.getEnabledSources().first()
            
            if (enabledSources.isEmpty()) {
                Result.failure(Exception("请先添加书源"))
            }

            val searchResults = mutableListOf<SearchResult>()
            val deferredResults = mutableListOf<Deferred<List<SearchResult>?>>()

            enabledSources.forEach { source ->
                val deferred = CoroutineScope(Dispatchers.IO).async {
                    try {
                        onlineBookParser.searchBooks(keyword, source).getOrNull() ?: emptyList()
                    } catch (e: Exception) {
                        null
                    }
                }
                deferredResults.add(deferred)
            }

            deferredResults.forEach { deferred ->
                deferred.await()?.let { results ->
                    searchResults.addAll(results)
                }
            }

            if (searchResults.isEmpty()) {
                Result.failure(Exception("未找到相关书籍"))
            } else {
                Result.success(searchResults.distinctBy { it.title })
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookInfo(bookUrl: String, sourceId: String): Result<BookInfo> {
        val source = bookSourceRepository.getSourceById(sourceId)
            ?: return Result.failure(Exception("书源不存在"))
        
        return onlineBookParser.getBookInfo(bookUrl, source)
    }

    suspend fun getChapterList(bookUrl: String, sourceId: String): Result<List<ChapterInfo>> {
        val source = bookSourceRepository.getSourceById(sourceId)
            ?: return Result.failure(Exception("书源不存在"))
        
        return onlineBookParser.getChapterList(bookUrl, source)
    }

    suspend fun getChapterContent(chapterUrl: String, sourceId: String): Result<String> {
        val source = bookSourceRepository.getSourceById(sourceId)
            ?: return Result.failure(Exception("书源不存在"))
        
        return onlineBookParser.getChapterContent(chapterUrl, source)
    }

    suspend fun addOnlineBook(
        bookInfo: BookInfo,
        bookUrl: String,
        sourceId: String
    ): Result<OnlineBookEntity> {
        return try {
            val book = OnlineBookEntity(
                id = "online_${bookUrl.hashCode()}",
                sourceId = sourceId,
                bookUrl = bookUrl,
                title = bookInfo.title,
                author = bookInfo.author,
                description = bookInfo.description,
                coverUrl = null,
                addedTime = System.currentTimeMillis()
            )
            
            onlineBookRepository.addOnlineBook(book)
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun autoDetectSource(baseUrl: String): Result<BookSource> {
        return onlineBookParser.tryAutoDetectSource(baseUrl)
    }

    fun getOnlineBooks(): Flow<List<OnlineBookEntity>> {
        return onlineBookRepository.getAllOnlineBooks()
    }

    suspend fun deleteOnlineBook(bookId: String) {
        onlineBookRepository.deleteOnlineBook(bookId)
    }
}
