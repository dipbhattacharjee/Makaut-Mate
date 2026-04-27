package com.dev.makautmate.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.domain.usecase.CheckAIRateLimitUseCase
import com.dev.makautmate.domain.usecase.GetAIResponseUseCase
import com.dev.makautmate.domain.usecase.IncrementAIUsageUseCase
import com.dev.makautmate.ui.screens.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val getAIResponseUseCase: GetAIResponseUseCase,
    private val checkAIRateLimitUseCase: CheckAIRateLimitUseCase,
    private val incrementAIUsageUseCase: IncrementAIUsageUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _chatMessages = mutableStateListOf<ChatMessage>()
    val chatMessages: List<ChatMessage> = _chatMessages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        _chatMessages.add(ChatMessage("Hello! I'm your MAKAUT Mate AI. How can I help you today?", false))
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userId = auth.currentUser?.uid ?: return

        _chatMessages.add(ChatMessage(text, true))
        _isTyping.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Check Rate Limit
                val canProceed = checkAIRateLimitUseCase(userId).getOrDefault(false)
                
                if (!canProceed) {
                    _chatMessages.add(ChatMessage("Daily/Monthly limit reached. Upgrade to Premium for unlimited AI access!", false))
                    _isTyping.value = false
                    return@launch
                }

                val result = getAIResponseUseCase(_chatMessages)
                
                result.onSuccess { aiResponse ->
                    _chatMessages.add(ChatMessage(aiResponse, false))
                    incrementAIUsageUseCase(userId)
                }.onFailure { e ->
                    _chatMessages.add(ChatMessage("Error: ${e.localizedMessage ?: "Unknown error"}", false))
                }

            } catch (e: Exception) {
                _chatMessages.add(ChatMessage("Error: ${e.localizedMessage ?: "Unknown error"}", false))
            } finally {
                _isTyping.value = false
            }
        }
    }
}
