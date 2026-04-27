package com.dev.makautmate.data.repository

import com.dev.makautmate.data.local.dao.StudentProfileDao
import com.dev.makautmate.data.local.entity.toDomain
import com.dev.makautmate.data.local.entity.toEntity
import com.dev.makautmate.data.remote.LoginRequest
import com.dev.makautmate.data.remote.PortalApiService
import com.dev.makautmate.domain.model.StudentProfile
import com.dev.makautmate.domain.repository.PortalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalRepositoryImpl @Inject constructor(
    private val apiService: PortalApiService,
    private val studentProfileDao: StudentProfileDao
) : PortalRepository {

    override fun getStudentProfile(): Flow<StudentProfile?> {
        return studentProfileDao.getStudentProfile().map { it?.toDomain() }
    }

    override suspend fun syncProfile(roll: String, pass: String): Result<Unit> = try {
        val profile = apiService.getStudentData(LoginRequest(roll, pass))
        studentProfileDao.insertStudentProfile(profile.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
