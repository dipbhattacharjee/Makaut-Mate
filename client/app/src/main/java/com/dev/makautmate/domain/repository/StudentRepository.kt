package com.dev.makautmate.domain.repository

import com.dev.makautmate.domain.model.*
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getNotices(): Flow<List<Notice>>
    suspend fun submitMarks(subject: String, marks: Int, semester: Int): Result<Unit>
    suspend fun trackActivity(type: String): Result<Unit>
    suspend fun generateGradeCard(studentId: String): Result<GradeCardResponse>
}
