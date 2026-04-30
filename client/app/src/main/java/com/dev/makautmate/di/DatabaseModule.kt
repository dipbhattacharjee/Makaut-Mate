package com.dev.makautmate.di

import android.content.Context
import androidx.room.Room
import com.dev.makautmate.data.local.AppDatabase
import com.dev.makautmate.data.local.dao.AttendanceDao
import com.dev.makautmate.data.local.dao.BookDao
import com.dev.makautmate.data.local.dao.NoteDao
import com.dev.makautmate.data.local.dao.StudentProfileDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "makaut_mate_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    fun provideStudentProfileDao(database: AppDatabase): StudentProfileDao {
        return database.studentProfileDao()
    }

    @Provides
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }
}
