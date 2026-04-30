package com.dev.makautmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dev.makautmate.data.local.dao.AttendanceDao
import com.dev.makautmate.data.local.dao.BookDao
import com.dev.makautmate.data.local.dao.NoteDao
import com.dev.makautmate.data.local.dao.StudentProfileDao
import com.dev.makautmate.data.local.entity.AttendanceEntity
import com.dev.makautmate.data.local.entity.BookEntity
import com.dev.makautmate.data.local.entity.NoteEntity
import com.dev.makautmate.data.local.entity.StudentProfileEntity

@Database(
    entities = [NoteEntity::class, AttendanceEntity::class, StudentProfileEntity::class, BookEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun bookDao(): BookDao
}
