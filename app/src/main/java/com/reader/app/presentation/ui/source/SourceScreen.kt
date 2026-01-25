package com.reader.app.presentation.ui.source

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reader.app.data.model.BookSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceScreen(
    viewModel: SourceViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val sources by viewModel.sources.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isShowingAddDialog by viewModel.isShowingAddDialog.collectAsState()
    val editingSource by viewModel.editingSource.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("书源管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showAddDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                ErrorBanner(
                    message = error.orEmpty(),
                    onDismiss = { viewModel.clearError() }
                )
            } else if (sources.isEmpty()) {
                EmptySources(
                    onAddSource = { viewModel.showAddDialog() }
                )
            } else {
                SourceList(
                    sources = sources,
                    onEditClick = { viewModel.editSource(it) },
                    onDeleteClick = { viewModel.deleteSource(it.id) },
                    onToggleEnabled = { id, enabled -> viewModel.toggleSourceEnabled(id, enabled) }
                )
            }
        }
    }

    if (isShowingAddDialog) {
        AddSourceDialog(
            source = editingSource,
            onDismiss = { viewModel.hideAddDialog() },
            onAdd = { viewModel.addSource(it) },
            onUpdate = { viewModel.updateSource(it) },
            onAutoDetect = { url -> viewModel.autoDetectSource(url) }
        )
    }
}

@Composable
fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun EmptySources(
    onAddSource: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.AddToHomeScreen,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "还没有书源",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "点击右上角按钮添加书源",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onAddSource,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text("添加书源")
            }
        }
    }
}

@Composable
fun SourceList(
    sources: List<BookSource>,
    onEditClick: (BookSource) -> Unit,
    onDeleteClick: (BookSource) -> Unit,
    onToggleEnabled: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sources) { source ->
            SourceItem(
                source = source,
                onEditClick = { onEditClick(source) },
                onDeleteClick = { onDeleteClick(source) },
                onToggleEnabled = { onToggleEnabled(source.id, it) }
            )
        }
    }
}

@Composable
fun SourceItem(
    source: BookSource,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = source.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = source.baseUrl,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = source.enabled,
                    onCheckedChange = onToggleEnabled
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "编辑",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("编辑")
                }

                TextButton(
                    onClick = onDeleteClick,
                    colors = TextButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("删除")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourceDialog(
    source: BookSource?,
    onDismiss: () -> Unit,
    onAdd: (BookSource) -> Unit,
    onUpdate: (BookSource) -> Unit,
    onAutoDetect: (String) -> Result<BookSource>
) {
    var name by remember { mutableStateOf(source?.name ?: "") }
    var url by remember { mutableStateOf(source?.baseUrl ?: "") }
    var isDetecting by remember { mutableStateOf(false) }
    var detectError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (source == null) "添加书源" else "编辑书源")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("书源名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    OutlinedTextField(
                        value = url,
                        onValueChange = { 
                            url = it 
                            detectError = null
                        },
                        label = { Text("网站URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = detectError != null
                    )

                    if (isDetecting) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            isDetecting = true
                            detectError = null
                        },
                        enabled = url.isNotBlank() && !isDetecting
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("自动检测配置")
                    }

                    detectError?.let { error ->
                        Text(
                            text = error,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || url.isBlank()) {
                        return@Button
                    }
                    
                    val newSource = BookSource(
                        id = source?.id ?: "source_${System.currentTimeMillis()}",
                        name = name,
                        baseUrl = url,
                        searchPathPattern = source?.searchPathPattern ?: "/search?q={keyword}",
                        chapterListSelector = source?.chapterListSelector ?: "a",
                        contentSelector = source?.contentSelector ?: "div",
                        titleSelector = source?.titleSelector ?: "h1",
                        authorSelector = source?.authorSelector ?: "span",
                        bookUrlSelector = source?.bookUrlSelector ?: "a@href",
                        bookItemSelector = source?.bookItemSelector ?: "div",
                        enabled = source?.enabled ?: true
                    )

                    if (source == null) {
                        onAdd(newSource)
                    } else {
                        onUpdate(newSource)
                    }
                },
                enabled = name.isNotBlank() && url.isNotBlank() && !isDetecting
            ) {
                Text(if (source == null) "添加" else "保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}