package com.dev.makautmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.data.local.SecurityHelper
import com.dev.makautmate.domain.model.StudentProfile
import com.dev.makautmate.domain.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortalViewModel @Inject constructor(
    private val repository: PortalRepository,
    private val securityHelper: SecurityHelper
) : ViewModel() {

    val studentProfile: StateFlow<StudentProfile?> = repository.getStudentProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun syncData() {
        val roll = securityHelper.getRoll()
        val pass = securityHelper.getPass()

        if (roll == null || pass == null) {
            viewModelScope.launch { _error.emit("Credentials not found. Please log in to the portal.") }
            return
        }

        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncProfile(roll, pass)
                .onFailure { _error.emit(it.message ?: "Unknown error occurred") }
            _isSyncing.value = false
        }
    }

    fun loginToPortal(roll: String, dob: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncProfile(roll, dob)
                .onSuccess {
                    securityHelper.saveCredentials(roll, dob)
                    onSuccess()
                }
                .onFailure { _error.emit(it.message ?: "Portal login failed: ${it.localizedMessage}") }
            _isSyncing.value = false
        }
    }
    
    fun saveCredentials(roll: String, pass: String) {
        securityHelper.saveCredentials(roll, pass)
    }
}
