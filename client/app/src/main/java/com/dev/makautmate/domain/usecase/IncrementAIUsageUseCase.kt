package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.repository.AIRepository
import javax.inject.Inject

class IncrementAIUsageUseCase @Inject constructor(
    private val repository: AIRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.incrementUsage(userId)
    }
}
