package com.reader.app.data.model

import androidx.room.TypeConverter
import com.reader.app.data.model.FileType

class FileTypeConverter {
    @TypeConverter
    fun fromFileType(fileType: FileType): String {
        return fileType.name
    }

    @TypeConverter
    fun toFileType(fileType: String): FileType {
        return FileType.valueOf(fileType)
    }
}