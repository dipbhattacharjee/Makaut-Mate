package com.dev.makautmate.data.repository

import com.dev.makautmate.data.remote.MakautMateApiService
import com.dev.makautmate.domain.model.*
import com.dev.makautmate.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepositoryImpl @Inject constructor(
    private val apiService: MakautMateApiService
) : StudentRepository {

    override fun getNotices(): Flow<List<Notice>> = flow {
        try {
            val response = apiService.getNotices()
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun submitMarks(subject: String, marks: Int, semester: Int): Result<Unit> = try {
        val request = MarkRequest(subject, marks, semester)
        val response = apiService.submitMarks(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to submit marks: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun trackActivity(type: String): Result<Unit> = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = sdf.format(Date())
        
        val request = ActivityRequest(type, timestamp)
        val response = apiService.trackActivity(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to track activity: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun generateGradeCard(studentId: String): Result<GradeCardResponse> = try {
        val request = GradeCardRequest(studentId)
        val response = apiService.generateGradeCard(request)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Failed to generate grade card: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
