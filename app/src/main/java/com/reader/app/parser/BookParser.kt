package com.reader.app.parser

import android.content.Context
import com.reader.app.data.model.Chapter
import com.reader.app.data.model.FileType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface BookParser {
    suspend fun parse(filePath: String, fileType: FileType): Result<BookContent>
    suspend fun extractCover(filePath: String, fileType: FileType): String?
}

data class BookContent(
    val title: String,
    val author: String,
    val chapters: List<Chapter>
)

@Singleton
class BookParserImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val txtParser: TxtParser,
    private val epubParser: EpubParser,
    private val mobiParser: MobiParser
) : BookParser {

    override suspend fun parse(filePath: String, fileType: FileType): Result<BookContent> {
        return withContext(Dispatchers.IO) {
            when (fileType) {
                FileType.TXT -> txtParser.parse(filePath)
                FileType.EPUB -> epubParser.parse(filePath)
                FileType.MOBI -> mobiParser.parse(filePath)
            }
        }
    }

    override suspend fun extractCover(filePath: String, fileType: FileType): String? {
        return withContext(Dispatchers.IO) {
            when (fileType) {
                FileType.TXT -> null
                FileType.EPUB -> epubParser.extractCover(filePath)
                FileType.MOBI -> mobiParser.extractCover(filePath)
            }
        }
    }
}

interface FileParser {
    suspend fun parse(filePath: String): Result<BookContent>
    suspend fun extractCover(filePath: String): String?
}
