package com.dev.makautmate.domain.usecase.portal

import com.dev.makautmate.domain.repository.PortalRepository
import javax.inject.Inject

class LoginToPortalUseCase @Inject constructor(
    private val repository: PortalRepository
) {
    suspend operator fun invoke(roll: String, dob: String): Result<Unit> {
        if (roll.isBlank() || dob.isBlank()) {
            return Result.failure(Exception("Roll Number and DOB are required"))
        }
        return repository.syncProfile(roll, dob)
    }
}
