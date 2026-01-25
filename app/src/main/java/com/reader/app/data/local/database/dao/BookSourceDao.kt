package com.reader.app.data.local.database.dao

import androidx.room.*
import com.reader.app.data.local.database.entity.BookSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSourceDao {
    @Query("SELECT * FROM book_sources ORDER BY name ASC")
    fun getAllSources(): Flow<List<BookSourceEntity>>

    @Query("SELECT COUNT(*) FROM book_sources WHERE enabled = 1")
    suspend fun getEnabledCount(): Int

    @Query("SELECT COUNT(*) FROM book_sources")
    suspend fun getSourcesCount(): Int

    @Query("SELECT * FROM book_sources WHERE enabled = 1 ORDER BY name ASC")
    fun getEnabledSources(): Flow<List<BookSourceEntity>>

    @Query("SELECT * FROM book_sources WHERE id = :sourceId")
    suspend fun getSourceById(sourceId: String): BookSourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: BookSourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<BookSourceEntity>)

    @Update
    suspend fun updateSource(source: BookSourceEntity)

    @Delete
    suspend fun deleteSource(source: BookSourceEntity)

    @Query("DELETE FROM book_sources WHERE id = :sourceId")
    suspend fun deleteSourceById(sourceId: String)

    @Query("UPDATE book_sources SET enabled = :enabled WHERE id = :sourceId")
    suspend fun setEnabled(sourceId: String, enabled: Boolean)
}