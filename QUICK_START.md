# 快速开始指南

## 前提条件

确保您已经安装了：
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34

## 项目导入步骤

1. **克隆或下载项目**
   ```bash
   cd /path/to/your/projects
   ```

2. **用Android Studio打开项目**
   - 启动Android Studio
   - 选择 "Open an Existing Project"
   - 选择 `reader-app` 文件夹
   - 等待Gradle同步完成（首次可能需要几分钟）

3. **配置SDK**
   - File > Project Structure > SDK Location
   - 确保Android SDK已正确配置

4. **运行应用**
   - 连接Android设备（API 26+）或启动模拟器
   - 点击工具栏的运行按钮（绿色三角形）
   - 或使用快捷键 `Shift + F10`

## 应用功能概览

### 书架
- 显示所有导入的书籍
- 点击书籍图标开始阅读
- 长按书籍可查看详情

### 导入书籍
1. 点击右下角的"+"按钮
2. 选择文件（支持EPUB、TXT、MOBI）
3. 等待导入完成

### 在线搜索
1. 点击顶部搜索图标
2. 输入书名或作者
3. 浏览搜索结果
4. 点击书籍开始阅读

### 阅读设置
1. 点击顶部设置图标
2. 调整字体大小（14-30sp）
3. 切换主题（默认/护眼/夜间）
4. 管理缓存

### 书源管理
1. 点击顶部源图标
2. 点击"+"添加新书源
3. 输入网站URL
4. 点击"自动检测配置"

## 首次使用建议

1. **先导入本地书籍测试**
   - 准备一些EPUB或TXT文件
   - 测试基本的阅读功能

2. **测试主题切换**
   - 尝试三种不同的主题
   - 调整字体大小

3. **添加在线书源**
   - 找一个提供小说的网站
   - 尝试自动检测配置
   - 测试搜索功能

## 常见问题

### Q: 导入书籍失败
A: 确保文件格式正确（EPUB、TXT或MOBI），检查文件权限

### Q: 在线搜索无结果
A: 检查网络连接，确保已添加并启用的书源有效

### Q: 阅读器显示空白
A: 确保书籍已正确解析，尝试重新导入

### Q: 章节无法切换
A: 检查书籍是否包含多个章节，或尝试从目录选择

## 开发调试

### 查看日志
```
adb logcat -s ReaderApp
```

### 清理项目
Build > Clean Project

### 重建项目
Build > Rebuild Project

## 技术架构

```
MainActivity (入口)
    ↓
ViewModels (业务逻辑)
    ↓
Repositories (数据访问)
    ↓
Room Database / File System / Network (数据源)
```

## 自定义开发

### 添加新的主题
编辑 `ReaderTheme.kt`:
```kotlin
object CustomTheme : ReaderTheme(
    name = "自定义主题",
    id = "custom",
    backgroundColor = Color.Cyan,
    textColor = Color.White,
    secondaryTextColor = Color.LightGray
)
```

### 添加新的文件格式解析器
1. 在 `parser` 目录创建新的解析器
2. 实现 `FileParser` 接口
3. 在 `BookParser.kt` 中注册

### 添加新的书源
1. 打开书源管理
2. 添加新源
3. 输入URL并自动检测
4. 手动调整CSS选择器（如需要）

## 性能优化建议

1. **章节缓存**
   - 在设置中调整最大缓存章节数
   - 定期清理缓存释放空间

2. **网络请求**
   - 启用书源时注意网络使用
   - 考虑使用WiFi进行大量搜索

3. **电池优化**
   - 关闭不必要的书源
   - 合理设置自动缓存

## 贡献指南

欢迎提交Issue和Pull Request！

## 联系方式

如有问题，请查看GitHub Issues。

---

祝您使用愉快！
