package com.dev.makautmate.domain.repository

import com.dev.makautmate.ui.screens.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    suspend fun getChatResponse(messages: List<ChatMessage>): Result<String>
    suspend fun checkRateLimit(userId: String): Result<Boolean>
    suspend fun incrementUsage(userId: String): Result<Unit>
}
