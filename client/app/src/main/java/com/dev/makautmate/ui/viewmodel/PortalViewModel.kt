package com.dev.makautmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.data.local.SecurityHelper
import com.dev.makautmate.domain.model.StudentProfile
import com.dev.makautmate.domain.usecase.portal.FetchStudentResultsUseCase
import com.dev.makautmate.domain.usecase.portal.LoginToPortalUseCase
import com.dev.makautmate.domain.usecase.portal.SyncPortalDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortalViewModel @Inject constructor(
    private val loginToPortalUseCase: LoginToPortalUseCase,
    private val fetchStudentResultsUseCase: FetchStudentResultsUseCase,
    private val syncPortalDataUseCase: SyncPortalDataUseCase,
    private val securityHelper: SecurityHelper
) : ViewModel() {

    val studentProfile: StateFlow<StudentProfile?> = fetchStudentResultsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun syncData() {
        viewModelScope.launch {
            _isSyncing.value = true
            syncPortalDataUseCase()
                .onFailure { _error.emit(it.message ?: "Sync failed") }
            _isSyncing.value = false
        }
    }

    fun loginToPortal(roll: String, dob: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            loginToPortalUseCase(roll, dob)
                .onSuccess {
                    securityHelper.saveCredentials(roll, dob)
                    onSuccess()
                }
                .onFailure { _error.emit(it.message ?: "Portal login failed") }
            _isSyncing.value = false
        }
    }
    
    fun saveCredentials(roll: String, pass: String) {
        securityHelper.saveCredentials(roll, pass)
    }
}
