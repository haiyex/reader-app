package com.reader.app.parser

import com.reader.app.data.model.BookSource
import com.reader.app.data.model.SearchResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.inject.Inject

@ViewModelScoped
class OnlineBookParser @Inject constructor() {

    suspend fun searchBooks(keyword: String, source: BookSource): Result<List<SearchResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val searchUrl = source.searchPathPattern
                    .replace("{keyword}", keyword)
                    .replace("{name}", keyword)
                
                val document = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                val results = document.select(source.bookItemSelector).mapNotNull { element ->
                    try {
                        extractBookInfo(element, source)
                    } catch (e: Exception) {
                        null
                    }
                }
                
                Result.success(results)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBookInfo(bookUrl: String, source: BookSource): Result<BookInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(bookUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                val title = document.selectFirst(source.titleSelector)?.text()?.trim() ?: "未知"
                val author = document.selectFirst(source.authorSelector)?.text()?.trim() ?: "未知"
                val description = document.selectFirst("meta[name=description]")?.attr("content")?.trim()
                
                Result.success(BookInfo(title, author, description ?: ""))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getChapterList(bookUrl: String, source: BookSource): Result<List<ChapterInfo>> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(bookUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                val chapters = document.select(source.chapterListSelector).mapIndexed { index, element ->
                    val title = element.text().trim()
                    val url = element.attr("href")
                    ChapterInfo(index, title, url)
                }
                
                Result.success(chapters)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getChapterContent(chapterUrl: String, source: BookSource): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(chapterUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                val contentElement = document.selectFirst(source.contentSelector)
                var content = contentElement?.text()?.trim() ?: ""
                
                if (content.isEmpty()) {
                    content = contentElement?.html()?.let { cleanHtml(it) } ?: ""
                }
                
                Result.success(content)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun tryAutoDetectSource(baseUrl: String): Result<BookSource> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(baseUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                val guessedConfig = guessSourceConfig(document)
                Result.success(BookSource(
                    id = "",
                    name = "自动检测",
                    baseUrl = baseUrl,
                    searchPathPattern = guessedConfig.searchPathPattern,
                    chapterListSelector = guessedConfig.chapterListSelector,
                    contentSelector = guessedConfig.contentSelector,
                    titleSelector = guessedConfig.titleSelector,
                    authorSelector = guessedConfig.authorSelector,
                    bookUrlSelector = guessedConfig.bookUrlSelector,
                    bookItemSelector = guessedConfig.bookItemSelector,
                    enabled = false
                ))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun extractBookInfo(element: Element, source: BookSource): SearchResult? {
        val titleElement = element.selectFirst(source.titleSelector)
        val title = titleElement?.text()?.trim() ?: return null
        
        val authorElement = element.selectFirst(source.authorSelector)
        val author = authorElement?.text()?.trim() ?: "未知"
        
        val urlElement = element.selectFirst(source.bookUrlSelector)
        val bookUrl = urlElement?.attr("href")?.let { 
            if (it.startsWith("http")) it else source.baseUrl + it 
        } ?: return null
        
        val description = element.selectFirst("p")?.text()?.trim()
        
        return SearchResult(
            bookId = "online_${bookUrl.hashCode()}",
            title = title,
            author = author,
            description = description,
            coverUrl = null,
            sourceName = source.name,
            bookUrl = bookUrl
        )
    }

    private fun guessSourceConfig(document: Document): SourceConfig {
        val contentElements = listOf(
            "div.content", "div.chapter-content", "div.text", "div#content",
            "#content", "div.main-content", "div.book-content", ".content"
        )
        
        val chapterElements = listOf(
            "div.chapter-list a", ".chapter-item a", "#chapter-list a",
            "ul.chapters a", "ol.chapters a", "div.chapters a"
        )
        
        val titleElements = listOf(
            "h1", "h2", ".title", ".book-title", "h3"
        )
        
        fun findBestSelector(selectors: List<String>): String? {
            return selectors.find { selector ->
                !document.select(selector).isEmpty()
            }
        }
        
        return SourceConfig(
            searchPathPattern = "/search?q={keyword}",
            chapterListSelector = findBestSelector(chapterElements) ?: "a",
            contentSelector = findBestSelector(contentElements) ?: "div",
            titleSelector = findBestSelector(titleElements) ?: "h1",
            authorSelector = "meta[name=author], .author, span.author",
            bookUrlSelector = "a@href",
            bookItemSelector = ".book-item, .search-result, .list-item, tr"
        )
    }

    private fun cleanHtml(html: String): String {
        return html
            .replace(Regex("""<script[^>]*>.*?</script>""", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("""<style[^>]*>.*?</style>""", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("""<[^>]+>"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }
}

data class BookInfo(val title: String, val author: String, val description: String)
data class ChapterInfo(val index: Int, val title: String, val url: String)
data class SourceConfig(
    val searchPathPattern: String,
    val chapterListSelector: String,
    val contentSelector: String,
    val titleSelector: String,
    val authorSelector: String,
    val bookUrlSelector: String,
    val bookItemSelector: String
)
