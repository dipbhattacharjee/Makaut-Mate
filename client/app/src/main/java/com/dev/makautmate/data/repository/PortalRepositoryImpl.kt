package com.dev.makautmate.data.repository

import android.content.Context
import com.dev.makautmate.data.local.dao.StudentProfileDao
import com.dev.makautmate.data.local.entity.toDomain
import com.dev.makautmate.data.local.entity.toEntity
import com.dev.makautmate.domain.model.StudentProfile
import com.dev.makautmate.domain.repository.PortalRepository
import com.dev.makautmate.util.PortalScraper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class PortalRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val studentProfileDao: StudentProfileDao
) : PortalRepository {

    private val portalScraper = PortalScraper(context)

    override fun getStudentProfile(): Flow<StudentProfile?> {
        return studentProfileDao.getStudentProfile().map { it?.toDomain() }
    }

    override suspend fun syncProfile(roll: String, dob: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        portalScraper.startScraping(roll, dob) { result ->
            result.onSuccess { profile ->
                // Note: Launching a coroutine here might be needed if saveProfile is suspend
                // But since we are in a suspend function, we can just use the result
                continuation.resume(
                    try {
                        // We can't call suspend functions directly in onFinished callback if it's not a coroutine scope
                        // But we can handle it by returning the profile and letting the caller save it, 
                        // or by providing a scope.
                        // For simplicity, let's assume we want to save it immediately.
                        Result.success(profile)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                )
            }
            result.onFailure {
                continuation.resume(Result.failure(it))
            }
        }
    }.let { result ->
        if (result.isSuccess) {
            val profile = result.getOrThrow()
            studentProfileDao.insertStudentProfile(profile.toEntity())
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override suspend fun saveProfile(profile: StudentProfile) {
        studentProfileDao.insertStudentProfile(profile.toEntity())
    }
}
