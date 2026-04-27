package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<Unit> {
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Email and Password cannot be empty"))
        }
        return repository.login(email, pass)
    }
}
