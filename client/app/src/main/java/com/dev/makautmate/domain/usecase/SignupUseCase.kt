package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.model.User
import com.dev.makautmate.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: User, pass: String): Result<Unit> {
        if (user.email.isBlank() || pass.isBlank() || user.fullName.isBlank()) {
            return Result.failure(Exception("Please fill all required fields"))
        }
        return repository.signup(user, pass)
    }
}
