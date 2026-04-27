package com.dev.makautmate.domain.usecase

import com.dev.makautmate.domain.repository.AuthRepository
import javax.inject.Inject

class GuestModeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    fun setGuestMode(isGuest: Boolean) {
        repository.setGuestMode(isGuest)
    }

    fun isGuestMode(): Boolean {
        return repository.isGuestMode()
    }
}
