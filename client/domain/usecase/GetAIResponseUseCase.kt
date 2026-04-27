package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.repository.AIRepository
import com.dev.makautmate.ui.screens.ChatMessage
import javax.inject.Inject

class GetAIResponseUseCase @Inject constructor(
    private val repository: AIRepository
) {
    suspend operator fun invoke(messages: List<ChatMessage>): Result<String> {
        return repository.getChatResponse(messages)
    }
}
