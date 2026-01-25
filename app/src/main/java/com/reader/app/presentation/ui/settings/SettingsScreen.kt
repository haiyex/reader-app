package com.reader.app.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reader.app.presentation.theme.ReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val fontSize by viewModel.fontSize.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()
    val autoCacheEnabled by viewModel.autoCacheEnabled.collectAsState()
    val maxCacheSize by viewModel.maxCacheSize.collectAsState()
    val isClearingCache by viewModel.isClearingCache.collectAsState()

    val currentTheme = ReaderTheme.fromId(themeMode)

    LaunchedEffect(Unit) {
        viewModel.loadCacheSize()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Section(title = "阅读设置") {
                FontSizeSetting(
                    fontSize = fontSize,
                    onFontSizeChange = { viewModel.setFontSize(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Section(title = "主题设置") {
                ThemeSetting(
                    currentTheme = currentTheme,
                    onThemeChange = { viewModel.setTheme(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Section(title = "缓存设置") {
                CacheSetting(
                    cacheSize = cacheSize,
                    autoCacheEnabled = autoCacheEnabled,
                    maxCacheSize = maxCacheSize,
                    isClearingCache = isClearingCache,
                    onAutoCacheChange = { viewModel.setAutoCacheEnabled(it) },
                    onMaxCacheSizeChange = { viewModel.setMaxCacheSize(it) },
                    onClearCache = { viewModel.clearCache() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Section(title = "关于") {
                AboutSection()
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun FontSizeSetting(
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                Text(
                    text = "字体大小",
                    fontSize = 16.sp
                )
                Text(
                    text = "${fontSize}sp",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "14",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Slider(
                    value = fontSize.toFloat(),
                    onValueChange = { onFontSizeChange(it.toInt()) },
                    valueRange = 14f..30f,
                    steps = 15,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Text(
                    text = "30",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ThemeSetting(
    currentTheme: ReaderTheme,
    onThemeChange: (ReaderTheme) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "主题",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ReaderTheme.getAllThemes().forEach { theme ->
                ThemeOption(
                    theme = theme,
                    isSelected = theme.id == currentTheme.id,
                    onClick = { onThemeChange(theme) }
                )
                if (theme != ReaderTheme.Night) {
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    theme: ReaderTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.shape.CircleShape
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(theme.backgroundColor, CircleShape)
                    )
                }

                Text(
                    text = theme.name,
                    fontSize = 16.sp
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "已选择",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CacheSetting(
    cacheSize: Long,
    autoCacheEnabled: Boolean,
    maxCacheSize: Int,
    isClearingCache: Boolean,
    onAutoCacheChange: (Boolean) -> Unit,
    onMaxCacheSizeChange: (Int) -> Unit,
    onClearCache: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                Column {
                    Text(
                        text = "缓存",
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatCacheSize(cacheSize),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (!isClearingCache) {
                    TextButton(
                        onClick = onClearCache,
                        enabled = cacheSize > 0
                    ) {
                        Text("清除")
                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SwitchSetting(
                title = "自动缓存",
                description = "自动缓存最近的章节",
                checked = autoCacheEnabled,
                onCheckedChange = onAutoCacheChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            SliderSetting(
                title = "最大缓存章节数",
                value = maxCacheSize.toFloat(),
                valueRange = 5f..50f,
                steps = 9,
                onValueChange = { onMaxCacheSizeChange(it.toInt()) },
                valueText = "${maxCacheSize}章"
            )
        }
    }
}

@Composable
fun SwitchSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SliderSetting(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueText: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp
            )
            Text(
                text = valueText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun AboutSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "阅读器",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "版本 1.0.0",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "一款简洁的Android阅读器应用，支持多种电子书格式和在线书籍阅读。",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

fun formatCacheSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
