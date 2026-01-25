package com.reader.app.parser

import android.util.Log
import com.reader.app.data.model.Chapter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject

@ViewModelScoped
class MobiParser @Inject constructor() : FileParser {

    override suspend fun parse(filePath: String): Result<BookContent> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    return@withContext Result.failure(Exception("文件不存在"))
                }

                val content = extractTextFromMobi(file)
                val chapters = parseChapters(content)
                
                val fileName = file.nameWithoutExtension
                val bookContent = BookContent(
                    title = fileName,
                    author = "未知",
                    chapters = chapters
                )
                
                Result.success(bookContent)
            } catch (e: Exception) {
                Log.e("MobiParser", "解析MOBI文件失败", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun extractCover(filePath: String): String? {
        return null
    }

    private fun extractTextFromMobi(file: File): String {
        return try {
            val content = StringBuilder()
            ZipInputStream(file.inputStream()).use { zipInput ->
                var entry = zipInput.nextEntry
                while (entry != null) {
                    if (entry.name.endsWith(".html") || 
                        entry.name.endsWith(".htm") || 
                        entry.name.contains("content")) {
                        
                        val fileContent = zipInput.bufferedReader().use { it.readText() }
                        val textContent = cleanHtml(fileContent)
                        if (textContent.isNotEmpty()) {
                            content.append(textContent).append("\n\n")
                        }
                    }
                    zipInput.closeEntry()
                    entry = zipInput.nextEntry
                }
            }
            
            if (content.isEmpty()) {
                file.readText()
            } else {
                content.toString()
            }
        } catch (e: Exception) {
            Log.e("MobiParser", "提取文本失败", e)
            file.readText()
        }
    }

    private fun cleanHtml(html: String): String {
        return html
            .replace(Regex("""<script[^>]*>.*?</script>""", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("""<style[^>]*>.*?</style>""", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("""<[^>]+>"""), "")
            .replace(Regex("""&nbsp;"""), " ")
            .replace(Regex("""&amp;"""), "&")
            .replace(Regex("""&lt;"""), "<")
            .replace(Regex("""&gt;"""), ">")
            .replace(Regex("""&quot;"""), "\"")
            .replace(Regex("""&#39;"""), "'")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun parseChapters(content: String): List<Chapter> {
        val chapterPatterns = listOf(
            Regex("""第[零一二三四五六七八九十百千0-9]+章\s*.+"""),
            Regex("""Chapter\s*[0-9]+\s*.+"""),
            Regex("""第\s*[0-9]+\s*章""")
        )

        val lines = content.split("\n")
        val chapters = mutableListOf<Chapter>()
        var currentContent = StringBuilder()
        var currentTitle = "序言"
        var chapterIndex = 0

        lines.forEach { line ->
            val trimmedLine = line.trim()
            
            val isChapterHeader = chapterPatterns.any { pattern ->
                pattern.matches(trimmedLine)
            }

            if (isChapterHeader && currentContent.isNotEmpty()) {
                chapters.add(
                    Chapter(
                        index = chapterIndex,
                        title = currentTitle,
                        content = currentContent.toString().trim()
                    )
                )
                chapterIndex++
                currentContent = StringBuilder()
                currentTitle = trimmedLine
            } else {
                currentContent.appendLine(line)
            }
        }

        if (currentContent.isNotEmpty()) {
            chapters.add(
                Chapter(
                    index = chapterIndex,
                    title = currentTitle,
                    content = currentContent.toString().trim()
                )
            )
        }

        if (chapters.isEmpty()) {
            chapters.add(
                Chapter(
                    index = 0,
                    title = "正文",
                    content = content
                )
            )
        }

        return chapters
    }
}
