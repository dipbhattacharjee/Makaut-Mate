package com.dev.makautmate.domain.repository

import com.dev.makautmate.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signup(user: User, password: String): Result<Unit>
    suspend fun logout()
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun isGuestMode(): Boolean
    fun setGuestMode(isGuest: Boolean)
}
