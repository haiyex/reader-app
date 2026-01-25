package com.reader.app.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.reader.app.data.local.database.ReaderDatabase
import com.reader.app.data.local.database.dao.*
import com.reader.app.data.local.preferences.ReaderPreferences
import com.reader.app.data.repository.*
import com.reader.app.data.repository.impl.*
import com.reader.app.utils.FileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideReaderDatabase(
        @ApplicationContext context: Context
    ): ReaderDatabase {
        return Room.databaseBuilder(
            context,
            ReaderDatabase::class.java,
            "reader_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBookDao(database: ReaderDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    fun provideReadingProgressDao(database: ReaderDatabase): ReadingProgressDao {
        return database.readingProgressDao()
    }

    @Provides
    fun provideChapterCacheDao(database: ReaderDatabase): ChapterCacheDao {
        return database.chapterCacheDao()
    }

    @Provides
    fun provideBookSourceDao(database: ReaderDatabase): BookSourceDao {
        return database.bookSourceDao()
    }

    @Provides
    fun provideOnlineBookDao(database: ReaderDatabase): OnlineBookDao {
        return database.onlineBookDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideReaderPreferences(
        @ApplicationContext context: Context
    ): ReaderPreferences {
        return ReaderPreferences(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBookRepository(impl: BookRepositoryImpl): BookRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideReadingProgressRepository(impl: ReadingProgressRepositoryImpl): ReadingProgressRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideChapterCacheRepository(impl: ChapterCacheRepositoryImpl): ChapterCacheRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideBookSourceRepository(impl: BookSourceRepositoryImpl): BookSourceRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideOnlineBookRepository(impl: OnlineBookRepositoryImpl): OnlineBookRepository {
        return impl
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFileManager(@ApplicationContext context: Context): FileManager {
        return FileManager(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
