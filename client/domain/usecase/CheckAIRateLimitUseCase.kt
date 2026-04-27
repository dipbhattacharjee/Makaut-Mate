package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.repository.AIRepository
import javax.inject.Inject

class CheckAIRateLimitUseCase @Inject constructor(
    private val repository: AIRepository
) {
    suspend operator fun invoke(userId: String): Result<Boolean> {
        return repository.checkRateLimit(userId)
    }
}
