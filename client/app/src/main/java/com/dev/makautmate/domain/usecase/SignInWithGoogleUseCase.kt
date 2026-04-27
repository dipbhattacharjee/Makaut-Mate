package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.signInWithGoogle(idToken)
    }
}
