package com.reader.app.utils

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

abstract class DatabaseCallback(
    private val context: Context,
    private val database: RoomDatabase,
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        onDatabaseCreated()
    }

    abstract fun onDatabaseCreated()
}
