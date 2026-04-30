package com.dev.makautmate.domain.repository

import com.dev.makautmate.domain.model.StudentProfile
import kotlinx.coroutines.flow.Flow

interface PortalRepository {
    fun getStudentProfile(): Flow<StudentProfile?>
    suspend fun syncProfile(roll: String, dob: String): Result<Unit>
    suspend fun saveProfile(profile: StudentProfile)
}
