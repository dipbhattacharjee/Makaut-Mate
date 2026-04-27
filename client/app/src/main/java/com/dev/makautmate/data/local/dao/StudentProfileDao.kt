package com.dev.makautmate.data.local.dao

import androidx.room.*
import com.dev.makautmate.data.local.entity.StudentProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profile LIMIT 1")
    fun getStudentProfile(): Flow<StudentProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: StudentProfileEntity)

    @Query("DELETE FROM student_profile")
    suspend fun clearProfile()
}
