package com.reader.app.presentation.ui.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reader.app.presentation.theme.ReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = hiltViewModel(),
    bookId: String,
    onBack: () -> Unit
) {
    val book by viewModel.book.collectAsState()
    val currentChapterIndex by viewModel.currentChapterIndex.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isShowMenu by viewModel.isShowMenu.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val themeMode by viewModel.theme.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()

    val currentTheme = ReaderTheme.fromId(themeMode)

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    Scaffold(
        topBar = {
            if (isShowMenu) {
                ReaderTopAppBar(
                    book = book,
                    currentTime = currentTime,
                    batteryLevel = batteryLevel,
                    onBack = onBack,
                    currentTheme = currentTheme
                )
            }
        },
        bottomBar = {
            if (isShowMenu) {
                ReaderBottomAppBar(
                    currentChapterIndex = currentChapterIndex,
                    totalChapters = chapters.size,
                    currentChapter = chapters.getOrNull(currentChapterIndex),
                    onPreviousChapter = { viewModel.previousChapter() },
                    onNextChapter = { viewModel.nextChapter() },
                    currentTheme = currentTheme
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable { viewModel.toggleMenu() }
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    ErrorView(
                        message = error.orEmpty(),
                        onDismiss = { viewModel.clearError() },
                        currentTheme = currentTheme
                    )
                }
                else -> {
                    ChapterContent(
                        chapter = chapters.getOrNull(currentChapterIndex),
                        fontSize = fontSize.sp,
                        currentTheme = currentTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderTopAppBar(
    book: com.reader.app.data.model.Book?,
    currentTime: String,
    batteryLevel: Int,
    onBack: () -> Unit,
    currentTheme: ReaderTheme
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = currentTheme.backgroundColor
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = book?.title ?: "阅读",
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = currentTheme.textColor
                        )
                        Text(
                            text = "${book?.author ?: ""}",
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = currentTheme.secondaryTextColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = currentTheme.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentTheme.backgroundColor,
                    titleContentColor = currentTheme.textColor
                )
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                color = currentTheme.backgroundColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentTime,
                        fontSize = 12.sp,
                        color = currentTheme.secondaryTextColor
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${batteryLevel}%",
                            fontSize = 12.sp,
                            color = currentTheme.secondaryTextColor
                        )
                        BatteryIndicator(
                            level = batteryLevel,
                            currentTheme = currentTheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryIndicator(
    level: Int,
    currentTheme: ReaderTheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(24.dp)
            .height(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = currentTheme.secondaryTextColor,
                size = androidx.compose.ui.geometry.Size(
                    size.width - 4.dp.toPx(),
                    size.height
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )
            
            if (level > 0) {
                drawRoundRect(
                    color = currentTheme.textColor,
                    size = androidx.compose.ui.geometry.Size(
                        (size.width - 4.dp.toPx()) * level / 100f,
                        size.height
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                )
            }
            
            drawRoundRect(
                color = currentTheme.secondaryTextColor,
                size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height * 0.5f),
                topLeft = androidx.compose.ui.geometry.Offset(
                    size.width - 4.dp.toPx(),
                    size.height * 0.25f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
            )
        }
    }
}

@Composable
fun ReaderBottomAppBar(
    currentChapterIndex: Int,
    totalChapters: Int,
    currentChapter: com.reader.app.data.model.Chapter?,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    currentTheme: ReaderTheme
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = currentTheme.backgroundColor
    ) {
        Column {
            Divider(color = currentTheme.secondaryTextColor.copy(alpha = 0.3f))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                color = currentTheme.backgroundColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    currentChapter?.let { chapter ->
                        Text(
                            text = chapter.title,
                            fontSize = 13.sp,
                            color = currentTheme.secondaryTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = currentTheme.backgroundColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousChapter,
                        enabled = currentChapterIndex > 0
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "上一章",
                            tint = if (currentChapterIndex > 0) {
                                currentTheme.textColor
                            } else {
                                currentTheme.secondaryTextColor
                            }
                        )
                    }

                    Text(
                        text = "${currentChapterIndex + 1} / ${totalChapters.coAtLeast(1)}",
                        fontSize = 14.sp,
                        color = currentTheme.secondaryTextColor
                    )

                    IconButton(
                        onClick = onNextChapter,
                        enabled = currentChapterIndex < totalChapters - 1
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "下一章",
                            tint = if (currentChapterIndex < totalChapters - 1) {
                                currentTheme.textColor
                            } else {
                                currentTheme.secondaryTextColor
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterContent(
    chapter: com.reader.app.data.model.Chapter?,
    fontSize: androidx.compose.ui.unit.TextUnit,
    currentTheme: ReaderTheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (chapter != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = chapter.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.textColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                itemsIndexed(chapter.content.split("\n")) { _, paragraph ->
                    if (paragraph.isNotBlank()) {
                        Text(
                            text = paragraph,
                            fontSize = fontSize,
                            color = currentTheme.textColor,
                            lineHeight = (fontSize.value * 1.6).sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onDismiss: () -> Unit,
    currentTheme: ReaderTheme
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = currentTheme.backgroundColor.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = currentTheme.textColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("确定")
                }
            }
        }
    }
}