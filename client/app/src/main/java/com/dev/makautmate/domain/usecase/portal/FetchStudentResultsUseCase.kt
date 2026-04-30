package com.dev.makautmate.domain.usecase.portal

import com.dev.makautmate.domain.model.StudentProfile
import com.dev.makautmate.domain.repository.PortalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchStudentResultsUseCase @Inject constructor(
    private val repository: PortalRepository
) {
    operator fun invoke(): Flow<StudentProfile?> {
        return repository.getStudentProfile()
    }
}
