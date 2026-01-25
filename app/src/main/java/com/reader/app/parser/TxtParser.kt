package com.reader.app.parser

import android.util.Log
import com.reader.app.data.model.Chapter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@ViewModelScoped
class TxtParser @Inject constructor() : FileParser {

    override suspend fun parse(filePath: String): Result<BookContent> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    return@withContext Result.failure(Exception("文件不存在"))
                }

                val content = detectAndRead(file)
                val chapters = parseChapters(content)
                
                val fileName = file.nameWithoutExtension
                val bookContent = BookContent(
                    title = fileName,
                    author = "未知",
                    chapters = chapters
                )
                
                Result.success(bookContent)
            } catch (e: Exception) {
                Log.e("TxtParser", "解析TXT文件失败", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun extractCover(filePath: String): String? {
        return null
    }

    private fun detectAndRead(file: File): String {
        val encodings = listOf(
            StandardCharsets.UTF_8,
            Charset.forName("GBK"),
            Charset.forName("GB2312"),
            StandardCharsets.ISO_8859_1
        )
        
        for (encoding in encodings) {
            try {
                val content = file.readText(encoding)
                if (isValidUTF8(content)) {
                    return content
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        return file.readText(StandardCharsets.UTF_8)
    }

    private fun isValidUTF8(text: String): Boolean {
        val bytes = text.toByteArray(StandardCharsets.UTF_8)
        val decoded = String(bytes, StandardCharsets.UTF_8)
        return text == decoded
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
