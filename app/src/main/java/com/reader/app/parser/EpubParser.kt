package com.reader.app.parser

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.reader.app.data.model.Chapter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@ViewModelScoped
class EpubParser @Inject constructor() : FileParser {

    private val epubReader = EpubReader()

    override suspend fun parse(filePath: String): Result<BookContent> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    return@withContext Result.failure(Exception("文件不存在"))
                }

                val epubBook = epubReader.readEpub(file.inputStream())
                val chapters = parseChapters(epubBook)
                
                val bookContent = BookContent(
                    title = epubBook.title ?: "未知书名",
                    author = epubBook.metadata.authors.firstOrNull()?.toString() ?: "未知作者",
                    chapters = chapters
                )
                
                Result.success(bookContent)
            } catch (e: Exception) {
                Log.e("EpubParser", "解析EPUB文件失败", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun extractCover(filePath: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                val epubBook = epubReader.readEpub(file.inputStream())
                val coverImage = epubBook.coverImage
                
                if (coverImage != null) {
                    val coverFile = File(filePath.parent, "${file.nameWithoutExtension}_cover.jpg")
                    FileOutputStream(coverFile).use { output ->
                        output.write(coverImage.data)
                    }
                    coverFile.absolutePath
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("EpubParser", "提取封面失败", e)
                null
            }
        }
    }

    private suspend fun parseChapters(epubBook: Book): List<Chapter> = withContext(Dispatchers.IO) {
        val chapters = mutableListOf<Chapter>()
        var chapterIndex = 0

        try {
            val contents = epubBook.tableOfContents
            if (contents.size > 0) {
                contents.forEachIndexed { index, tocReference ->
                    val resource = tocReference.resource
                    val title = tocReference.title ?: "第${index + 1}章"
                    val content = extractTextFromResource(resource)
                    
                    chapters.add(
                        Chapter(
                            index = chapterIndex,
                            title = title,
                            content = content
                        )
                    )
                    chapterIndex++
                }
            } else {
                epubBook.spine.spineReferences.forEachIndexed { index, spineRef ->
                    val resource = epubBook.resources.getByHref(spineRef.href)
                    val title = "第${index + 1}章"
                    val content = extractTextFromResource(resource)
                    
                    chapters.add(
                        Chapter(
                            index = chapterIndex,
                            title = title,
                            content = content
                        )
                    )
                    chapterIndex++
                }
            }
        } catch (e: Exception) {
            Log.e("EpubParser", "解析章节失败", e)
        }

        if (chapters.isEmpty()) {
            chapters.add(
                Chapter(
                    index = 0,
                    title = "正文",
                    content = "无法解析章节内容"
                )
            )
        }

        chapters
    }

    private fun extractTextFromResource(resource: Resource?): String {
        if (resource == null) return ""
        
        return try {
            val input = resource.inputStream
            val content = input.bufferedReader().use { it.readText() }
            content.replace(Regex("""<[^>]+>"""), "")
                .replace(Regex("""\s+"""), " ")
                .trim()
        } catch (e: Exception) {
            Log.e("EpubParser", "提取文本失败", e)
            ""
        }
    }
}
