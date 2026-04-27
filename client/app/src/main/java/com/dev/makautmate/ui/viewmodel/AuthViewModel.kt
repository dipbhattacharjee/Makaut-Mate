package com.dev.makautmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.domain.model.User
import com.dev.makautmate.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val guestModeUseCase: GuestModeUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collectLatest { user ->
                _currentUserProfile.value = user
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            loginUseCase(email, pass).fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.localizedMessage ?: "Login Failed") }
            )
        }
    }

    fun signup(
        fullName: String,
        email: String,
        pass: String,
        phone: String,
        college: String,
        department: String,
        semester: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = User(
                fullName = fullName,
                email = email,
                phoneNumber = phone,
                college = college,
                department = department,
                semester = semester
            )
            signupUseCase(user, pass).fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.localizedMessage ?: "Signup Failed") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState.Idle
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            signInWithGoogleUseCase(idToken).fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.localizedMessage ?: "Google Login Failed") }
            )
        }
    }

    fun loginAsGuest() {
        guestModeUseCase.setGuestMode(true)
        _authState.value = AuthState.Success
    }

    fun isGuestMode(): Boolean = guestModeUseCase.isGuestMode()

    fun isUserLoggedIn(): Boolean {
        // This is a bit tricky because getCurrentUser is a Flow. 
        // For splash check, we might want a synchronous check or a one-shot UseCase.
        // Let's add a check in the repository for immediate state.
        return _currentUserProfile.value != null || guestModeUseCase.isGuestMode()
    }
}
