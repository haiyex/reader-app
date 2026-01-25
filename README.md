# 阅读器 Android应用

一款简洁易用的Android端电子书阅读应用，支持本地书籍阅读和在线书籍搜索阅读。

## 功能特性

### 本地阅读
- 支持的文件格式：EPUB、TXT、MOBI
- 文件导入和书籍管理
- 阅读进度自动保存
- 章节导航

### 阅读设置
- 字体大小调整（14sp-30sp）
- 字体颜色调整
- 背景颜色调整
- 主题切换（默认白色、护眼米色、夜间黑底）

### 阅读器功能
- 实时显示时间和电量
- 当前章节信息显示
- 阅读进度显示
- 章节导航（上一章/下一章）

### 在线阅读
- 多源在线书籍搜索
- 自动识别书源配置
- 按章节浏览
- HTML解析支持

### 章节缓存
- 自动缓存最近阅读的10章内容
- 随阅读进度自动缓存后续章节
- 超过缓存限制自动清理旧章节
- 已缓存章节支持离线阅读

### 书源管理
- 添加自定义书源
- 书源自动检测
- 书源启用/禁用
- 书源编辑和删除

## 技术栈

- **开发语言**：Kotlin
- **UI框架**：Jetpack Compose
- **架构模式**：MVVM
- **依赖注入**：Hilt
- **数据库**：Room
- **网络请求**：OkHttp + Retrofit
- **HTML解析**：Jsoup
- **EPUB解析**：EpubLib
- **协程**：Kotlin Coroutines + Flow
- **后台任务**：WorkManager

## 项目结构

```
app/
├── data/
│   ├── local/
│   │   ├── database/          # Room数据库
│   │   ├── preferences/       # DataStore偏好设置
│   │   └── cache/             # 文件缓存
│   ├── repository/           # 数据仓库层
│   ├── service/              # 业务服务层
│   └── model/                # 数据模型
├── domain/
│   ├── usecase/              # 用例层（暂未使用）
│   └── model/                # 领域模型
├── presentation/
│   ├── ui/                   # UI界面
│   │   ├── bookshelf/        # 书架
│   │   ├── reader/           # 阅读器
│   │   ├── search/           # 搜索
│   │   ├── settings/         # 设置
│   │   └── source/           # 书源管理
│   ├── viewmodel/            # ViewModel
│   └── theme/                # 主题
├── parser/                   # 文件解析器
│   ├── BookParser.kt         # 主解析器
│   ├── TxtParser.kt          # TXT解析
│   ├── EpubParser.kt         # EPUB解析
│   ├── MobiParser.kt         # MOBI解析
│   └── OnlineBookParser.kt   # 在线书籍解析
├── workers/                  # 后台任务
│   └── ChapterCacheWorker.kt # 章节缓存
├── utils/                    # 工具类
│   └── FileManager.kt        # 文件管理
└── di/                       # 依赖注入
    └── AppModule.kt          # 模块配置
```

## 数据库设计

### 主要表

- **books**: 本地书籍信息
- **reading_progress**: 阅读进度
- **chapter_cache**: 章节缓存
- **book_sources**: 书源配置
- **online_books**: 在线书籍信息

## 章节缓存策略

- 默认缓存最近10章
- 单章最大100KB
- 随阅读自动清理旧章节
- 支持手动清除缓存

## 书源配置

书源配置包含以下字段：
- 基础URL
- 搜索路径模板
- 章节列表选择器
- 内容选择器
- 书名选择器
- 作者选择器
- 书籍URL选择器
- 书籍项选择器

## 开发环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK 34
- Kotlin 1.9.20
- Gradle 8.2

## 构建和运行

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 运行应用

## 依赖项

主要依赖项包括：
- Jetpack Compose
- Hilt
- Room
- OkHttp
- Retrofit
- Jsoup
- EpubLib
- WorkManager

详细依赖版本请参考 `app/build.gradle.kts`

## 注意事项

1. **EPUB解析**：使用EpubLib库进行解析
2. **MOBI解析**：当前实现为简化版本，可能不完全支持所有MOBI格式
3. **在线书源**：需要用户提供有效的书源URL，不同网站结构可能需要手动调整CSS选择器
4. **网络请求**：需要INTERNET权限
5. **文件访问**：需要适当的存储权限

## 未来改进方向

- [ ] 支持更多电子书格式
- [ ] 改进MOBI解析器
- [ ] 添加书籍搜索历史
- [ ] 支持书籍分类和标签
- [ ] 添加书签功能
- [ ] 支持自定义CSS
- [ ] 添加阅读统计
- [ ] 支持云同步

## 许可证

本项目仅供学习和研究使用。
